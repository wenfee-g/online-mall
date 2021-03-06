package com.wenfee.onlinemall.gateway.config;

import com.wenfee.onlinemall.gateway.filter.AuthProperties;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * @program: online-mall
 * @author: Mr.Wenfee
 * @create: 2021-03-19 09:15
 **/
//@Component
public class ReplacePathGatewayFilterFactory extends AbstractGatewayFilterFactory<ReplacePathGatewayFilterFactory.Config> {
    public static final String REGEXP_KEY = "regexp";
    public static final String REPLACEMENT_KEY = "replacement";


    @Autowired
    private AuthProperties authProperties;

    public ReplacePathGatewayFilterFactory() {
        super(ReplacePathGatewayFilterFactory.Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(REGEXP_KEY, REPLACEMENT_KEY);
    }

    @Override
    public GatewayFilter apply(ReplacePathGatewayFilterFactory.Config config) {
        // String replacement = config.replacement.replace("$\\", "$");
        return (exchange, chain) -> {
            ServerHttpRequest req = exchange.getRequest();
            addOriginalRequestUrl(exchange, req.getURI());
            String path = req.getURI().getRawPath();
            //就改这个，liyinshan
            String newPath=path;

            String serviceName= StringUtils.substringBetween(path,"/");
            String newServicePath=this.authProperties.getServiceUrlMapping()!=null?this.authProperties.getServiceUrlMapping().get(serviceName):null;
            //String newServicePath="/COUPON-SERVICE/coupon/coupon/list";
            if(newServicePath!=null){
                //newPath= RegExUtils.replaceFirst(path,serviceName,newServicePath);
                newPath= "/coupon/coupon/lis";
            }


            ServerHttpRequest request = req.mutate()
                    .path(newPath)
                    .build();

            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, request.getURI());

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    public static class Config {
        private String regexp;
        private String replacement;

        public String getRegexp() {
            return regexp;
        }

        public Config setRegexp(String regexp) {
            this.regexp = regexp;
            return this;
        }

        public String getReplacement() {
            return replacement;
        }

        public Config setReplacement(String replacement) {
            this.replacement = replacement;
            return this;
        }
    }
}