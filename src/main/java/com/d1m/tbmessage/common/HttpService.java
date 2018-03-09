package com.d1m.tbmessage.common;

import com.d1m.tbmessage.server.teambition.service.TeambitionService;
import com.d1m.tbmessage.server.wechat.constant.Config;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class HttpService {

    private static Logger LOG = LoggerFactory.getLogger(HttpService.class);

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    private static HttpService instance = null;

    private static CookieStore cookieStore;

    private HttpService() {

    }

    static {
        cookieStore = new BasicCookieStore();

        // 将CookieStore设置到httpClient中
        httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    /**
     * 获取cookies
     *
     * @author Joe He
     * @date 2017年5月7日 下午8:37:17
     * @return
     */
    public static HttpService getInstance() {
        if (instance == null) {
            synchronized (HttpService.class) {
                if (instance == null) {
                    instance = new HttpService();
                }
            }
        }
        return instance;
    }

    public static String getCookie(String name) {
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

    public static HttpResponse post(Map<String, String> headers, String customUrl, HttpEntity entity, String customAuthBasic, Integer... allowStatus) throws HttpException {
        HttpResponse response = null;
        try {
            response = sendRequest(RequestBuilder.post(), headers, customUrl, null, entity, customAuthBasic, allowStatus);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static HttpResponse get(Map<String, String> headers, String customUrl, Map<String, Object> params, String customAuthBasic, Integer... allowStatus) throws HttpException {
        HttpResponse response = null;
        try {
            response = sendRequest(RequestBuilder.get(), headers, customUrl, params, null, customAuthBasic, allowStatus);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     *
     * @param request
     * @param headers
     * @param customUrl
     * @param params
     * @param entity
     * @param customAuthBasic
     * @return
     * @throws IOException
     */
    private static HttpResponse sendRequest(RequestBuilder request, Map<String, String> headers, String customUrl, Map<String, Object> params, HttpEntity entity, String customAuthBasic, Integer... allowStatus) throws IOException, HttpException {
        request = buildRequest(request, headers, customUrl, params, entity, customAuthBasic);
        HttpResponse response = httpClient.execute(request.build());
        if (ArrayUtils.isEmpty(allowStatus)) allowStatus = new Integer[]{200};
        checkResponse(response, customUrl, allowStatus);
        return response;
    }

    /**
     *
     * @param request
     * @param headers
     * @param url
     * @param params
     * @param entity
     * @param authBasic
     * @return
     */
    private static RequestBuilder buildRequest(RequestBuilder request, Map<String, String> headers, String url, Map<String, Object> params, HttpEntity entity, String authBasic) {
        // set headers
        if (headers != null && headers.size() > 0){
            for (String headerKey : headers.keySet()){
                request.setHeader(headerKey, headers.get(headerKey));
            }
        }
        // set url
        request.setUri(buildFullURL(url, params));
        // set entity
        if (entity != null) request.setEntity(entity);
        // set Authorization
        if (authBasic != null ) request.setHeader(HttpUtil.AUTHORIZATION, authBasic);
        return request;
    }
    /**
     * Builds the full url.
     *
     * @param url
     *            input URL to be completed
     * @param params
     *            request parameters to send to the remote service
     * @return full URL with params
     */
    private static String buildFullURL(String url, Map<String, Object> params) {
        String fullURL = url;
        if(MapUtils.isNotEmpty(params)) {
            StringBuilder paramsURL = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (paramsURL.length() > 0) {
                    paramsURL.append("&");
                }
                String key = param.getKey();
                if (key != null && key.length() > 0) {
                    Object val = "";
                    try {
                        val = ObjectUtils.defaultIfNull(param.getValue(),"");
                        paramsURL.append(URLEncoder.encode(key, HttpUtil.UTF_8)).append("=");
                        paramsURL.append(URLEncoder.encode(val.toString(), HttpUtil.UTF_8));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            fullURL += "?" + paramsURL.toString();
        }
        return fullURL;
    }

    /**
     * Check response
     *
     * @param response response
     * @param customUrl url
     * @throws IOException,HttpException response error
     */
    private static void checkResponse(HttpResponse response, String customUrl, Integer[] allowStatus) throws IOException, HttpException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (Arrays.asList(allowStatus).contains(statusCode)) {
            return;
        }
        StringBuilder respMsg = new StringBuilder();
        respMsg.append("Error calling ").append("service on URL ").append(customUrl).append(" code=").append(statusCode).append(";");

        // Append with response error message.
        HttpEntity entity = response.getEntity();
        if (entity != null) respMsg.append(EntityUtils.toString(entity, HttpUtil.UTF_8));
        throw new HttpException(respMsg.toString());
    }
}

