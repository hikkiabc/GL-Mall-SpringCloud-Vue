package com.glmall.glproduct.controller;

import com.glmall.utils.CodeEnum;
import com.glmall.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.glmall.glproduct.controller")
public class GLMallExceptionControllerAdvice {
@ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
    log.error(e.getMessage(),e.getClass());
    BindingResult result = e.getBindingResult();

        Map errMap=new HashMap();
        result.getFieldErrors().forEach(item->{
            errMap.put(item.getField(),item.getDefaultMessage());
        });
        return R.error(CodeEnum.VALID_EXCEPTION.getCode(),CodeEnum.VALID_EXCEPTION.getMsg())
                .put("data",errMap);

}
//@ExceptionHandler(value = Throwable.class)
//    public R handleUnknownException(Exception e ){
//    System.out.println(e);
//        return R.error(CodeEnum.UNKNOWN_EXCEPTION.getCode(),CodeEnum.UNKNOWN_EXCEPTION.getMsg());
//}
}
