CREATE TABLE `ALIPAY_RESULT` (
  `ALIPAY_ID` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `BUYER_ID` VARCHAR(20) NULL COMMENT '',
  `BUYER_ACCOUNT` VARCHAR(50) NULL COMMENT '',
  `ORDER_ID` INT NULL COMMENT '',
  `CREATION_DATE` DATETIME NULL COMMENT '',
  `UPDATE_DATE` DATETIME NULL COMMENT '',
  PRIMARY KEY (`ALIPAY_ID`)  COMMENT '')
  ENGINE = InnoDB;
  
  
CREATE TABLE `STORE` (
  `STORE_ID` INT NOT NULL COMMENT '',
  `ADDRESS` VARCHAR(300) NULL COMMENT '',
  `PHONE` VARCHAR(20) NULL COMMENT '',
  PRIMARY KEY (`STORE_ID`)  COMMENT '')
  ENGINE = InnoDB;
  
  
CREATE TABLE `STORE_PRODUCT` (
  `ID` INT NOT NULL COMMENT '',
  `PRODUCT_ID` VARCHAR(45) NOT NULL COMMENT '',
  `STORE_ID` INT NOT NULL COMMENT '',
  PRIMARY KEY (`ID`)  COMMENT '')
  ENGINE = InnoDB;
  