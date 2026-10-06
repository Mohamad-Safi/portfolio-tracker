
DROP TABLE IF EXISTS `holding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `holding` (
  `holdingId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `stockId` int NOT NULL,
  `quantity` decimal(18,6) NOT NULL,
  `averagePurchasePrice` decimal(18,4) NOT NULL,
  PRIMARY KEY (`holdingId`),
  UNIQUE KEY `userId` (`userId`,`stockId`),
  KEY `stockId` (`stockId`),
  CONSTRAINT `holding_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user_account` (`userId`),
  CONSTRAINT `holding_ibfk_2` FOREIGN KEY (`stockId`) REFERENCES `stock` (`stockId`),
  CONSTRAINT `holding_chk_1` CHECK ((`quantity` > 0)),
  CONSTRAINT `holding_chk_2` CHECK ((`averagePurchasePrice` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stock`
--

DROP TABLE IF EXISTS `stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock` (
  `stockId` int NOT NULL AUTO_INCREMENT,
  `tickerSymbol` varchar(20) NOT NULL,
  `companyName` varchar(255) NOT NULL,
  PRIMARY KEY (`stockId`),
  UNIQUE KEY `tickerSymbol` (`tickerSymbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_history`
--

DROP TABLE IF EXISTS `transaction_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_history` (
  `transactionId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `stockId` int NOT NULL,
  `transactionType` varchar(20) NOT NULL,
  `quantity` decimal(18,6) NOT NULL,
  `price` decimal(18,4) NOT NULL,
  `transactionDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transactionId`),
  KEY `userId` (`userId`),
  KEY `stockId` (`stockId`),
  CONSTRAINT `transaction_history_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user_account` (`userId`),
  CONSTRAINT `transaction_history_ibfk_2` FOREIGN KEY (`stockId`) REFERENCES `stock` (`stockId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_account`
--

DROP TABLE IF EXISTS `user_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_account` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `passwordHash` varchar(255) NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `watchlist_item`
--

DROP TABLE IF EXISTS `watchlist_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `watchlist_item` (
  `watchlistItemId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `stockId` int NOT NULL,
  `addedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`watchlistItemId`),
  UNIQUE KEY `userId` (`userId`,`stockId`),
  KEY `stockId` (`stockId`),
  CONSTRAINT `watchlist_item_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user_account` (`userId`),
  CONSTRAINT `watchlist_item_ibfk_2` FOREIGN KEY (`stockId`) REFERENCES `stock` (`stockId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-05 14:30:32
