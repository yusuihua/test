package com.itheima.controller;

import com.itheima.entity.Result;
import com.itheima.exception.MyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 拦截controller抛出的异常
 */
@RestControllerAdvice
public class MyExceptionHandler {

    //声明要捕获的异常类型
    @ExceptionHandler(MyException.class)
    public Result handleMyException(MyException my){
        my.printStackTrace();
        // 包装一下返回的结果
        return new Result(false, my.getLocalizedMessage());
    }

    //声明要捕获的异常类型
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception unknown){
        unknown.printStackTrace();
        // 包装一下返回的结果
        return new Result(false, "发生异常了");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result noAccessHandler(AccessDeniedException accessDeniedException){

        return new Result(false, "权限不足");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result noInvalidHandler(MethodArgumentNotValidException e){
        // 校验的结果
        BindingResult bindingResult = e.getBindingResult();
        // 校验没通过的属性
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuffer sb = new StringBuffer();
        if(null != fieldErrors){
            for (FieldError error : fieldErrors) {
                sb.append(error.getField() + ":" + error.getDefaultMessage());
            }
        }
        return new Result(false, sb.toString());
    }
}
