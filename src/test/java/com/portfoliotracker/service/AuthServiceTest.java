package com.portfoliotracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfoliotracker.dao.UserAccountDao;
import com.portfoliotracker.dto.RegisterRequestDto;
import com.portfoliotracker.exception.EmailTakenException;
import com.portfoliotracker.exception.InvalidCredentialsException;
import com.portfoliotracker.exception.NotLoggedInException;
import com.portfoliotracker.exception.UsernameTakenException;
import com.portfoliotracker.model.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAccountDao userAccountDao;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userAccountDao, encoder);
    }

    @Test
    void registerStoresAHashNotTheRawPassword() {
        when(userAccountDao.createUser(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.register(new RegisterRequestDto("  bob ", " Bob@Example.com ", "secret1x"));

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountDao).createUser(captor.capture());
        UserAccount saved = captor.getValue();

        assertEquals("bob", saved.getUsername());
        assertEquals("bob@example.com", saved.getEmail());
        assertNotEquals("secret1x", saved.getPasswordHash());
        assertTrue(encoder.matches("secret1x", saved.getPasswordHash()));
    }

    @Test
    void registerWithATakenUsernameThrows() {
        when(userAccountDao.findByUsername("bob")).thenReturn(new UserAccount());

        assertThrows(UsernameTakenException.class,
                () -> authService.register(new RegisterRequestDto("bob", "bob@example.com", "secret1x")));
        verify(userAccountDao, never()).createUser(any());
    }

    @Test
    void registerWithATakenEmailThrows() {
        when(userAccountDao.findByEmail("bob@example.com")).thenReturn(new UserAccount());

        assertThrows(EmailTakenException.class,
                () -> authService.register(new RegisterRequestDto("bob", "bob@example.com", "secret1x")));
        verify(userAccountDao, never()).createUser(any());
    }

    @Test
    void registerThrowsUsernameTakenWhenTheDatabaseRejectsADuplicate() {
        when(userAccountDao.createUser(any(UserAccount.class))).thenThrow(new DuplicateKeyException("duplicate"));

        assertThrows(UsernameTakenException.class,
                () -> authService.register(new RegisterRequestDto("bob", "bob@example.com", "secret1x")));
    }

    @Test
    void loginSucceedsWithTheRightPassword() {
        UserAccount stored = storedUser("bob", "secret1x");
        when(userAccountDao.findByUsername("bob")).thenReturn(stored);

        assertSame(stored, authService.login("bob", "secret1x"));
    }

    @Test
    void loginWithAWrongPasswordThrows() {
        when(userAccountDao.findByUsername("bob")).thenReturn(storedUser("bob", "secret1x"));

        assertThrows(InvalidCredentialsException.class, () -> authService.login("bob", "wrong1x"));
    }

    @Test
    void loginWithAnUnknownUsernameThrows() {
        when(userAccountDao.findByUsername("nobody")).thenReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> authService.login("nobody", "secret1x"));
    }

    @Test
    void findByIdReturnsTheStoredUser() {
        UserAccount stored = storedUser("bob", "secret1x");
        when(userAccountDao.findById(1)).thenReturn(stored);

        assertSame(stored, authService.findById(1));
    }

    @Test
    void findByIdForAMissingUserThrows() {
        when(userAccountDao.findById(99)).thenReturn(null);

        assertThrows(NotLoggedInException.class, () -> authService.findById(99));
    }

    private UserAccount storedUser(String username, String rawPassword) {
        UserAccount user = new UserAccount();
        user.setUserName(username);
        user.setPasswordHash(encoder.encode(rawPassword));
        return user;
    }
}
