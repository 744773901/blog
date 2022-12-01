package com.zjj.blog.utils;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zjj.blog.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JwtToken生成的工具类
 * JWT token的格式：header.payload.signature
 * header的格式（算法、token的类型）：
 * {"alg": "HS512","typ": "JWT"}
 * payload的格式（用户名、创建时间、生成时间）：
 * {"sub":"username","created":1489079981393,"exp":1489684781}
 * signature的生成算法：
 * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 */
@Component
@SuppressWarnings("all")
public class TokenUtil {

    @Resource
    private SecurityProperties properties;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenUtil.class);
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String SECRET = "zjj-blog";

    /**
     * 根据负载生成JWT的token
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 从token中获取JWT中的负载
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 如果过期, 需要在此处异常调用如下的方法, 否则拿不到Claims
            claims = e.getClaims();
        } catch (Exception e) {
            LOGGER.info("JWT格式验证失败:{}", token);
        }
        return claims;
    }

    /**
     * 生成token的过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + properties.getExpiredMillisecond());
    }

    /**
     * 从token中获取登录用户名
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 验证token是否还有效
     *
     * @param token       客户端传入的token
     * @param userDetails 从数据库中查询出来的用户信息
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        if (isTokenExpired(token)) {
            return false;
        }
        String username = getUserNameFromToken(token);
        return username.equals(userDetails.getUsername());
    }

    /**
     * 判断token是否已经失效
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 根据用户信息生成token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 当原来的token没过期时是可以刷新的
     *
     * @param oldToken 带Bearer 的token
     */
    public String refreshHeadToken(String oldToken) {
        if (StringUtils.isBlank(oldToken)) {
            return null;
        }
        String token = oldToken.substring(properties.getTokenStartWith().length());
        if (StringUtils.isBlank(token)) {
            return null;
        }
        //token校验不通过
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        //如果token已经过期，不支持刷新
        if (isTokenExpired(token)) {
            return null;
        }
        //如果token在30分钟之内刚刷新过，返回原token
        if (tokenRefreshJustBefore(token, 30 * 60)) {
            return token;
        } else {
            claims.put(CLAIM_KEY_CREATED, new Date());
            return generateToken(claims);
        }
    }

    /**
     * 判断token在指定时间内是否刚刚刷新过
     *
     * @param token 原token
     * @param time  指定时间（秒）
     */
    private boolean tokenRefreshJustBefore(String token, int time) {
        Claims claims = getClaimsFromToken(token);
        Date created = claims.get(CLAIM_KEY_CREATED, Date.class);
        Date refreshDate = new Date();
        //刷新时间在创建时间的指定时间内
        return refreshDate.after(created) && refreshDate.before(DateUtil.offsetSecond(created, time));
    }

    public static void main(String[] args) throws InterruptedException {
        TokenUtil tokenUtil = new TokenUtil();
        tokenUtil.properties = new SecurityProperties();
//        tokenUtil.properties.setExpiredMillisecond(14400000L);
        tokenUtil.properties.setExpiredMillisecond(3000L);
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, "root");
        claims.put(CLAIM_KEY_CREATED, new Date());
        String token = tokenUtil.generateToken(claims);
        System.out.println("token:" + token);
        String name = tokenUtil.getUserNameFromToken(token);
        System.out.println("username:" + name);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(tokenUtil.getExpiredDateFromToken(token)));
        System.out.println("token是否过期:" + tokenUtil.isTokenExpired(token));
        System.out.println(tokenUtil.getClaimsFromToken(token));
        Thread.sleep(5000L);
        System.out.println(tokenUtil.getClaimsFromToken(token));
        System.out.println("token是否过期:" + tokenUtil.isTokenExpired(token));

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("admin"));
        User user = new User("root", "6666", list);
        System.out.println(tokenUtil.validateToken(token, user));
        System.out.println("+++++++++++++++++++++++++");
        System.out.println(tokenUtil.isTokenExpired("Bearer 1eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBxcS5jb20iLCJjcmVhdGVkIjoxNjY2MzU3NjY0NTg2LCJleHAiOjE2NjYzNzIwNjR9.DBT36TVdg2UvCRfStTxO5hMddpWaD5SpqFVP71hMBRoAfTPIkQ62ZI13_WzxLLe8Y_F790m-JNLpc-aD4CHnTA"));
    }
}
