[TOC]

# SpringBoot开发秒杀项目

## 第一章 简介

使用IDEA+Maven搭建SpringBoot开发环境,集成MyBatis操作数据库,实现前后端分离电商秒杀项目

![image](https://github.com/sparrowZG/limit-seckill/blob/master/images/秒杀系统架构图.png)

## 第二章  应用SpringBoot完成基础项目搭建

### 2.1 使用SpringBoot写一个helloworld测试环境

### 2.2引入SpringBoot依赖包实现简单的WEB项目

1. 在SpringBoot官方网站上,添加starter-parent和起步依赖

   ```xml
   <!--所有的SpringBoot都要继承starter-parent-->
       <parent>		
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-parent</artifactId>
           <version>2.0.1.RELEASE</version>
       </parent>
   <!--起步依赖-->
   <!--注意起步依赖以功能为引导-->
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
   ```

2. 定义SpringBoot引导类,运行引导类,定义一个REST

   ```java
   @EnableAutoConfiguration //声明该类是一个SpringBoot引导类
   @RestController //说明是REST
   public class App
   {
       @RequestMapping("/")
       public String home(){
           return "Hello World,前端页面";
       }
       public static void main( String[] args )
       {
           System.out.println( "Hello World!" );
           SpringApplication.run(App.class,args);
           //表示运行Spring引导类, run参数就是SpringBoot引导类的字节码对象
       }
   }
   ```

### 2.3Mybatis接入SpringBoot项目

1. 添加数据库5.1.41,和使用Druid(1.1.3)管理数据库连接

2. 添加Mybatis对于SpringBoot的支持(1.3.1)

3. 通过在application.properties配置文件,添加Mybatis的配置

   ```properties
   #导入Mybatis的配置,启动一个带mybatis的SpringBoot
   mybatis.mapperLocations = classpath:mapping/*.xml
   ```

4. 使用Mybatis自动生成文件的插件Mybatis generator

   ```xml
       <plugin>
           <groupId>org.mybatis.generator</groupId>
           <artifactId>mybatis-generator-maven-plugin</artifactId>
           <version>1.3.5</version>
           <dependencies>
               <dependency>
                   <groupId>org.mybatis.generator</groupId>
                   <artifactId>mybatis-generator-core</artifactId>
                   <version>1.3.5</version>
               </dependency>
               <dependency>
                   <groupId>mysql</groupId>
                   <artifactId>mysql-connector-java</artifactId>
                   <version>5.1.41</version>
               </dependency>
           </dependencies>
           <executions>
               <execution>
                   <id>mybatis generator</id>
                   <phase>package</phase>
                   <goals>
                       <goal>generate</goal>
                   </goals>
               </execution>
           </executions>
           <!--  允许移动生成的文件 -->
           <configuration>
               <verbose>true</verbose>
               <!-- 允许generatot自动覆盖文件 : 开发的时候千万别这样设置 -->
               <overwrite>true</overwrite>
               <configurationFile>
                   <!--配置文件路径-->
                   src/main/resources/mybatis-generator.xml
               </configurationFile>
           </configuration>
       </plugin>
   ```

5. 添加mybatis-generator.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   
   <!DOCTYPE generatorConfiguration
   		PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
   		"http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
   <generatorConfiguration>
   
   	<context id="DB2Tables" targetRuntime="MyBatis3">
   		<jdbcConnection driverClass="com.mysql.jdbc.Driver"
   						connectionURL="jdbc:mysql://localhost:3306/limitseckill"
   						userId="root"
   						password="181917">
   		</jdbcConnection>
   		<!-- 生成DataObject类存放位置DTO的类 -->
   		<javaModelGenerator targetPackage="com.sparrow.seckill.dataobject" targetProject="src/main/java">
   			<property name="enableSubPackages" value="true" />
   			<property name="trimStrings" value="true" />
   		</javaModelGenerator>
   		<!-- 生成映射文件存放位置 -->
   		<sqlMapGenerator targetPackage="mapping"  targetProject="src/main/resources">
   			<property name="enableSubPackages" value="true" />
   		</sqlMapGenerator>
   		<!-- 生成Dao类存放位置 -->
   		<!-- 客户端代码，生成易于使用的针对Model对象和XML配置文件的代码
   			  type="ANNOTATIONDMAPPER"，生成Java Model和基于注解的Mapper 对象
   			  type="MIXEDMAPPER",生成基于注解的Java Model和相应的Mapper对象
   			  type="XMLMAPPER",生成SQLMap XML 文件和独立的Mapper接口-->
   		<javaClientGenerator type="XMLMAPPER" targetPackage="com.sparrow.seckill.dao"  targetProject="src/main/java">
   			<property name="enableSubPackages" value="true" />
   		</javaClientGenerator>
   		<table schema="limitseckill" tableName="user_info" domainObjectName="UserDO" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false"
   		>
   		</table>
   		<table schema="limitseckill" tableName="user_password" domainObjectName="UserPasswordDO" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false">
   		</table>
   	</context>
   </generatorConfiguration>
   ```

6. 运行generator,自动生成表dao,daomapper以及对应的增删改查的sql语句的XML配置文件

7. 在application.properties中配置datasource对应的四个参数(url,password,username,name(数据库名称))

   并配置datasoure使用那个数据源

   ```properties
   spring.datasource.name=limitseckill
   spring.datasource.url=jdbc:mysql://127.0.0.1:3306/limitseckill
   spring.datasource.username=root
   spring.datasource.password=181917
   
   #使用Druid数据库连接池
   spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
   spring.datasource.driver-class-name=com.mysql.jdbc.Driver
   ```

8. 在APP中添加注解使SpringBoot扫描UserDO的包

9. 使用查询语句,验证SpringBoot通过mybatis连接到数据库

   ```java
   @SpringBootApplication(scanBasePackages = {"com.sparrow.seckill"}) //定义SpringBoot引导类
   @RestController //说明是REST
   @MapperScan("com.sparrow.seckill.dao")
   public class App {
       @Autowired
       private UserDOMapper userDOMapper;
   ```

## 第三章 用户模块开发

### 3.1 使用SpringMVC方式开发用户信息

1. 开发controller层

   ```java
   @Controller("user") //Spring管理Controller
   @RequestMapping("/user") //访问格式
   public class UserController {
   	@Autowired
   	private UserService userService;
       
   	@RequestMapping("/get")  //方法标签
       @ResponseBody            //返回给前端页面
   	public UserModel getUser(@RequestParam(name="id")Integer id){
   		//调用Service层服务获取对应id的用户对象并返回给前端
   		UserModel userModel = userService.getUserById(id);
   		return userModel;
   	}
   }
   ```

2. service层

   ```java
   //接口
   public interface UserService {
   	UserModel getUserById(Integer id);//通过用户Id获取用户信息
   }
   //impl
   @Service
   public class UserServiceImpl implements UserService {
   	@Autowired
   	private UserDOMapper userDOMapper;
   
   	@Autowired
   	private UserPasswordDOMapper userPasswordDOMapper;
   /**
     * 添加一个查询方法
     *			1.在Mapping中添加方法
     *			2.在对应的XML中添加Sql语句
     */
   	@Override
   	public UserModel getUserById(Integer id) {
   		//调用UserDOMapper获取到用户dataobject
   		UserDO userDO = userDOMapper.selectByPrimaryKey(id);
   		if(userDO==null){
   			return null;
   		}
   		//查询用户对应的密码
   		UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
   		return convertFromDataObject(userDO,userPasswordDO);
   	}
   
   	public UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
   		if (userDO == null) {
   			return null;
   		}
   		UserModel userModel = new UserModel();
   		BeanUtils.copyProperties(userDO, userModel);
   		if (userPasswordDO != null) {
   			userModel.setEncrptPassward(userPasswordDO.getEncrptPassward());
   		}
   		return userModel;
   	}
   }
   
   ```

   ##### 注意: 返回给前端页面的方法一定需要添加@ResponseBody注解

3. 返回给前端页面的数据包含了一些前端不需要的数据,而且有一些数据前端不能有(比如密码),否则可能导致密码泄漏,我们此时要重新封装一个viewobject包下的UserOV,保证UI只使用了展示的参数即可.

### 3.2 定义通用的返回正确信息

问题: 不能归一化的处理json信息

定义一个有意义的错误信息

归一化ResponseBody返回信息CommonReturnType

```java
    //定义一个状态,标识data的请求结果
	private String status;
	//若status=success,则data内返回的前端需要的json数据
	//若status=fail,则data内使用通用的错误处理格式
	private Object data;

	//定义一个通用的创建方法
	public static CommonReturnType create(Object result){
		return CommonReturnType.create(result,"success");
	}

	private static CommonReturnType create(Object result, String status) {
		CommonReturnType commonReturnType = new CommonReturnType();
		commonReturnType.setData(result);
		commonReturnType.setStatus(status);
		return commonReturnType;
	}
```

### 3.3 返回通用的返回对象--返回错误信息

1. 定义通用的错误形式
2. 声明CommonError接口,getErrCode,getErrMsg,setErrMsg
3. 将Err信息取出来,public  enum  EmBusinessError 
4. BusinessException extends Exception implement CommonError
5. 并发问题????

### 3.4 通用的异常处理

SpringBoot解决方式: 定义exceptionhandler解决未被controller层吸收的exception

钩子设计思想

```java
	//定义exceptionHandler解决未被Controller层吸收的exception
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Object handlerException(HttpServletRequest request, Exception ex){
		Map<String,Object> responesData = new HashMap<>();
		if(ex instanceof BusinessException){
			BusinessException businessException = (BusinessException)ex;
			responesData.put("errCode",businessException.getErrCode());
			responesData.put("errMsg",businessException.getErrMsg());
		}else{
			responesData.put("errCode",EmBusinessError.UNKNOWN_ERROR.getErrCode());
			responesData.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrMsg());
		}
		return CommonReturnType.create(responesData,"fail");
	}

```

小结:最开始我们先把返回给前端的信息统一化处理,包装成一个CommonRetureType类,一个statue属性,一个data属性,当statue=success时将数据包装发送给前端页面,当statue=fail时,使用通用的错误信息处理

声明CommonError接口,有getErrCode,getErrMsg,setErrMsg,有两个子类,EmBusinessError(枚举异常信息),BusinessException(继承Exception),产生异常时,需要捕获,SpringBoot的功能自定义一个exceptionHander解决未被Controller层吸收的exception,最后封装成通用的信息,传给前端页面

### 3.5 用户模型管理--otp验证码获取

基础能力建设

模型能力管理

- 商品模型
- 下单模型
- 用户信息管理

用户信息管理

- opt短信验证
- opt注册用户
- 用户手机登录

注册界面:

```java
public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone){
    //需要按照一定的规则生成一个OTP验证码
    Random random = new Random();
    int randomInt = random.nextInt(99999);
    randomInt += 10000;
    Spring optCode = String.valueOf(randomInt);
    //将OTP验证码对应用户的手机好关联
    //@HttpServletRequest
    httpServletRequest.getSession().setAttribute(telphone,otpCode);
    //将OPT验证码通过短信发送给用户,省略
    System.out.Println(telphone+otpCode);
    return CommonReturnType.create(null);
}
```

### 3.6 getotp页面实现

1. 基本的注册要求

2. script,JQuery引入

3. @requestMapping参数produces,consumes

   produces: 指定返回值的类型,produces="application/json"返回值是json数据,@responseBody就是返回的json类型

   consumes: 指定处理请求的提交内容格式application/json,text/html

### 3.7 前端页面优化

1. 添加css样式

### 3.8 用户注册功能实现

1. 用户注册(register)接口  telphone otpcode name gender age(必要的)
2. 验证对应用户的手机号和对应的otpCode符合,在session中取出telphone对应的otpcode

3. 使用内库中的equals方法比较otpCode
4. 数据库属性的null: 一般都指定为不是null,但是在要求比如说只有一个telphone

5. 封装UserModel,传到服务层,将UserModel对象转成数据库表对应的对象,插入数据库

6. 修改前端页面跳转到register页面,

7. 前端效验的作用,为了提升用户体验,后端效验是不可少的,保证程序的可靠性

8. CrossOrigin的allowHeaders="*",允许跨域传输所有的hander参数,将用于使用token放入header域做session共享的跨域请求

9. 将数据插入数据库中完成

10. 问题: 验证的时候,同一个telphone可以注册多个用户,这是不允许的在数据库中,将telphone字段设置成唯一索引,再次去注册时,服务器端抛出一个DuplicateKeyException异常,这个异常是在插入数据时发生的,使用try-catch,抛出一个自定义异常(为了使客户端能收到一个有用的异常

    ```java
    try {
    	userDOMapper.insertSelective(userDO);
    }catch (DuplicateKeyException ex){
        throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号以重复注册");
    }
    ```

### 3.9 用户登录模块

1. login,前端参数(telphone,password)
2. 入参效验
3. 用户登录服务:效验用户登录是否合法:通过用户telphone手机号拿到用户信息,对比用户的password是否相同
4. 通过telphone查询用户信息(两步走: 1. 在UserDOMapper中添加selectUserByTelphone 2. 在UserDOMaping.xml中添加对应的sql语句)

### 3.10 优化效验

1. 引入vaildation

2. 创建一个validator包,ValidationImpl(对错误信息的校验)和ValidationResult(用于处理结果:是否有错,错误信息)

3. ```
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
   
   ```

   ```java
   //使用valication效验userModel的各个属性是否符合校验规则
   		ValidationResult validationResult = validation.validate(userModel);
   		if(validationResult.isHasErrors()){
   			throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,validationResult.getErrMsg());
   		}
   ```

   ```java
   private Integer id;
   	@NotBlank(message = "用户名不能为空")
   	private String name;
   	@NotNull(message = "性别不能不填写")
   	private Byte gender;
   
   	@NotNull(message = "年龄不能不填写")
   	@Min(value = 0,message = "年龄大于0岁")
   	@Max(value = 150,message = "年龄不大于150")
   
   ```

## 第四章 4 商品模块开发

### 4.1 商品创建

1. 首先创建商品的领域模型,

2. 将pom中的允许覆盖改为false

3. 将ItemDOMapper.xml中的insert的id设置为自增,通过查询可以获取id

4. 定义ItemService接口,创建商品(createItem),商品列表(listItem),商品详情(getItemById)
5. 在Controller层,调用服务层方法,并将结果返回给前端页面

### 4.2 商品列表

通过itemService查询所有的商品信息,将商品信息Model转换为VO,并将查询到的数据发送给前端

```java
	//将model转为vo
		//使用stream api将list内的itemModel转为ItemVO
		List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
			ItemVO itemVO = this.convertVOFromModel(itemModel);
			return itemVO;
		}).collect(Collectors.toList());
		return CommonReturnType.create(itemVOList);
```



### 4.3 商品详情页

Conller层

```java
	@RequestMapping(value = "/get", method = {RequestMethod.GET})
	@ResponseBody
	public CommonReturnType getItem(@RequestParam(name="id")Integer id) {
		ItemModel itemModel = itemService.getItemById(id); //调用服务层,根据商品id查询信息
		//将model转为vo
		//使用stream api将list内的itemModel转为ItemVO
		ItemVO itemVO = convertVOFromModel(itemModel);
		return CommonReturnType.create(itemVO);
	}
```

Service层

```java
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
```

## 第五章 5 交易模块

### 5.1创建交易模型

#### 订单模型

```java
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
```

在查询商品详情页面,查看商品,选择下单操作,前端请求http://localhost:8090/order/createorder,

Conller层接收到请求,核对用户登录信息,调用Service层方法,执行下单操作,注意此时生成订单的方法是加有事务注解的,我们在服务层保证数据的一致性

#### 创建订单

1. 校验下单状态,下单的商品是否存在,用户是否合法,购买数量是否正确

2. 下单减库存,支付减库存,我们采用下单减库存

3. 受影响的行数>0,说明更新库存成功;否则更新库存失败(仅通过返回值就可以判断减库存是否成功)

4. 生成订单号:订单号有16位,前8位为时间信息,年月日,中间6位为自增序列,最后两位为分库分表

5. 创建一个Sequence表,用于获取对应的自增序列

6. 解决一个全局唯一的sequence的保证,

​       在生成订单时,如果当前的订单号已经被生成,就不需要回滚操作

​       在订单号生成的方法上添加@Transcational(propagetion=Propagation.REQUIRES_NEW)

​       这个参数可以保证在事务A错误时,不会回滚在事务A中的事务B

## 第六章 秒杀模型管理

1. PromoModel: 

   ```java
   	private Integer id;
   	//秒杀活动状态 1还没开始, 2 进行中, 3 已经结束
   	private Integer status;
   	//秒杀活动名称
   	private String promoName;
   	//秒杀活动开始时间
   	private DateTime startTime;
   	//秒杀结束时间
   	//使用jodatime
   	private DateTime endTime;
   	//秒杀活动的使用商品
   	private Integer itemId;
   	//秒杀活动的商品价格
   	private BigDecimal promoItemPrice;
   ```

2. 在Service层,将PromoModel聚合到itemModel中,用来表示商品是否参与秒杀活动,若promoModel不为空,表示这个商品在秒杀会场中

   **promoModel的添加:**在查询商品详情页的时候,在服务层查询是否存在秒杀活动,若存在则将数据存到ItemModel中,并返回给前端页面

3. 下单

   ```java
   	//创建订单
   	//需要用户id,商品id,商品数量
   	// 1. 通过前端页面传过来的id,下单接口内效验id是否存在,并且活动已经开始
   	// 2. 直接在下单接口中判断对应商品是否有秒杀活动,若存在进行秒杀商品的下单
   	// 使用第一种,这样就不用每次都要查询是否有秒杀活动
   	OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer amount) throws BusinessException;
   ```

4. ```java
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
   ```

5. ```java
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
   ```

## 总结



## 补充

异常: 

1. ![1564587326773](C:\Users\zg\AppData\Roaming\Typora\typora-user-images\1564587326773.png)
2. NoClassDefFoundError发生在编译时对应的类可用，而运行时在Java的classpath路径中，对应的类不可用导致的错误。 
3. 不要在try-catch块中添加大量的代码,只加必须的,使用异常控制代码流程性能不如if/else,switch
4. 每实例化一个Exception都在堆栈中创建一个快照

2.Stream(java8特性)

```java
List<ItemModel> itemModelList = itemService.listItem();
		//将model转为vo
		//使用stream api将list内的itemModel转为ItemVO
		List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
			ItemVO itemVO = this.convertVOFromModel(itemModel);
			return itemVO;
		}).collect(Collectors.toList());
```

- 元素是特定类型的对象，形成一个队列。 Java中的Stream并不会存储元素，而是按需计算。
- **数据源** 流的来源。 可以是集合，数组，I/O channel， 产生器generator 等。
- **聚合操作** 类似SQL语句一样的操作， 比如filter, map, reduce, find, match, sorted等。

```java
//filter
List<String> strings = Arrays.asList("df","dfd","","ad");
//计算空字符串的数量
int count  = strings.stream().filter(string -> string.isEmpty()).count();
```

3.mysql中的for updata语句

由于InnoDB预设的是Row-Level-Lock,在for updata的字段为索引或主键的时候,锁住的是行锁

其他的都是整个数据表的锁,在一个事务中,for updata执行的结果对后面的语句可见

4.当请求中有对应的参数不是一定需要的时候,可以使用required=false

```java
public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
										@RequestParam(name = "amount")Integer amount,
										@RequestParam(name = "promoId",required = false)Integer promoId
										){
```

5.问题:下单减库存 和 支付减库存

6.Metronic框架

### 问题

1. 多商品,多库存,多活动模型
2. 容量问题
3. 系统水平扩展
4. 查询效率低下
5. 活动开始前页面疯狂刷新
6. 库存行锁
7. 下单操作多,速度慢
8. 浪涌流量如何解决

### 错误

1. 在用户注册模块中,将用户数据放到数据库中时,user_info中的id需要对应user_password中的user_id,步骤:

   将UserDOMapper.xml中的insertSelective后添加keyProperty="id" useGenerateKeys="true",此时就可以在插入数据之后,获取到对应的id(更新两个有关联的表,第二张表需要第一张表的id)

2. 问题:库存为什莫要单独一个表,优化减库存操作解决行锁问题,理论上销量也要单独统计

   

