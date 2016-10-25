package com.winorout.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 常用辅助工具类
 * @author  ryzhang
 * @data 2016/10/25 16:25
 */
public class Tools {

    /**
     * 将当前时间转换成字符串
     * @return
     */
    public static String getDate() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(now);
        return time;
    }


}
