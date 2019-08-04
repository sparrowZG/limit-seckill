package com.sparrow.seckill.service.impl;

import com.sparrow.seckill.dao.OrderDOMapper;
import com.sparrow.seckill.dao.SequenceDOMapper;
import com.sparrow.seckill.dataobject.OrderDO;
import com.sparrow.seckill.dataobject.SequenceDO;
import com.sparrow.seckill.error.BusinessException;
import com.sparrow.seckill.error.EmBusinessError;
import com.sparrow.seckill.service.ItemService;
import com.sparrow.seckill.service.OrderService;
import com.sparrow.seckill.service.UserService;
import com.sparrow.seckill.service.model.ItemModel;
import com.sparrow.seckill.service.model.OrderModel;
import com.sparrow.seckill.service.model.UserModel;
import com.sun.tools.internal.xjc.reader.dtd.bindinfo.BIUserConversion;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ItemService itemService;
	@Autowired
	private UserService userService;
	@Autowired
	private OrderDOMapper orderDOMapper;
	@Autowired
	private OrderService orderService;

	//生成订单
	@Override
	@Transactional
	public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
		//1.校验下单状态,下单商品是否存在,用户是否合法,购买数量是否正确
		ItemModel itemModel = itemService.getItemById(itemId);
		if (itemModel == null) {
			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
		}
		UserModel userModel = userService.getUserById(userId);
		if (userModel == null) {
			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
		}
		if (amount <= 0 || amount > 99) {
			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "数量信息错误");
		}
		//校验活动信息,promoId不等于null
		if (promoId != null) {
			//校验对应活动是否存在这个商品
			//看传过来的秒杀模型是否和商品模型聚合的秒杀模型一致,(该商品有秒杀活动会将秒杀模型聚合进商品Model)
			if (promoId.intValue() != itemModel.getPromoModel().getId()) {
				throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
			} else if (itemModel.getPromoModel().getStatus() != 2) {
				throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "秒杀活动不在进行中");
			}
		}

		//2. 落单减库存
		boolean result = itemService.decreateStock(itemId, amount);
		if (!result) {
			throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
		}
		//3. 订单入库
		OrderModel orderModel = new OrderModel();
		orderModel.setUserId(userId);
		orderModel.setItemId(itemId);
		orderModel.setAmount(amount);
		//如果有秒杀,下单价格是秒杀价格
		if (promoId != null) {
			orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
		} else {
			//否则就是平销价格
			orderModel.setItemPrice(itemModel.getPrice());
		}
		//先设置单价,设置完成后在计算总价
		orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));
		orderModel.setPromoId(promoId);
		//生成订单号
		orderModel.setId(generateOrderNum());
		OrderDO orderDO = convertFromOrderModel(orderModel);
		//保存订单
		orderDOMapper.insertSelective(orderDO);
		//增加该商品的销量
		//itemService.
		itemService.increateSales(itemId, amount);
		//4.返回给前端
		return orderModel;

	}

	@Autowired
	private SequenceDOMapper sequenceDOMapper;

	/**
	 * 为了保证订单号的全局唯一性,在生成方法中添加事务注解,并设置传播机制为NEW
	 * 这样即使外围的事务失败需要回滚,里面的订单方法都会生成一个新的事务提交
	 * 这样下一个事务操作订单生成,会拿到新的,而不是回滚生成过的
	 *
	 * @return
	 */
	//生成订单号
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String generateOrderNum() {
		//订单号16位信息
		StringBuffer sb = new StringBuffer();

		//前8位为时间信息:需要根据时间归档的时候使用
		LocalDateTime now = LocalDateTime.now();
		//格式化后的格式2018-12-12带横线的去掉
		String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
		sb.append(nowDate);
		int sequence = 0;
		SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
		//获取当前数据库中的序列值
		sequence = sequenceDO.getCurrentValue();
		//获取当前之后生成新的,步长+1
		sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
		//之后更新表中的sequence
		sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);

		//6位自增:保证不重复
		String sequenceStr = String.valueOf(sequence);
		//序列值前面的几位补0
		for (int i = 0; i < 6 - sequenceStr.length(); i++) {
			sb.append(0);
		}
		sb.append(sequenceStr);
		//2位分库分表:如果需要拆分可以,做分库
		//订单信息落到拆分后的100个表中,分散数据库查询和下单压力
		//暂时写死
		sb.append("00");
		return sb.toString();
	}

	private OrderDO convertFromOrderModel(OrderModel orderModel) {
		if (orderModel == null) {
			return null;
		}
		OrderDO orderDO = new OrderDO();
		BeanUtils.copyProperties(orderModel, orderDO);
		orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
		orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
		return orderDO;
	}

}
