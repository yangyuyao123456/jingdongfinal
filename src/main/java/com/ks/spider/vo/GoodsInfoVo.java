package com.ks.spider.vo;

import lombok.Data;

/**
 * @author yuyao.yang
 * @Date 2020/6/20
 * @Description :商品信息vo
 */
@Data
public class GoodsInfoVo  {
    private String goodsName;

    private int pageNum;

    private int pageSize;
}
