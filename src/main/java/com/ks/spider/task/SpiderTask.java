package com.ks.spider.task;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.ks.spider.common.SysParam;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yuyao.yang
 * @Date 2020/6/19
 * @Description : 爬虫线程任务
 */
@Slf4j
public class SpiderTask extends Thread {

    private JdbcTemplate jdbcTemplate;

    /**
     * 总共
     */
    private int threadTotal;

    /**
     * 计数
     */
    private int threadCount;
    /**
     * 布隆过滤器大小
     */

    private static int size = 10000000;

    private static BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")),size);

    /**
     * 创建OkHttpClient对象
     */
    OkHttpClient okHttpClient = new OkHttpClient()
            .newBuilder()
            .connectTimeout(2000, TimeUnit.MILLISECONDS)
            .build();

    /**
     * 构造方法
     * @param threadCount
     * @param threadTotal
     * @param jdbcTemplate
     */
    public SpiderTask(int threadCount, int threadTotal, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.threadCount = threadCount;
        this.threadTotal = threadTotal;
    }

    @Override
    public void run() {
        List<String> keywords = getKeywords(threadCount, threadTotal);
        for (String keyword : keywords) {
            try {
                spiderData(keyword);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 爬取某个关键字数据方法
     * @param keyword
     * @throws IOException
     */
    private void spiderData(String keyword) throws IOException {
        String useragent = SysParam.getUserAgent();
        System.out.println("========================分割线========================");
        Response response = okHttpClient.newCall(
                new Request.Builder()
                        .url("https://search.jd.com/Search?enc=utf-8&keyword=" + keyword)
                        .header("User-Agent", useragent)
                        .header("referer", "https://search.jd.com/Search?enc=utf-8&keyword=")
                        .build()
        ).execute();
        if (response.isSuccessful()) {
            String html = response.body().string();
            Document document = Jsoup.parse(html);
            Element element = document.select("span.fp-text").first();
            if (element != null) {
                Integer page = Integer.parseInt(element.selectFirst("i").text());
                spiderDataWithPage(page, keyword);
            } else {
                System.out.println("---------未获取到该关键字的页面信息: " + keyword + ";  html:" + html+"----------------------");
            }
        }
    }

    /**
     * 分页获取html页面数据
     * @param page
     * @param keyword
     * @throws IOException
     */
    private void spiderDataWithPage(Integer page, String keyword) throws IOException {
        int total = page * 2 - 1;
        int pageInternal = 2;
        String userAgent = SysParam.getUserAgent();
        for (int i = 1; i <= total; i = i + pageInternal) {
            Response response = okHttpClient.newCall(
                    new Request.Builder()
                            .url("https://search.jd.com/s_new.php?enc=utf-8&keyword=" + keyword + "&page=" + i)
                            .header("User-Agent", userAgent)
                            .header("referer", "https://search.jd.com/Search.php?enc=utf-8&keyword=")
                            .build()
            ).execute();

            if (response.isSuccessful()) {
                String html = response.body().string();
                Document document = Jsoup.parse(html);
                Element element = document.select("div.J-goods-list").first();
                if (element != null) {
                    Elements elements = element.select("div.gl-i-wrap");
                    for (Element good : elements) {
                        Element nameElement = good.selectFirst("div.p-name");
                        String url = good.select("a").attr("href");
                        //使用布隆过滤器排重
                        boolean ifContain = bloomFilter.mightContain(url);
                        if (!ifContain) {
                            System.out.println("--------------url不重复，布隆过滤器中不存在该条url ： " + url+"----------------------");
                            String goodsName = nameElement.selectFirst("em").text();
                            String pic = good.select("img").attr("src");
                            String sku = good.select("a").attr("data-sku");
                            String sql = " insert into jingdongspider(goods_name,url,sku,pic) values(?,?,?,?)";
                            int update = jdbcTemplate.update(sql, goodsName, url, sku, pic);
                            if (update > 0) {
                                System.out.println("--------向数据库中插入第 "+ SysParam.insertCount.getAndIncrement()+" 条数据成功--------");
                                //插入成功加入布隆过滤器中
                                bloomFilter.put(url);
                            } else {
                                System.out.println("-------插入数据到数据库失败！-------");
                            }
                        }else {
                            System.out.println("-----------url重复-------------" + url);
                        }
                    }
                } else {
                    System.out.println("---------未获取到该关键字的页面信息" + keyword + ";  html:" + html+"---------");
                }
            }

            /**
             * 获取滚屏后的数据
             */
            Response scrollingResponse = okHttpClient.newCall(
                    new Request.Builder()
                            .url("https://search.jd.com/s_new.php?enc=utf-8&keyword=" + keyword + "&page=" + i + "&scrolling=y")
                            .header("User-Agent", userAgent)
                            .header("referer", "https://search.jd.com/Search.php?enc=utf-8&keyword=")
                            .build()
            ).execute();
            if (scrollingResponse.isSuccessful()) {
                String html = scrollingResponse.body().string();
                String scrollingHtml=html.split("</li>\n" +
                        "<script>")[0]+"</li>";
                Document document = Jsoup.parse(scrollingHtml);
                Elements elements = document.select("li.gl-item");
                if (elements != null) {
                    for (Element good : elements) {
                        Element nameElement = good.selectFirst("div.p-name");
                        String url = good.select("a").attr("href");
                        //使用布隆过滤器排重
                        boolean ifContain = bloomFilter.mightContain(url);
                        if (!ifContain) {
                            System.out.println("---------url不重复，布隆过滤器中不存在该条url ： " + url+"---------");
                            String goodsName = nameElement.selectFirst("em").text();
                            String pic = good.select("img").attr("src");
                            String sku = good.select("a").attr("data-sku");
                            String sql = " insert into jingdongspider(goods_name,url,sku,pic) values(?,?,?,?)";
                            int update = jdbcTemplate.update(sql, goodsName, url, sku, pic);
                            if (update > 0) {
                                System.out.println("------------向数据库中插入第 "+ SysParam.insertCount.getAndIncrement()+" 条数据成功-------------");
                                //插入成功加入布隆过滤器中
                                bloomFilter.put(url);
                            } else {
                                System.out.println("-----------插入数据到数据库失败！----------");
                            }
                        }else {
                            System.out.println("------------url重复-------------" + url);
                        }
                    }
                } else {
                    System.out.println("------------未获取到该关键字的页面信息" + keyword + ";  html:" + html+"--------------");
                }
            }
        }

    }

    /**
     * 不同线程 分段获取关键字
     * @param threadCount
     * @param threadTotal
     * @return
     */
    private List<String> getKeywords(int threadCount, int threadTotal) {
        Integer pools = threadTotal;
        int dictTotal = SysParam.keywordsList.size();
        int region = dictTotal / pools;
        int start = threadCount * region;
        int end = (threadCount + 1) * region;
        if ((threadCount + 1) == pools && end < dictTotal) {
            end = dictTotal;
        }
        List<String> keywords = SysParam.keywordsList.subList(start, end);
        return keywords;
    }


}
