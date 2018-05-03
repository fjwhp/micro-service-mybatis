/*##################### 说明：本脚本支持重复升级   #####################*/
/*
 功能描述：A9协同办公系统(v1.0.1)升级脚本
*/
/*注意1：一下的SCHEMA名称需要根据具体的环境进行修改*/
USE `aljoin-a9`;
SET FOREIGN_KEY_CHECKS=0;
/*创建 及修改表结构*/
DROP PROCEDURE IF EXISTS `sp_a9_table_structure_update`;
CREATE PROCEDURE `sp_a9_table_structure_update`()
BEGIN
/*注意2：一下的SCHEMA名称需要根据具体的环境进行修改*/
DECLARE D_TABLE_SCHEMA VARCHAR(30) DEFAULT 'aljoin-a9';
/************************************请在以下区域编写数据库升级脚本***********************************BEGIN*/
/*********************************************************************************************************BEGIN*/
/*********************************************************************************************************BEGIN*/
/*********************************************************************************************************BEGIN*/
/*
功能描述：建表TTTT脚本(例子)
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'abcdefg' LIMIT 0,1; 
	IF v_cnt = 0 THEN
			/*******建表sql*******BEGIN*/
			SELECT 1 FROM DUAL;
			/*******建表sql*******END*/
	END IF;
END;

/*
功能描述：TTTT表增加BBBB字段(例子)
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'abcdefg' and COLUMN_NAME = 'c' LIMIT 0,1; 
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			SELECT 1 FROM DUAL;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_FORM_DATA_DRAFT 新增 is_read  data_resource 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_FORM_DATA_DRAFT' and COLUMN_NAME = 'is_read' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `ACT_ALJOIN_FORM_DATA_DRAFT`
      ADD COLUMN is_read int(1) default '0'  not null;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_FORM_DATA_DRAFT 新增 data_resource 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_FORM_DATA_DRAFT' and COLUMN_NAME = 'data_resource' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `ACT_ALJOIN_FORM_DATA_DRAFT`
      ADD COLUMN data_resource text  null;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_FORM_DATA_HIS 新增 is_read  字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_FORM_DATA_HIS' and COLUMN_NAME = 'is_read' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `ACT_ALJOIN_FORM_DATA_DRAFT`
      ADD COLUMN is_read int(1) default '0'  not null;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_FORM_DATA_HIS 新增  data_resource 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_FORM_DATA_HIS' and COLUMN_NAME = 'data_resource' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `ACT_ALJOIN_FORM_DATA_DRAFT`
      ADD COLUMN data_resource text  null;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_FORM_DATA_RUN 新增 is_read  字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_FORM_DATA_RUN' and COLUMN_NAME = 'is_read' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `ACT_ALJOIN_FORM_DATA_DRAFT`
      ADD COLUMN is_read int(1) default '0'  not null;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_FORM_DATA_RUN 新增  data_resource 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_FORM_DATA_RUN' and COLUMN_NAME = 'data_resource' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `ACT_ALJOIN_FORM_DATA_DRAFT`
      ADD COLUMN data_resource text  null;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_TASK_ASSIGNEE 新增 sign_comment_widget_ids 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_TASK_ASSIGNEE' and COLUMN_NAME = 'sign_comment_widget_ids' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `act_aljoin_task_assignee`
      ADD COLUMN `sign_comment_widget_ids`  text NOT NULL AFTER `is_return_creater`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：创建加签信息表（ACT_ALJOIN_TASK_SIGN_INFO）
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_TASK_SIGN_INFO' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******建表sql*******BEGIN*/
			CREATE TABLE `act_aljoin_task_sign_info` (
          `id` bigint(20) NOT NULL COMMENT '主键ID',
          `create_time` datetime NOT NULL COMMENT '创建时间',
          `last_update_time` datetime NOT NULL COMMENT '修改时间',
          `version` int(11) NOT NULL COMMENT '版本号',
          `is_delete` int(1) NOT NULL COMMENT '是否删除',
          `last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
          `last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
          `create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
          `create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
          `bpmn_id` bigint(20) NOT NULL COMMENT '流程表ID',
          `process_instance_id` varchar(64) NOT NULL COMMENT '流程实例ID',
          `process_def_id` varchar(64) NOT NULL COMMENT '流程定义ID',
          `execution_id` varchar(64) NOT NULL COMMENT '执行流ID',
          `task_id` varchar(64) NOT NULL COMMENT '任务ID（原来任务ID）',
          `task_key` varchar(64) NOT NULL COMMENT '任务key',
          `task_name` varchar(100) NOT NULL COMMENT '任务名称',
          `sign_task_id` varchar(64) NOT NULL COMMENT '加签产生的任务ID',
          `task_ids` text NOT NULL COMMENT '加签任务ID路径（从开始到结束）',
          `task_owner_id` bigint(20) NOT NULL COMMENT '任务拥有者ID（最开始的任务拥有者，第一次开始加签人）',
          `task_owner_name` varchar(100) NOT NULL COMMENT '任务拥有者姓名（最开始的任务拥有者，第一次开始加签人）',
          `task_sign_user_id` bigint(20) NOT NULL COMMENT '加签者用户ID',
          `task_sign_user_name` varchar(100) NOT NULL COMMENT '加签者用户姓名',
          `task_signed_user_id` bigint(20) NOT NULL COMMENT '被加签者用户ID，task_sign_user_ids分割后的结果',
          `task_signed_user_name` varchar(100) NOT NULL COMMENT '被加签者用户姓名，task_sign_user_names分割后的结果',
          `is_back_owner` int(1) NOT NULL COMMENT '是否返回流程的拥有者,0-提交进入下一环节，1-返回流程拥有者',
          `task_sign_user_ids` text NOT NULL COMMENT '加签用户ID路径（从开始到结束，逗号分隔）',
          `task_sign_user_names` text NOT NULL COMMENT '加签用户姓名路径（从开始到结束，逗号分隔）',
          `finish_type` int(1) NOT NULL COMMENT '完成类型：0-没做，1-加签完成，2-提交完成',
          `all_task_ids` text NOT NULL COMMENT '所有被加签任务ID路径（从开始到结束）',
          PRIMARY KEY (`id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='加签信息表';
			/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：创建流水号表（SYS_SERIAL_NUMBER）
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'SYS_SERIAL_NUMBER' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******建表sql*******BEGIN*/
			CREATE TABLE `sys_serial_number` (
  			`id` bigint(20) NOT NULL COMMENT '主键ID',
  			`create_time` datetime NOT NULL COMMENT '创建时间',
  			`last_update_time` datetime NOT NULL COMMENT '修改时间',
  			`version` int(11) NOT NULL COMMENT '版本号',
		  	`is_delete` int(1) NOT NULL COMMENT '是否删除',
		  	`last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
		  	`last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
		  	`create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
		  	`create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
		  	`serial_num_name` varchar(100) NOT NULL COMMENT '流水号名称',
		  	`prefix` varchar(100) NOT NULL COMMENT '前缀',
		  	`reign_title_rule` int(1) NOT NULL COMMENT '年号规则（1:无 2：年 3：年月4：年月日）',
		  	`sign` varchar(20) NOT NULL COMMENT '符号',
		  	`is_fix_length` int(1) NOT NULL DEFAULT '0' COMMENT '是否固定长度（1：是；0：否）',
		  	`start_value` int(11) NOT NULL COMMENT '起始值',
		  	`digit` int(11) NOT NULL COMMENT '位数',
		  	`postfix` varchar(100) NOT NULL COMMENT '后缀',
		  	`resetting_rule` int(1) NOT NULL COMMENT '重置规则（1:不重置 2：按年重置 3：按月重置）',
		  	`current_value` bigint(20) NOT NULL COMMENT '当前值',
		  	`status` int(1) NOT NULL COMMENT '状态（1：启用；0：停用）',
		  	`category_id` bigint(20) NOT NULL COMMENT '流水号分类ID',
		  	`process_names` text COMMENT '流程名称(多个用;隔开)',
		     PRIMARY KEY (`id`)
		     ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流水号表';
			/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：创建文号表（SYS_DOCUMENT_NUMBER）
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'SYS_DOCUMENT_NUMBER' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******建表sql*******BEGIN*/
			CREATE TABLE `sys_document_number` (
			  `id` bigint(20) NOT NULL COMMENT '主键ID',
			  `create_time` datetime NOT NULL COMMENT '创建时间',
			  `last_update_time` datetime NOT NULL COMMENT '修改时间',
			  `version` int(11) NOT NULL COMMENT '版本号',
			  `is_delete` int(1) NOT NULL COMMENT '是否删除',
			  `last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
			  `last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
			  `create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
			  `create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
			  `document_num_name` varchar(100) NOT NULL COMMENT '文号名称',
			  `agency_code` varchar(100) NOT NULL COMMENT '机构代字',
			  `reign_title_rule` int(1) NOT NULL COMMENT '年号规则（1:无 2：年）',
			  `document_num_pattern` varchar(100) NOT NULL COMMENT '文号格式(用&拼接)',
			  `is_fix_length` int(1) NOT NULL DEFAULT '0' COMMENT '是否固定长度（1：是；0：否）',
			  `start_value` int(11) NOT NULL COMMENT '起始值',
			  `digit` int(11) NOT NULL COMMENT '位数',
			  `resetting_rule` int(1) NOT NULL COMMENT '重置规则（1:不重置 2：按年重置）',
			  `current_value` bigint(20) NOT NULL COMMENT '当前值',
			  `status` int(1) NOT NULL COMMENT '状态（1：启用；0：停用）',
			  `category_id` bigint(20) NOT NULL COMMENT '文号分类ID',
			  `process_names` text COMMENT '流程名称(多个用;分开)',
			  PRIMARY KEY (`id`)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='公文文号表';
			/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：创建常用字典表（SYS_COMMON_DICT）
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'SYS_COMMON_DICT' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******建表sql*******BEGIN*/
			CREATE TABLE `sys_common_dict` (
			  `id` bigint(20) NOT NULL COMMENT '主键ID',
			  `create_time` datetime NOT NULL COMMENT '创建时间',
			  `last_update_time` datetime NOT NULL COMMENT '修改时间',
			  `version` int(11) NOT NULL COMMENT '版本号',
			  `is_delete` int(1) NOT NULL COMMENT '是否删除',
			  `last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
			  `last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
			  `create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
			  `create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
			  `dict_name` varchar(100) NOT NULL COMMENT '常用字典名称',
			  `dict_rank` int(11) NOT NULL COMMENT '常用字典排序',
			  `dict_content` varchar(200) NOT NULL COMMENT '字典内容',
			  `dict_content_rank` int(11) NOT NULL COMMENT '字典内容排序',
			  `category_id` bigint(20) NOT NULL COMMENT '常用字典分类ID',
			  `is_active` int(1) NOT NULL COMMENT '是否激活(1:是;0:否)',
			  PRIMARY KEY (`id`)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='常用字典表';
			/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：创建公共意见表（SYS_PUBLIC_OPINION）
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'SYS_PUBLIC_OPINION' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******建表sql*******BEGIN*/
			CREATE TABLE `sys_public_opinion` (
			  `id` bigint(20) NOT NULL COMMENT '主键ID',
			  `create_time` datetime NOT NULL COMMENT '创建时间',
			  `last_update_time` datetime NOT NULL COMMENT '修改时间',
			  `version` int(11) NOT NULL COMMENT '版本号',
			  `is_delete` int(1) NOT NULL COMMENT '是否删除',
			  `last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
			  `last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
			  `create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
			  `create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
			  `content` varchar(300) NOT NULL COMMENT '公共意见内容',
			  `content_rank` int(1) NOT NULL COMMENT '公共意见排序',
			  `is_active` int(1) NOT NULL COMMENT '是否激活(1:是;0:否)',
			  PRIMARY KEY (`id`)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='公共意见表';
			/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：创建数据字典分类表（SYS_DICT_CATEGORY）
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'SYS_DICT_CATEGORY' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******建表sql*******BEGIN*/
			CREATE TABLE `sys_dict_category` (
			  `id` bigint(20) NOT NULL COMMENT '主键ID',
			  `create_time` datetime NOT NULL COMMENT '创建时间',
			  `last_update_time` datetime NOT NULL COMMENT '修改时间',
			  `version` int(11) NOT NULL COMMENT '版本号',
			  `is_delete` int(1) NOT NULL COMMENT '是否删除',
			  `last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
			  `last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
			  `create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
			  `create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
			  `category_name` varchar(100) NOT NULL COMMENT '分类名称',
			  `category_rank` int(11) NOT NULL COMMENT '分类排序',
			  `category_level` int(11) NOT NULL COMMENT '分类级别',
			  `parent_id` bigint(20) NOT NULL COMMENT '父级分类ID',
			  `is_active` int(1) NOT NULL COMMENT '是否激活（1：是；0：否）',
			  `dict_type` int(1) NOT NULL COMMENT '字典类型（1：流水号分类；2：公文文号分类；3常用字典分类）',
			  PRIMARY KEY (`id`)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据字典分类表';
			/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_QUERY 新增 serial_number 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_QUERY' and COLUMN_NAME = 'serial_number' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `act_aljoin_query`
      ADD COLUMN `serial_number`  varchar(160) NULL COMMENT '编号' AFTER `start_task`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_QUERY 新增 reference_number 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_QUERY' and COLUMN_NAME = 'reference_number' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `act_aljoin_query`
      ADD COLUMN `reference_number`  varchar(160) NULL COMMENT '文号' AFTER `serial_number`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_QUERY_HIS 新增 serial_number 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_QUERY_HIS' and COLUMN_NAME = 'serial_number' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `ACT_ALJOIN_QUERY_HIS`
      ADD COLUMN `serial_number`  varchar(160) NULL COMMENT '编号' AFTER `start_task`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_QUERY_HIS 新增 reference_number 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_QUERY_HIS' and COLUMN_NAME = 'reference_number' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `ACT_ALJOIN_QUERY_HIS`
      ADD COLUMN `reference_number`  varchar(160) NULL COMMENT '文号' AFTER `serial_number`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_TASK_ASSIGNEE 新增 staff_members_department 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_TASK_ASSIGNEE' and COLUMN_NAME = 'staff_members_department' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `act_aljoin_task_assignee`
      ADD COLUMN `staff_members_department`  int(1) NULL COMMENT '创建人所在部门人员' AFTER `sign_comment_widget_ids`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_TASK_ASSIGNEE 新增 lastlink_department 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_TASK_ASSIGNEE' and COLUMN_NAME = 'lastlink_department' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `act_aljoin_task_assignee`
      ADD COLUMN `lastlink_department`  int(1) NULL COMMENT '上一个环节办理人人所在部门人员' AFTER `staff_members_department`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：ACT_ALJOIN_TASK_ASSIGNEE 新增 create_persons_job 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_TASK_ASSIGNEE' and COLUMN_NAME = 'create_persons_job' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `act_aljoin_task_assignee`
      ADD COLUMN `create_persons_job`  int(1) NULL COMMENT '创建人所在岗位人员办理' AFTER `lastlink_department`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：创建传阅表(ioa_circula)
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ioa_circula' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******建表sql*******BEGIN*/
		CREATE TABLE `ioa_circula` (
  `id` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL,
  `is_delete` int(1) NOT NULL,
  `create_user_id` bigint(20) NOT NULL,
  `create_user_name` varchar(100) NOT NULL,
  `last_update_user_id` bigint(20) NOT NULL,
  `last_update_user_name` varchar(100) NOT NULL,
  `process_instance_id` varchar(64) NOT NULL,
  `cir_ids` text NOT NULL,
  `cir_names` text NOT NULL,
  `create_userfull_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='传阅表';
			/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：创建传阅明细表(ioa_circula_user)
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ioa_circula_user' LIMIT 0,1;
	IF v_cnt = 0 THEN
	/*******建表sql*******BEGIN*/
	CREATE TABLE `ioa_circula_user` (
  `id` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL,
  `is_delete` int(1) NOT NULL,
  `create_user_id` bigint(20) NOT NULL,
  `create_user_name` varchar(100) NOT NULL,
  `last_update_user_id` bigint(20) NOT NULL,
  `last_update_user_name` varchar(100) NOT NULL,
  `process_instance_id` varchar(64) NOT NULL COMMENT '流程实例ID',
  `dept_name` varchar(255) DEFAULT NULL COMMENT '部门名称',
  `opinon` text COMMENT '意见',
  `opinon_time` datetime DEFAULT NULL COMMENT '填写意见时间',
  `create_user_full_name` varchar(100) DEFAULT NULL,
  `return_opinon_time` text,
  `return_opinon` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='传阅明细表';
	/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：新添收发文登记分类表(ioa_reg_category)
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ioa_reg_category' LIMIT 0,1;
	IF v_cnt = 0 THEN
	/*******建表sql*******BEGIN*/
	CREATE TABLE `ioa_reg_category` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `last_update_time` datetime NOT NULL COMMENT '修改时间',
  `version` int(11) NOT NULL COMMENT '版本号',
  `is_delete` int(1) NOT NULL COMMENT '是否删除',
  `last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
  `last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
  `create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
  `category_name` varchar(100) NOT NULL COMMENT '分类名称',
  `is_active` int(1) NOT NULL COMMENT '是否激活',
  `category_rank` int(11) NOT NULL COMMENT '(同级)分类排序',
  `parent_id` bigint(20) NOT NULL COMMENT '父级分类ID',
  `category_level` int(11) NOT NULL COMMENT '分类级别',
  `reg_type` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收发文分类表';
	/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：新添收文登记表(ioa_reg_closed)
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ioa_reg_closed' LIMIT 0,1;
	IF v_cnt = 0 THEN
	/*******建表sql*******BEGIN*/
	CREATE TABLE `ioa_reg_closed` (
  `id` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL,
  `is_delete` int(1) NOT NULL,
  `create_user_id` bigint(20) NOT NULL,
  `create_user_name` varchar(255) NOT NULL,
  `last_update_user_id` bigint(20) NOT NULL,
  `last_update_user_name` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `registration_name` varchar(255) NOT NULL COMMENT '登记人员名称',
  `registration_time` datetime NOT NULL COMMENT '登记日期',
  `closed_no` varchar(255) NOT NULL COMMENT '收文文号',
  `to_no` varchar(255) NOT NULL COMMENT '来文文号',
  `to_type` varchar(255) NOT NULL COMMENT '来文类型',
  `to_unit` varchar(255) NOT NULL COMMENT '来文单位',
  `secret_level` varchar(255) NOT NULL COMMENT '密级',
  `closed_date` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '收文日期',
  `priorities_level` int(1) NOT NULL COMMENT '缓急程度',
  `priorities` varchar(60) DEFAULT NULL COMMENT '返回页面显示缓急程度',
  `closed_number` int(11) NOT NULL COMMENT '份数',
  `level` varchar(60) DEFAULT NULL COMMENT '返回密级字符串',
  `category` varchar(25) NOT NULL COMMENT '所属分类',
  `is_change` int(1) NOT NULL DEFAULT '0' COMMENT '是否可以修改'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收文登记表';
	/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：新添发文登记表(ioa_reg_hair)
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ioa_reg_hair' LIMIT 0,1;
	IF v_cnt = 0 THEN
	/*******建表sql*******BEGIN*/
	CREATE TABLE `ioa_reg_hair` (
  `id` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL,
  `is_delete` int(1) NOT NULL,
  `create_user_id` bigint(20) NOT NULL,
  `create_user_name` varchar(255) NOT NULL,
  `last_update_user_id` bigint(20) NOT NULL,
  `last_update_user_name` varchar(255) NOT NULL,
  `hair_type` varchar(255) NOT NULL COMMENT '发文类型',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `registration_name` varchar(255) NOT NULL COMMENT '登记人员名称',
  `registration_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '登记日期',
  `hair_no` varchar(255) NOT NULL COMMENT '发文文号',
  `hair_unit` varchar(255) NOT NULL COMMENT '发文单位',
  `hair_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '签发时间',
  `secret_level` int(1) NOT NULL,
  `level` varchar(60) DEFAULT NULL COMMENT '返回页面密级程度',
  `category` varchar(25) NOT NULL COMMENT '所属分类',
  `is_change` int(1) NOT NULL DEFAULT '0' COMMENT '是否可以修改'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='发文登记表';
	/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：收件箱索引表(mai_receive_box_search)
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box_search' LIMIT 0,1;
	IF v_cnt = 0 THEN
	/*******建表sql*******BEGIN*/
	CREATE TABLE `mai_receive_box_search` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `subject_text` varchar(200) DEFAULT NULL COMMENT '主题',
  `receive_user_id` bigint(20) NOT NULL COMMENT '(单独)发件人用户ID',
  `is_delete` int(1) NOT NULL COMMENT '是否删除',
  `is_scrap` int(1) NOT NULL COMMENT '是否设置为废件（即删除操作）',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `is_read` int(1) NOT NULL COMMENT '是否已读',
  `is_urgent` int(1) NOT NULL DEFAULT '0' COMMENT '是否紧急（发件方设定）',
  `is_important` int(1) NOT NULL DEFAULT '0' COMMENT '是否重要（收件方自己设定）',
  `attachment_count` int(11) NOT NULL COMMENT '附件个数',
  `send_full_name` varchar(100) NOT NULL COMMENT '发件人账号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `last_update_time` datetime NOT NULL COMMENT '修改时间',
  `version` int(11) NOT NULL COMMENT '版本号',
  `last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
  `last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
  `create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收件箱(索引)表';
	/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：旧数据处理，同步当前数据到mai_receive_box_search表（mai_receive_box表中无数据时，不需此操作，此步骤必须在修改原表前执行）
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT count(1) INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box_search' LIMIT 0,1; 
	IF v_cnt = 0 THEN
		/*******修改sql*******BEGIN*/
		INSERT INTO mai_receive_box_search (
		id,
		subject_text,
		receive_user_id,
		is_delete,
		is_scrap,
		send_time,
		is_read,
		is_urgent,
		is_important,
		attachment_count,
		send_full_name,
		create_time,
		last_update_time,
		version,
		last_update_user_id,
		last_update_user_name,
		create_user_id,
		create_user_name
		)(
		SELECT
			id,
			subject_text,
			receive_user_id,
			is_delete,
			is_scrap,
			send_time,
			is_read,
			is_urgent,
			is_important,
			attachment_count,
			send_full_name,
			create_time,
			last_update_time,
			version,
			last_update_user_id,
			last_update_user_name,
			create_user_id,
			create_user_name
		FROM
			mai_receive_box
	)
	/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：mai_receive_box表删除字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box' and COLUMN_NAME = 'receive_user_id' LIMIT 0,1; 
	IF v_cnt != 0 THEN
		/*******修改sql*******BEGIN*/
		ALTER TABLE `mai_receive_box` DROP COLUMN `receive_user_id`;
		/*******修改sql*******END*/
	END IF;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box' and COLUMN_NAME = 'send_full_name' LIMIT 0,1; 
	IF v_cnt != 0 THEN
		/*******修改sql*******BEGIN*/
		ALTER TABLE `mai_receive_box` DROP COLUMN `send_full_name`;
		/*******修改sql*******END*/
	END IF;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box' and COLUMN_NAME = 'subject_text' LIMIT 0,1; 
	IF v_cnt != 0 THEN
		/*******修改sql*******BEGIN*/
		ALTER TABLE `mai_receive_box` DROP COLUMN `subject_text`;
		/*******修改sql*******END*/
	END IF;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box' and COLUMN_NAME = 'is_scrap' LIMIT 0,1; 
	IF v_cnt != 0 THEN
		/*******修改sql*******BEGIN*/
		ALTER TABLE `mai_receive_box` DROP COLUMN `is_scrap`;
		/*******修改sql*******END*/
	END IF;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box' and COLUMN_NAME = 'receive_user_id' LIMIT 0,1; 
	IF v_cnt != 0 THEN
		/*******修改sql*******BEGIN*/
		ALTER TABLE `mai_receive_box` DROP COLUMN `receive_user_id`;
		/*******修改sql*******END*/
	END IF;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box' and COLUMN_NAME = 'attachment_count' LIMIT 0,1; 
	IF v_cnt != 0 THEN
		/*******修改sql*******BEGIN*/
		ALTER TABLE `mai_receive_box` DROP COLUMN `attachment_count`;
		/*******修改sql*******END*/
	END IF;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box' and COLUMN_NAME = 'send_time' LIMIT 0,1; 
	IF v_cnt != 0 THEN
		/*******修改sql*******BEGIN*/
		ALTER TABLE `mai_receive_box` DROP COLUMN `send_time`;
		/*******修改sql*******END*/
	END IF;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box' and COLUMN_NAME = 'is_read' LIMIT 0,1; 
	IF v_cnt != 0 THEN
		/*******修改sql*******BEGIN*/
		ALTER TABLE `mai_receive_box` DROP COLUMN `is_read`;
		/*******修改sql*******END*/
	END IF;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box' and COLUMN_NAME = 'is_urgent' LIMIT 0,1; 
	IF v_cnt != 0 THEN
		/*******修改sql*******BEGIN*/
		ALTER TABLE `mai_receive_box` DROP COLUMN `is_urgent`;
		/*******修改sql*******END*/
	END IF;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'mai_receive_box' and COLUMN_NAME = 'is_important' LIMIT 0,1; 
	IF v_cnt != 0 THEN
		/*******修改sql*******BEGIN*/
		ALTER TABLE `mai_receive_box` DROP COLUMN `is_important`;
		/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：用户表(aut_post)
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'AUT_POST' LIMIT 0,1;
	IF v_cnt = 0 THEN
	/*******建表sql*******BEGIN*/
	CREATE TABLE `aut_post` (
	  `id` bigint(20) NOT NULL COMMENT '主键ID',
	  `create_time` datetime NOT NULL COMMENT '创建时间',
	  `last_update_time` datetime NOT NULL COMMENT '修改时间',
	  `version` int(11) NOT NULL COMMENT '版本号',
	  `is_delete` int(1) NOT NULL COMMENT '是否删除',
	  `last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
	  `last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
	  `create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
	  `create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
	  `post_name` varchar(100) NOT NULL COMMENT '岗位名称',
	  `post_rank` int(11) NOT NULL COMMENT '岗位排序',
	  `is_active` int(1) NOT NULL COMMENT '是否激活（1：是；0：否）',
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='岗位表';
	/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：用户岗位表(aut_user_post)
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'AUT_USER_POST' LIMIT 0,1;
	IF v_cnt = 0 THEN
	/*******建表sql*******BEGIN*/
	CREATE TABLE `aut_user_post` (
	  `id` bigint(20) NOT NULL COMMENT '主键ID',
	  `create_time` datetime NOT NULL COMMENT '创建时间',
	  `last_update_time` datetime NOT NULL COMMENT '修改时间',
	  `version` int(11) NOT NULL COMMENT '版本号',
	  `is_delete` int(1) NOT NULL COMMENT '是否删除',
	  `last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
	  `last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
	  `create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
	  `create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
	  `user_id` bigint(20) NOT NULL COMMENT '用户id',
	  `post_id` bigint(20) NOT NULL COMMENT '岗位id',
	  `is_active` int(1) NOT NULL COMMENT '是否激活（1：是；0：否）',
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户-岗位表';
	/*******建表sql*******END*/
	END IF;
END;
/*
功能描述：TIM_SCHEDULE 修改自动生成下月考勤数据定时器表达式
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'TIM_SCHEDULE' and COLUMN_NAME = 'cron_expression' LIMIT 0,1;
	IF v_cnt = 1 THEN
			/*******修改sql*******BEGIN*/
			update tim_schedule set cron_expression = '0 5 1 L * ?' WHERE exe_class_name = 'AttGenDataJob';
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：SYS_SERIAL_NUMBER 修改起始值数据类型
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'SYS_SERIAL_NUMBER' and COLUMN_NAME = 'start_value' LIMIT 0,1;
	IF v_cnt = 1 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE sys_serial_number MODIFY COLUMN start_value BIGINT(20)  NOT NULL COMMENT '起始值' AFTER `is_fix_length`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：SYS_DOCUMENT_NUMBER 修改起始值数据类型
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'SYS_DOCUMENT_NUMBER' and COLUMN_NAME = 'start_value' LIMIT 0,1;
	IF v_cnt = 1 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE sys_document_number MODIFY COLUMN start_value BIGINT(20)  NOT NULL COMMENT '起始值' AFTER `is_fix_length`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：SYS_COMMON_DICT 新增 dict_code 字段
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`COLUMNS` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'SYS_COMMON_DICT' and COLUMN_NAME = 'dict_code' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******修改sql*******BEGIN*/
			ALTER TABLE `sys_common_dict`
      ADD COLUMN `dict_code`  varchar(50) NOT NULL COMMENT '常用字典唯一标识符' AFTER `category_id`;
			/*******修改sql*******END*/
	END IF;
END;
/*
功能描述：创建流程相关扩展信息表（ACT_ALJOIN_EXTINFO）
*/
BEGIN
	DECLARE v_cnt INT;
	SET v_cnt = 0;
	SELECT 1 INTO v_cnt FROM information_schema.`TABLES` WHERE table_schema = D_TABLE_SCHEMA and table_name = 'ACT_ALJOIN_EXTINFO' LIMIT 0,1;
	IF v_cnt = 0 THEN
			/*******建表sql*******BEGIN*/
			CREATE TABLE `act_aljoin_extinfo` (
        `id` bigint(20) NOT NULL COMMENT '主键ID',
        `create_time` datetime NOT NULL COMMENT '创建时间',
        `last_update_time` datetime NOT NULL COMMENT '修改时间',
        `version` int(11) NOT NULL COMMENT '版本号',
        `is_delete` int(1) NOT NULL COMMENT '是否删除',
        `last_update_user_id` bigint(20) NOT NULL COMMENT '最后修改用户ID',
        `last_update_user_name` varchar(100) NOT NULL COMMENT '最后修改用户账号',
        `create_user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
        `create_user_name` varchar(100) NOT NULL COMMENT '创建用户账号',
        `process_instance_id` varchar(64) DEFAULT NULL COMMENT '主流程实例ID',
        `execution_id` varchar(64) DEFAULT NULL COMMENT '执行流ID',
        `ext_key` text DEFAULT NULL COMMENT '键',
        `ext_value` text DEFAULT NULL COMMENT '值',
        `description` text NOT NULL COMMENT '描述',
         PRIMARY KEY (`id`)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '流程相关 扩展信息表';
			/*******建表sql*******END*/
	END IF;
END;
/*
/*********************************************************************************************************END*/
/*********************************************************************************************************END*/
/*********************************************************************************************************END*/
/************************************请在以上区域编写数据库升级脚本***********************************END*/
END;
CALL `sp_a9_table_structure_update`;
DROP PROCEDURE IF EXISTS `sp_a9_table_structure_update`;
SET FOREIGN_KEY_CHECKS=1;