package com.mmall.service;

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

}
