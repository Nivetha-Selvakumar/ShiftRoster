#employeedb
CREATE TABLE `tbl_employee` (
  `id` int NOT NULL,
  `emp_name` varchar(25) NOT NULL,
  `emp_code` varchar(10) NOT NULL,
  `role` enum('APPRAISER','EMPLOYEE') NOT NULL,
  `email` varchar(50) NOT NULL,
  `appraiser_id` int DEFAULT NULL,
  `emp_status` enum('ACTIVE','INACTIVE') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKAppraiserId` (`appraiser_id`),
  CONSTRAINT `FKAppraiserId` FOREIGN KEY (`appraiser_id`) REFERENCES `tbl_employee` (`id`)
);

#shiftmanagementdb
CREATE TABLE `tbl_shift` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shift_name` varchar(25) NOT NULL,
  `shift_type` enum('DAY','NIGHT') NOT NULL,
  `from_time` time NOT NULL,
  `to_time` time NOT NULL,
  `allowed_in_time` time(6) NOT NULL,
  `allowed_out_time` time(6) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `created_by` varchar(25) NOT NULL,
  `created_date` timestamp NOT NULL,
  `updated_by` varchar(25) NOT NULL,
  `updated_date` timestamp NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `tbl_shift_roster` (
  `id` int NOT NULL AUTO_INCREMENT,
  `emp_id` int NOT NULL,
  `day_01` int DEFAULT NULL,
  `day_02` int DEFAULT NULL,
  `day_03` int DEFAULT NULL,
  `day_04` int DEFAULT NULL,
  `day_05` int DEFAULT NULL,
  `day_06` int DEFAULT NULL,
  `day_07` int DEFAULT NULL,
  `day_08` int DEFAULT NULL,
  `day_09` int DEFAULT NULL,
  `day_10` int DEFAULT NULL,
  `day_11` int DEFAULT NULL,
  `day_12` int DEFAULT NULL,
  `day_13` int DEFAULT NULL,
  `day_14` int DEFAULT NULL,
  `day_15` int DEFAULT NULL,
  `day_16` int DEFAULT NULL,
  `day_17` int DEFAULT NULL,
  `day_18` int DEFAULT NULL,
  `day_19` int DEFAULT NULL,
  `day_20` int DEFAULT NULL,
  `day_21` int DEFAULT NULL,
  `day_22` int DEFAULT NULL,
  `day_23` int DEFAULT NULL,
  `day_24` int DEFAULT NULL,
  `day_25` int DEFAULT NULL,
  `day_26` int DEFAULT NULL,
  `day_27` int DEFAULT NULL,
  `day_28` int DEFAULT NULL,
  `day_29` int DEFAULT NULL,
  `day_30` int DEFAULT NULL,
  `day_31` int DEFAULT NULL,
  `month` int NOT NULL,
  `year` int NOT NULL,
  `created_by` varchar(20) NOT NULL,
  `created_date` timestamp NOT NULL,
  `updated_by` varchar(20) NOT NULL,
  `updated_date` timestamp NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `tbl_template` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ref_id` int NOT NULL,
  `ref_type` varchar(25) NOT NULL,
  `doc_type` enum('EXCEL','PDF','WORD') NOT NULL,
  `document_file` varchar(300) NOT NULL,
  `created_by` varchar(20) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `updated_by` varchar(20) NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
);





