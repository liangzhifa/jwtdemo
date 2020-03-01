package com.zhifa.jwtdemo.handlerInterceptor;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.zhifa.jwtdemo.mapper.TbUserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Slf4j
public class JwtHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String headerToken = request.getHeader("token");
        if (ObjectUtils.isEmpty(headerToken)) {
            String token = Jwts.builder()
                    //主题 放入用户名
                    .setSubject("niceyoo")
                    //自定义属性 放入用户拥有请求权限
                    .claim("authorities", "admin")
                    //失效时间
                    .setExpiration(new Date(System.currentTimeMillis() + 5000))
                    //签名算法和密钥
                    .signWith(SignatureAlgorithm.HS512, "tmax")
                    .compact();

            JSONObject jsonObject = new JSONObject();
            returnJson(response, token);
            return false;
        }
        try {
            //解析token
            Claims claims = Jwts.parser()
                    .setSigningKey("tmax")
                    .parseClaimsJws(headerToken)
                    .getBody();

            System.out.println(claims);
            //获取用户名
            String username = claims.getSubject();
            System.out.println("username:" + username);
            //获取权限
            String authority = claims.get("authorities").toString();
            System.out.println("权限：" + authority);
        } catch (ExpiredJwtException e) {
            System.out.println("jwt异常 时间---");
        } catch (Exception e) {
            System.out.println("解码异常");
        }
            return true;
}

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            log.error("response error", e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
