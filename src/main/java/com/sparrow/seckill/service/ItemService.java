package com.sparrow.seckill.service;

import com.sparrow.seckill.error.BusinessException;
import com.sparrow.seckill.service.model.ItemModel;

import java.util.List;

public interface ItemService {
	//创建商品
	ItemModel createItem(ItemModel itemModel) throws BusinessException;

	//商品列表
	List<ItemModel> listItem();

	//商品详情
	ItemModel getItemById(Integer id);

	//库存扣减
	boolean decreateStock(Integer itemId,Integer amount);
	//下单成功,增加商品的销量
	void increateSales(Integer itemId, Integer amount);
}
