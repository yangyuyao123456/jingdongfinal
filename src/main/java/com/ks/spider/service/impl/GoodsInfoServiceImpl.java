package com.ks.spider.service.impl;

import com.ks.spider.entity.GoodsInfo;
import com.ks.spider.mapper.GoodsInfoMapper;
import com.ks.spider.service.GoodsInfoService;
import com.ks.spider.vo.GoodsInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;

/**
 * @author yuyao.yang
 * @Date 2020/6/20
 * @Description :京东爬虫数据查询接口
 */
@Service
public class GoodsInfoServiceImpl implements GoodsInfoService {


    private static final int MAX_PAGE_SIZE = 100;

    private static final int MAX_PAGE_NUM = 100;

    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    /**
     * 模糊查询商品名称信息
     * @param vo
     * @return
     */
    @Override
    public List<GoodsInfo> getGoodsInfoByName(GoodsInfoVo vo) {
        if (!StringUtils.isNoneBlank(vo.getGoodsName())) {
            return null;
        }
        if (vo.getPageSize() > MAX_PAGE_SIZE) {
            vo.setPageSize(MAX_PAGE_SIZE);
        }
        if (vo.getPageNum() > MAX_PAGE_NUM) {
            vo.setPageNum(MAX_PAGE_NUM);
        }
        int offset = (vo.getPageNum()-1) * vo.getPageSize();
        List<GoodsInfo> goodsList = goodsInfoMapper.getGoodsInfoByName(vo.getGoodsName(),vo.getPageSize(),offset);
        if (CollectionUtils.isEmpty(goodsList)) {
            return null;
        }
        return goodsList;
    }
}
