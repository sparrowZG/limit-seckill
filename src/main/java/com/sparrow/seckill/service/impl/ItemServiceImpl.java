package com.sparrow.seckill.service.impl;

import com.sparrow.seckill.dao.ItemDOMapper;
import com.sparrow.seckill.dao.ItemStockDOMapper;
import com.sparrow.seckill.dataobject.ItemDO;
import com.sparrow.seckill.dataobject.ItemStockDO;
import com.sparrow.seckill.error.BusinessException;
import com.sparrow.seckill.error.EmBusinessError;
import com.sparrow.seckill.service.PromoService;
import com.sparrow.seckill.service.model.ItemModel;
import com.sparrow.seckill.service.ItemService;
import com.sparrow.seckill.service.model.PromoModel;
import com.sparrow.seckill.validator.ValidationImpl;
import com.sparrow.seckill.validator.ValidationResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
	//校验参数
	@Autowired
	private ValidationImpl validation;
	@Autowired
	public ItemDOMapper itemDOMapper;

	@Autowired
	public ItemStockDOMapper itemStockDOMapper;

	@Override
	public ItemModel createItem(ItemModel itemModel) throws BusinessException {
		//1. 入参校验
		ValidationResult validationResult = validation.validate(itemModel);
		if (validationResult.isHasErrors()) {
			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, validationResult.getErrMsg());
		}
		//2, 将ItemModel转为ItemDO
		ItemDO itemDO = convertItemDoFromItemModel(itemModel);
		//3. 将数据写入数据库
		itemDOMapper.insertSelective(itemDO);
		itemModel.setId(itemDO.getId());
		ItemStockDO itemStockDO = convertItemStockDoFromItemModel(itemModel);
		itemStockDOMapper.insertSelective(itemStockDO);
		//4. 返回创建完成的对象
		return this.getItemById(itemModel.getId());
	}


	private ItemStockDO convertItemStockDoFromItemModel(ItemModel itemModel) {
		if (itemModel == null) {
			return null;
		}
		ItemStockDO itemStockDO = new ItemStockDO();
		itemStockDO.setStock(itemModel.getStock());
		itemStockDO.setItemId(itemModel.getId());
		return itemStockDO;
	}

	private ItemDO convertItemDoFromItemModel(ItemModel itemModel) {
		if (itemModel == null) {
			return null;
		}
		ItemDO itemDO = new ItemDO();
		BeanUtils.copyProperties(itemModel, itemDO);
		//将BigDecimal转为double
		itemDO.setPrice(itemModel.getPrice().doubleValue());
		return itemDO;
	}

	@Override
	public List<ItemModel> listItem() {
		//查询商品
		List<ItemDO> itemDOList = itemDOMapper.listItem();
		//遍历list将ItemDo 转为 ItemModel
		List<ItemModel> itemModelList = itemDOList.stream().map(itemDO -> {
			ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
			ItemModel itemModel = this.convertModelFromDataObject(itemDO, itemStockDO);
			return itemModel;
		}).collect(Collectors.toList());

		return itemModelList;
	}

	@Autowired
	public PromoService promoService;

	/**
	 * 根据商品ID查出商品的信息,在通过商品的ip查询销量,封装成itemModel
	 *
	 * @param id
	 * @return
	 */
	@Override
	public ItemModel getItemById(Integer id) {
		//入参校验
		ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
		if (itemDO == null) {
			return null;
		}
		ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
		ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);
		//获取活动信息
		PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
		//如果该商品存在秒杀对象并且秒杀状态不等于3
		if (promoModel != null && promoModel.getStatus() != 3) {
			//将秒杀商品的秒杀信息存到Modal中
			itemModel.setPromoModel(promoModel);
		}
		return itemModel;
	}

	//减库存操作
	@Override
	@Transactional
	public boolean decreateStock(Integer itemId, Integer amount) {
		int affectedRow = itemStockDOMapper.decreateStock(itemId, amount);
		if (affectedRow > 0) {
			//更新库存成功
			return true;
		} else {
			//更新库存失败
			return false;
		}
	}

	//下单成功,增加商品的销量
	@Override
	public void increateSales(Integer itemId, Integer amount) {
		itemDOMapper.increateSales(itemId, amount);
	}

	private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO) {
		ItemModel itemModel = new ItemModel();
		BeanUtils.copyProperties(itemDO, itemModel);
		itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
		itemModel.setStock(itemStockDO.getStock());
		return itemModel;
	}
}
