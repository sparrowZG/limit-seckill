package com.sparrow.seckill.error;

//包装器业务异常实现
public class BusinessException extends Exception implements CommonError {
	private CommonError commonError;

	//直接接受EmBusinessError的传参用于构造业务异常
	public BusinessException(CommonError commonError) {
		super();//Exception自身的初始化
		this.commonError = commonError;
	}

	public BusinessException(CommonError commonError, String errMsg) {
		super();
		this.commonError = commonError;
		this.commonError.setErrMsg(errMsg);
	}

	@Override
	public Integer getErrCode() {
		return this.commonError.getErrCode();
	}

	@Override
	public String getErrMsg() {
		return this.commonError.getErrMsg();
	}

	@Override
	public CommonError setErrMsg(String errMsg) {
		this.commonError.setErrMsg(errMsg);
		return this;
	}
}
