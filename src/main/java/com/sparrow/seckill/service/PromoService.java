package com.sparrow.seckill.service;

import com.sparrow.seckill.service.model.PromoModel;

public interface PromoService {
	//秒杀活动,根据id获取商品的秒杀信息
	PromoModel getPromoByItemId(Integer itemId);

}
