-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: musicappdb
-- ------------------------------------------------------
-- Server version	8.0.41

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
  `genre` varchar(255) NOT NULL,
  `duration_ms` int NOT NULL,
  `spotify_url` varchar(512) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `spotify_id` (`spotify_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `music`
--

LOCK TABLES `music` WRITE;
/*!40000 ALTER TABLE `music` DISABLE KEYS */;
INSERT INTO `music` VALUES (6,'6noDJ06v7z8bCsBitjxFG2','The Sentinel','Judas Priest','Defenders Of The Faith','heavy metal, metal, hard rock, glam metal',302066,'https://open.spotify.com/track/6noDJ06v7z8bCsBitjxFG2'),(7,'43zdsphuZLzwA9k4DJhU0I','when the party\'s over','Billie Eilish','WHEN WE ALL FALL ASLEEP, WHERE DO WE GO?','Unknown',196077,'https://open.spotify.com/track/43zdsphuZLzwA9k4DJhU0I'),(8,'57P8gH8rjt4OqEoqzut1bL','Round and Round','Ratt','The Essentials: Ratt','glam metal, glam rock, hard rock',265893,'https://open.spotify.com/track/57P8gH8rjt4OqEoqzut1bL'),(9,'2g02rt0UFudctbAnmJsm22','Astronomy','Blue Öyster Cult','Secret Treaties','classic rock, hard rock',383973,'https://open.spotify.com/track/2g02rt0UFudctbAnmJsm22'),(10,'2xU2QWzGunslGXtJbmYbeE','Barrel of a Gun','Depeche Mode','Ultra (Deluxe)','new wave, synthpop, darkwave',336293,'https://open.spotify.com/track/2xU2QWzGunslGXtJbmYbeE');
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

-- Dump completed on 2025-03-17  4:12:21
