package com.sparrow.seckill.controller.viewobject;

import java.math.BigDecimal;

public class ItemVO {
	private Integer id;
	//名字

	private String title;
	//价格

	private BigDecimal price;

	//库存
	private Integer stock;
	//商品描述
	private String description;
	//销量

	private Integer sales;
	//描述图片
	private String imgUrl;

	//秒杀
	//记录商品中是否在秒杀活动中,以及对应的状态,0没有开始,1,即将开始,2.正在进行
	private Integer promoStatus;

	//秒杀活动价格
	private BigDecimal promoPrice;

	//秒杀活动id
	private Integer promoId;

	//秒杀开始时间
	private String startTime;

	public Integer getPromoStatus() {
		return promoStatus;
	}

	public void setPromoStatus(Integer promoStatus) {
		this.promoStatus = promoStatus;
	}

	public BigDecimal getPromoPrice() {
		return promoPrice;
	}

	public void setPromoPrice(BigDecimal promoPrice) {
		this.promoPrice = promoPrice;
	}

	public Integer getPromoId() {
		return promoId;
	}

	public void setPromoId(Integer promoId) {
		this.promoId = promoId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		this.sales = sales;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
