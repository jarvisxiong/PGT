-- MySQL Script generated by MySQL Workbench
-- Sat Nov 14 13:21:01 2015
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema dianjinzi
-- -----------------------------------------------------
# DROP SCHEMA IF EXISTS `DIANJINZI` ;

-- -----------------------------------------------------
-- Schema dianjinzi
-- -----------------------------------------------------
# CREATE SCHEMA IF NOT EXISTS `DIANJINZI` DEFAULT CHARACTER SET UTF8 ;
# USE `DIANJINZI` ;

-- -----------------------------------------------------
-- Table `dianjinzi`.`LOAN`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `LOAN` ;

CREATE TABLE IF NOT EXISTS `LOAN` (
  `LOAN_ID` INT NOT NULL COMMENT '',
  `USER_ID` VARCHAR(45) NOT NULL COMMENT '',
  `LOAN_PERIOD_ID` INT NOT NULL COMMENT '',
  `SETTLEMENT_CYCLE_ID` INT NOT NULL COMMENT '',
  `NAME` VARCHAR(500) NOT NULL COMMENT '',
  `EARNINGS_RATE` DECIMAL(5,2) NOT NULL COMMENT '',
  `SERVICE_FEE_RATE` DECIMAL(5,2) NOT NULL COMMENT '',
  `COLLECT_END_DATE` DATETIME NOT NULL COMMENT '',
  `AMOUNT` DECIMAL(12,2) NOT NULL COMMENT '',
  `INVEST_AMOUNT_MIN` DECIMAL(12,2) NOT NULL COMMENT '',
  `INVEST_AMOUNT_MAX` DECIMAL(12,2) NOT NULL COMMENT '',
  `COUNTER_PURCHASE` INT NOT NULL COMMENT '回购时间',
  `PUBLISHER_ID` INT NOT NULL COMMENT '',
  `STATUS` INT NOT NULL COMMENT '0 = INVALID\n1 = AVAILABLE',
  `REFUND_AMOUNT` DECIMAL(10,2) NULL COMMENT '',
  `CREATION_DATE` DATETIME NOT NULL COMMENT '',
  `UPDATE_DATE` DATETIME NOT NULL COMMENT '',
  `IS_MOCK_DATA` TINYINT(1) NOT NULL COMMENT '',
  `IS_POPULATE` TINYINT(1) NOT NULL COMMENT '',
  `CATEGORY_ID` INT NOT NULL COMMENT '',
  `DISCRIPTION` BLOB NULL COMMENT '',
  PRIMARY KEY (`LOAN_ID`)  COMMENT '')
  ENGINE = InnoDB;

-- ----------------------------
-- Table structure for `banner`
-- ----------------------------
DROP TABLE IF EXISTS `BANNER`;
CREATE TABLE `BANNER` (
  `BANNER_ID` INT(11) NOT NULL AUTO_INCREMENT,
  `IMAGE_URL` VARCHAR(200) DEFAULT NULL,
  `IMAGE_PATH` VARCHAR(200) DEFAULT NULL,
  `TYPE` VARCHAR(45) DEFAULT NULL,
  `STATUS` INT(11) DEFAULT NULL,
  PRIMARY KEY (`BANNER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=UTF8;


-- ----------------------------
-- Table structure for `staticpage`
-- ----------------------------
DROP TABLE IF EXISTS `STATICPAGE`;
CREATE TABLE `STATICPAGE` (
  `STATICPAGE_ID` INT(11) NOT NULL AUTO_INCREMENT,
  `NAME` VARCHAR(30) DEFAULT NULL,
  `JS` VARCHAR(200) DEFAULT NULL,
  `CSS` VARCHAR(200) DEFAULT NULL,
  `STATUS` INT(11) DEFAULT NULL,
  PRIMARY KEY (`STATICPAGE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=UTF8;


-- ----------------------------
-- Table structure for `staticpage_banner`
-- ----------------------------
DROP TABLE IF EXISTS ` STATICPAGE_BANNER`;
CREATE TABLE `STATICPAGE_BANNER` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `BANNER_ID` INT(11) DEFAULT NULL,
  `STATICPAGE_ID` INT(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=UTF8;


-- ----------------------------
-- Table structure for `staticpage_category`
-- ----------------------------
DROP TABLE IF EXISTS `STATICPAGE_CATEGORY`;
CREATE TABLE `STATICPAGE_CATEGORY` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `CATEGORY_ID` INT(11) DEFAULT NULL,
  `STATICPAGE_ID` INT(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=UTF8;




-- -----------------------------------------------------
-- Table `dianjinzi`.`INVESTMENT`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `INVESTMENT` ;

CREATE TABLE IF NOT EXISTS  `INVESTMENT` (
  `INVEST_ID` INT NOT NULL COMMENT '',
  `LOAN_ID` VARCHAR(45) NOT NULL COMMENT '',
  `USER_ID` INT NOT NULL COMMENT '',
  `INVEST_AMOUNT` DECIMAL(12,2) NOT NULL COMMENT '',
  `STATUS` INT NOT NULL COMMENT '0 = INVALID\n1 = AVAILABLE\n-1 = SETTLE_FAILD',
  `INCOMING_AMOUNT` DECIMAL(12,2) NULL COMMENT '',
  `CREATION_DATE` DATETIME NOT NULL COMMENT '',
  `UPDATE_DATE` DATETIME NOT NULL COMMENT '',
  `SETTLE_START_TIME` TIMESTAMP NOT NULL COMMENT 'settle time depends on api',
  PRIMARY KEY (`INVEST_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`USERS`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `USERS` ;

CREATE TABLE IF NOT EXISTS  `USERS` (
  `USER_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `LOGIN` VARCHAR(45) NOT NULL COMMENT '',
  `PASSWORD` VARCHAR(45) NOT NULL COMMENT '',
  `PHONE_NUMBER` VARCHAR(45) NOT NULL COMMENT '',
  `SALT` VARCHAR(45) NOT NULL COMMENT '',
  `EMAIL` VARCHAR(100) NULL COMMENT '',
  `CREATION_DATE` DATETIME NOT NULL COMMENT '',
  `UPDATE_DATE` DATETIME NOT NULL COMMENT '',
  `COUNT` INT NULL COMMENT '',
  `VERIFY_CODE` VARCHAR(20) NULL COMMENT '',
  `AVAILABLE` INT NULL COMMENT '',
  PRIMARY KEY (`USER_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`LOAN_PERIOD`
-- -----------------------------------------------------
DROP TABLE IF EXISTS   `LOAN_PERIOD` ;

CREATE TABLE IF NOT EXISTS  `LOAN_PERIOD` (
  `LOAN_PERIOD_ID` INT NOT NULL COMMENT '',
  `AMOUNT` INT NOT NULL COMMENT '',
  `UNIT` INT NOT NULL COMMENT 'VALUE = 1 -> month\nVALUE = 2 -> day\nVALUE = 3 -> year',
  `DISPLAY_NAME` VARCHAR(200) NOT NULL COMMENT '',
  PRIMARY KEY (`LOAN_PERIOD_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`SETTLEMENT_CYCLE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `SETTLEMENT_CYCLE` ;

CREATE TABLE IF NOT EXISTS  `SETTLEMENT_CYCLE` (
  `SETTLEMENT_CYCLE_ID` INT NOT NULL COMMENT '',
  `DISPLAY_NAME` VARCHAR(200) NOT NULL COMMENT '',
  PRIMARY KEY (`SETTLEMENT_CYCLE_ID`)  COMMENT '')
  ENGINE = InnoDB
  COMMENT = 'ID= 1 -> 到期结算\nID= 2 -> 月结';


-- -----------------------------------------------------
-- Table `dianjinzi`.`CATEGORIES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `CATEGORIES` ;

CREATE TABLE IF NOT EXISTS  `CATEGORIES` (
  `CATEGORY_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `NAME` VARCHAR(45) NOT NULL COMMENT '',
  `PARENT_CATEGORY_ID` INT NULL COMMENT '',
  `CODE` VARCHAR(45) NOT NULL COMMENT '',
  `STATUS` INT NULL COMMENT '0 = INVALID\n1 = AVAILABLE',
  `TYPE` VARCHAR(45) NULL COMMENT '1 = NAVIGATION\n2 = RELAED PRODUCT\n',
  PRIMARY KEY (`CATEGORY_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`INTERNAL_USER`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `INTERNAL_USER` ;

CREATE TABLE IF NOT EXISTS  `INTERNAL_USER` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT COMMENT '',
  `LOGIN` VARCHAR(40) NOT NULL COMMENT '',
  `PASSWORD` VARCHAR(32) NOT NULL COMMENT '',
  `SALT` VARCHAR(32) NOT NULL COMMENT '',
  `ROLE` VARCHAR(20) NOT NULL COMMENT 'GHOST(-1), INVESTOR(1), ADMINISTRATOR(2), DEVELOPER(4)',
  `INVEST_TYPE` VARCHAR(20) NOT NULL COMMENT '',
  `AVAILABLE` BIT(1) NOT NULL COMMENT '0 = INVALID\n1 = AVAILABLE',
  `NAME` VARCHAR(40) NULL COMMENT '',
  `PHONE` VARCHAR(11) NULL COMMENT '',
  `EMAIL` VARCHAR(50) NULL COMMENT '',
  `IP` VARCHAR(45) NULL COMMENT '',
  `CREATION_DATE` DATETIME NOT NULL COMMENT '',
  `LAST_LOGIN_DATE` DATETIME NOT NULL COMMENT '',
  `UPDATE_DATE` DATETIME NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '')
  ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARACTER SET = UTF8;


-- -----------------------------------------------------
-- Table `dianjinzi`.`PRODUCT`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `PRODUCT` ;

CREATE TABLE IF NOT EXISTS  `PRODUCT` (
  `PRODUCT_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `NAME` VARCHAR(200) NULL COMMENT '',
  `SERIAL_NUMBER` VARCHAR(45) NULL COMMENT '',
  `LIST_PRICE` DECIMAL(10,2) NULL COMMENT '',
  `SALE_PRICE` DECIMAL(10,2) NULL COMMENT '',
  `STATUS` INT NULL COMMENT '0 = INVALID\n1 = AVAILABLE',
  `DESCRIPTION` BLOB NULL COMMENT '',
  `SHIPPING_FEE` DECIMAL(10,2) NULL COMMENT '',
  `RELATED_CATEGORY_ID` VARCHAR(45) NULL COMMENT '',
  `STOCK` INT NULL COMMENT '',
  `CREATION_DATE` DATETIME NULL COMMENT '',
  `UPDATE_DATE` DATETIME NULL COMMENT '',
  PRIMARY KEY (`PRODUCT_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`PRODUCT_CATEGORY`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `PRODUCT_CATEGORY` ;

CREATE TABLE IF NOT EXISTS  `PRODUCT_CATEGORY` (
  `ID` INT NOT NULL COMMENT '',
  `PRODUCT_ID` VARCHAR(45) NOT NULL COMMENT '',
  `CATEGORY_ID` VARCHAR(45) NOT NULL COMMENT '',
  PRIMARY KEY (`ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`ORDER`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `ORDER` ;

CREATE TABLE IF NOT EXISTS  `ORDER` (
  `ORDER_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `USER_ID` VARCHAR(45) NULL COMMENT '',
  `STATUS` INT NULL COMMENT '1=INITAL\n2=COMPLETE\n3=PAY\n4=NO_PENDING_ACTION',
  `SHIPPING_FEE` DECIMAL(10,2) NULL COMMENT '',
  `SUBTOTAL` DECIMAL(10,2) NULL COMMENT '',
  `TOTAL` DECIMAL(10,2) NULL COMMENT '',
  `CREATION_DATE` DATETIME NULL COMMENT '',
  `UPDATE_DATE` DATETIME NULL COMMENT '',
  `USER_COMMENTS` VARCHAR(500) NULL COMMENT '',
  `SUBMIT_DATE` DATETIME NULL COMMENT '',
  PRIMARY KEY (`ORDER_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`COMMERCE_ITEM`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `COMMERCE_ITEM` ;

CREATE TABLE IF NOT EXISTS  `COMMERCE_ITEM` (
  `COMMERCE_ITEM_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `ORDER_ID` VARCHAR(45) NULL COMMENT '',
  `REFERENCE_ID` INT NULL COMMENT '',
  `NAME` VARCHAR(45) NULL COMMENT '',
  `LIST_PRICE` DECIMAL(10,2) NULL COMMENT '',
  `SALE_PRICE` DECIMAL(10,2) NULL COMMENT '',
  `QUANTITY` INT NULL COMMENT '',
  `AMOUNT` DECIMAL(10,2) NULL COMMENT 'TOTAL PRICE WITH QUANTITY\nNOT INCLUDE SHIPPING FEE',
  `SNAPSHOT_ID` INT NULL COMMENT 'Reference to media',
  `SHIPPING_FEE` DECIMAL(10,2) NULL COMMENT 'COPY FROM PRODUCT\nUSE TO CALCULATE SHIPPING AMOUNT\n',
  `INDEX` INT NULL COMMENT '',
  `CREATION_DATE` DATETIME NULL COMMENT '',
  `UPDATE_DATE` DATETIME NULL COMMENT '',
  PRIMARY KEY (`COMMERCE_ITEM_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`MEDIA`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `MEDIA` ;

CREATE TABLE IF NOT EXISTS  `MEDIA` (
  `MEDIA_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `REFERENCE_ID` INT NOT NULL COMMENT '',
  `TITLE` VARCHAR(45) NULL COMMENT '',
  `PATH` VARCHAR(500) NULL COMMENT '',
  `TYPE` VARCHAR(45) NULL COMMENT '',
  `INDEX` INT NULL COMMENT '',
  PRIMARY KEY (`MEDIA_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`SHIPPING`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `SHIPPING` ;

CREATE TABLE IF NOT EXISTS  `SHIPPING` (
  `SHIPPING_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `ORDER_ID` VARCHAR(45) NULL COMMENT '',
  `SHIPPING_METHOD_ID` INT NULL COMMENT '',
  `TRACKING_NUMBER` VARCHAR(100) NULL COMMENT '',
  `SHIPING_ADDRESS_ID` INT NULL COMMENT '',
  `STATUS` VARCHAR(45) NULL COMMENT '',
  `CREATION_DATE` DATETIME NULL COMMENT '',
  `UPDATE_DATE` DATETIME NULL COMMENT '',
  `AMOUNT` DECIMAL(10,2) NULL COMMENT 'CALCULATE FROM COMMERCE ITEM SHIPPING FEE',
  PRIMARY KEY (`SHIPPING_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`SHIPPING_ADDRESS`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `SHIPPING_ADDRESS` ;

CREATE TABLE IF NOT EXISTS  `SHIPPING_ADDRESS` (
  `ADDRESS_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `NAME` VARCHAR(100) NOT NULL COMMENT '',
  `PHONE` VARCHAR(45) NOT NULL COMMENT '',
  `PROVINCE` VARCHAR(45) NULL COMMENT '',
  `CITY` VARCHAR(45) NULL COMMENT '',
  `DISTRICT` VARCHAR(45) NULL COMMENT '',
  `ADDRESS` VARCHAR(200) NULL COMMENT '',
  `STATUS` INT NULL COMMENT '0=INVALID\n1=AVAILABLE',
  PRIMARY KEY (`ADDRESS_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`SHIPPING_METHOD`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `SHIPPING_METHOD` ;

CREATE TABLE IF NOT EXISTS  `SHIPPING_METHOD` (
  `SHIPPING_METHOD_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `NAME` VARCHAR(100) NULL COMMENT '',
  `STATUS` INT NULL COMMENT '0=INVALID\n1=AVAILABLE',
  `CREATION_DATE` DATETIME NULL COMMENT '',
  `UPDATE_DATE` DATETIME NULL COMMENT '',
  PRIMARY KEY (`SHIPPING_METHOD_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`PAYMENT_GROUP`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `PAYMENT_GROUP` ;

CREATE TABLE IF NOT EXISTS  `PAYMENT_GROUP` (
  `PAYMENT_GROUP_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `ORDER_ID` INT NULL COMMENT '',
  `AMOUNT` DECIMAL(12,2) NULL COMMENT '',
  `STATUS` INT NULL COMMENT '',
  `TYPE` INT NULL COMMENT '',
  `PAYMENT_INFO` VARCHAR(1000) NULL COMMENT 'JSON FORMAT',
  `CREATION_DATE` DATETIME NULL COMMENT '',
  PRIMARY KEY (`PAYMENT_GROUP_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`TRANSACTIONS`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `TRANSACTIONS` ;

CREATE TABLE IF NOT EXISTS  `TRANSACTIONS` (
  `TRANSACTION_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `PAYMENT_GROUP_ID` VARCHAR(45) NULL COMMENT '',
  `AMOUNT` DECIMAL(12,2) NULL COMMENT '',
  `TRACKING_ID` VARCHAR(100) NULL COMMENT '',
  `TRANSACTION_TIME` DATETIME NULL COMMENT '',
  `OTHER_INFO` VARCHAR(1000) NULL COMMENT 'JSON FORMAT ',
  `STATUS` INT NULL COMMENT '0=FAILED\n1=SUCESS',
  `CREATION_DATE` DATETIME NULL COMMENT '',
  `UDPATE_DATE` DATETIME NULL COMMENT '',
  PRIMARY KEY (`TRANSACTION_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`TRANSACTION_LOG`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `TRANSACTION_LOG` ;

CREATE TABLE IF NOT EXISTS  `TRANSACTION_LOG` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `ORDER_ID` INT NULL COMMENT '',
  `USER_ID` INT NULL COMMENT '',
  `PAYMENT_GROUP_ID` INT NULL COMMENT '',
  `TRANSACTION_ID` INT NULL COMMENT '',
  `INBOUND` BLOB NULL COMMENT '',
  `OUTBOUND` BLOB NULL COMMENT '',
  `INBOUND_TIME` DATETIME NULL COMMENT '',
  `OUTBOUND_TIME` DATETIME NULL COMMENT '',
  PRIMARY KEY (`ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`HOT`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `HOT` ;

CREATE TABLE IF NOT EXISTS  `HOT` (
  `HOT_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `LOCATION` INT NULL COMMENT '',
  `PRODUCT_ID` INT NULL COMMENT '',
  `BEGIN_DATE` TIME NULL COMMENT '',
  `END_DATE` TIME NULL COMMENT '',
  `STATUS` INT NULL COMMENT '',
  `TEST` VARCHAR(45) NULL COMMENT '',
  PRIMARY KEY (`HOT_ID`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dianjinzi`.`FAVOURITE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS  `FAVOURITE` ;

CREATE TABLE IF NOT EXISTS  `FAVOURITE` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `USER_ID` INT NOT NULL COMMENT '',
  `PROD_ID` INT NOT NULL COMMENT '',
  `NAME` VARCHAR(200) NOT NULL COMMENT '',
  `FINAL_PRICE` DECIMAL(10,2) NOT NULL COMMENT '',
  `DESCRIPTION` BLOB NULL COMMENT '',
  `SNAPSHOT_ID` INT NULL COMMENT '',
  `CRATEION_DATE` DATETIME NOT NULL COMMENT '',
  PRIMARY KEY (`ID`)  COMMENT '')
  ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;