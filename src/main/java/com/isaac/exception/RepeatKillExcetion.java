package com.isaac.exception;

/**
 * 秒杀异常
 * Created by Isaac on 2017/10/16.
 */
public class RepeatKillExcetion extends SeckillException{
    public RepeatKillExcetion(String message) {
        super(message);
    }

    public RepeatKillExcetion(String message, Throwable cause) {
        super(message, cause);
    }
}
