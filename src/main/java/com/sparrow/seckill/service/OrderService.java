package com.sparrow.seckill.service;

import com.sparrow.seckill.error.BusinessException;
import com.sparrow.seckill.service.model.OrderModel;

public interface OrderService {
	//创建订单
	//需要用户id,商品id,商品数量
	// 1. 通过前端页面传过来的id,下单接口内效验id是否存在,并且活动已经开始
	// 2. 直接在下单接口中判断对应商品是否有秒杀活动,若存在进行秒杀商品的下单
	// 使用第一种接收id
	OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer amount) throws BusinessException;
}
