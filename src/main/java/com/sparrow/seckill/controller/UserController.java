package com.sparrow.seckill.controller;

import com.alibaba.druid.util.StringUtils;
import com.sparrow.seckill.controller.viewobject.UserVO;
import com.sparrow.seckill.error.BusinessException;
import com.sparrow.seckill.error.EmBusinessError;
import com.sparrow.seckill.respones.CommonReturnType;
import com.sparrow.seckill.service.UserService;
import com.sparrow.seckill.service.model.UserModel;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")//用来被Spring扫描到
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")        //跨域请求
public class UserController extends BaseController {
	//用户登录接口
	@RequestMapping("/login")
	@ResponseBody
	public CommonReturnType login(@RequestParam(name="telphone")String telphone,
								  @RequestParam(name = "password")String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {
		if(StringUtils.isEmpty(telphone)||StringUtils.isEmpty(password)){
			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
		}
		//在服务层验证数据是否合法
		UserModel userModel = userService.validateLogin(telphone,this.EncodeByMd5(password));
		this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
		this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
		return CommonReturnType.create(null);
	}
	//用户注册接口,传入用户手机号,根据这个号生成验证码以及用户信息
	@RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
	@ResponseBody
	public CommonReturnType register(
		@RequestParam(name="name")String name,
		@RequestParam(name="telphone")String telphone,
		@RequestParam(name="otpCode")String otpCode,
		@RequestParam(name="password")String password,
		@RequestParam(name="gender")Byte gender,
		@RequestParam(name="age")Integer age
	) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
		//验证手机号和对应的otpCode是否相符
		//在成otpCode的时候已经将验证码放入了session中,根据注册的手机号去对应的otpCode
		String inSessionOtpCode = (String) httpServletRequest.getSession().getAttribute(telphone);
		//将取出的otp和输入的进行比较,使用的是StringUtils.equals方法
		if(!StringUtils.equals(inSessionOtpCode,otpCode)){
			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不匹配");
		}
		//用户注册
		UserModel userModel = new UserModel();
		userModel.setAge(age);
		userModel.setEncrptPassword(this.EncodeByMd5(password));
		userModel.setName(name);
		userModel.setGender(gender);
		userModel.setTelphone(telphone);
		userModel.setRegisterMode("byPhone");
		//传给服务层
		userService.register(userModel);
		return CommonReturnType.create(null);//插入成功
	}
	//MD5
	public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		//确定计算法
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		BASE64Encoder base64Encoder = new BASE64Encoder();
		//加密字符串
		String newstr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
		return newstr;
	}

	@Autowired
	private HttpServletRequest httpServletRequest;

	//opt验证码的获取
	@RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
	@ResponseBody
	public CommonReturnType getOtp(@RequestParam(name ="telphone")String  telphone){
		//按照一定的规则生成验证码
		Random random = new Random();
		int randomInt = random.nextInt(99999);
		randomInt+=10000;
		String otpCode = String.valueOf(randomInt);
		System.out.println("telphone:"+telphone+"  optCode:"+otpCode);
		//将telphone于optCode 关联
		httpServletRequest.getSession().setAttribute(telphone,otpCode);
		return CommonReturnType.create(null);
	}
	@Autowired
	private UserService userService;

	@RequestMapping("/get")
	@ResponseBody       //指定ResponseBody,返回给前端用户
	public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
		//调用Service层服务获取对应id的用户对象并返回给前端
		UserModel userModel = userService.getUserById(id);
		if (userModel == null) {
			//userModel.setEncrptPassword("124132");
			throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
		}
		//System.out.println(userModel.getAge()+userModel.getName());
		return CommonReturnType.create(userModel);
	}

	public UserVO convertFromUserModel(UserModel userModel) {
		if (userModel == null) {
			return null;
		}
		UserVO userVO = new UserVO();
		BeanUtils.copyProperties(userModel, userVO);
		return userVO;
	}

}
