package com.mmall.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zwm
 * @create 2020/5/12  9:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartProductVo {

    //结合产品和购物车的一个抽象对象


    private Integer id; //购车车id
    private Integer userId; //用户id
    private Integer productId; //产品id
    private Integer quantity; //购物车中此商品的数量
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus; //产品状态
    private BigDecimal productTotalPrice; //这个产品的总价
    private Integer productStock;//产品库存
    private Integer productChecked; //此商品在购物车中是否勾选

    private String limitQuantity; //这个比较重要，限制数量的一个返回结果




}
