package com.d1m.tbmessage.common;

/**
 * Created by Jo on 2017/5/6.
 */
public class SleepUtils {

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
