delete from requests;
delete from comments;
delete from bookings;
delete from ITEMS;
delete from USERS;

ALTER TABLE requests ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE comments ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE bookings ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE ITEMS ALTER COLUMN ID RESTART WITH 1;

