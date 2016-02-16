CREATE TABLE `SHARE_ORDER` (
  `SHARE_ORDER_ID` int(11) NOT NULL AUTO_INCREMENT,
  `TITLE` varchar(200) DEFAULT NULL,
  `START` int(11) DEFAULT NULL,
  `READING_QUANTITY` int(11) DEFAULT NULL,
  `CLICK_HIGH` int(11) DEFAULT NULL,
  `CLICK_LOW` int(11) DEFAULT NULL,
  `CREATE_DATE` datetime DEFAULT NULL,
  `IS_SHOW` tinyint(1) DEFAULT '0',
  `USER_ID` int(11) DEFAULT NULL,
  `PRODUCT_ID` int(11) DEFAULT NULL,
  `EVALUATE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`SHARE_ORDER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;