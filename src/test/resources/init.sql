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

CREATE PROCEDURE `get_person`()
BEGIN
	select * from person;
END

CREATE PROCEDURE `get_person_count`(OUT c int)
BEGIN
	SELECT COUNT(*) INTO c FROM person;
END