package org.web.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web.entity.Result;
import org.web.util.ResultUtil;

@ControllerAdvice
public class ExceptionHandle {
	
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public Result<?> handle(Exception e){
		if (e instanceof ErrorException){
			return ResultUtil.error(((ErrorException) e).getCode(), e.getMessage());
		}else{
			return ResultUtil.error(-1, "未知错误");
		}
	}
}
