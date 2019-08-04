package com.sparrow.seckill.controller;

import com.sparrow.seckill.controller.viewobject.ItemVO;
import com.sparrow.seckill.error.BusinessException;
import com.sparrow.seckill.respones.CommonReturnType;
import com.sparrow.seckill.service.model.ItemModel;
import com.sparrow.seckill.service.ItemService;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController {
	@Autowired
	private ItemService itemService;

	//创建商品
	@RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
	@ResponseBody
	public CommonReturnType createItem(@RequestParam(name = "title") String title,
									   @RequestParam(name = "description") String description,
									   @RequestParam(name = "price") BigDecimal price,
									   @RequestParam(name = "stock") Integer stock,
									   @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {

		//封装service请求用来创建商品
		//尽量让Controller层简单，让Service层负责，把服务逻辑尽可能聚合在Service层内部，实现流转处理
		//创建给service层的
		ItemModel itemModel = new ItemModel();
		itemModel.setTitle(title);
		itemModel.setDescription(description);
		itemModel.setPrice(price);
		itemModel.setStock(stock);
		itemModel.setImgUrl(imgUrl);

		//创建商品，返回itemModel

		ItemModel itemModelForReturn = itemService.createItem(itemModel);

		//返回给前端
		ItemVO itemVO = convertVOFromModel(itemModelForReturn);

		//将vo转json给页面
		return CommonReturnType.create(itemVO);

	}

	//查询商品列表
	@RequestMapping(value = "/list", method = {RequestMethod.GET})
	@ResponseBody
	public CommonReturnType listItem() {
		List<ItemModel> itemModelList = itemService.listItem();
		//将model转为vo
		//使用stream api将list内的itemModel转为ItemVO
		List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
			ItemVO itemVO = this.convertVOFromModel(itemModel);
			return itemVO;
		}).collect(Collectors.toList());
		return CommonReturnType.create(itemVOList);
	}

	//查询商品详情
	@RequestMapping(value = "/get", method = {RequestMethod.GET})
	@ResponseBody
	public CommonReturnType getItem(@RequestParam(name = "id") Integer id) {
		ItemModel itemModel = itemService.getItemById(id);
		//将model转为vo
		//使用stream api将list内的itemModel转为ItemVO
		ItemVO itemVO = convertVOFromModel(itemModel);
		return CommonReturnType.create(itemVO);
	}

	private ItemVO convertVOFromModel(ItemModel itemModel) {
		if (itemModel == null) {
			return null;
		}
		ItemVO itemVO = new ItemVO();
		BeanUtils.copyProperties(itemModel, itemVO);


		if (itemModel.getPromoModel() != null) {
			//&#x6709;&#x6b63;&#x5728;&#x8fdb;&#x884c;&#x6216;&#x5373;&#x5c06;&#x5f00;&#x59cb;&#x7684;&#x79d2;&#x6740;&#x6d3b;&#x52a8;
			//设置秒杀活动状态
			itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
			//设置秒杀活动id
			itemVO.setPromoId(itemModel.getPromoModel().getId());
			//设置秒杀活动开始时间
			itemVO.setStartTime(itemModel.getPromoModel().getStartTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
			//秒杀价格
			itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
		} else {
			//该商品没有秒杀活动
			itemVO.setPromoStatus(0);
		}

		return itemVO;
	}

}
