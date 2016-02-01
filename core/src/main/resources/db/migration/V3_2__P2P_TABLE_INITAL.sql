-- -----------------------------------------------------
-- Table `PAWN_SHOP`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `PAWN_SHOP` ;

CREATE TABLE IF NOT EXISTS `PAWN_SHOP` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `OWNER_ID` INT NULL,
  `MANAGER_ID` INT NULL,
  `NAME` VARCHAR(200) NULL,
  `ADDRESS` VARCHAR(500) NULL,
  `CREATION_DATE` DATETIME NULL,
  `UPDATE_DATE` DATETIME NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `TENDER`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `TENDER` ;

CREATE TABLE IF NOT EXISTS `TENDER` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `PAWN_SHOP_ID` INT NULL,
  `PAWN_TICKET_ID` INT NULL,
  `TENDER_TOTAL` DECIMAL(10,2) NULL,
  `TENDER_QUANTITY` INT NULL,
  `PUBLISH_DATE` DATETIME NULL,
  `DUE_DATE` DATETIME NULL,
  `POST_PERIOD` INT NULL,
  `INTEREST_RATE` DECIMAL(6,3) NULL,
  `NAME` VARCHAR(200) NULL,
  `DESCRIPTION` LONGBLOB NULL,
  `PRE_PERIOD` INT NULL,
  `CREATION_DATE` DATETIME NULL,
  `UPDATE_DATE` DATETIME NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `PAWN_TICKET`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `PAWN_TICKET` ;

CREATE TABLE IF NOT EXISTS `PAWN_TICKET` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NUM` VARCHAR(100) NULL,
  `PAWN_SHOP_ID` VARCHAR(45) NULL,
  `CREATION_DATE` DATETIME NULL,
  `UPDATE_DATE` DATETIME NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `P2P_INFO`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `P2P_INFO` ;

CREATE TABLE IF NOT EXISTS `P2P_INFO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `TYPE` INT NULL,
  `PAWN_SHOP_ID` INT NULL,
  `PAWN_SHOP_OWNER_ID` INT NULL,
  `TENDER_ID` INT NULL,
  `EXPECT_DUE_DATE` DATETIME NULL,
  `ACTUAL_DUE_DATE` DATETIME NULL,
  `EXPECT_INCOMING` DECIMAL NULL,
  `ACTUAL_INCOMING` DECIMAL NULL,
  `PUBLISH_DATE` DATETIME NULL,
  `PRE_PERIOD` INT NULL,
  `POST_PERIOD` INT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;

ALTER TABLE `PRODUCT` ADD `TENDER_ID` INT;

ALTER TABLE `ORDER` ADD `TYPE` INT;
ALTER TABLE `ORDER` ADD `P2P_INFO_ID` INT;
ALTER TABLE `ORDER` ADD `HOLDER_ALIAS` VARCHAR(100);

ALTER TABLE `COMMERCE_ITEM` ADD `TYPE` INT;
ALTER TABLE `COMMERCE_ITEM` ADD `OCCUPY` TINYINT;