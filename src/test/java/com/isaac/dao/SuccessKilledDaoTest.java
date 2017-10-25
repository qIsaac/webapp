package com.isaac.dao;

import com.isaac.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by Isaac on 2017/10/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccessKilled() throws Exception {
      long id = 1001L;
      long phone = 13381079137L;
      int insertCount = successKilledDao.insertSuccessKilled(id,phone);
      System.out.println("insertCount:"+insertCount);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long id = 1001L;
        long phone = 13381079137L;
         SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id,phone);
         System.out.println(successKilled);
    }

}