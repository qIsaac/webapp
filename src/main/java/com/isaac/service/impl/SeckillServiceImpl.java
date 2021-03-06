package com.isaac.service.impl;

import com.isaac.dao.SeckillDao;
import com.isaac.dao.SuccessKilledDao;
import com.isaac.dao.cache.RedisDao;
import com.isaac.dto.Exposer;
import com.isaac.dto.SeckillExecution;
import com.isaac.entity.Seckill;
import com.isaac.entity.SuccessKilled;
import com.isaac.enums.SeckillStateEnum;
import com.isaac.exception.RepeatKillExcetion;
import com.isaac.exception.SeckillCloseException;
import com.isaac.exception.SeckillException;
import com.isaac.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Isaac on 2017/10/16.
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao SuccessKilledDao;
    @Autowired
    private RedisDao redisDao;
    //用于混淆MD5
    private final String slat = "fasdfchrfg253145uyjhbvdbfdg@($*#&(#$f";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    private String getMd5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //缓存优化:超时的基础上维护一致性
        //1、访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //2、访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //3、放入redis
                redisDao.putSeckill(seckill);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //转换特定字符串的
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    @Transactional
    /**
     * 开发团队达成一致约定，明确标注事务方法编程分格
     * 保证事务方法执行时间尽可能短，不要穿插其他网络请求或者剥离到事务方法外部
     * 不是所有的事务都需要事务，如只有一条修改操作，只读操作不需要事务
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillExcetion, SeckillCloseException {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑
        Date nowTime = new Date();

        try {
            //记录购买行为
            int insertCount = SuccessKilledDao.insertSuccessKilled(seckillId, userPhone);
            //唯一：seckillId ,userPhone;
            if (insertCount <= 0) {
                throw new RepeatKillExcetion("seckill repeated");
            } else {
                //减库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新到记录，秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = SuccessKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e) {
            throw e;
        } catch (RepeatKillExcetion e1) {
            throw e1;
        } catch (Exception e3) {
            logger.error(e3.getMessage(), e3);
            //所有的异常转换为RuntimeExecption
            throw new SeckillException("seckill inner error:" + e3.getMessage());
        }
    }

    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);

        try {
            seckillDao.killByProcedure(map);
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = SuccessKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }else {
                return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}
