package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {

  public static final String CURRENT_USER="currentUser";


  //校验类型
    public static final String EMAIL="email";
    public static final String USERNAME="username";


    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

  //角色
   public interface Role{
       int ROLE_CUSTOMER=0;//普通角色
       int ROLE_ADMIN=1;//管理员
    }


    //商品状态，在线
    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        //生成getter方法
        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

    }

    //购物车状态
    public interface Cart{
        int CHECKED=1;//购物车选中状态
        int UN_CHECKED=0;//购物车未选中状态

        //返回的规定
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }


}
