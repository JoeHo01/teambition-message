package com.d1m.tbmessage.common;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class HttpUtil {

    /** The Constant DEFAULT_RESPONSE_TYPE. */
    private static final String DEFAULT_RESPONSE_TYPE = "application/json;charset=UTF-8";

    /** The Constant UTF_8. */
    private static final String UTF_8 = "UTF-8";

    private static HttpClient httpClient = HttpClients.createDefault();


    public static HttpResponse post(Map<String, String> headers, String customUrl, HttpEntity entity, String customAuthBasic, Integer... allowStatus) {
        HttpResponse response = null;
        try {
            response = sendRequest(RequestBuilder.post(), headers, customUrl, null, entity, customAuthBasic, allowStatus);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static HttpResponse get(Map<String, String> headers, String customUrl, Map<String, Object> params, String customAuthBasic, Integer... allowStatus) {
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
    private static HttpResponse sendRequest(RequestBuilder request, Map<String, String> headers, String customUrl, Map<String, Object> params, HttpEntity entity, String customAuthBasic, Integer... allowStatus) throws IOException {
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
        if (authBasic != null ) request.setHeader("Authorization", authBasic);
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
                        paramsURL.append(URLEncoder.encode(key, UTF_8)).append("=");
                        paramsURL.append(URLEncoder.encode(val.toString(), UTF_8));
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
     *
     * @param response
     * @param customUrl
     * @throws IOException
     */
    private static void checkResponse(HttpResponse response, String customUrl, Integer[] allowStatus) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (Arrays.asList(allowStatus).contains(statusCode)) {
            return;
        }
        StringBuilder respMsg = new StringBuilder();
        respMsg.append("Error calling ").append("service on URL ").append(customUrl).append(" code=").append(statusCode).append(";");

        // Append with response error message.
        HttpEntity entity = response.getEntity();
        if (entity != null) respMsg.append(EntityUtils.toString(entity, UTF_8));
    }
}

