-- MySQL dump 10.13  Distrib 9.2.0, for macos15.2 (arm64)
--
-- Host: localhost    Database: calendar_system
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `security_level` enum('LEVEL_1','LEVEL_2','LEVEL_3','LEVEL_4') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_j9xgmd0ya5jmus09o0b8pqrpb` (`email`),
  UNIQUE KEY `UK_3gqbimdf7fckjbwt1kcud141m` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (1,_binary '',NULL,'管理部門','admin@example.com','系統管理員','$2a$10$Z1ve03kPxWTxvzpI.tEtQ.8o5oDpydOQ8avAer3bXSpSPgWBujYN6','LEVEL_1','2025-03-23 20:08:49.629100','admin'),(2,_binary '',NULL,'業務部門','user@example.com','普通用戶','$2a$10$Z1ve03kPxWTxvzpI.tEtQ.8o5oDpydOQ8avAer3bXSpSPgWBujYN6','LEVEL_4','2025-03-24 17:46:42.749704','level4'),(5,_binary '',NULL,'行政部門','level2@example.com','Level 2用戶','$2a$10$Z1ve03kPxWTxvzpI.tEtQ.8o5oDpydOQ8avAer3bXSpSPgWBujYN6','LEVEL_2',NULL,'level2'),(6,_binary '',NULL,'技術部門','level3@example.com','Level 3用戶','$2a$10$Z1ve03kPxWTxvzpI.tEtQ.8o5oDpydOQ8avAer3bXSpSPgWBujYN6','LEVEL_3',NULL,'level3');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_view_permissions`
--

DROP TABLE IF EXISTS `event_view_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_view_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `granted_at` datetime(6) DEFAULT NULL,
  `granted_by` varchar(255) DEFAULT NULL,
  `employee_id` bigint NOT NULL,
  `event_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo9oocd58epmoh1ppvss43gy7f` (`employee_id`),
  KEY `FKhm9cqug8t3gg255ujghyaliaq` (`event_id`),
  CONSTRAINT `FKhm9cqug8t3gg255ujghyaliaq` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`),
  CONSTRAINT `FKo9oocd58epmoh1ppvss43gy7f` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_view_permissions`
--

LOCK TABLES `event_view_permissions` WRITE;
/*!40000 ALTER TABLE `event_view_permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_view_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `end_time` datetime(6) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `security_level` enum('LEVEL_1','LEVEL_2','LEVEL_3','LEVEL_4') NOT NULL,
  `start_time` datetime(6) NOT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `creator_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqqhhbecoakiqr3ubdy79cxghm` (`creator_id`),
  CONSTRAINT `FKqqhhbecoakiqr3ubdy79cxghm` FOREIGN KEY (`creator_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,'2025-03-23 20:09:49.048978','go happy','2025-03-23 18:00:00.000000','','LEVEL_4','2025-03-23 17:00:00.000000','happy','2025-03-23 20:09:49.048978',2),(2,'2025-03-23 20:22:23.202817','哈囉','2025-03-27 18:00:00.000000','教室','LEVEL_1','2025-03-27 17:00:00.000000','你好','2025-03-23 20:22:23.202817',1),(3,'2025-03-24 10:44:51.631653','gogo','2025-03-29 01:00:00.000000','my house','LEVEL_3','2025-03-28 22:00:00.000000','come','2025-03-24 10:45:34.955065',6),(4,'2025-03-24 15:05:45.279533','good','2025-03-10 10:00:00.000000','','LEVEL_3','2025-03-10 09:00:00.000000','哈哈哈','2025-03-24 15:16:00.128577',6),(5,'2025-03-24 15:35:16.413932','level2','2025-03-06 18:00:00.000000','','LEVEL_2','2025-03-06 17:00:00.000000','我是l2 event','2025-03-24 17:32:02.208617',5),(7,'2025-03-24 17:24:41.208404','qqq','2025-03-24 10:00:00.443000','','LEVEL_2','2025-03-24 09:00:00.443000','l2','2025-03-24 17:24:51.363297',5);
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'calendar_system'
--

--
-- Dumping routines for database 'calendar_system'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-25 13:03:35
