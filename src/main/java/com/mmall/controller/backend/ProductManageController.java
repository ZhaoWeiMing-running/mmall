package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.ProductServiceI;
import com.mmall.service.UserServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author zwm
 * @create 2020/5/9  17:13
 */
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private UserServiceI userServiceI;

    @Autowired
    private ProductServiceI productServiceI;


    //新增和更新操作
    @RequestMapping("/save.do")
    public ServerResponse saveAndUpdateProduct(HttpSession session,Product product){
        //强制登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        //判断是否是管理员
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //填充早呢更加或修改的逻辑
            return productServiceI.saveAndUpdateProduct(product);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }

    //商品上下架
    @RequestMapping("/set_sale_status.do")
    public ServerResponse setSaleStatus(HttpSession session,Integer productId,Integer status){
        //强制登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        //判断是否是管理员
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //填充修改状态逻辑
            return productServiceI.setSaleStatus(productId, status);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }


    //商品详情
    @RequestMapping("/detail.do")
    public ServerResponse getDetail(HttpSession session,Integer productId){
        //强制登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        //判断是否是管理员
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //填充商品详情逻辑
            return productServiceI.manageProductDetail(productId);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }

}
