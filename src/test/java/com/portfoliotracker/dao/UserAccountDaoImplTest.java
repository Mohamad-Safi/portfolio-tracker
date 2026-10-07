package com.portfoliotracker.dao;

import com.portfoliotracker.model.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserAccountDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private UserAccountDao userAccountDao;

    @Autowired
    public void UserAccountDaoImplTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        userAccountDao = new UserAccountDaoImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("Create User Then Find By Username Test")
    public void createUserThenFindByUsernameTest() {
        UserAccount user = new UserAccount();
        user.setUserName("dao_test_user");
        user.setEmail("dao_test_user@example.com");
        user.setPasswordHash("hashed-password");
        UserAccount created = userAccountDao.createUser(user);
        assertNotNull(created);
        assertTrue(created.getUserId() > 0);
        assertNotNull(created.getCreatedAt());

        UserAccount found = userAccountDao.findByUsername("dao_test_user");
        assertNotNull(found);
        assertEquals(created.getUserId(), found.getUserId());
        assertEquals("dao_test_user", found.getUsername());
        assertEquals("dao_test_user@example.com", found.getEmail());
        assertEquals("hashed-password", found.getPasswordHash());
    }

    @Test
    @DisplayName("Find By Username Returns Null For Unknown User")
    public void findByUsernameUnknownUserTest() {
        UserAccount found = userAccountDao.findByUsername("dao_test_no_such_user");
        assertNull(found);
    }

    @Test
    @DisplayName("Duplicate Username Throws DuplicateKeyException")
    public void duplicateUsernameTest() {
        UserAccount first = new UserAccount();
        first.setUserName("dao_test_duplicate");
        first.setEmail("dao_test_first@example.com");
        first.setPasswordHash("hashed-password");
        userAccountDao.createUser(first);

        //Same username, different email, so only the username key can be violated.
        UserAccount second = new UserAccount();
        second.setUserName("dao_test_duplicate");
        second.setEmail("dao_test_second@example.com");
        second.setPasswordHash("hashed-password");
        assertThrows(DuplicateKeyException.class, () -> userAccountDao.createUser(second));

        //Only the first insert should have reached the table.
        String sql = "Select count(userId) from user_account where username = 'dao_test_duplicate'";
        int userCount = jdbcTemplate.queryForObject(sql, Integer.class);
        assertEquals(1, userCount);
    }
}
