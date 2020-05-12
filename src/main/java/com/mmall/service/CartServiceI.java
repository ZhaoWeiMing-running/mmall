package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * @author zwm
 * @create 2020/5/12  9:40
 */
public interface CartServiceI {
    //添加
    ServerResponse<CartVo> add(Integer productId, Integer count, Integer userId);

    //修改
    ServerResponse<CartVo> update(Integer productId,Integer count,Integer userId);

    //删除
    ServerResponse<CartVo> deleteProduct(String productIds,Integer userId);
}
