package com.reggie.common;


import com.reggie.common.CustomException;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody//将数据封装成JSON类型返回给前端
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.info(1111 + ex.getMessage());//报错信息
        if (ex.getMessage().contains("Duplicate entry"))//判断错误信息是否包含Duplicate entry
        {
            //要求前端报错显示：   XXX已存在
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已经存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 自定义异常
     * @param ce
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ce) {
//        log.info(ce.getMessage());//报错信息
        return R.error(ce.getMessage());
    }
}
