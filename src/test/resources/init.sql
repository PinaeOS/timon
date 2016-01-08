/* 
 * Test Database Init File
 *  
 * Author: Huiyugeng
 * Date: 2015-08-21
 */

DROP TABLE IF EXISTS `person`;

CREATE TABLE `person` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
