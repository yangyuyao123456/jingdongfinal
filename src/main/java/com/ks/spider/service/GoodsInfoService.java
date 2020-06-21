package com.ks.spider.service;

import com.ks.spider.entity.GoodsInfo;
import com.ks.spider.vo.GoodsInfoVo;

import java.util.List;

/**
 * @author yuyao.yang
 * @Date 2020/6/20
 * @Description :商品信息
 */
public interface GoodsInfoService {

    /**
     * 模糊查询商品名称信息
     * @param vo
     * @return
     */
    List<GoodsInfo> getGoodsInfoByName(GoodsInfoVo vo);
}
