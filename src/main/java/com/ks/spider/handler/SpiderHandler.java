package com.ks.spider.handler;

import com.ks.spider.common.SysParam;
import com.ks.spider.task.SpiderTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;

/**
 * @author yuyao.yang
 * @Date 2020/6/20 18:07
 * @Description : 爬虫调度处理器
 */
@Component
@Slf4j
public class SpiderHandler  {
    private static final int THREAD_TOTAL = 100;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 服务加载时运行
     * @throws Exception
     */
    @PostConstruct
    public void spiderData() throws Exception {
        log.info("===============爬虫开始===============");
        InputStream inputStream = new ClassPathResource("keywords.txt").getInputStream();
        SysParam.keywordsList = IOUtils.readLines(inputStream);
        System.out.println("---read keywords.txt finished---");
        //开启多个线程来读取不同的关键字分段
        for (int i = 0; i < THREAD_TOTAL; i++) {
            SpiderTask thread = new SpiderTask(i, THREAD_TOTAL,jdbcTemplate);
        thread.start();
    }
    }
}
