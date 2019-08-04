package com.sparrow.seckill.respones;

public class CommonReturnType {
	//定义一个状态,标识data的请求结果
	private String status;
	//若status=success,则data内返回的前端需要的json数据
	//若status=fail,则data内使用通用的错误处理格式
	private Object data;

	//定义一个通用的创建方法
	public static CommonReturnType create(Object result) {
		return CommonReturnType.create(result, "success");
	}

	public static CommonReturnType create(Object result, String status) {
		CommonReturnType commonReturnType = new CommonReturnType();
		commonReturnType.setData(result);
		commonReturnType.setStatus(status);
		return commonReturnType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
