package com.cqnu.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cqnu.exception.GitApiRequestException;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author zh
 * @date 2019/9/19
 */
public class HttpUtils {

    private static final  Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * {@link HttpUtils#handleResponseForString(jodd.http.HttpResponse, java.nio.charset.Charset)}
     * 参数简化的重载形式
     *
     * @see HttpUtils#handleResponseForString(jodd.http.HttpResponse, java.nio.charset.Charset)
     */
    private static String handleResponseForString(HttpResponse httpResponse) {
        return handleResponseForString(httpResponse, StandardCharsets.UTF_8);
    }

    /**
     * 按照指定的编码从响应对象中编码响应内容为字符串类型, 默认编码UTF-8
     * @param httpResponse 响应对象
     * @param charset 指定字符串的编码, 默认编码UTF-8
     * @return 字符串类型的响应内容
     */
    private static String handleResponseForString(HttpResponse httpResponse, Charset charset){
        return httpResponse.charset(charset.name()).bodyText();
    }

    /**
     * 从响应对象中获取响应状态码
     * @param httpResponse 响应对象
     * @return 响应状态码
     */
    private static int handleResponseForStatusCode(HttpResponse httpResponse) {
        return  httpResponse.statusCode();
    }

    /**
     * 创建Post请求, 不返回响应流
     *
     * @param parameterMap 请求需要附加的实体内容集合
     */
    public static Integer requestPostForStatusCode(String url, Map<String, Object> parameterMap) {
        HttpResponse httpResponse = HttpRequest.post(url).form(parameterMap).send();
        return handleResponseForStatusCode(httpResponse);
    }

    /**
     * 创建Post请求
     *
     * @param parameterMap 请求需要附加的实体内容集合
     * @return post请求的文本内容
     */
    public static String requestPostForString(String url, Map<String, Object> parameterMap) {
        HttpResponse httpResponse = HttpRequest.post(url).form(parameterMap).send();
        String responseString = handleResponseForString(httpResponse);
        LOGGER.info("POST: 请求响应的字符串内容 = {}", responseString);
        return responseString;
    }

    /**
     * 创建Get请求, 并将响应流转换为String
     *
     * @param parameterMap 请求参数集合
     * @return 字符串类型的响应实体
     */
    public static String requestGetForString(String url, Map<String, String> parameterMap) {
        HttpResponse httpResponse = HttpRequest.get(url).query(parameterMap).send();
        String responseString = handleResponseForString(httpResponse);
        LOGGER.info("GET: 请求响应的字符串内容 = {}", responseString);
        return responseString;
    }

    /**
     * 创建Get请求, 并将响应流转换为JsonArray
     *
     * @param parameterMap 请求参数集合
     * @return JsonArray类型的响应实体
     */
    public static JSONArray requestGetForJsonArray(String url, Map<String, String> parameterMap) {
        String text = requestGetForString(url, parameterMap);
        return JSON.parseArray(text);
    }

    /**
     * 创建Put请求, 返回响应流中解析出的文本
     *
     * @param parameterMap 请求参数集合
     * @return 响应流中解析出的文本
     */
    public static String requestPutForString(String url, Map<String, Object> parameterMap) {
        HttpResponse httpResponse = HttpRequest.put(url).form(parameterMap).send();
        String responseString = handleResponseForString(httpResponse);
        LOGGER.info("PUT: 请求响应的字符串内容 = {}", responseString);
        return responseString;
    }

    /**
     * 创建Put请求, 返回响应流中解析出的JSONObject
     *
     * @param parameterMap 请求参数集合
     * @return 响应流中解析出的文本
     */
    public static JSONObject requestPutForJsonObject(String url, Map<String, Object> parameterMap) {
        String text = requestPutForString(url, parameterMap);
        return JSON.parseObject(text);
    }

    /**
     * 创建Delete请求
     *
     * @param parameterMap 请求参数集合
     * @return 响应状态码
     */
    public static int requestDelete(String url, Map<String, String> parameterMap) {
        HttpResponse httpResponse = HttpRequest.delete(url).query(parameterMap).send();
        return handleResponseForStatusCode(httpResponse);
    }

    public static void download(String url, Map<String, Object> params, OutputStream outputStream) throws GitApiRequestException {
        HttpResponse httpResponse = HttpRequest.get(url).form(params).send();
        int statusCode = httpResponse.statusCode();
        if (statusCode >= 400) {
            String errorMessage = httpResponse.charset(StandardCharsets.UTF_8.name()).bodyText();
            throw new GitApiRequestException(errorMessage, statusCode);
        }
        byte[] rawContent = httpResponse.bodyBytes();
        try {
            if (rawContent != null) {
                outputStream.write(rawContent);
            }
        } catch (IOException e) {
            throw new GitApiRequestException("向指定的输出流中写入响应流失败.",
                    HttpStatus.HTTP_INTERNAL_ERROR, e);
        }
    }
}
