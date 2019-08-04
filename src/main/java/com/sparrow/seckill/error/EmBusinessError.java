package com.sparrow.seckill.error;

public enum EmBusinessError implements CommonError {
	//通用的错误类型
	PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),
	UNKNOWN_ERROR(10002, "未知错误"),
	//20000开头为用户信息相关错误定义
	USER_NOT_EXIST(20000, "用户不存在"),
	USER_LOGIN_FALL(20001, "用户名或密码错误"),
	//用户还未登录,不能下单
	USER_NOT_LOGIN(20002,"用户还未登录"),

	//30000开头的为交易信息错误
	STOCK_NOT_ENOUGH(30001,"库存不足")
	;

	private int errCode;
	private String errMsg;

	private EmBusinessError(int errCode, String errMsg) {
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	@Override
	public Integer getErrCode() {
		return this.errCode;
	}

	@Override
	public String getErrMsg() {
		return this.errMsg;
	}

	@Override
	public CommonError setErrMsg(String errMsg) {
		this.errMsg = errMsg;
		return this;
	}
}
