# Overview

> 参考：
>
> 1. [【编程不良人】微服务小项目实战](https://www.bilibili.com/video/BV1iq4y1f7Bs?p=1)

**班级管理系统**：

该项目是学习微服务时的一个demo项目，只具备后端功能，且因为服务的功能和代码极其相似，最后两个服务没有开发完成。文档尽可能事无巨细地记录整个开发过程，方便未来查阅。



## **目录**

[0. How to run](# 0. How to run)：如何启动运行本项目

[1. Setup](# 1. Setup)：如何从0搭建项目骨架

[2. Service Development](# 2. Service Development)：服务开发细节，包括：城市服务、标签服务、班级服务、小组服务（未完成）和学生服务（未完成）

[3. commons](# 3. commons)：关于公共类

[4. Conclusion](# 4. Conclusion)：一点点🤏小总结



## 技术选型

数据库：**MySQL**

持久化：**Mybatis**

后端：**SpringBoot** + **SpringCloud**

注册中心：**Consul**

服务间通信：**openfeign**

API测试：**Postman**





# 0. How to run

1. 安装consul，并启动（运行命令）：

   ```shell
   consul agent -dev  
   ```

2. 启动`clazz-gateway`项目，访问http://localhost:8500，即可看到consul的可视化界面

3. 启动需要的服务的项目，如`clazz-city`，在consul的界面查看服务是否注册成功

4. 使用Postman测试API



# 1. Setup

创建maven项目



## 0. 分析

**基于现有业务进行服务拆分**：

1. city 服务
2. tag 服务
3. class 服务
4. group 服务
5. student 服务
6. gateway 服务（微服务项目才有）



库表设计（班级管理系统）:

	城市表 city  单表
	标签表 tag   单表
	班级表 clazz  tagId外键
	小组表 group  clazzId 外键
	学生表 student  cityId 外键  clazzid外键  groupid外键  
	关系表  学生标签关系表  student_tag  sid tid    

项目要求：“整个系统中全部使用**单表进行查询**”



> [为什么微服务一定要有网关？](https://zhuanlan.zhihu.com/p/101341556)
>
> - 服务网关 = 路由转发 + 过滤器（在服务网关中可以完成一系列的横切功能，例如权限校验、限流以及监控等）
> - 为什么需要服务网关？
>   - 代码开发不会冗余
>   - 将权限校验的逻辑写在网关的过滤器中，后端服务不需要关注权限校验的代码，所以服务的jar包中也不会引入权限校验的逻辑，不会增加jar包大小
>   - 如果想修改权限校验的逻辑，只需要修改网关中的权限校验过滤器即可，而不需要升级所有已存在的微服务



## 1.  库表入库

创建clazz-web库

name: clazz-web

character set: utf8mb4

collation: utf8mb4_bin

```mysql
/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : localhost:3306
 Source Schema         : baizhi_sys

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 31/05/2021 20:24:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_city
-- ----------------------------
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_city
-- ----------------------------
BEGIN;
INSERT INTO `city` VALUES (4, '北京');
INSERT INTO `city` VALUES (5, '南京');
INSERT INTO `city` VALUES (6, '天津');
INSERT INTO `city` VALUES (7, '杭州');
INSERT INTO `city` VALUES (8, '上海');
INSERT INTO `city` VALUES (9, '深圳');
INSERT INTO `city` VALUES (11, '福建');
INSERT INTO `city` VALUES (12, '郑州');
INSERT INTO `city` VALUES (13, '云南');
INSERT INTO `city` VALUES (15, '辽宁');
COMMIT;

-- ----------------------------
-- Table structure for t_clazz
-- ----------------------------
DROP TABLE IF EXISTS `clazz`;
CREATE TABLE `clazz` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL,
  `path` varchar(300) DEFAULT NULL,
  `tagId` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_clazz
-- ----------------------------
BEGIN;
INSERT INTO `clazz` VALUES (10, '2010班', '/20210531170642.png', '13');
INSERT INTO `clazz` VALUES (11, '2011班', '/20210531170831.png', '14');
INSERT INTO `clazz` VALUES (12, '2012班', '/20210531170842.png', '5');
INSERT INTO `clazz` VALUES (13, '2013班', '/20210531170853.png', '5');
COMMIT;

-- ----------------------------
-- Table structure for t_group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL,
  `createDate` date DEFAULT NULL,
  `clazzId` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_group
-- ----------------------------
BEGIN;
INSERT INTO `group` VALUES (5, '1组', '2021-05-31', '11');
INSERT INTO `group` VALUES (6, '2组', '2021-05-31', '10');
INSERT INTO `group` VALUES (7, '1组', '2021-05-31', '10');
INSERT INTO `group` VALUES (8, '2组', '2021-05-31', '11');
INSERT INTO `group` VALUES (9, '3组', '2021-05-31', '11');
COMMIT;

-- ----------------------------
-- Table structure for t_student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL,
  `age` int(3) DEFAULT NULL,
  `qq` varchar(20) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `bir` date DEFAULT NULL,
  `starts` varchar(20) DEFAULT NULL,
  `attr` varchar(10) DEFAULT NULL,
  `mark` varchar(600) DEFAULT NULL,
  `clazzId` varchar(40) DEFAULT NULL,
  `groupId` varchar(40) DEFAULT NULL,
  `cityId` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_student
-- ----------------------------
BEGIN;
INSERT INTO `student` VALUES (11, 'yannan chen', 9, '344355', '01010533789', '2012-12-12', '射手座', '龙', '', '11', '5', '4');
COMMIT;

-- ----------------------------
-- Table structure for t_student_tag
-- ----------------------------
DROP TABLE IF EXISTS `student_tag`;
CREATE TABLE `student_tag` (
  `sid` int(40) NOT NULL,
  `tid` int(40) NOT NULL,
  PRIMARY KEY (`sid`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_student_tag
-- ----------------------------
BEGIN;
INSERT INTO `student_tag` VALUES (11, 16);
INSERT INTO `student_tag` VALUES (11, 17);
INSERT INTO `student_tag` VALUES (11, 18);
COMMIT;

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL,
  `type` varchar(8) DEFAULT NULL,
  `createDate` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_tag
-- ----------------------------
BEGIN;
INSERT INTO `tag` VALUES (3, '帅哥', '学生', '2021-05-24');
INSERT INTO `tag` VALUES (4, '美女', '学生', '2021-05-24');
INSERT INTO `tag` VALUES (5, '探知源码', '班级', '2021-05-24');
INSERT INTO `tag` VALUES (6, '学渣', '学生', '2021-05-24');
INSERT INTO `tag` VALUES (13, '人数最多', '班级', '2021-05-31');
INSERT INTO `tag` VALUES (14, '坚持不懈', '班级', '2021-05-31');
INSERT INTO `tag` VALUES (15, '勤学好问', '班级', '2021-05-31');
INSERT INTO `tag` VALUES (16, '聪明', '学生', '2021-05-31');
INSERT INTO `tag` VALUES (17, '浪漫', '学生', '2021-05-31');
INSERT INTO `tag` VALUES (18, '油腻', '学生', '2021-05-31');
INSERT INTO `tag` VALUES (19, '懒惰', '学生', '2021-05-31');
COMMIT;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------

-- ----------------------------
-- Records of t_user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (6, 'xiao', '123', NULL, '2021-05-31', 'admin');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
```



## 2. 项目架子搭建

- clazz-web-parent  父项目  不写代码（src删除），只维护依赖
  - clazz-commons  公共服务  维护公共依赖 公共代码 
  - clazz-city     城市服务
  - clazz-clazz    班级服务
  - clazz-group    小组服务
  - clazz-tag      标签服务
  - clazz-student  学生服务
  - clazz-gateway   网关服务

插件：

1. Log Support: [Log Support2日志插件(springboot)](https://blog.csdn.net/weixin_46273997/article/details/119148991)

   <img src="https://i.imgur.com/zeHK3yd.png" alt="zeHK3yd" style="zoom:50%;" />

2. Save Actions: [IntelliJ Save Action](https://blog.csdn.net/hustzw07/article/details/82824713)：在Team开发项目中，都是多人维护一个项目。因此，保持良好的代码规范与风格很重要。IntelliJ 默认是自动保存的，因此很多时候修改后就出现：代码没有格式化、存在无用的import。

   ![LJrCnV3](https://i.imgur.com/LJrCnV3.png)



**pom.xml**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>clazz-web-parent</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!--引入springboot父项目版本-->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.5.RELEASE</version>
    </parent>

    <!--自定义属性-->
    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <spring.cloud.version>Hoxton.SR6</spring.cloud.version>
        <mybatis.springboot.version>2.2.0</mybatis.springboot.version>
        <mysql.version>5.1.40</mysql.version>
        <druid.version>1.2.6</druid.version>
    </properties>

    <!--  维护springcloud依赖  -->
    <!--书写在这个标签里面依赖 只维护版本号 不会实际引入到项目中-->
    <dependencyManagement>
        <dependencies>
            <!--全局维护使用那个springcloud 版本-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring.cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>


            <!--mybatis-->
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>${mybatis.springboot.version}</version>
            </dependency>

            <!--mysql-->
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <version>${mysql.version}</version>
            </dependency>

            <!--druid-->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid</artifactId>
                <version>${druid.version}</version>
            </dependency>

            <!--redis-->
            <!--es-->
            <!--mq-->
        </dependencies>
    </dependencyManagement>

</project>
```

注意``<dependencyManagement>``只维护版本，不会引入依赖。



项目结构：

<img src="https://i.imgur.com/2CpzSbR.png" alt="2CpzSbR" style="zoom:50%;" />



## 3. 处理业务代码微服务

除了commons和gateway，其余服务都需要：

- 引入springboot依赖
- 引入consul client依赖
- 引入consul actuator依赖

> 作者这里使用了`consul`，记得下载安装开启

给每个子项目引入下面依赖：

```xml
<!--web-->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- 这个包是用做健康度监控的-->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!--引入consul client依赖-->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```



## 4. 每一个微服务

- 指定服务端口号
- 指定服务名称
- 注册consul server上面

```properties
# 指定微服务基本信息
# 每个服务有自己的服务端口号和服务名称
server.port=8081
spring.application.name=CITIES
# 注册consul server配置
spring.cloud.consul.host=localhost
spring.cloud.consul.port=8500
```



## 5. 开发入口类

给每个服务开发入口类

```java
@SpringBootApplication
@EnableDiscoveryClient  // 这个注解可以省略不写
public class CityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CityApplication.class, args);
    }
}
```

然后运行所有服务，检查是否所有服务都可以正常注册：

IDEA:

![79WypZe](https://i.imgur.com/79WypZe.png)



**Consul**:

安装consul完成后在terminal中打开:

```shell
consul agent -dev
```

![McHF51i](https://i.imgur.com/McHF51i.png)





## 6. 网关服务

先给每个服务添加一个demo controller（不写任何业务代码，只是测试/demo服务可以正常运行）

![udPuppX](https://i.imgur.com/udPuppX.png)



运行成功后就可以访问服务了，如：localhost:8081/demo（访问City服务）

![gejGpyi](https://i.imgur.com/gejGpyi.png)

但是我们后续的服务肯定不能直接通过url来访问，肯定需要通过网关来调用。



引入依赖:

```xml
<!-- 引入gateway网关依赖 -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<!-- 这个包是用做健康度监控的-->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!--引入consul client依赖-->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```



创建网关入口类

```java
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```



### consul

terminal启动命令

```bash
consul agent -dev  
```



启动，访问：http://localhost:8500，发现网关服务

![Pnom559](https://i.imgur.com/Pnom559.png)



配置网关服务``application.yml``

```yaml
#指定网关端口
server:
  port: 9999
spring:
  application:
    name: GATEWAY
  cloud:
    consul:
      host: localhost
      port: 8500

    gateway:
      routes: # 配置路由规则
        - id: city_router #配置城市路由
          uri: lb://CITIES
          predicates:
            - Path=/clazz-web/city/demos/**,/clazz-web/city/cities/**
          filters:
            - StripPrefix=2

        - id: tag_router #配置标签路由
          uri: lb://TAGS
          predicates:
            - Path=/clazz-web/tag/**
          filters:
            - StripPrefix=2

        - id: group_router #配置小组路由
          uri: lb://GROUPS
          predicates:
            - Path=/clazz-web/group/**
          filters:
            - StripPrefix=2


        - id: student_router #配置学生路由
          uri: lb://STUDENTS
          predicates:
            - Path=/clazz-web/student/**
          filters:
            - StripPrefix=2

        - id: clazz_router #配置班级路由
          uri: lb://CLAZZS
          predicates:
            - Path=/clazz-web/clazz/**
          filters:
            - StripPrefix=2


```

注意：gateway-routes-predicates下面不能用空格，不然会报错：

<img src="https://i.imgur.com/h8bQRkx.png" alt="h8bQRkx" style="zoom:47%;" />



然后就可以通过网关来访问服务，如：localhost:9999/clazz-web/city/demo

![VR0EHYz](https://i.imgur.com/VR0EHYz.png)



至此，微服务的环境就搭建好了，之后就可以开始针对微服务的每个服务/业务进行开发。





# 2. Service Development

## 1. 城市服务

### 1. 引入依赖

```xml
<!--mybatis-->
<dependency>
  <groupId>org.mybatis.spring.boot</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version>2.2.1</version>
</dependency>

<!--mysql-->
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>5.1.47</version>
</dependency>

<!--druid-->
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>druid</artifactId>
  <version>1.2.6</version>
</dependency>
```



### 2. 配置文件

- 创建数据源
- 整合mybatis
- 配置日志信息

```properties
# 指定微服务基本信息
server.port=8081
spring.application.name=CITIES
# 注册consul server配置
spring.cloud.consul.host=localhost
spring.cloud.consul.port=8500

# 创建数据源
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/clazz-web?characterEncoding=UTF-8&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
# 整合mybatis
mybatis.mapper-locations=classpath:com/zzw/mapper/*.xml
mybatis.type-aliases-package=com.zzw.entity
# 配置日志信息
logging.level.com.zzw=debug
```



### 3. API管理

**使用Postman**

创建一个`GET`请求并测试接口

![NekynK](https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/NekynK.png)



因为每次都写url的前缀太麻烦，可以使用postman的环境：

![DeqdJ9](https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/DeqdJ9.png)

之后就可以直接在URL里使用环境：

``{{Base_URL}}city/cities``



### 4. 三层开发

#### Entity

```java
public class City implements Serializable {
    private static final long serialVersionUID = 267321629878486813L;
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
  
    public void setName(String name) {
        this.name = name;
    }
}
```



#### DAO

```java
@Mapper //在工厂创建cityDao
public interface CityDao {
    //添加城市
    int insert(City city);

    //查询所有
    List<City> queryAll();

    //城市信息
    City queryById(Integer id);

}
```



#### DAO XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zzw.dao.CityDao">

    <resultMap type="com.zzw.entity.City" id="CityMap">
        <result property="id" column="id" jdbcType="INTEGER"/>
        <result property="name" column="name" jdbcType="VARCHAR"/>
    </resultMap>

    <!--查询所有-->
    <select id="queryAll" resultMap="CityMap">
        select id,
               name
        from city
    </select>

    <!--添加城市-->
    <insert id="insert" keyProperty="id" useGeneratedKeys="true">
        insert into city(name)
        values (#{name})
    </insert>


    <!--查询城市信息-->
    <select id="queryById" resultMap="CityMap">
        select id,
               name
        from city
        where id = #{id}
    </select>
</mapper>


```



#### Service

```java
public interface CityService {
    //城市列表
    List<City> queryAll();

    //保存城市
    City insert(City city);

    //城市信息
    City queryById(Integer id);
}
```



#### ServiceImpl

```java
@Service("cityService")
public class CityServiceImpl implements CityService {

    private CityDao cityDao;

    @Autowired
    public CityServiceImpl(CityDao cityDao) {
        this.cityDao = cityDao;
    }

    @Override
    public List<City> queryAll() {
        return this.cityDao.queryAll();
    }

    @Override
    public City insert(City city) {
        this.cityDao.insert(city); //myabtis 插入操作之后
        return city; //city id  name
    }

    @Override
    public City queryById(Integer id) {
        return cityDao.queryById(id);
    }
}
```

注意上面使用的是构造器注入，和直接把`@Autowired`写在参数上没有区别



#### Controller

```java
@RestController
@RequestMapping("/cities")
public class CityController {
  
    //使用cityService
    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }


    //城市列表
    @GetMapping
    public List<City> cities() {
        return cityService.queryAll();
    }

    //添加城市
  	// 注意前端把城市信息通过JSON格式传递给后端
  	// @RequestBody可以将JSON格式解析成一个Java object，即City
    @PostMapping
    public City create(@RequestBody City city) {
        return cityService.insert(city);
    }

    //城市信息
  	// 这个方法是日后服务间通信用的
    @GetMapping("{id}")
    public City city(@PathVariable("id") Integer id) {
        return cityService.queryById(id);
    }
}
```



**注入依赖**

- 上面同样使用了构造器注入依赖，并且给service添加了`final`，这样service就只能被注入一次。（关于构造器注入和其他注入方式的区别）



#### 添加城市

- 添加城市功能：前端传给我们是的JSON格式，后端使用`@RequestBody`可以自动将JSON里的值赋给`City`对象。

  - 关于`@RequestBody`的使用，（[链接](https://www.baeldung.com/spring-request-response-body)）

  - > Simply put, **the `@RequestBody` annotation maps the `HttpRequest` body to a transfer or domain object, enabling automatic deserialization** of the inbound `HttpRequest` body onto a Java object.
    >
    > Spring automatically deserializes the JSON into a Java type, assuming an appropriate one is specified.
    >
    > By default, **the type we annotate with the `@RequestBody` annotation must correspond to the JSON sent from our client-side controller:**

  - ```java
    @PostMapping("/request")
    public ResponseEntity postController(
      @RequestBody LoginForm loginForm) {
     
        exampleService.fakeAuthenticate(loginForm);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    ```



- 添加城市后需要向前端返回新插入**城市在数据库中的ID**，这里我们使用Mybatis:

  - ```xml
    <!--添加城市-->
    <insert id="insert" keyProperty="id" useGeneratedKeys="true">
      insert into city(name)
      values (#{name})
    </insert>
    ```

  - 由于我们使用了``useGeneratedKeys="true"``来自动生成ID，而这个新的自动生成的ID又可以通过`keyProperty="id"`方式，把它放到传给Mybatis参数的对象的特定属性，即City对象的id属性。换言之，没有id的一个City对象进到数据库，给他加上了一个id，又被传回去了。

  - **CityServiceImpl.java**

    ```java
    @Override
    public City insert(City city) {
      this.cityDao.insert(city); //myabtis 插入操作之后
      return city; //city id  name
    }
    ```

  - [Mybatis 参数 useGeneratedKeys ，keyColumn，keyProperty作用和用法](https://blog.csdn.net/qq_19007335/article/details/88627366?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7EHighlightScore-1.queryctrv2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7EHighlightScore-1.queryctrv2&utm_relevant_index=2)



- 使用Postman测试：![O6WpJ5](https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/O6WpJ5.png)



#### 删除

略...



## 2. 标签服务

流程与**城市服务开发**基本一样：

- 导入依赖（Mybatis，MySQL，Druid）
- application.properties配置
  - 整合mybatis配置（和之前一模一样）
- 把controller dao entity service mapper都创建好（拷贝粘贴）
- 启动项目，使用Postman测试接口



注意数据库中Tag的创建时间叫`createDate`，Tag实体类中叫`createdate`，而在最终前端输出的JSON中叫`created_at`，这其中分别用到了Mybatis的`resultMap`和Spring的注解`@JsonProperty`：

- TagDAO.xml中将mysql中的`createDate`映射到java中Tag实体类的`createdate`属性

  ```xml
      <resultMap type="com.zzw.entity.Tag" id="TagMap">
          <result property="id" column="id" jdbcType="INTEGER"/>
          <result property="name" column="name" jdbcType="VARCHAR"/>
          <result property="type" column="type" jdbcType="VARCHAR"/>
          <result property="createdate" column="createDate" jdbcType="TIMESTAMP"/>
      </resultMap>
  
  
      <!--查询所有-->
      <select id="queryAll" resultMap="TagMap">
          select id,
                 name,
                 type,
                 createDate
          from tag
      </select>
  ```

- Tag实体类中使用`@JsonProperty`，把当前属性在转换JSON属性时，把当前属性名转换为特定value属性；此外，这个注解在**反序列化**时同样生效。所以在这个例子中，添加了`@JsonProperty("created_at")`后，后端的`createdate`属性名会自动变成前端（JSON）的`created_at`，而前端（JSON）的`created_at`属性名也会在后端自动被反序列化为`createdate`。

  ```java
  @JsonProperty("created_at")
  private Date createdate;
  ```




**删除标签**

这个功能city服务中没有，tag服务中有，也很简单，后端使用`@DeleteMapping`，然后参数是需要删除的tag的id，前面使用`@PathVariable`，意味着id由前端在url中传过来，SpringBoot会自动解析url并获取id参数

```java
//删除标签
@DeleteMapping("{id}")
public void delete(@PathVariable("id") Integer id) {
  tagService.deleteById(id);
}
```



## 3. 班级服务

流程与**城市服务开发**基本一致：

- 导入依赖（Mybatis，MySQL，Druid）
- application.properties配置
  - 整合mybatis配置（和之前一模一样）
- 把controller dao entity service mapper都创建好（拷贝粘贴）（ClazzServiceImpl稍有不同，涉及到**服务间通信**）
- 启动项目，使用Postman测试接口



### 查询班级

这里以查询所有班级为例，假设我们需要查询所有班级的信息，包括：班级id、name、path(头像)和tagId，而我们前端还额外需要我们返回标签的信息（标签id、name和type）。原本在**单体系统**中我们的做法是在数据库通过联表查询来获取标签信息；现在因为我们开发的是微服务项目，需要跨服务来调用服务，所以我们在初期开发时，可以先写完一部分代码，进行单表测试，如通过单表测试，再跨服务（可以用TODO来标记）：

```java
@Service
@Transactional
public class ClazzServiceImpl implements ClazzService {

    private final ClazzDAO clazzDAO;

    @Autowired
    public ClazzServiceImpl(ClazzDAO clazzDAO) {
        this.clazzDAO = clazzDAO;
    }

    @Override
    public List<Clazz> queryAll() {
        List<Clazz> clazzes = clazzDAO.queryAll();
        // TODO 实现：遍历班级 根据班级标签id 查询当前班级标签对象，思路：根据班级标签id 跨服务调用"标签服务"
        return clazzes;
    }
}
```

在`ClazzController`别写一个GetMapping测试一下能否通过**单表查询**的测试，使用Postman来测试接口->成功。进入下一步，跨服务调用**标签服务**的接口

**TagController**

创建一个根据id查询标签的方法

```java
//标签信息
@GetMapping("{id}")
public Tag tag(@PathVariable("id") Integer id) {
  	return tagService.queryById(id);
}
```



#### 服务间通信

##### 依赖

班级服务中引入**openfeign**依赖

```xml
<!--openfeign-->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```



##### 开启调用

在班级的入口类开启feign的调用

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ClazzApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClazzApplication.class, args);
    }
}
```



##### 创建TagClient

在与dao、service、controller平级的目录下，创建/feign目录，创建一个feign client接口

**TagClient**

```java
@FeignClient("TAGS")	// 服务名
public interface TagClient {
    // 根据标签id，查询标签信息
    @GetMapping("/tags/{id}")
    Tag tag(@PathVariable("id") Integer id);
}
```

- 注意`@GetMapping`，我们要调用的是TAGS服务下的`/tags/{id}`的这个服务，所以我们也需要在feign client中也加上`/tags`，即通过``GET``调用`TAGS`服务的下的`/tags/{id}`。
- 注意开发到这里，班级项目中是还没有Tag类的，这里为了快速演示，先忽略项目**公共类**的事情，直接把班级服务中的``Tag``复制到班级中的entity目录里。



##### ClazzServiceImpl

注入我们前面创建的`TagClient`（同样是构造器注入）；因为我们拿到了``tagClient``，可以直接调用他的`tag`方法来获取对应Tag

```java
@Service
@Transactional
public class ClazzServiceImpl implements ClazzService {

    private final ClazzDAO clazzDAO;

    private final TagClient tagClient;

    @Autowired
    public ClazzServiceImpl(ClazzDAO clazzDAO, TagClient tagClient) {
        this.clazzDAO = clazzDAO;
        this.tagClient = tagClient;
    }

    @Override
    public List<Clazz> queryAll() {
        List<Clazz> clazzes = clazzDAO.queryAll();
        clazzes.forEach(clazz -> {
            Integer tagId = clazz.getTagId();
            Tag tag = tagClient.tag(tagId);	// 跨服务调用
            clazz.setTag(tag);
        });
        return clazzes;
    }
}

```



##### 测试

GET: http://localhost:9999/clazz-web/clazz/clazzs

返回：

```json
[
    {
        "id": 10,
        "name": "2010班",
        "path": "/20210531170642.png",
        "tagId": 13,
        "tag": {
            "id": 13,
            "name": "人数最多",
            "type": "班级",
            "created_at": "2021-05-30T23:00:00.000+0000"
        }
    },
    {
        "id": 11,
        "name": "2011班",
        "path": "/20210531170831.png",
        "tagId": 14,
        "tag": {
            "id": 14,
            "name": "坚持不懈",
            "type": "班级",
            "created_at": "2021-05-30T23:00:00.000+0000"
        }
    },
    {
        "id": 12,
        "name": "2012班",
        "path": "/20210531170842.png",
        "tagId": 5,
        "tag": {
            "id": 5,
            "name": "探知源码",
            "type": "班级",
            "created_at": "2021-05-23T23:00:00.000+0000"
        }
    },
    {
        "id": 13,
        "name": "2013班",
        "path": "/20210531170853.png",
        "tagId": 5,
        "tag": {
            "id": 5,
            "name": "探知源码",
            "type": "班级",
            "created_at": "2021-05-23T23:00:00.000+0000"
        }
    }
]
```

成功



### 添加班级

#### 接口信息

> Path: /clazzs
>
> Method: POST
>
> Headers:
>
> - Content-Type: multipart/form-data（带有文件上传的表单提交）
>
> Body:
>
> - name - text - 班级名称
> - logo - file - 班级logo文件
> - tagId - text - 班级标签id

注意接口中规定上传是以`POST`方式，并且是`multipart/form-data（带有文件上传的表单提交）`格式，所以后端在接收时应当以**接收表单**的方式接收参数



#### 日志

注意：在参数复杂的时候需要小心，建议使用日志记录参数

```java
// 添加班级
@PostMapping
public Clazz create(String name, MultipartFile logo, Integer tagId) {
    log.debug("班级名称: {}", name);	// 大括号代表占位
    log.debug("班级log名称: {}", logo.getOriginalFilename());
    log.debug("标签id: {}", tagId);
    return null;
}
```

**测试**

使用Postman测试，注意请求的body要选**form-data**

<img src="https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/aHjwGD.png" alt="aHjwGD" style="zoom:40%;" />

**log.debug**

```java
022-02-16 09:18:44.714 DEBUG 26459 --- [nio-8082-exec-2] com.zzw.controller.ClazzController       : 班级名称: 2022班
2022-02-16 09:18:44.714 DEBUG 26459 --- [nio-8082-exec-2] com.zzw.controller.ClazzController       : 班级log名称: Java.png
2022-02-16 09:18:44.714 DEBUG 26459 --- [nio-8082-exec-2] com.zzw.controller.ClazzController       : 标签id: 5
```



#### 文件上传

在当前项目（clazz）的根目录下创建`files`文件夹：

<img src="https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/hrXrVa.png" alt="hrXrVa" style="zoom: 50%;" />



在当前项目的配置文件中定义文件夹path，注意使用绝对路径

```properties
# 定义文件上传路径，注意下面的变量名是自己随意取的，不是规定的
upload.dir=/Users/ryan/Projects/micro-service/clazz-web-parent/clazz-clazz/files
```



注入`realpath`

```java
@Value("${upload.dir}")
private String realpath;
```



保存文件

```java
// 处理文件上传：
//  1. 修改文件名称（改用UUID），避免文件重名问题
String newFileName = UUID.randomUUID().toString().replace("-", "") + "."
  + FilenameUtils.getExtension(logo.getOriginalFilename());
//  2. 保存文件
logo.transferTo(new File(realpath, newFileName));
```



#### 保存班级信息

```java
// 保存班级信息
Clazz clazz = new Clazz();
clazz.setName(name);
clazz.setPath(newFileName);
clazz.setTagId(tagId);
// tag无需赋值
return clazzService.insert(clazz);
```

这里的逻辑和`tag`一样，把要插入的``clazz``对象传入数据库后，自动为`clazz`添加id属性并，service层再把`clazz`返回给前端。



测试结果：postman成功请求并收到json格式数据（前端可以接收到JSON格式数据是因为controller上用了`RestController`注解）；项目中也保存了上传的文件

```json
{
    "id": 14,
    "name": "2022班",
    "path": "3119d40e01d7425894a2eb9fe1bec82f.png",
    "tagId": 5,
    "tag": null
}
```



在返回的JSON数据中，可以注意到`"tag": null`，这条数据是用于**班级查询**，在**班级创建**时返回给前端并无意义（暂时），想要去除这条数据可以在实体类中使用注解`@JsonInclude`：

- 由jackson提供
- 修饰范围：用在类上
- 作用：指定类中哪些属性在转换为json时存在

用法：

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Clazz implements Serializable {
    private static final long serialVersionUID = -98210032670644857L;
    private Integer id;
    private String name;
  	...
}
```



### 删除班级

略...



## 4. 小组服务

基本和前面的开发流程一样

唯一难点：查询小组的时候需要跨服务查询班级信息（和班级服务的查询功能一样）

<img src="https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/EDvAkK.png" alt="EDvAkK" style="zoom:50%;" />



### 添加小组

<img src="https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/qOGDew.png" alt="qOGDew" style="zoom:50%;" />

前端传输格式：`Content-Type:application/json`，后端可以使用`@RequestBody`直接接收并构建对象



## 5. 学生服务

学生服务不难，但是复杂，需要查询的信息：

<img src="https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/FErUEJ.png" alt="FErUEJ" style="zoom:50%;" />



因为信息很多，但是`Student`类只保存基本信息，没有`clazz`, `city`, `tags`这些信息，不完整。

在实际项目中应该除了`/entity`包之外，还应该有一个`/dto`包。

**DTO**：数据传输对象，主要用于后端与前端的数据包裹

![8nfbKF](https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/8nfbKF.png)



跨服务调用查询信息（`Tag`除外）

![qVweDb](https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/qVweDb.png)



`Tag`需要拿学生id去`student_tag`查询：

![F2KAXy](https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/F2KAXy.png)



### 查

**StudentServiceImpl**：

```java
@Override
public List<StudentResponseDTO> queryAll() {
    //1.查询学生列表
    List<Student> students = studentDao.queryAll();
    //2.将students转为studentResponseDTO的list
    List<StudentResponseDTO> studentResponseDTOS = new ArrayList<>();
    students.forEach(student -> {
        //3.属性复制
        StudentResponseDTO studentResponseDTO = new StudentResponseDTO();
        BeanUtils.copyProperties(student, studentResponseDTO);
        //TODO 处理班级 小组  城市    标签信息  //openfeign 调用服务过程如果服务执行超过1s openfegin报错
        studentResponseDTO.setClazz(clazzClient.clazz(student.getClazzId()));
        studentResponseDTO.setCity(cityClient.city(student.getCityId()));
        studentResponseDTO.setGroup(groupClient.group(student.getGroupId()));

        //根据学生id获取学生标签id集合
        List<Integer> tagIds = studentTagDao.queryByStudentId(student.getId());
        tagIds.forEach(id -> {
          	studentResponseDTO.getTags().add(tagClient.tag(id));
        });
        //4.放入集合
        studentResponseDTOS.add(studentResponseDTO);
    });
    return studentResponseDTOS;
}
```



### 增

**StudentServiceImpl**：

```java
@Override
public StudentResponseDTO insert(StudentRequestDTO studentRequestDTO) {
    //1.转为学生对象
    Student student = new Student();
    BeanUtils.copyProperties(studentRequestDTO, student);
    //2.设置学生(年龄 属相 星座)业务属性
    student.setAge(DateUtil.getAge(student.getBir()));//年龄
    student.setAttr(DateUtil.getYear(student.getBir()));//属相
    student.setStarts(DateUtil.getConstellation(student.getBir()));//星座
    //3.保存学生
    studentDao.insert(student);  //student 存在自己id
    List<Integer> tagIds = studentRequestDTO.getTagIds();
    if (!ObjectUtils.isEmpty(tagIds)) {
      	//4.保存学生标签关系
      	tagIds.forEach(tagId ->
                     	studentTagDao.insert(new StudentTag(student.getId(), tagId))
                    	);
    }
    StudentResponseDTO studentResponseDTO = new StudentResponseDTO();
    BeanUtils.copyProperties(student, studentResponseDTO);
    //TODO 处理学生班级 小组  就业城市 标签等信息
    studentResponseDTO.setClazz(clazzClient.clazz(student.getClazzId()));
    studentResponseDTO.setCity(cityClient.city(student.getCityId()));
    studentResponseDTO.setGroup(groupClient.group(student.getGroupId()));
    //根据学生id获取学生标签id集合
    tagIds.forEach(id -> {
      	studentResponseDTO.getTags().add(tagClient.tag(id));
    });
    return studentResponseDTO;
}
```



# 3. commons

- 可以放1. 实体类 2. 工具类 3. 公共依赖 
- commons项目不是一个服务，没有入口类，不需要启动

## 1. 实体类

实体类可以放入commons，然后其他项目的`/entity`都可以删除，需要在其他项目中引入commons：

```xml
<!--引入自己的公共commons依赖-->
<dependency>
    <groupId>org.example</groupId>
    <artifactId>clazz-commons</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```



## 2. 工具类

存放公共的方法...utils...工具类...



## 3. 依赖

在开发演示过程中，每个项目都各自引入了自己需要的依赖，但是大多项目的依赖都是一样的：mysql, openfeign等，这些公共的依赖就可以放入commons项目的`pom.xml`

```xml
<!--web-->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!--openfeign-->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>


<!-- 这个包是用做健康度监控的-->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!--引入consul client依赖-->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>

<!--mybatis-->
<dependency>
  <groupId>org.mybatis.spring.boot</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
</dependency>

<!--mysql-->
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
</dependency>

<!--druid-->
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>druid</artifactId>
</dependency>

```

修改后刷新发现，每个项目的**Dependencies**，只有commons，commons下面才是原本的依赖

<img src="https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/5ec2cX.png" alt="5ec2cX" style="zoom:50%;" />



关于网关gateway的依赖，两种解决方案：

1. 网关保留自己的依赖
2. 全局统一，所有依赖放到commons。



方法2存在的问题：

1. 网关也包含了mysql web那一套依赖
2. 网关和commons里的web依赖不融合



解决方法：

使用`<exclusion></exclusion>`排除不需要的依赖：

<img src="https://cdn.jsdelivr.net/gh/zewei94yomi/ImageLoader@master/uPic/reCyjv.png" alt="reCyjv" style="zoom:33%;" />





# 4. Conclusion

## Maven聚合的项目clean

- 在父项目上``clean``，所有子项目的target都会被删除
- 在父项目上``package``，会把所有子项目都打包（单独的jar包）
  - 注意，想要打包运行，每个项目都必须引入``springboot``的插件



## 版本管理

关于依赖版本，父项目是管理版本，所以子项目或者commons就可以不把公共依赖的版本写上，而是让父项目管理，这样做的好处是未来想要更换依赖版本，只需要在父项目中更换版本，子项目中的依赖版本自动更换。但是等项目庞大，依赖变多，`<dependencyManagement>`中会有大段xml代码，不便阅读，所以可以把版本号放到`<properties>`中集中管理：

```xml
  <!--自定义属性-->
<properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <spring.cloud.version>Hoxton.SR6</spring.cloud.version>
    <mybatis.springboot.version>2.2.0</mybatis.springboot.version>
    <mysql.version>5.1.40</mysql.version>
    <druid.version>1.2.6</druid.version>
</properties>

<!--书写在这个标签里面依赖 只维护版本号 不会实际引入到项目中-->
<dependencyManagement>
    <dependencies>
        <!--全局维护使用那个springcloud 版本-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring.cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <!--mybatis-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis.springboot.version}</version>
        </dependency>

        <!--mysql-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>

        <!--druid-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>${druid.version}</version>
        </dependency>
      
      <!--redis-->
      <!--es-->
      <!--mq-->
    </dependencies>
</dependencyManagement>
```



