package com.isaac.dao;

import com.isaac.entity.SuccessKilled;
import com.sun.net.httpserver.Authenticator;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Isaac on 2017/10/11.
 */
public interface SuccessKilledDao {
    /**
     *  插入购买明细, 可过滤重复
     * @param seckillId
     * @param userPhone
     * @return
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId , @Param("userPhone") long userPhone);

    /**
     * 根据id查询SuccessKilled并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId , @Param("userPhone") long userPhone);
}
