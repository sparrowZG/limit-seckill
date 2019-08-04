package com.sparrow.seckill.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.sparrow.seckill.dao.UserDOMapper;
import com.sparrow.seckill.dao.UserPasswordDOMapper;
import com.sparrow.seckill.dataobject.UserDO;
import com.sparrow.seckill.dataobject.UserPasswordDO;
import com.sparrow.seckill.error.BusinessException;
import com.sparrow.seckill.error.EmBusinessError;
import com.sparrow.seckill.service.UserService;
import com.sparrow.seckill.service.model.UserModel;
import com.sparrow.seckill.validator.ValidationImpl;
import com.sparrow.seckill.validator.ValidationResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDOMapper userDOMapper;

	@Autowired
	private UserPasswordDOMapper userPasswordDOMapper;

	@Autowired
	private ValidationImpl validation;

	@Override
	public UserModel getUserById(Integer id) {
		//调用UserDOMapper获取到用户dataobject
		UserDO userDO = userDOMapper.selectByPrimaryKey(id);
		if (userDO == null) {
			return null;
		}
		//查询用户对应的密码
		UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

		return convertFromDataObject(userDO, userPasswordDO);
	}

	//登录验证数据是否合法
	@Override
	public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
		//根据用户telphone查询用户信息
		UserDO userDO = userDOMapper.selectUserByTelphone(telphone);
		if (userDO == null) {
			throw new BusinessException(EmBusinessError.USER_LOGIN_FALL);
		}
		//验证密码
		UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

		UserModel userModel = convertFromDataObject(userDO, userPasswordDO);
		//对比用户信息内加密的密码是否和传输过来的一致,
		if (!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())) {
			//不一致就报错
			throw new BusinessException(EmBusinessError.USER_LOGIN_FALL);
		}
		return userModel;
	}


	//用户注册功能
	@Override
	@Transactional //保证数据中不插入不完整的数据
	public void register(UserModel userModel) throws BusinessException {
		if (userModel == null) {
			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
		}
//		//判断userModel的各个字段
//		if (StringUtils.isEmpty(userModel.getName())
//			|| userModel.getAge() == null
//			|| userModel.getGender() == null
//			|| StringUtils.isEmpty(userModel.getTelphone())) {
//			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//		}
		//使用valication效验userModel的各个属性是否符合校验规则
		ValidationResult validationResult = validation.validate(userModel);
		if(validationResult.isHasErrors()){
			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,validationResult.getErrMsg());
		}
		//插入user_info
		UserDO userDO = converFromUserModel(userModel);
		try {
			userDOMapper.insertSelective(userDO);
		} catch (DuplicateKeyException ex) {
			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号已重复注册");
		}

		//注意: 获取用户id用于填写到user_password表中
		userModel.setId(userDO.getId());
		//插入user_password
		UserPasswordDO userPasswordDO = convertPasswordUserModel(userModel);
		userPasswordDOMapper.insertSelective(userPasswordDO);
	}


	private UserPasswordDO convertPasswordUserModel(UserModel userModel) {
		if (userModel == null) {
			return null;
		}
		UserPasswordDO userPasswordDO = new UserPasswordDO();
		userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
		userPasswordDO.setUserId(userModel.getId());
		return userPasswordDO;
	}

	public UserDO converFromUserModel(UserModel userModel) {
		if (userModel == null) {
			return null;
		}
		UserDO userDO = new UserDO();
		BeanUtils.copyProperties(userModel, userDO);
		return userDO;
	}

	public UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
		if (userDO == null) {
			return null;
		}
		UserModel userModel = new UserModel();
		BeanUtils.copyProperties(userDO, userModel);
		if (userPasswordDO != null) {
			userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
		}
		return userModel;
	}
}
