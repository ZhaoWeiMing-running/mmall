package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface UserServiceI {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    //校验用户名和邮箱，防止恶意调用接口注册
    ServerResponse<String> checkValid(String str,String type);

    //找回密码问题
    ServerResponse<String> selectQuestion(String username);


    //校验问题是否正确
    ServerResponse<String> checkAnswer(String username,String question,String answer);


    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);

    //校验密码
    ServerResponse<String> resetPassword(String passwordOld, String passwordNew,User user);
    //更新个人信息
    ServerResponse<User> updateInformation(User user);
    //获取个人详细信息
    ServerResponse<User> getInformation(Integer userId);
}
