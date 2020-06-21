package com.ks.spider.entity;

import lombok.Data;

/**
 * @author yuyao.yang
 * @Date 2020/6/20 18:13
 * @Description :商品信息实体类
 */
@Data
public class GoodsInfo {
    private Integer id;

    private String goodsName;

    private String url;

    private String pic;

    private Long  sku;
}
