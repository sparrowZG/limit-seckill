package com.sparrow.seckill.service.impl;

import com.sparrow.seckill.dao.PromoDOMapper;
import com.sparrow.seckill.dataobject.PromoDO;
import com.sparrow.seckill.service.PromoService;
import com.sparrow.seckill.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.ProviderMismatchException;

@Service
public class PromoServiceImpl implements PromoService {
	@Autowired
	private PromoDOMapper promoDOMapper;

	//根据商品 id查询秒杀信息
	@Override
	public PromoModel getPromoByItemId(Integer itemId) {
		//获取对应的秒杀活动信息
		PromoDO promoDO = promoDOMapper.selectByItemId(itemId);
		//将DO -> Model
		PromoModel promoModel = convertFromPromoDO(promoDO);
		if(promoModel==null){
			return null;
		}
		//判断当前时间和秒杀活动开始时间的关系
		//开始时间在当前时间之后
		if(promoModel.getStartTime().isAfterNow()){
			//秒杀还未开始
			promoModel.setStatus(1);
		}else if(promoModel.getEndTime().isBeforeNow()){
			//秒杀已经结束
			promoModel.setStatus(3);
		}else{
			//秒杀正在进行
			promoModel.setStatus(2);
		}
		return promoModel;
	}

	private PromoModel convertFromPromoDO(PromoDO promoDO) {
		if(promoDO==null){
			return null;
		}
		PromoModel promoModel = new PromoModel();
		BeanUtils.copyProperties(promoDO,promoModel);
		promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
		//单独设置时间
		promoModel.setStartTime(new DateTime(promoDO.getStartTime()));
		promoModel.setEndTime(new DateTime(promoDO.getEndTime()));
		return promoModel;
	}
}
