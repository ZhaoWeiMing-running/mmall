package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.UserServiceI;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserServiceI {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUsername(username);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        // 密码登录md5
        String md5Password=MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username, md5Password);
        if (user==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //将密码置为null
        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登录成功",user);
    }

    //注册
    @Override
    public ServerResponse<String> register(User user){
        /*//校验用户名
        int resultCount = userMapper.checkUsername(user.getUsername());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        //校验邮箱
        resultCount = userMapper.checkEmail(user.getEmail());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }*/
        //进行复用
        //对用户名校验
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        //对邮箱校验
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        //设置为普通角色
        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }


    //校验用户名和邮箱，防止恶意调用接口注册
    @Override
    public ServerResponse<String> checkValid(String str,String type){
        //如果type不为空
        if (StringUtils.isNotBlank(type)){
            //开始校验
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数有误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }


    //忘记密码
    @Override
    public ServerResponse<String> selectQuestion(String username){
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            //如果是成功，表示用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }


    //校验问题答案
    @Override
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount>0){
            //说明问题以及问题答案是这个用户的，并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            //新建TokenCache类，设置过期时间
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }


    //重置密码
    @Override
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        //对forgetToken校验
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数有误，token需要传递");
        }
        //对username校验
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            //如果是成功，表示用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //从缓存中获取token
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        //对token判断
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或已过期");
        }
        //开始重置密码
        if (StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (resultCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }

        return ServerResponse.createByErrorMessage("修改密码失败");
    }


    //修改密码
    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,User user){
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户的，因为我们会进行查询count(1)，如果不指定id，那么结果就是true，count>0;
        int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        //旧密码错误
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        //设置新密码
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        //根据主键更新,
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        //updateCount返回的操作的条数
        if (updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更改成功");
        }
        return ServerResponse.createByErrorMessage("密码更改失败");
    }


    //更新用户个人信息
    @Override
    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新
        //email是需要校验，校验新的email是不是已经存在，如果存在的email如果相同的话，不能是我们当前的这个用户的
        int resultCount=userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("Email已存在，请更换新的Email后再重试");
        }
        //创建新的对象
        User updateUser=new User();
        //设置需要更新的参数
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        //只有不等于空的时候进行更新
        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);

        //对更新操作返回的条数进行判断，是否更新成功
        if (updateCount>0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }


    //获取用户详细信息
    @Override
    public ServerResponse<User> getInformation(Integer userId){
        //根据userId查出用户所有信息
        User user = userMapper.selectByPrimaryKey(userId);
        if (user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        //将密码置为空，防止密码泄露
        user.setPassword(StringUtils.EMPTY);
        //返回user
        return ServerResponse.createBySuccess(user);
    }


}
