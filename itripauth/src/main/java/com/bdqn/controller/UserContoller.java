package com.bdqn.controller;

import cn.itrip.common.*;
import cn.itrip.dao.itripUser.ItripUserMapper;
import cn.itrip.pojo.ItripUser;
import com.alibaba.fastjson.JSONObject;
import com.bdqn.biz.SentSSM;
import com.bdqn.biz.TokenBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.ibatis.executor.ReuseExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Random;


@Controller
@RequestMapping("/api")
@Api(value = "api",description = "用户模块")
public class UserContoller {

    @Resource
    ItripUserMapper dao;


    @Resource
    TokenBiz tokenBiz;


    @Resource
    JredisApi redisAPI;


    @Resource
    SentSSM sentSSM;

    @Resource
    ItripUserMapper userdao;


    @RequestMapping(value = "/validatephone")
    @ResponseBody
    public Dto validatephone(String user,String code) throws Exception {
         //去redis中查找数据
         try {
             String result=redisAPI.getRedis("Code:"+user);
             if(result.equals(code))
             {
                 //根据手机号查询实体，然后update
              //   ItripUser tt=dao.getUserE(user);
                 dao.UpdateByid(user);
                 return DtoUtil.returnSuccess("激活成功！！");
             }
         }
         catch (Exception ex)
         {
             return DtoUtil.returnSuccess("激活失败！！");
         }

        return DtoUtil.returnSuccess("激活失败！！");
    }


    @RequestMapping(value = "/registerbyphone", method = RequestMethod.POST)
    @ResponseBody

    public Dto registerbyphone(@RequestBody ItripUser itripUser) throws Exception {
        try {
            //为手机号发送验证码
            Random random = new Random(7);
            int i = random.nextInt(9999);
            //把随机4位数字发送给手机，把这个四位验证码存入到redis 中去
            sentSSM.setPhone(itripUser.getUserCode(), "" + i);

            //存入到redis 中
            redisAPI.SetRedis("Code:" + itripUser.getUserCode(), "" + i, 60*60);

            //把实体类存入到数据库中去
            ItripUser user = new ItripUser();
            user.setUserCode(itripUser.getUserCode());
            user.setUserName(itripUser.getUserName());
            user.setUserPassword(MD5.getMd5(itripUser.getUserPassword(),32));
            user.setActivated(0);


            Integer result = userdao.insertItripUser(user);
            if (result > 0) {
                return DtoUtil.returnDataSuccess(user);
            }
        } catch (Exception ex) {
            return DtoUtil.returnFail("注册失败", "10000");
        }
        return DtoUtil.returnFail("注册失败", "10000");
    }
    @RequestMapping(value="/dologin",method = RequestMethod.POST)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户名",required = true,name = "name",paramType = "form"),
            @ApiImplicitParam(value = "密码",required = true,name = "password",paramType = "form")

    })
    public Dto Getlist(HttpServletRequest request, String name, String password) throws Exception {
        try {
            //判断数据库中是否存在
            ItripUser itripUser=dao.iflogin(name,MD5.getMd5(password,32));
            //把标识存入到redis 中
            if(itripUser!=null)
            {
                //生成一个token
                 String token=tokenBiz.generateToken(request.getHeader("User-Agent"),itripUser);
                redisAPI.SetRedis(token,JSONObject.toJSONString(itripUser),60*60*2);
                //返回前台页面需要当前时间与过期时间
                ItripTokenVO tokenVO=new ItripTokenVO(token,Calendar.getInstance().getTimeInMillis()*3600*2,Calendar.getInstance().getTimeInMillis());
                return DtoUtil.returnDataSuccess(tokenVO);
            }

        }catch (Exception ex)
        {

        }
        return DtoUtil.returnFail("登录失败","1000");
       //  ItripUser user=dao.getItripUserById(new Long(12));

      //   return JSONArray.toJSONString(user);
    }


}
