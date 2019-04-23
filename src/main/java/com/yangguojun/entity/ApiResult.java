package com.yangguojun.entity;

/**
 * Created by yangguojun on 2019/2/14.
 */
public class ApiResult<T> {
    private int messageCode;
    private String message;
    private T data;

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
