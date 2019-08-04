package com.sparrow.seckill.service.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URL;

public class ItemModel {
	private Integer id;
	//名字
	@NotBlank(message = "商品名称不能为空")
	private String title;
	//价格
	@NotNull(message = "商品价格不能为空")
	@Min(value = 0,message = "商品价格必须大于0")
	private BigDecimal price;
	//库存
	@NotNull(message = "商品库存不能为空")
	private Integer stock;
	//商品描述
	@NotNull(message = "商品描述不能为空")
	private String description;
	//销量

	private Integer sales;
	//描述图片
	@NotNull(message = "商品描述不能为空")
	private String imgUrl;

	public PromoModel getPromoModel() {
		return promoModel;
	}

	public void setPromoModel(PromoModel promoModel) {
		this.promoModel = promoModel;
	}

	//使用聚合模型，如果promModel不为空，则表示其拥有还未结束的秒杀聚合对象
	private PromoModel promoModel;

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
