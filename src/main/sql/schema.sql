-- 数据库初始化脚本
-- 创建数据库
CREATE DATABASE seckill;
-- 使用数据库
use seckill;
-- 创建秒杀表
CREATE TABLE seckill(
seckill_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
name varchar(120) NOT NULL  COMMENT '商品名称',
number INT NOT NULL  COMMENT '库存数量',
start_time TIMESTAMP NOT NULL  COMMENT '秒杀开启时间',
end_time TIMESTAMP NOT NULL  COMMENT '秒杀结束时间',
create_time TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀数据库表';

-- 初始化数据
INSERT INTO seckill(name,number,start_time,end_time)
VALUES('1000元秒杀iPhone8',100,'2017-10-10 00:00:00','2017-10-15 00:00:00'),
('500元秒杀iPad Air',200,'2017-10-10 00:00:00','2017-10-15 00:00:00'),
('300元秒杀小米 mix2',300,'2017-10-10 00:00:00','2017-10-15 00:00:00'),
('1000元秒杀红米 note',400,'2017-10-10 00:00:00','2017-10-15 00:00:00');


-- 秒杀成功明细
CREATE TABLE success_killed(
seckill_id bigint NOT NULL COMMENT '秒杀商品id',
user_phone bigint NOT NULL COMMENT '商户手机号',
state tinyint NOT NULL  DEFAULT -1 COMMENT '状态标识：-1：无效 0：成功 1：已付款 2：已发货',
create_time TIMESTAMP NOT NULL COMMENT '创建时间',
PRIMARY KEY (seckill_id,user_phone),/*联合主键*/
key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成明细表';