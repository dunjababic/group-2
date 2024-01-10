-- ----- DO NOT CHANGE THE FOLLOWING SQL STATEMENTS -------
-- ------------- BUT EXECUTE THE STATEMENTS ---------------
-- create database
CREATE DATABASE reservation_system;

-- select database
USE reservation_system;

-- create customers table
CREATE TABLE `customers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(100) NOT NULL,
  `lastName` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `customers_UN` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- fill customers table with data
INSERT INTO reservation_system.customers (firstName,lastName,email,password) VALUES 
('Maximilian','Maier','max.m@gmail.com','yxcvuioq')
,('Karla','Arrowsmith','karla.a@gmx.de','kl,.p=q§we')
,('Isabel','Raap','raap.i@web.de','vbnmopiu')
,('Ted','Runkel','t.r@gmail.com','erzte89&')
,('Josephine','Lukowski','josie.luko@gmx.de','qwerasdf')
,('Hans','Massaro','hans.m@web.de','qwerqwer')
,('Syble','Hocking','hocking.s@gmail.com','winter20')
,('Crissy','Deaton','d.crissy@gmx.de','summer21')
,('Edward','Vanmeter','van.ed@web.de','vfrzum21?')
,('Jinny','Toews','toews.j@gmail.com','qwec16%9') 
,('Fiona','Metts','fiona.metts@gmail.com','1oij7&wer')
,('Noah','Constantino','noah.const@web.de','9ikwelf%');

-- ------------------------ END ---------------------------

-- CREATE TABLES
--
create table 'price'
(
    'id'        int auto_increment,
    'currrency' varchar(100) not null,
    PRIMARY KEY ('id')
);



-- INSERT DATA
-- TODO: insert SQL statements to fill tables with exemplary DATA

