package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * @author zwm
 * @create 2020/5/9  17:31
 */
public interface ProductServiceI {


    ServerResponse saveAndUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId,Integer status);


    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    //商品列表
    ServerResponse<PageInfo> getList(int pageNum, int pageSize);

    //商品搜索
    ServerResponse<PageInfo> search(String productName,Integer productId,int pageNum,int pageSize);

    //商品详情
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    //商品列表分页+模糊查询
    ServerResponse<PageInfo> list(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
