package com.isaac.service;

import com.isaac.dao.SeckillDao;
import com.isaac.dto.Exposer;
import com.isaac.dto.SeckillExecution;
import com.isaac.entity.Seckill;
import com.isaac.exception.RepeatKillExcetion;
import com.isaac.exception.SeckillException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Isaac on 2017/10/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);

    }

    @Test
    public void getById() throws Exception {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
}

    @Test
    public void exportSeckillUrl() throws Exception {
        long id = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);
        /**
         * Exposer{exposed=true, md5='8295fcc96943290768466f89ec9f6302', seckillId=1000, now=0, start=0, end=0}
         */
    }

    @Test
    public void executeSeckill() throws Exception {
        long id = 1000L;
        long phoneNum = 13381079137L;
        String md5= "8295fcc96943290768466f89ec9f6302";
        SeckillExecution execution = null;
        try {
            execution = seckillService.executeSeckill(id,phoneNum,md5);
            logger.info("result={}",execution);
        } catch (RepeatKillExcetion e){
            logger.error(e.getMessage(),e);
        } catch (SeckillException e1) {
            logger.error(e1.getMessage(),e1);
        }
    }
    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1001L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.getExposed()) {
            logger.info("exposer={}",exposer);
            long phoneNum = 13381079137L;
            String md5= exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id,phoneNum,md5);
                logger.info("result={}",execution);
            } catch (RepeatKillExcetion e){
                logger.error(e.getMessage(),e);
            } catch (SeckillException e1) {
                logger.error(e1.getMessage(),e1);
            }
        } else {
            logger.warn("exposer={}",exposer);
        }

    }
}