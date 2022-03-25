/*
 Navicat Premium Data Transfer

 Source Server         : Toanhv22
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : bank

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 20/11/2021 20:49:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`  (
                            `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                            `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                            `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                            `role` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                            `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES ('01FMN2MMD82N7WD1HQHTA18ZF9', 'employee', '123456', 'EMPLOYEE', 'abcdefgh');
INSERT INTO `account` VALUES ('01FMQPYD9E3DZBCGVTCBDHN9YH', 'employee_1', '123456', 'EMPLOYEE', 'abcdefgh');
INSERT INTO `account` VALUES ('543632', 'admin', '123456', 'ADMIN', 'toantoan24620@gmail.com');

-- ----------------------------
-- Table structure for bank_account
-- ----------------------------
DROP TABLE IF EXISTS `bank_account`;
CREATE TABLE `bank_account`  (
                                 `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `balance` double NULL DEFAULT NULL,
                                 `interest_rate` double NOT NULL,
                                 `created_date` date NOT NULL,
                                 `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `customer_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `employee_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bank_account
-- ----------------------------
INSERT INTO `bank_account` VALUES ('1111111111111', 6000000, 10, '2020-06-01', 'CREDIT', '6546654', '1111');
INSERT INTO `bank_account` VALUES ('222222', 5000000, 6, '2020-12-01', 'DEPOSIT', '6546654', '1111');

-- ----------------------------
-- Table structure for credit_account
-- ----------------------------
DROP TABLE IF EXISTS `credit_account`;
CREATE TABLE `credit_account`  (
                                   `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `max_loan` double NOT NULL,
                                   `expiration_date` date NOT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of credit_account
-- ----------------------------
INSERT INTO `credit_account` VALUES ('1111111111111', 20000000, '2023-06-13');

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer`  (
                             `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             `id_card` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             `fullname` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             `phonenumber` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             `date_of_birth` date NULL DEFAULT NULL,
                             `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             `account_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer
-- ----------------------------
INSERT INTO `customer` VALUES ('6546654', '5646436436', 'Hoang Vinh Toan', '09796548654', '2015-10-01', 'HN', '654376');

-- ----------------------------
-- Table structure for deposit_account
-- ----------------------------
DROP TABLE IF EXISTS `deposit_account`;
CREATE TABLE `deposit_account`  (
                                    `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                    `min_balance` double NOT NULL,
                                    `first_recharge` double NULL DEFAULT NULL,
                                    `first_deposit_date` date NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of deposit_account
-- ----------------------------
INSERT INTO `deposit_account` VALUES ('222222', 100000, 5000000, '2021-11-20');

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
                             `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             `id_card` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             `fullname` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             `phonenumber` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             `monthly_salary` double NOT NULL,
                             `date_of_birth` date NULL DEFAULT NULL,
                             `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             `job_level` int NULL DEFAULT NULL,
                             `seniority` int NULL DEFAULT NULL,
                             `position` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             `account_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES ('1111', '685437067346', 'ToanHV', '0764589758', 658943869, '2015-10-01', 'HN', 1, 2, 'SV', '01FMN2MMD82N7WD1HQHTA18ZF9');
INSERT INTO `employee` VALUES ('1122', '746554624654', 'ToanHV246', '653467367', 10000000, '2000-10-01', 'HN', 4, 2, 'DEV', '01FMQPYD9E3DZBCGVTCBDHN9YH');

-- ----------------------------
-- Table structure for paycheck
-- ----------------------------
DROP TABLE IF EXISTS `paycheck`;
CREATE TABLE `paycheck`  (
                             `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             `amount` double NOT NULL,
                             `created_date` date NOT NULL,
                             `employee_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                             `monthly_salary` double NOT NULL,
                             `month` int NOT NULL,
                             `year` int NOT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of paycheck
-- ----------------------------

-- ----------------------------
-- Table structure for transaction
-- ----------------------------
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction`  (
                                `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                `id_bank_source` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                `id_bank_target` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                `transaction_amount` double NOT NULL,
                                `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                `transaction_date` date NOT NULL,
                                `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transaction
-- ----------------------------
INSERT INTO `transaction` VALUES ('01FMYPZ9BCHKMHBCHH8HQEZ3RD', NULL, '222222', 5000000, 'NAP_TIEN_VAO_TK_GUI_TIEN', '2021-11-20', NULL);

SET FOREIGN_KEY_CHECKS = 1;
