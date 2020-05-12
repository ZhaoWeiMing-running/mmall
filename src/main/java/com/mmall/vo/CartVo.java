package com.mmall.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zwm
 * @create 2020/5/12  9:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartVo {

    //产品列表
    private List<CartProductVo> cartProductVoList;
    //购物车总价
    private BigDecimal cartTotalPrice;
    //是否已经都勾选
    private Boolean allChecked;
    //图片地址
    private String imageHost;





}
