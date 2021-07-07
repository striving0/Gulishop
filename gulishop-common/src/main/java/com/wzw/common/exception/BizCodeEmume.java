package com.wzw.common.exception;

/**
 * @auther Kevin
 * @ClassName BizCodeEmume
 * @Date 2021.04.22 20:03
 */
public enum BizCodeEmume {
    UNKNOW_EXCRPTION(10000,"系统未知错误"),
    VAILD_EXCEPTION(10001,"参数校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");

    private int code;
    private String msg;
    BizCodeEmume(int code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
