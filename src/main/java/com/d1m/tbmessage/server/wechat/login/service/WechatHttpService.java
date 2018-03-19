package com.d1m.tbmessage.server.wechat.login.service;

import com.d1m.tbmessage.common.HttpUtil;
import com.d1m.tbmessage.server.wechat.constant.Config;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class WechatHttpService {

    private static Logger LOG = LoggerFactory.getLogger(WechatHttpService.class);

    private CloseableHttpClient httpClient = HttpClients.createDefault();

    private CookieStore cookieStore;

    public WechatHttpService() {
        cookieStore = new BasicCookieStore();

        // 将CookieStore设置到httpClient中
        httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    public String getCookie(String name) {
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return cookie.getValue();
            }
        }
        return null;

    }

    /**
     * for WeChat GET
     *
     * @author Joe He
     * @param url url
     * @param params params
     * @return response payload
     */
    public HttpEntity doGet(String url, List<BasicNameValuePair> params, boolean redirect, Map<String, String> headerMap) {
        HttpEntity entity = null;
        HttpGet httpGet = new HttpGet();

        try {
            if (params != null) {
                String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
                httpGet = new HttpGet(url + "?" + paramStr);
            } else {
                httpGet = new HttpGet(url);
            }
            if (!redirect) {
                httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build()); // 禁止重定向
            }
            httpGet.setHeader(HttpUtil.USER_AGENT, Config.USER_AGENT);
            if (headerMap != null) {
                Set<Map.Entry<String, String>> entries = headerMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            entity = response.getEntity();
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }

        return entity;
    }

    /**
     * for WeChat POST
     *
     * @author Joe He
     * @param url url
     * @param paramsStr string param
     * @return response payload
     */
    public HttpEntity doPost(String url, String paramsStr) {
        HttpEntity entity = null;
        HttpPost httpPost = new HttpPost();
        try {
            StringEntity params = new StringEntity(paramsStr, Consts.UTF_8);
            httpPost = new HttpPost(url);
            httpPost.setEntity(params);
            httpPost.setHeader(HttpUtil.CONTENT_TYPE, HttpUtil.DEFAULT_RESPONSE_TYPE);
            httpPost.setHeader(HttpUtil.USER_AGENT, Config.USER_AGENT);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            entity = response.getEntity();
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }

        return entity;
    }
}

