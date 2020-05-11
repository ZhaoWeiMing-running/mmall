package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.ProductServiceI;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zwm
 * @create 2020/5/11  14:10
 */
@RestController
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private ProductServiceI productServiceI;


    //商品详情
    @RequestMapping("detail.do")
    public ServerResponse<ProductDetailVo> detail(Integer productId){
       return productServiceI.getProductDetail(productId);
    }

    //商品列表分页+模糊查询
    @RequestMapping("list.do")
    public ServerResponse<PageInfo> list(@RequestParam(value ="keyword" ,required = false)String keyword,
                                         @RequestParam(value ="categoryId" ,required = false) Integer categoryId,
                                         @RequestParam(value ="pageNum" ,defaultValue = "1") int pageNum,
                                         @RequestParam(value ="pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value ="orderBy" ,defaultValue = "") String orderBy){

        return productServiceI.list(keyword, categoryId, pageNum, pageSize, orderBy);
    }




}
