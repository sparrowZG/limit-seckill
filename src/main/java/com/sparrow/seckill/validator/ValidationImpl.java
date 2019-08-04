package com.sparrow.seckill.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
@Component
public class ValidationImpl implements InitializingBean {


	private Validator validator;

	//实现效验方法并返回校验结果
	public ValidationResult validate(Object bean){
		//创建自定义的验证结果对象,用来封装是否错误和错误信息
		ValidationResult result = new ValidationResult();

		//调用验证器的验证方法,返回一个set
		Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(bean);
		if(constraintViolationSet.size()>0){
			//bean中有错
			result.setHasErrors(true);
			constraintViolationSet.forEach(constraintViolation->{
				//获取bean的属性上注解定义的错误信息
				String errMsg = constraintViolation.getMessage();
				//获取那个属性有错误
				String propertyName = constraintViolation.getPropertyPath().toString();
				//将错误信息和对应的属性放入错误的Map中
				result.getErrorMsgMap().put(errMsg,propertyName);
			});
		}
		return result;
	}

	//在bean初始化属性之后,调用这个方法
	@Override
	public void afterPropertiesSet() throws Exception {
		this.validator = Validation.buildDefaultValidatorFactory().getValidator();
	}
}
