# 数据库初始化

-- 创建库
create database if not exists my_zc2;

-- 切换库
use my_zc2;

-- 资产基础信息表
create table assets
(
    id                bigint auto_increment comment 'id' primary key,
    device_code       varchar(50)                        not null comment '设备编码',
    device_name       varchar(100)                       null comment '设备名称',
    building_code     varchar(50)                        null comment '建筑物编码',
    building_name     varchar(100)                       null comment '建筑物名称',
    floor             varchar(50)                        null comment '楼层',
    room_number       varchar(50)                        null comment '房间号',
    location_remarks  text                               null comment '地点备注',
    inventory_remarks text                               null comment '盘点备注',
    clearance_status  varchar(50)                        null comment '清查状态',
    createTime        datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime        datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete          tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_device_code (device_code),
    INDEX idx_device_name (device_name),
    INDEX idx_clearance_status (clearance_status)
) comment '资产基础信息表' collate = utf8mb4_unicode_ci;


-- 资产财务信息表
create table financial_info
(
    id                     bigint auto_increment comment 'id' primary key,
    device_code            varchar(50)                        not null comment '设备编码',
    device_unit            varchar(50)                        null comment '计量单位',
    unit_price             decimal(10, 2)                     null comment '单价',
    device_num             int                                null comment '数量',
    total_price            decimal(10, 2)                     null comment '总价',
    attachment_quantity    int                                null comment '附件数量',
    attachment_total_price decimal(10, 2)                     null comment '附件总价',
    maintenance_times      int                                null comment '维修次数',
    maintenance_cost       decimal(10, 2)                     null comment '维修费用',
    supplier_code          varchar(50)                        null comment '供应商码',
    purchase_date          varchar(50)                        null comment '购置日期',
    acceptance_date        varchar(50)                        null comment '验收日期',
    user_id                varchar(50)                        null comment '领用人工号',
    user_name              varchar(100)                       null comment '领用人名称',
    unit_name              varchar(100)                       null comment '单位名称',
    department_name        varchar(100)                       null comment '科室名称',
    createTime             datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime             datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete               tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_device_code (device_code)
) comment '资产财务信息表' collate = utf8mb4_unicode_ci;

-- 资产附加信息表
create table additional_info
(
    id                   bigint auto_increment comment 'id' primary key,
    device_code          varchar(50)                        not null comment '设备编码',
    device_purpose       text                               null comment '设备用途',
    asset_source         varchar(50)                        null comment '资产来源',
    country_code         varchar(10)                        null comment '国别码',
    country_name         varchar(100)                       null comment '国别',
    purchase_type        varchar(50)                        null comment '采购类型',
    photo                varchar(1024)                      null comment '设备照片',
    predevice_code       varchar(50)                        null comment '原设备编码',
    brand                varchar(100)                       null comment '品牌',
    specification        text                               null comment '设备规格',
    model                varchar(100)                       null comment '设备型号',
    category_code        varchar(50)                        null comment '分类编码',
    category_description varchar(255)                       null comment '分类说明',
    warehouse_type       varchar(50)                        null comment '分库类型',
    serial_number        varchar(100)                       null comment '序列号',
    status               varchar(50)                        null comment '设备状态',
    submit               varchar(50)                        null comment '报送',
    isTag                varchar(10)                        null comment '有无标签',
    createTime           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime           datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete             tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_device_code (device_code)
) comment '资产附加信息表' collate = utf8mb4_unicode_ci;

-- 资产盘点记录表
create table assets_records
(
    id               bigint auto_increment comment 'id' primary key,
    device_code      varchar(50)                        not null comment '设备编码',
    userId           bigint                             not null comment '用户id',
    building_code    varchar(50)                        null comment '建筑物编码',
    building_name    varchar(100)                       null comment '建筑物名称',
    floor            varchar(50)                        null comment '楼层',
    room_number      varchar(50)                        null comment '房间号',
    location_remarks text                               null comment '地点备注',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    editTime         datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    isDelete         tinyint  default 0                 not null comment '是否删除',
    edit_status      int      default 0                 not null comment '审核状态(0:未通过 1:已通过)',
    auditTime       datetime                           null     comment '审核时间',
    auditName        varchar(30)                        null comment '审核人',
    INDEX idx_device_code (device_code)
) comment '资产盘点记录表' collate = utf8mb4_unicode_ci;

-- 盘点错误日志记录表
create table inventoryrecord
(
    id           bigint auto_increment comment 'id' primary key,
    assetCode    varchar(256)                       null comment '条形码id',
    status       tinyint                            null comment '操作状态(1.成功 2.失败)',
    errorMessage text                               null comment '记录错误信息(如果状态为失败)',
    operName     varchar(50)                        null comment '操作人员 ',
    location     varchar(50)                        null comment '资产所在位置',
    checkTime    datetime default CURRENT_TIMESTAMP not null comment '盘点时间',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    title        varchar(50)                        null comment '模块标题',
    content      varchar(50)                        null comment '日志内容'
) comment '盘点错误日志记录表' collate = utf8mb4_unicode_ci;

-- 用户表
create table user
(
    id           bigint auto_increment comment 'id' primary key ,
    userAccount  varchar(256)                           not null comment '工号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           not null comment '用户昵称',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '用户表' collate = utf8mb4_unicode_ci;


