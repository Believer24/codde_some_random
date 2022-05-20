package com.fbhome.yygh.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.fbhome.yygh.common.helper.JwtHelper;
import com.fbhome.yygh.common.result.Result;
import com.fbhome.yygh.model.user.UserInfo;
import com.fbhome.yygh.user.service.UserInfoService;
import com.fbhome.yygh.user.utils.ConstantWxProperties;
import com.fbhome.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseObject;

//微信操作接口
@Controller
//不返回数据，仅满足页面跳转
@CrossOrigin
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {
    @Autowired
    private UserInfoService userInfoService;
    private static final String OPEN_ID = "openid";
    @GetMapping("callback")
    public String callback(String code,String state) {
        //1.获取临时票据 code

        //2.拿code 和微信id和密钥，请求微信固定地址，得到两个值 StringBuffer是线程安全的
        StringBuilder baseAccessTokenUrl = new StringBuilder()
                .append( "https://api.weixin.qq.com/sns/oauth2/access_token" )
                .append( "?appid=%s" )
                .append( "&secret=%s" )
                .append( "&code=%s" )
                .append( "&grant_type=authorization_code" );

        String accessTokenUrl = String.format( baseAccessTokenUrl.toString(),
                ConstantWxProperties.WX_OPEN_APP_ID,
                ConstantWxProperties.WX_OPEN_APP_SECRET,
                code );

        //使用httpclient请求这个地址
        try {
            String accesstokenInfo = HttpClientUtils.get( accessTokenUrl );

            JSONObject jsonObject = parseObject( accesstokenInfo );
            String access_token = jsonObject.getString( "access_token" );
            String openid = jsonObject.getString( OPEN_ID );
            UserInfo userInfo=userInfoService.selectWxInfoOpenId(openid);
            if(userInfo==null){    //数据库不存在
                //第三步 拿着openid和access_token请求微信地址，得到扫描人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl=String.format( baseUserInfoUrl,access_token,openid );
                String resultInfo=HttpClientUtils.get(userInfoUrl);

                //解析用户信息
                JSONObject resultUserInfoJson = parseObject(resultInfo);
                String nickname = resultUserInfoJson.getString("nickname");  //昵称

                //获取扫描人信息
                userInfo=new UserInfo();
                userInfo.setName( nickname );
                userInfo.setOpenid( openid );
                userInfo.setStatus( 1 );
                userInfoService.save(userInfo);
            }

            //返回name和token字符串
            Map<String,Object>  map=new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            //判断userInfo是否有手机号，如果手机号为空，返回openid
            //如果为空，返回openid值是空字符串
            //前端判断，如果openid不为空，绑定手机号，反之不绑定手机号
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put(OPEN_ID, userInfo.getOpenid());
            } else {
                map.put(OPEN_ID, "");
            }
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            //跳转到前端页面
            return "redirect:" + ConstantWxProperties.YYGH_BASE_URL +
                    "/weixin/callback?token=" + map.get("token") +
                    "&openid=" + map.get(OPEN_ID) +
                    "&name=" + URLEncoder.encode((String)map.get("name"),"utf-8");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "";
    }
    //1.生成微信扫码二维码
    //返回生成二维码所需要的参数
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result<Object> genQrConnect(){

        try {
            Map<String,Object> map=new HashMap<>();
            map.put( "appid", ConstantWxProperties.WX_OPEN_APP_ID );
            map.put( "scope","snsapi_login" );
            String wxOpenRedirectUrl=ConstantWxProperties.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl = URLEncoder.encode( wxOpenRedirectUrl, "utf-8" );
            map.put( "redirect_url",wxOpenRedirectUrl );
            map.put( "state",System.currentTimeMillis()+"" );
            return Result.ok(map);
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
            return null;
        }


    }

    //2.回调方法
}
