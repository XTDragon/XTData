package com.xtdragon.xtdata.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xtdragon.xtdata.utils.ResultCode.FAILED;
import static com.xtdragon.xtdata.utils.ResultCode.SUCCESS;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {

    private long code;
    private String message;
    private T data;

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(SUCCESS.getCode(), SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<>(SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     *
     */
    public static <T> CommonResult<T> failed() {
        return new CommonResult<>(FAILED.getCode(), FAILED.getMessage(), null);
    }

    /**
     * 失败返回结果
     *
     * @param message   错误信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<>(FAILED.getCode(), message, null);
    }

}