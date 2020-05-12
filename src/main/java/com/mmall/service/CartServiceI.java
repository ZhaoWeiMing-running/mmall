package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * @author zwm
 * @create 2020/5/12  9:40
 */
public interface CartServiceI {

    ServerResponse<CartVo> add(Integer productId, Integer count, Integer userId);


}
