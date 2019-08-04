package com.sparrow.seckill.controller;

import com.sparrow.seckill.error.BusinessException;
import com.sparrow.seckill.error.EmBusinessError;
import com.sparrow.seckill.respones.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

	public static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";

	//定义exceptionHandler解决未被Controller层吸收的exception
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Object handlerException(HttpServletRequest request, Exception ex) {
		Map<String, Object> responesData = new HashMap<>();
		if (ex instanceof BusinessException) {
			BusinessException businessException = (BusinessException) ex;
			responesData.put("errCode", businessException.getErrCode());
			responesData.put("errMsg", businessException.getErrMsg());
		} else {
			responesData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
			responesData.put("errMsg", EmBusinessError.UNKNOWN_ERROR.getErrMsg());
		}
		return CommonReturnType.create(responesData, "fail");
	}
}
