package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserServiceI;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author zwm
 * @create 2020/5/8  11:08
 */
@RestController
@RequestMapping("/manage/user")
public class UserManageController {

    @Resource
    private UserServiceI userServiceI;


    //登录功能
    @RequestMapping(value="login.do",method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session){
        //根据用户名和密码登录
        ServerResponse<User> response = userServiceI.login(username, password);
        //如果登录成功则判断是否是管理员，是管理员则存入session中，不是管理员则无法登录
        if (response.isSuccess()){
            //获取数据
            User user = response.getData();
            //判断是不是管理员
            if (user.getRole()==Const.Role.ROLE_ADMIN){
                //将当前用户存入session中
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }
        return response;
    }



}
