/* 
 * SQLScript Test File
 *  
 * Author: Huiyugeng
 * Date: 2015-08-06
 */

-- First SQL
select * /* All Field */ from User /*User Table*/ where id = 1; --Test This SQL

-- Second SQL
select 
	id, name, password -- Use for Login
from User; /* User Table */

-- Third SQL
/* Edit by Hui */ select
	 rowid, /* 
	 * Field
	 */ id,
	 name, password
from User;

select id from User; select name from User; select password from User;

select id from User; select 
 name, password from User;