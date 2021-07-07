package com.wzw.gulishop.product.exception;

import com.wzw.common.exception.BizCodeEmume;
import com.wzw.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther Kevin
 * @ClassName GulishopExceptionControllerAdvice
 * @Date 2021.04.22 19:33
 */

/**
 * 集中处理异常
 */

@Slf4j
//@ControllerAdvice(basePackages = "com.wzw.gulishop.product.controller")
//@ResponseBody
@RestControllerAdvice(basePackages = "com.wzw.gulishop.product.controller")
public class GulishopExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e) {

        log.error("数据校验出现问题{}，异常类型:{}", e.getMessage(), e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError) -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return R.error(BizCodeEmume.VAILD_EXCEPTION.getCode(), BizCodeEmume.VAILD_EXCEPTION.getMsg()).put("data", errorMap);

    }


    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {


        log.error("错误",throwable);
        return R.error(BizCodeEmume.UNKNOW_EXCRPTION.getCode(),BizCodeEmume.UNKNOW_EXCRPTION.getMsg());
    }


}
