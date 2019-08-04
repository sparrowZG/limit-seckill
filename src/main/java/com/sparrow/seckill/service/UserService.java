package com.sparrow.seckill.service;

import com.sparrow.seckill.error.BusinessException;
import com.sparrow.seckill.service.model.UserModel;

public interface UserService {
	UserModel getUserById(Integer id);
	//注册用户
	void register(UserModel userModel) throws BusinessException;
	//根据用户telphone和password验证用户
	UserModel validateLogin(String telphone,String encrptPassword) throws BusinessException;
}
