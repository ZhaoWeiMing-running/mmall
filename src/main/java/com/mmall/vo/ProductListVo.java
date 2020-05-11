package com.mmall.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zwm
 * @create 2020/5/11  9:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductListVo {

    private Integer id;

    private Integer categoryId;

    private String imageHost;

    private String mainImage;

    private String name;

    private BigDecimal price;

    private Integer status;

    private String subtitle;




}
