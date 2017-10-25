package com.isaac.exception;

/**
 * 秒杀相关的业务异常
 * Created by Isaac on 2017/10/16.
 */
public class SeckillException extends RuntimeException{
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
