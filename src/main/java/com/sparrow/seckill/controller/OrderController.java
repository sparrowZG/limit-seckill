package com.sparrow.seckill.controller;

import com.sparrow.seckill.error.BusinessException;
import com.sparrow.seckill.error.EmBusinessError;
import com.sparrow.seckill.respones.CommonReturnType;
import com.sparrow.seckill.service.OrderService;
import com.sparrow.seckill.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/order")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class OrderController extends BaseController {

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/createorder",method = {RequestMethod.POST})
	@ResponseBody
	public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
										@RequestParam(name = "amount")Integer amount,
										@RequestParam(name = "promoId",required = false)Integer promoId
										) throws BusinessException {
		//1.从session中拿出登录标记
		Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
		if(isLogin==null||!isLogin.booleanValue()){
			throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
		}
		//2.获取用户登录信息
		UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

		//3.调用下单
		orderService.createOrder(userModel.getId(),itemId,promoId,amount);
		return CommonReturnType.create(null);
	}
}
