package com.yangguojun.util;

import com.yangguojun.entity.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangguojun on 2019/2/14.
 */
public class ApiClient {

    private static final Logger log = LoggerFactory.getLogger(ApiClient.class);

    private static <T> ApiResult<T> request(String url, Object params, Map<String, String> headers, String signatureKey, Class<T> clazz, Class<?>... elementClass) throws IOException {
//        ObjectNode node = JsonUtil.createObjectNode();
//        String paramString;
//
//        if (params == null) {
//            paramString = "{}";
//        } else if (params instanceof String) {
//            paramString = (String) params;
//        } else {
//            paramString = JsonUtil.toJsonString(params);
//        }
//
//        if (params == null) {
//            node.putPOJO("params", new HashMap<>());
//        } else {
//            node.putPOJO("params", params);
//        }
//
//        if (StringUtil.isNotEmpty(signatureKey)) {
//            String signature = MD5Util.encode(signatureKey.concat(paramString));
//            node.put("signature", signature);
//        }
//
//        String json = HttpUtil.postByJson(url, JsonUtil.toJsonString(node), headers);
//        if (json == null) {
//            ApiResult<T> apiResult = new ApiResult<>();
//            apiResult.setMessageCode(MessageCode.SYSTEM_ERROR.getMsgCode());
//            apiResult.setMessage(MessageCode.SYSTEM_ERROR.getMessage());
//            return apiResult;
//        }
//
//        JsonNode jsonNode = JsonUtil.parseJson(json);
//        ApiResult<T> apiResult = new ApiResult<>();
//        apiResult.setMessageCode(jsonNode.path("msgCode").asInt());
//        apiResult.setMessage(jsonNode.path("message").asText());
//        if (apiResult.getMessageCode() == MessageCode.RESP_OK.getMsgCode()) {
//            T data;
//            if (elementClass != null && elementClass.length > 0) {
//                data = JsonUtil.jsonToObject(JsonUtil.toJsonString(jsonNode.path("data")), clazz, elementClass);
//            } else {
//                data = JsonUtil.jsonToObject(JsonUtil.toJsonString(jsonNode.path("data")), clazz);
//            }
//            apiResult.setData(data);
//        } else {
//            log.warn("[url]{}, [params]{}, [response]{}", url, paramString, json);
//        }
//
//        return apiResult;
        return null;
    }


    public static <T> ApiResult<T> request(String url, Object params, Class<T> clazz) throws IOException {
        return request(url, params, null, null, clazz);
    }


    public static <T> ApiResult<T> request(String url, Object params, Class<T> clazz, String token) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("snc-token", token);
        return request(url, params, headers, null, clazz);
    }


    public static <T> ApiResult<T> requestInternal(String url, Object params, Class<T> clazz, String signatureKey) throws IOException {
        return request(url, params, null, signatureKey, clazz);
    }


    public static <T> ApiResult<T> requestInternal(String url, Object params, Class<T> clazz, String signatureKey, String token) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("snc-token", token);
        return request(url, params, headers, signatureKey, clazz);
    }


    public static <T> ApiResult<T> requestInternal(String url, Object params, String signatureKey, Class<T> clazz, Class<?>... elementClass) throws IOException {
        return request(url, params, null, signatureKey, clazz, elementClass);
    }


    public static <T> ApiResult<T> requestInternal(String url, Object params, String signatureKey, String token, Class<T> clazz, Class<?>... elementClass) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("snc-token", token);
        return request(url, params, headers, signatureKey, clazz, elementClass);
    }

}