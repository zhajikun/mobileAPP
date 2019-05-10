package com.appspdeveloperblogapp.ws.security;

import com.appspdeveloperblogapp.ws.SpringApplicaitonContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";


    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicaitonContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }

}
