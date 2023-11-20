package com.zhoujl.learnshiro.config.jwt;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2023-11-17 9:20
 * @see: com.zhoujl.learnshiro.config.jwt
 * @Version: 1.0
 */
@Data
public class JwtToken implements AuthenticationToken,DataAuthToken{

    private String token;


    public JwtToken(String token){
        this.token=token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public String getName() {
        return "UserRealm";
    }
}