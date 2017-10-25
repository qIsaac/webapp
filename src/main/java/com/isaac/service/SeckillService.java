package com.isaac.service;

import com.isaac.dto.Exposer;
import com.isaac.dto.SeckillExecution;
import com.isaac.entity.Seckill;
import com.isaac.exception.RepeatKillExcetion;
import com.isaac.exception.SeckillCloseException;
import com.isaac.exception.SeckillException;

import java.util.List;

/**
 * 业务接口 ：站在使用者的角度设计接口
 * 三个方面：
 * 1.方法定义粒度
 * 2.参数
 * 3.返回类型 return 类型友好
 * Created by Isaac on 2017/10/16.
 */
public interface SeckillService {
    /**
     * 查询秒杀条件
     *
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开始是输出秒杀接口地址
     * 否者输出系统时间和秒杀时间
     *
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillExcetion, SeckillCloseException;
}
