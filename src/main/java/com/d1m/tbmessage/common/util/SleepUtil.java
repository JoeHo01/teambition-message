package com.d1m.tbmessage.common.util;

/**
 * Created by Jo on 2017/5/6.
 */
public class SleepUtil {

    /**
     * 毫秒为单位
     * @param time time
     */
    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
