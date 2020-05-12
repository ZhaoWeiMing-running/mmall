package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.CartServiceI;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zwm
 * @create 2020/5/12  9:40
 */
@Service
public class CartServiceImpl implements CartServiceI {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加购物车
     */
    @Override
    public ServerResponse<CartVo> add(Integer productId,Integer count,Integer userId){
        if (productId==null || count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //根据用户id和产品id查询购物车
       Cart cart= cartMapper.selectCartByUserIdProductId(userId,productId);
        if (cart==null){
            //如果这个产品不再购物车里，则需要新增一个这个产品记录
            Cart cartItem = new Cart();
            cartItem.setProductId(productId);
            cartItem.setQuantity(count);
            cartItem.setUserId(userId);
            //购物车选中状态，在枚举类定义
            cartItem.setChecked(Const.Cart.CHECKED);
            //插入
            cartMapper.insert(cartItem);
        }else {
            //这个产品已经在购物车中了，则进行数量相加,更新操作
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    //数量限制的方法
    private CartVo getCartVoLimit(Integer userId){
        //首选把返回值new一个，然后后面去拼装他
        CartVo cartVo = new CartVo();
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);
        //然后将cartproductVo放到cartvo当中
        List<CartProductVo> cartProductVoList= Lists.newArrayList();

        //初始化购物车的总价,一定要用String构造器，去写一个bigdecimal工具类
        BigDecimal cartTotalPrice=new BigDecimal("0");
        //对cartList做空判断
        if (CollectionUtils.isNotEmpty(cartList)){
            //如果不是空的话，遍历它
            for (Cart cartItem : cartList) {
                //需要的进行赋值,购物车id、用户id、产品id
                CartProductVo cartProductVo=new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                //查询购物车里面的产品对象
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //对库存进行判断
                    int buyLimitCount=0;
                    //库存充足时
                    if (product.getStock()>=cartItem.getQuantity()){
                        buyLimitCount=cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        //购买限制数量==产品库存数量
                        buyLimitCount=product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        //设置为产品的存库
                        cartForQuantity.setQuantity(buyLimitCount);
                        //根据id去找quantity字段
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    //设置数量
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价(商品的价格x购物车中的数量)
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    //默认全部选中
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                //如果产品是勾选的话，
                if (cartItem.getChecked()==Const.Cart.CHECKED){
                    //如果已经勾选，增加到整个的购物车总价中（自己的价格+产品的总价），所以cartTotalPrice是购物车的产品总价
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }

        //这个是购物车汇总产品的vo对象
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        //选中状态
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    //判断是否是全选状态
    //逻辑：如果未勾选的数量为0，就代表是全选
    private boolean getAllCheckedStatus(Integer userId){
        if (userId==null){
            return false;
        }
        //如果等于0的话，结果直接返回true，是全选状态
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }








}
