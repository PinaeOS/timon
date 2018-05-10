USE `test`;
DROP procedure IF EXISTS `get_person`;

DELIMITER $$
USE `test`$$

CREATE PROCEDURE `get_person` ()
BEGIN
select * from person;
END$$

DELIMITER ;

DROP PROCEDURE IF EXISTS `get_person_count`;
DELIMITER $$
USE `test`$$

CREATE PROCEDURE `get_person_count`(OUT c int)
BEGIN
	SELECT COUNT(*) INTO c FROM person;
END$$

DELIMITER ;

GRANT SELECT ON mysql.proc TO 'test'@'%'; 