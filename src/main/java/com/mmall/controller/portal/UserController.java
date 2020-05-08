package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserServiceI userServiceI;


    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session){

        ServerResponse<User> response = userServiceI.login(username, password);
        //如果成功的话,设置当前为用户
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    //登出,直接移除session
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccessMessage("退出成功");
    }

    //注册
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    public ServerResponse<String> register(User user){
       return userServiceI.register(user);
    }

    //校验用户名和邮箱
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str,String type){
        return userServiceI.checkValid(str, type);
    }


    //获取用户当前信息
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    //密码问题获取
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    public ServerResponse<String> forgetGetQuestion(String username){
        return userServiceI.selectQuestion(username);

    }

    //使用本地缓存，校验问题答案是否正确
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
         return userServiceI.checkAnswer(username, question, answer);
    }

    //密码重置
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
       return userServiceI.forgetResetPassword(username, passwordNew, forgetToken);
    }

    //修改密码
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户登录状态
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //用户已登录
        return userServiceI.resetPassword(passwordOld,passwordNew,user);
    }



    //更新个人信息
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        //获取当前用户
        User currentUser= (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //防止越权问题，设置登录用户id和用户名，防止id的变化
        user.setId(currentUser.getId());
        //一定要有username的，否则session中存的对象就没有username了
        user.setUsername(currentUser.getUsername());
        //更新用户信息
        ServerResponse<User> response = userServiceI.updateInformation(user);
        //如果更新成功，更新session
        if (response.isSuccess()){
            //设置username
            response.getData().setUsername(currentUser.getUsername());
            //存入session中
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    //获取用户详细信息
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    public ServerResponse<User> getInformation(HttpSession session){
        //获取当前用户
        User currentUser= (User) session.getAttribute(Const.CURRENT_USER);
        //判断是否登录
        if (currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要重新登录");
        }
        //返回当前用户信息
        return userServiceI.getInformation(currentUser.getId());
    }


}
