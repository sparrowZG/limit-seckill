package com.sparrow.seckill.service.model;
//用户下单交易模型

import java.math.BigDecimal;

public class OrderModel {
	//商品订单号
	private String id;
	//购买用户id
	private Integer userId;
	//商品Id,买的是哪个商品
	private Integer itemId;
	//购买时的价格
	private BigDecimal orderPrice;
	//现在的价格
	private BigDecimal itemPrice;
	//购买的数量
	private Integer amount;

	//若非空,则表示一秒杀方式下单
	private Integer promoId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public BigDecimal getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(BigDecimal orderPrice) {
		this.orderPrice = orderPrice;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getPromoId() {
		return promoId;
	}

	public void setPromoId(Integer promoId) {
		this.promoId = promoId;
	}
}
