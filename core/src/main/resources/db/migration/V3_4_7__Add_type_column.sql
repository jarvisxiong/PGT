ALTER TABLE `FAVOURITE` ADD COLUMN `TYPE` INT NOT NULL DEFAULT 1;
UPDATE `FAVOURITE`
SET `TYPE` = 1;

ALTER TABLE `RECENT_BROWSE` ADD COLUMN `TYPE` INT NOT NULL DEFAULT 1;
UPDATE `RECENT_BROWSE`
SET `TYPE` = 1;