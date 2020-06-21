package com.ks.spider.mapper;

import com.ks.spider.entity.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yuyao.yang
 * @Date 2020/6/19
 * @Description : 京东爬虫数据查询接口
 */
@Mapper
public interface GoodsInfoMapper {

    /**
     * 模糊查询商品名称信息
     * @param name
     * @param pageSize
     * @param offset
     * @return
     */
    List<GoodsInfo> getGoodsInfoByName(@Param("name") String name,@Param("pageSize") int pageSize,@Param("offset") int offset);
}
