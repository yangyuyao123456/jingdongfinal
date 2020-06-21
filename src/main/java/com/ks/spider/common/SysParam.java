package com.ks.spider.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yuyao.yang
 * @Date 2020/6/19
 * @Description : 系统参数
 */
public class SysParam {
    /**
     * 关键字list
     */
    public static List<String> keywordsList = new ArrayList<>();
    /**
     * 向数据库插入数据的条数
     */

    public static AtomicLong insertCount = new AtomicLong(1);
    /**
     * 编码格式
     */

    public static final String DEFAULT_CHARSET = "utf-8";

    private static final String MOZILLA = "Mozilla/5.0 ";

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final String[][] OS = {
            {
                    "Windows NT 6.1",
                    "Windows NT 6.1; Win64; x64",
                    "Windows NT 6.2",
                    "Windows NT 6.2; Win64; x64",
                    "Windows NT 6.3",
                    "Windows NT 6.3; Win64; x64",
                    "Windows NT 6.4",
                    "Windows NT 6.4; Win64; x64",
                    "Windows NT 10",
                    "Windows NT 10; Win64; x64"
            },
            {
                    "Macintosh; Intel Mac OS X 10_12_1",
                    "Macintosh; Intel Mac OS X 10_12_2",
                    "Macintosh; Intel Mac OS X 10_12_3",
                    "Macintosh; Intel Mac OS X 10_12_4",
                    "Macintosh; Intel Mac OS X 10_12_5",
                    "Macintosh; Intel Mac OS X 10_13_2",
                    "Macintosh; Intel Mac OS X 10_13_3",
                    "Macintosh; Intel Mac OS X 10_13_4",
                    "Macintosh; Intel Mac OS X 10_13_5",
            }
    };

    public static String getUserAgent() {
        StringBuffer sb = new StringBuffer();
        sb.append(MOZILLA);
        sb.append("(");
        sb.append(randomStringArray((OS[RANDOM.nextInt(OS.length)])));
        sb.append(")");
        sb.append(" AppleWebKit/537.36 (KHTML, like Gecko) Chrome/");
        sb.append(CHROME_VERSION[RANDOM.nextInt(CHROME_VERSION.length)]);
        sb.append(" Safari/537.36");

        return sb.toString();
    }


    private static final String[] CHROME_VERSION = {
            "68.0.3440.0",
            "69.0.3497.0",
            "70.0.3538.0",
            "71.0.3578.0",
            "72.0.3626.0",
            "73.0.3683.0",
    };

    private static String randomStringArray(String[] array) {
        int length = array.length;
        return array[RANDOM.nextInt(length)];
    }
}
