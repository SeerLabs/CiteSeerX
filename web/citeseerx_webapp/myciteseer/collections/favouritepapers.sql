-- MySQL dump 10.10
--
-- Host: localhost    Database: citeseer
-- ------------------------------------------------------
-- Server version	5.0.19-standard-log

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
-- Table structure for table `favouritepapers`
--

DROP TABLE IF EXISTS `favouritepapers`;
CREATE TABLE `favouritepapers` (
  `id` int(10) NOT NULL auto_increment,
  `did` int(10) NOT NULL,
  `userid` int(11) NOT NULL,
  `folder` varchar(100) default 'Default',
  PRIMARY KEY  (`id`),
  KEY `did` (`did`),
  KEY `userid` (`userid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `favouritepapers`
--


/*!40000 ALTER TABLE `favouritepapers` DISABLE KEYS */;
LOCK TABLES `favouritepapers` WRITE;
INSERT INTO `favouritepapers` VALUES (1,22422,8,'Default'),(2,189,8,'Default'),(3,6878,1,'Default'),(4,17917,1,'Default'),(5,17917,1,'Default'),(6,189,8,'Default'),(7,133605,8,'Default'),(8,433321,8,'Default'),(9,89237,7,'Default'),(10,564088,7,'Default'),(11,553903,7,'Default'),(12,315287,12,'Default'),(13,536149,12,'Default'),(14,691021,12,'Default'),(15,683843,12,'Default'),(16,669796,12,'Default'),(17,656052,12,'Default'),(18,502993,12,'Default'),(19,7893,12,'Default'),(20,118571,12,'Default'),(21,298209,12,'Default'),(22,531714,12,'Default'),(23,1792,12,'Default'),(24,53107,12,'Default'),(25,193646,12,'Default'),(26,268218,12,'Default'),(27,429830,12,'Default'),(28,449651,12,'Default'),(29,697588,12,'Default'),(30,35520,13,'Default'),(31,512682,13,'Default'),(32,542328,13,'Default'),(33,546054,13,'Default'),(34,546869,13,'Default'),(35,546054,1,'Default'),(36,37130,1,'Default'),(37,624920,1,'Default'),(38,447891,1,'Default'),(39,47440,1,'Default'),(40,20053,8,'Default'),(41,321970,8,'Default'),(42,321970,8,'machine learning'),(43,141221,8,'machine learning'),(44,17917,8,'machine learning'),(45,447891,8,'machine learning'),(46,13255,8,'machine learning'),(47,0,8,'data mining'),(48,22422,8,'data mining'),(49,321970,8,'data mining'),(50,0,8,'temporary'),(51,141221,8,'temporary');
UNLOCK TABLES;
/*!40000 ALTER TABLE `favouritepapers` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

