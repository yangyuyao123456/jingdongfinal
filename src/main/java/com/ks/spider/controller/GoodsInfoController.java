package com.ks.spider.controller;

import com.ks.spider.entity.GoodsInfo;
import com.ks.spider.service.GoodsInfoService;
import com.ks.spider.vo.GoodsInfoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yuyao.yang
 * @Date 2020/6/20
 * @Description : 商品信息
 */
@RestController
@RequestMapping("/api/goodsInfo")
@Validated
public class GoodsInfoController {
    @Autowired
    private GoodsInfoService goodsInfoService;

    /**
     * 模糊查询商品名称信息
     * @param vo
     * @return
     */
    @GetMapping("/getGoodsInfoByName")
    @ApiOperation("通过名称获取商品信息")
    public List<GoodsInfo> getGoodsInfoByName(GoodsInfoVo vo) {
        return goodsInfoService.getGoodsInfoByName(vo);
    }
}
