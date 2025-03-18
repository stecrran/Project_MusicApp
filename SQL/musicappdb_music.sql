-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: musicappdb
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `music`
--

DROP TABLE IF EXISTS `music`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `music` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `spotify_id` varchar(50) NOT NULL,
  `title` varchar(255) NOT NULL,
  `artist` varchar(255) NOT NULL,
  `album` varchar(255) NOT NULL,
  `genre` varchar(255) DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  `preview_url` varchar(512) DEFAULT NULL,
  `spotify_url` varchar(512) NOT NULL,
  `duration_ms` int NOT NULL DEFAULT '0',
  `popularity` int DEFAULT '0',
  `username` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `spotify_id` (`spotify_id`),
  KEY `fk_music_user` (`user_id`),
  CONSTRAINT `fk_music_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=156 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `music`
--

LOCK TABLES `music` WRITE;
/*!40000 ALTER TABLE `music` DISABLE KEYS */;
INSERT INTO `music` VALUES (152,'6J2LdBN97cDWn0MLxYh9HB','July','Noah Cyrus','THE END OF EVERYTHING','Unknown',NULL,NULL,'https://open.spotify.com/track/6J2LdBN97cDWn0MLxYh9HB',156106,0,NULL,NULL),(153,'2xU2QWzGunslGXtJbmYbeE','Barrel of a Gun','Depeche Mode','Ultra (Deluxe)','new wave, synthpop, darkwave',NULL,NULL,'https://open.spotify.com/track/2xU2QWzGunslGXtJbmYbeE',336293,0,NULL,NULL),(154,'4gUNTtPmsTzpTdJY3aZbLw','Fluff (2009 Remaster)','Black Sabbath','Sabbath Bloody Sabbath','metal, heavy metal, rock, stoner rock, doom metal, hard rock, classic rock',NULL,NULL,'https://open.spotify.com/track/4gUNTtPmsTzpTdJY3aZbLw',248013,0,NULL,NULL),(155,'6uU0Hu8oVk9gBihokcrDMH','One of These Days','Pink Floyd','Meddle','progressive rock, psychedelic rock, classic rock, symphonic rock, art rock, rock',NULL,NULL,'https://open.spotify.com/track/6uU0Hu8oVk9gBihokcrDMH',354613,0,NULL,NULL);
/*!40000 ALTER TABLE `music` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-18  1:59:04
