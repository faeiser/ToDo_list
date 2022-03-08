/*!40101 SET NAMES utf8 */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET SQL_NOTES=0 */;
CREATE DATABASE
/*!32312 IF NOT EXISTS*/
todo
/*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE todo;
DROP TABLE IF EXISTS tasks;
CREATE TABLE `tasks` (
  `tasksId` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `task` varchar(150) DEFAULT NULL,
  `todoId` int(11) NOT NULL,
  `create_time` date DEFAULT NULL COMMENT 'Create Time',
  `update_time` date DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`tasksId`),
  KEY `todoId` (`todoId`),
  CONSTRAINT `tasks_ibfk_1` FOREIGN KEY (`todoId`) REFERENCES `todo` (`todoId`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
DROP TABLE IF EXISTS todo;
CREATE TABLE `todo` (
  `todoId` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `name` varchar(31) DEFAULT NULL,
  `user` varchar(31) NOT NULL,
  `create_time` date DEFAULT NULL COMMENT 'Create Time',
  `update_time` date DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`todoId`),
  KEY `user` (`user`),
  CONSTRAINT `todo_ibfk_1` FOREIGN KEY (`user`) REFERENCES `user` (`user`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
DROP TABLE IF EXISTS user;
CREATE TABLE `user` (
  `user` varchar(31) NOT NULL COMMENT 'Primary Key',
  `create_time` date DEFAULT NULL COMMENT 'Create Time',
  `update_time` date DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`user`),
  UNIQUE KEY `user` (`user`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;