-- 秒杀执行存储过程
DELIMITER $$  -- ;为 $$
--定义存储过程
--参数：in 输入参数 out 输出参数；
-- row_count (); 返回上一条修改类型 sql(delete, insert, update  )影响行数
--row_count: 0:未修改数据;>0 :表示修改行数;<0 :sql 错误/未执行修改;
CREATE PROCEDURE `seckill`.`execute_seckill`
  (IN  v_seckill_id bigint,IN v_phone bigint,IN v_kill_time TIMESTAMP,out r_result int)
  BEGIN
      DECLARE insert_count int DEFAULT 0;
      start TRANSACTION ;
      INSERT ignore INTO success_killed
        (seckill_id,user_phone,create_time)
        VALUES (v_seckill_id,v_phone,v_kill_time);
      SELECT ROW_COUNT() INTO insert_count;
      IF (insert_count = 0) THEN
          ROLLBACK ;
          SET r_result = -1;
      ELSEIF (insert_count <0 ) THEN
          ROLLBACK ;
          SET r_result = -2;
      ELSE
          UPDATE seckill
          SET number = number-1
          WHERE seckill_id = v_seckill_id
              and end_time > v_kill_time
              and start_time < v_kill_time
              AND number > 0;
          SELECT ROW_COUNT() INTO insert_count;
          IF (insert_count = 0) THEN
              ROLLBACK ;
              SET r_result = 0;
          elseif (insert_count < 0) THEN
              ROLLBACK ;
              SET r_result = -2;
          ELSE
              COMMIT ;
              SET  r_result = 1;
          END if;
      end if;
  END;
$$
--存储过程定义结束
DELIMITER ;
set @r_result = -3
call execute_seckill(1003,13219801920,now(),r_result);
--获取结果
select @r_result;