CREATE DATABASE  IF NOT EXISTS `MsgSaving` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `MsgSaving`;
-- MySQL dump 10.13  Distrib 5.6.22, for osx10.8 (x86_64)
--
-- Host: 127.0.0.1    Database: MsgSaving
-- ------------------------------------------------------
-- Server version	5.7.8-rc

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `savedmessages`
--

DROP TABLE IF EXISTS `savedmessages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `savedmessages` (
  `num_id` int(11) NOT NULL AUTO_INCREMENT,
  `mongoid` varchar(45) DEFAULT NULL,
  `raw_id_str` varchar(45) DEFAULT NULL,
  `creat_at` varchar(45) DEFAULT NULL,
  `timestamp_ms` varchar(45) DEFAULT NULL,
  `text` longtext,
  `media_type` varchar(150) DEFAULT NULL,
  `media_urls` varchar(400) DEFAULT NULL,
  `placetype` varchar(45) DEFAULT NULL,
  `placename` varchar(100) DEFAULT NULL,
  `placefullname` varchar(100) DEFAULT NULL,
  `placeboundingtype` varchar(45) DEFAULT NULL,
  `placecoordinates` varchar(200) DEFAULT NULL,
  `country` varchar(45) DEFAULT NULL,
  `province` varchar(45) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `geo_type` varchar(45) DEFAULT NULL,
  `geo_coordinates` varchar(100) DEFAULT NULL,
  `hashtags` varchar(200) DEFAULT NULL,
  `replay_to` varchar(45) DEFAULT NULL,
  `lang` varchar(10) DEFAULT NULL,
  `emotion_text_human` varchar(10) DEFAULT NULL,
  `emotion_text_human_times` int(11) DEFAULT NULL,
  `emotion_text_human_confidence` double DEFAULT NULL,
  `emotion_media_human` varchar(100) DEFAULT NULL,
  `emotion_media_human_times` int(11) DEFAULT NULL,
  `emotion_media_human_confidence` varchar(45) DEFAULT NULL,
  `emotion_text_machine` varchar(45) DEFAULT NULL,
  `emotion_media_machine` varchar(100) DEFAULT NULL,
  `messageFrom` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`num_id`),
  UNIQUE KEY `raw_id_str_UNIQUE` (`raw_id_str`)
) ENGINE=InnoDB AUTO_INCREMENT=39362 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-08-31  0:32:35
