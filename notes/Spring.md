## IOC
IOC（Inversion Of Control）：控制反转

* Spring Core最核心的部分。它不能说是一种技术，而是一种思想
* 需要先了解依赖注入DI（Dependency Inversion）

<u>**依赖注入的含义：**</u>
把底层类作为参数，传递给上层类，实现上层对下层的控制。
（造行李箱的例子，用的就是通过构造方法方式注入。还有别的方式，下面介绍）

依赖注入的方式：

* Setter
* Interface，实现特定接口
* Constructor，构造函数
* Annotation，注解。例如:Autowired


<u>**依赖倒置原则，IOC，DI，IOC容器之间的关系：**</u>
依赖倒置原则主要是说上层不应该依赖底层模块，正式因为这个原则的指导，才出现了控制反转IOC的思路，实现这个思路离不开DI依赖注入的支撑。spring框架基于IOC才提出的容器的概念，对于IOC来说，最重要的就是容器。容器管理着bean的生命周期，控制着bean的依赖注入。

IOC容器的优势：

* 避免在各处使用New来创建类，并且可以做到统一维护
* 创建实例的时候不需要了解其中的细节

* * *

### spring的IOC容器

spring启动时，去读取应用程序提供的bean配置信息，并在spring容器中生成一份响应的bean配置注册表，根据这张配置表去实例化bean，装配好bean之间的依赖关系，为上层提供准备就绪的运行环境。spring提供一个配置文件，描述bean和bean之间的依赖关系，根据Java语言的反射功能来实例化bean，并建立bean之间的依赖关系。

<u>**spring IOC支持的功能**</u>

1. 依赖注入
2. 依赖检查
3. 自动装配
4. 支持集合
5. 指定初始化方法和销毁方法
6. 支持回调方法

最核心的就是 依赖注入 和 自动装配。

<u>**Spring IOC容器的核心接口**</u>

* BeanFactory
* ApplicationContexr

为了进一步深入分析上面这两个，我们先来弄清楚spring里面的几个核心的接口或者类

一.BeanDefinition

* 主要用来描述Bean的定义。

二.BeanDefinitionRegistry

* 提供向IOC容器注册BeanDefinition对象的方法。

三.BeanFactory：spring框架最核心的接口

* 提供了IOC的配置机制
* 包含各种Bean的定义，便于实例化Bean
* 建立Bean之间的联系
* Bean生命周期的控制

在spring IOC中，bean默认都是以单例来存在的。去BeanFactory源码中也可以看到方法。

* * *

我们使用IOC的时候，大部分都是ApplicationConntext的实现类。BeanFactory和ApplicationConntext的关系如下：

* BeanFactory是spring框架的基础设施，面向spring本身；而ApplicationConntext是面向使用spring框架的开发者。
* 如果将spring容器比作一辆汽车，那BeanFactory就相当于汽车的发动机，ApplicationConntext相当于一辆完整的汽车，不但有发动机，还有离合器，变速器，底盘，，等等组件。

ApplicationConntext的功能（继承了如下多个接口，源码可见）

* BeanFactory，能够管理，装配Bean
* ResourcePatternResolver接口，能够加载资源文件
* MessageSource，能够实现国际化等功能
* ApplicationEventPublisher，能够注册监听器，实现监听机制


* * *

下面以spring boot的方式举例子，来看看bean是如何装载到IOC容器中的。

springboot的默认启动类的run方法里面，通过源码一步一步点进去，有个createApplicationContext方法，再点进去，可以看到，它用的也是ApplicationContext的一个子类：AnnotationConfigServletWebServerApplicationContext

我们创建两个类，一个作为配置类，一个就是实体类Person。如下：
```
/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-07 17:19
 * @description     spring练习
 */
@Configuration
public class ApplicationConfig {

    @Bean(name = "person")
    public Person initPerson() {
        Person user = new Person();
        user.setId(1L);
        user.setName("Jack");
        return user;
    }
}

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-07 17:18
 * @description
 */
@Data
public class Person {

    private Long id;

    private String name;

}
```
@Configuration注解之后，它就会注入到容器里面。spring容器根据这些配置生成IOC容器，去装配对应的bean。
@Bean是将方法返回的实体类装配到IOC容器当中，name属性就是bean的名称，如果不指定的话，默认为方法名。
然后我们就可以通过AnnotationConfigServletWebServerApplicationContext来获取对象实例了。代码如下：
```
public class DemoApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
        Person bean = ctx.getBean(Person.class);
        System.out.println("Name is " + bean.getName());
    }

}
```
run方法的返回值是ConfigurableApplicationContext，它也是继承自ApplicationContext，所以这里我们直接可以用ApplicationContext来接收，拿到了ApplicationContext实例之后，再通过它的getBean方法，就可以获取到我们的person实例了。

上面这种显式装配bean的方法简单粗暴，但是如果每个bean都用这种方法的话去注入，那将是一件很痛苦的事情。
**像spring boot中，提供了注解@Component和@ComponentScan。
我们可以直接在person类上面标记注解@Component即可。并在启动类上标记注解@ComponentScan。
@Component注解是表明这个类要被扫描到spring容器中。因为我们springboot的启动类核心注解@SpringBootApplication里面包含了@ComponentScan注解，所以在启动类上不需要加了。**
代码如下：
```
@Data
@Component
public class Person {

    @Value("1")
    private Long id;

    @Value("Jack")
    private String name;

}
```
ApplicationConfig这个类也就没什么用了。优化了代码。同样能实现功能。
@ComponentScan这个注解，它默认只会扫描当前package和子package。如果在别的包下面，我们需要手动指定路径。


* * *

上面演示了bean是如何装载到IOC容易中的。下面来看看如何实现依赖注入的。
代码如下：我们新建一个pet接口，一个dog实现类
```
public interface Pet {

    void move();
}

@Component
public class Dog implements Pet {

    @Override
    public void move() {
        System.out.println("Running");
    }
}
```
在Person类里面加入：
```
@Data
@Component
public class Person {

    @Value("1")
    private Long id;

    @Value("Jack")
    private String name;

    @Autowired
    private Pet pet;

    public void call() {
        pet.move();
    }

}
```
这个@Autowired注解是非常常用的，它注入的机制是**自动根据类型**来自动将bean注入进来。
如果说，我有两个Pet接口的实现类，那么Autowired会选用哪一个呢？举例子，我现在加入一个Bird的实现类:
```
@Component
public class Bird implements Pet {

    @Override
    public void move() {
        System.out.println("Flying");
    }
}
```
这两个类都实现的Pet接口，假如这个时候我在run方法里面，调用move方法的话，spring也不知道应该用哪个。这时候需要我们手动指定，有两种方法：

* 将要指定的类上面，加入@Primary注解
* 在Autowired注解的同时，加入@Qualifier注解

上面两部分，解释了bean是如何装载和如何进行依赖注入的。


* * *


### Spring IOC的Refresh源码解析

还是跟之前一样，通过springboot的启动类，run方法一直点进去，点到这里：
![b2c356f9ca22de13598523f08c92f5ca](Spring.resources/378A6A4F-25E0-4F6F-9C6E-DEF23EBC62F4.png)
可以看到refresh方法。
![7579d61be86f1fcf5ec20403169406a6](Spring.resources/19B9C3A6-D8FD-4A23-ADFF-D8BC37C7B781.png)
主要看invokeBeanFactoryPostProcessors这个方法，比较重要。
这个方法里面调用工厂各种处理器，处理各类bean标签，扫描bean文件，并解析成一个个的bean。里面调用了一个类：ConfigurationClassParser，里面有个doProcessConfigurationClass方法。这个方法里面就是对带有几个注解的类（Component，PropertySources，ComponentScans等等）进行解析
![25bcd85f77032d6c687a31959e28081f](Spring.resources/4175ED33-C3C6-468C-82BA-B27FDBC07F56.png)

总结一下，refresh方法：

* 为IOC容器以及Bean的生命周期管理提供条件
* 刷新spring上下文信息，定义Spring上下文加载流程


* * *

### Spring IOC的getBean源码解析

项目启动之后，会调用这个方法。跟踪源码，会发现调用的deGetBean方法：
![b9be2c61be0c886050abe9bb2035a773](Spring.resources/8733492A-3646-4B7F-8886-F38030AF1430.png)
首先第一行就是获取bean的名字；
然后根据名字，调用getSingleton方法去获取一个共享的实例；
然后就会去调用getObjectForBeanInstance方法从实力工程或缓存中获取到bean，并返回；如果获取不到，就会调用getParentBeanFactory方法尝试获取。

总结一下，refresh方法代码逻辑：

* 转换beanName
* 从缓存中加载实例
* 实例化bean
* 检测parentBeanFactory
* 初始化依赖的bean
* 创建bean


* * *


### 面试题：

1.Spring Bean的作用域
五个作用域：
* singleton：默认作用域，容器里拥有唯一的Bean实例
* prototype：针对每个getBean请求，容器都会创建一个Bean实例
* request：为每个http请求创建一个Bean实例
* session：为每个session创建一个Bean实例
* globalSession：会为每个Http Session创建一个Bean实例


2.Spring Bean的生命周期

创建过程：
*  Bean的建立， 由BeanFactory读取Bean定义文件，并生成各个实例
* Setter注入，执行Bean的属性依赖注入
* BeanNameAware的setBeanName(), 如果实现该接口，则执行其setBeanName方法
* BeanFactoryAware的setBeanFactory()，如果实现该接口，则执行其setBeanFactory方法
* BeanPostProcessor的processBeforeInitialization()，如果有关联的processor，则在Bean初始化之前都会执行这个实例的processBeforeInitialization()方法
* InitializingBean的afterPropertiesSet()，如果实现了该接口，则执行其afterPropertiesSet()方法
* Bean定义文件中定义init-method
* BeanPostProcessors的processAfterInitialization()，如果有关联的processor，则在Bean初始化之前都会执行这个实例的processAfterInitialization()方法

销毁过程：
* DisposableBean的destroy()，在容器关闭时，如果Bean类实现了该接口，则执行它的destroy()方法
* 如果Bean定义文件中定义destroy-method，在容器关闭时，可以在Bean定义文件中使用“destory-method”定义的方法






## AOP

AOP，面向切面编程
关注点分离：不同的问题交给不同的部分去解决
面向切面编程AOP正是此种技术的体现；
通用化功能代码的实现，对应的就是所谓的切面（Aspect）;
业务功能代码和切面代码分开后，架构将变得高内聚低耦合；
为了确保功能的完整性，切面最终需要被合并到业务中。

<u>**AOP的三种织入方式：**</u>

* 编译时织入：需要特殊的Java编译器，如AspectJ
* 类加载时织入：需要特殊的Java编译器，如AspectJ，AspectWerkz
* 运行时织入：Spring采用的方式，通过动态代理的方式，调用切面代码，实现简单


举个例子，假如现在我想记录日志，在controller里面，记录下每个请求的来源，IP或者更多，那么我如果在每个controller里面都记录的话，会很繁琐。所以需要优雅的处理。我们便引入了AOP。
代码如下：
切面类：
```
/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-07 19:12
 * @description     AOP联系
 */
@Component  //定义为切面类
@Aspect
public class RequestLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogAspect.class);

    //定义切入点
    @Pointcut("execution(public * com.example.demo.web..*.*(..))")
    public void webLog(){}

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        //接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("URL：" + request.getRequestURL().toString());
        logger.info("IP：" + request.getRemoteAddr());

    }

    @AfterReturning(returning = "ret",pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        //处理完请求，返回内容
        logger.info("RESPONSE：" + ret);
    }
}
```
测试类：
```
/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-07 19:13
 * @description
 */
@RestController
public class HelloController {

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String hello() {
        String sentence = "sing,jump,rap and basketball";
        System.out.println("守护世界上最好的KK：" + sentence);
        return sentence;
    }
}
```

* * *

<u>**AOP的主要名词概念：**</u>

* Aspect 切面：通用功能的代码实现
* Target：被织入Aspect的对象
* Join Point：可以作为切入点的机会，所有方法都可以作为切入点
* Pointcut：Aspect实际被应用在的Join Point，支持正则
* Advice 通知：类里面的方法以及这个方法如何织入到目标方法的方式

<u>**Advice的种类：**</u>

* 前置通知（Before）
* 后置通知（AfterReturning）
* 异常通知（AfterThrowing）
* 最终通知（After）
* 环绕通知（Around）


* * *

### AOP的原理
AOP的实现：JDKProxy和Cglib

* 默认策略是：如果目标类是接口，那么通过JDKProxy来实现，否则用后者来生成代理
* JDKProxy的核心：JDK动态代理通过**反射**来接收被代理的类，并且要求被代理的类必须实现接口：InvocationHandler接口和Proxy类（像之前日志的例子，没有实现接口，那么就默认用的是Cglib代理来实现）
* Cglib：以继承的方式动态生成目标类的代理。


* * *

想摸清AOP原理，需要先了解代理模式
代理模式：接口+真实实现类+代理类

Spring里代理模式的实现：

* 真实实现类的逻辑包含在了getBean方法里
* getBean方法返回的实际上是Proxy的实例
* Proxy实例是Spring采用JDK动态代理或Cglib动态生成的

这也是为什么spring aop为什么只能作用域spring 容器中的bean的原因，如果不是使用IOC容器管理的对象，spring aop是无能为力的。


* * *


Spring事务的ACID，隔离级别，事务传播
