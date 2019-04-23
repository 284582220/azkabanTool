package com.yangguojun.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yangguojun on 2016/12/23.
 */
public final class HttpUtil {

    private HttpUtil() {
    }

    private static final int POST = 1;
    private static final int GET = 0;

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static String doHttpRequest(String url, Map<String, String> params, int method) {
        String result = null;

        log.debug("[request url]==> " + url + " [params]==> " + JsonUtil.toJsonString(params));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000).build();

            CloseableHttpResponse response = null;
            if (method == POST) {
                HttpPost post = new HttpPost(url);
                post.setConfig(requestConfig);
                List<BasicNameValuePair> nvps = new ArrayList<>();
                if (params != null) {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                    }
                    post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
                }
                response = httpClient.execute(post);
            } else if (method == GET) {
                HttpGet get = new HttpGet(getUrl(url, params));
                get.setConfig(requestConfig);
                response = httpClient.execute(get);
            }
            assert response != null;
            HttpEntity responseEntity = response.getEntity();
            result = EntityUtils.toString(responseEntity);
            EntityUtils.consume(responseEntity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log.debug("response ==> " + result);

        return result;
    }


    public static String httpPost(String url, Map<String, String> params) {
        return doHttpRequest(url, params, POST);
    }

    public static String httpGet(String url, Map<String, String> params) {
        return doHttpRequest(url, params, GET);
    }


    private static String getUrl(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder requestUrl = new StringBuilder(url);
        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null || "".equals(entry.getValue())) {
                continue;
            }
            if (i == 0) {
                if (url.matches(".*\\?.*")) {
                    requestUrl.append("&");
                } else {
                    requestUrl.append("?");
                }
                requestUrl.append(entry.getKey()).append("=").append(entry.getValue());
            } else {
                requestUrl.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            i++;
        }
        return requestUrl.toString();
    }


    public static String postByJson(String url, String jsonData, Map<String, String> headers) throws IOException {
        log.debug("url-->" + url + ",发送消息：{}", jsonData);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = null;
        try {
            HttpPost httppost = new HttpPost(url);
            StringEntity entity = new StringEntity(jsonData, "UTF-8");
            entity.setContentType("application/json");
            httppost.setEntity(entity);
            httppost.addHeader("Accept", "application/json");
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httppost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            CloseableHttpResponse response = httpClient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();
            if (HttpURLConnection.HTTP_OK == code) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
                log.error("[url]{}\n [response]{}", url, EntityUtils.toString(response.getEntity(), "UTF-8"));
            }
        } finally {
            httpClient.close();
        }

        log.debug("response == > {}", result);

        return result;
    }

    public static String uploadFiles(String url, Map<String, String> params, String filePath, String fileName) throws IOException {
        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build();
            httppost.setConfig(requestConfig);
//            httppost.addHeader("Content-Type", "multipart/mixed");
            InputStream inputStream = new FileInputStream(filePath + File.separator + fileName);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody
                    ("file", inputStream, ContentType.create("application/zip"), fileName);
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.addTextBody(entry.getKey(), entry.getValue());
                }
            }
            httppost.setEntity(builder.build());
            CloseableHttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(response.getEntity());
                log.debug("Response content length:" + result);
            }
            EntityUtils.consume(resEntity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static String postByJson(String url, String jsonData) throws IOException {
        return postByJson(url, jsonData, null);
    }

}

