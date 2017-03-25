---
title: spring学习
date: 2017-02-19 08:57:24
tags:
---

j2ee学习之五-spring

<!--more-->

# 简单使用

使用intellij创建spring项目，然后在src目录创建spring config xml文件。

接着创建一个bean类：
```
public class HelloWorld {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void hello(){
        System.out.println("hello," + name);
    }
}

```

在正常的main中执行HelloWorld方法的代码如下：
```
HelloWorld hello = new HelloWorld();
hello.setName("Spring");
hello.hello();
```

而在spring中则是这样实现，首先在spring config xml文件中配置，配置名为applicationContext.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="hello" class="con.qxg.bean.HelloWorld">
        <property name="name" value="Spring"></property>
    </bean>
</beans>
```
bean标签就是配置一个实体类，而property就是配置该类中的属性。

这样在main方法中调用就是：

```
//1.创建Spring的IOC容器对象
ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

//2. 获取bean实例
HelloWorld hello = (HelloWorld) ctx.getBean("hello");

//3. 执行hello方法
hello.hello();
```

# 配置bean

## xml形式

如上述简单用法中的bean的设置：
```
<bean id="hello" class="con.qxg.bean.HelloWorld">
    <property name="name" value="Spring"></property>
</bean>
```

使用该方式的要求是 HelloWorld有一个无参的构造器

而ApplicationContext就是IOC容器，在使用的时候要对这个类进行初始化。

得到ApplicationContext对象后，使用该对象的getBean方法来取得相应的bean。

如：
```
//1.创建Spring的IOC容器对象
ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

//2. 获取bean实例
HelloWorld hello = (HelloWorld) ctx.getBean("hello");
```

而getBean也可以通过传入类型来获取IOC容器中的bean：
```
HelloWorld hello = (HelloWorld)ctx.getBean(HelloWorld.class);
```

但是这种方式的限制是bean配置中的这种类型的bean只有一个。

### constructor-arg

在xml中配置bean时，属性的配置是使用property标签，这个标签对应的方法是setter方法，那么如果构造器中有传参的构造方法，怎么配置呢？

比如构造方法：
```
public HelloWorld(String name){
    this.name = name;
}
```

那么配置方法如下：
```
   <bean id="hello2" class="con.qxg.bean.HelloWorld">
        <constructor-arg value="Spring2"></constructor-arg>
    </bean>
```

其中constructor-arg就是表示构造方法中的参数，不制定位置的时候，会按照顺序和构造方法中的参数对应。

### index
那么如果规定其位置呢？

使用的是constructor-arg中的index属性：

```
<constructor-arg value="Spring2" index="0"></constructor-arg>
```

注意index是从0开始的。

### type
但是如果构造器中有两个都是只有一个参数的构造方法呢？

那么这个时候只能通过type来指定其参数类型：

```
<constructor-arg value="Spring2" type="java.lang.String"></constructor-arg>
```

### ![CDATA]
对于特殊符号的value值怎么书写呢？比如设置值为<Spring2>,因为<>为特殊值：

```
<constructor-arg value="<Spring2>" type="java.lang.String"></constructor-arg>
```

这个时候编译器会报错，因为<>在xml文档中有特殊作用，不能被解析，那么这个时候需要用![CDATA]来包含其值
```
<constructor-arg type="java.lang.String">
    <value>![CDATA[<Spring2>]]</value>
</constructor-arg>
```

### ref

比如下面的代码，一个人有一辆车，Car类为：
```
public class Car {

    private String name;
    private int size;

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "[" + name + "," + size + "]";
    }
}
```

而对于Person的类为：

```
public class Person {

    private Car car;
    private String name;
    private int age;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return "[" + name + "," + age + "," + car + "]";
    }
}

```

那么配置就如下：

```
    <bean id="car" class="con.qxg.bean.Car">
        <property name="name" value="xuefulai"/>
    </bean>

    <bean id="qxg" class="con.qxg.bean.Person">
        <property name="name" value="qxg"/>
        <property name="age" value="21"/>
        <property name="car" ref="car"/>
    </bean>
```

可以看到Person的bean中使用的ref属性来引用第一个carbean。

也有其他写法:
```
<property name="car">
    <ref bean="car"/>
</property>
```

那么对于类而言有匿名内部类，bean里面可以配置匿名内部bean么？当然可以，如下：

```
    <bean id="qxg2" class="con.qxg.bean.Person">
        <property name="name" value="qxg"/>
        <property name="age" value="21"/>
        <property name="car">
            <bean class="con.qxg.bean.Car">
                <property name="name" value="lanbojini"/>
                <property name="size" value="21"/>
            </bean>
        </property>
    </bean>
```

对于第三个参数，使用内部bean方法来配置，其中没有指明id，这就表明是匿名内部bean，不能被外部引用。

当然对于ref和内部bean也同样作用于constructor-arg标签内。

### <null/>
如何给属性设置为null？

```
<property name="car"><null/></property>
```


### 级联属性

说白了，就是修改ref所引用的那个对象的值：

```
    <bean id="qxg" class="con.qxg.bean.Person">
        <property name="name" value="qxg"/>
        <property name="age" value="21"/>
        <property name="car" ref="car"/>
        <property name="car.name" value="altername"/>
    </bean>
```

即如上，在引用了car之后，在后边配置property，修改car.name的值，这就是级联。

但是要注意，如果要使用级联，一定要在Person类中有getCar这个方法，否则配置出错，因为无法取得car这个对象。


### 集合属性

直接上代码，一目了然：
```
    <bean id="psesonNCar" class="con.qxg.bean.PersonWithNCar">
        <property name="cars">
            <list>
                <ref bean="car"/>
                <ref bean="car"/>
            </list>
        </property>
    </bean>
```
其中集合使用的是<list><set><map>等标签来进行配置，而内部使用ref来进行引用或者内部类来添加。注意数组也是使用<list>

不过map比较特殊，这里来段代码说明一下：

```
<map>
    <entry key="" value=""/>
    ...
</map>
```
可以看到其通过entry标签来设置key,value值，但是如果是自定义的类作为Key，那么就要使用key-ref，当然也会有value-ref。


### props

java中是有Properties这个类型的，其实就是保存key,valu一种集合。

那么对于bean中的配置为：

```
<property name="props">
    <props>
         <prop key="somekey">somevalue</prop>
         ...
    </props>
</property>
```

### util

util用于定义独立的集合，这样其他bean对象就可以引用该集合：
```
<util:list id="cars">
    <ref bean="car1"/>
    <ref bean="car2"/>
</util:list>
```

这样cars就表示一个List集合，内容有car1,和car2，那么对于一个bean有List的引用，就可以使用cars这个名称为其进行配置：

```
<property name="cars" ref="cars"/>
```

不过如果要使用util，必须要在xml根部加上util的命名空间。

### p
首先导入p命名空间，p简化了bean的配置，如原配置：
```
    <bean id="qxg" class="con.qxg.bean.Person">
        <property name="name" value="qxg"/>
        <property name="age" value="21"/>
        <property name="car" ref="car"/>
    </bean>
```

使用p后：
```
<bean id="qxg" class="con.qxg.bean.Person" p:name=“qxg” p:age="21" p:car-ref="car"/>
```

### 自动装配(不推荐使用)
autowire属性来自动装配，如以下例子：

```

    <bean id="car" class="con.qxg.bean.Car" p:name="xuefulannnnn" p:size="20" />
    <bean id="person" class="con.qxg.bean.Person" p:age="10" p:car-ref="car" p:name="haha"/>

```

最开始的写法如上，person中有car的引用，那么使用自动装配来写如下：

```
    <bean id="car" class="con.qxg.bean.Car" p:name="xuefulannnnn" p:size="20" />
    <bean id="person" class="con.qxg.bean.Person" p:age="10" autowire="byName" p:name="haha"/>
```

其中person的bean配置中没有了ref引用，而是换成了autowire，值是byName,这个值的意思是，会根据set方法中的名字来查找是否有该名字的类对象，比如setCar，就会找名为car的Car对象，但是如果把car的名字改为car2，这种方法就会失效。

第二种方法为
```
<bean id="person" class="con.qxg.bean.Person" p:age="10" autowire="byType" p:name="haha"/>
```

值为byType，根据类型装配，但是限制条件是该配置文件中，只有一个该类型的bean对象，即Car对象必须唯一。


看上去实际上很方便，但是也是有缺点的，比如不灵活，配置文件多的时候复杂不方便。一般项目中是不推荐使用的。

### 配置中的继承
注意是配置间的继承，并不是类中的继承。

```
    <bean id="person" class="con.qxg.bean.Person" p:age="10" autowire="byName" p:name="haha"/>
    <bean id="person2" parent="person" p:name="继承后的person"/>
```

person2和person的类是一样的，但是我不想再多余的配置这个class这个属性，所以使用继承的方法，制定parent为person，这样就不用配置多余的属性。但是注意，autowire这个属性是不会被继承的。同时子bean是可以复写父bean的任何属性，包括class。

因为bean中可以继承，所以可以创建一个bean模板，并加上abstract=“true”,表示为抽象bean，不能被实例化：
```
<bean id="person" class="con.qxg.bean.Person" p:age="10" p:car-ref="car" p:name="haha" abstract="true"/>
```
如果在配置bean的时候没有配置class，那么这个bean必须是抽象bean，即abstract="true"必须被设置。

### 配置中的依赖

使用depends-on，表示配置该bean前，依赖的bean对象一定先实例化，如果有过个bean，使用,隔开。

```
 <bean id="person" class="con.qxg.bean.Person"
          p:age="10"  p:name="haha" depends-on="car"/>
```

目前测试这个东西有问题，不知道什么原因。

### bean的作用域

默认使用getBean来取得对象时，取得的对象都是同一个对象。

```
    Person person = (Person) ctx.getBean("person");
    Person person2 = (Person) ctx.getBean("person");
    person.setName("修改后的值");
    System.out.println(person2);
```

打印日志为：
```
[修改后的值,10,[xuefufufufuufu,20]]
```

即默认的scope是单例的，那么配置作用域使用的是scope，其值有2个：
* prototype:每次getBean都会返回新的bean。
* singleton:单例

使用方法：
```
    <bean id="person" class="con.qxg.bean.Person" scope="prototype"
          p:age="10"  p:name="haha" p:car-ref="car"/>
```

### 使用外部属性文件

有时候配置bean的时候需要使用系统部署的细节信息，比如文件路径等，数据源配置信息等。
Spring提供了一个PropertyPlaceholderConfigurer的BeanFactory后置处理器。

比如通过c3p0来配置数据库，写法如下：

```
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="user" value="root"></property>
        <property name="password" value="root"></property>
        <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/spring"></property>
    </bean>
```

配置前记得添加c3p0的两个jar包，和mysql的jar包。

这样配置似乎是没有问题，但是如果我们想要更改账户名称密码等呢？只能更改这个xml文件，但是难免会碰到一些复杂的问题，所以一般是创建一些配置文件，将这些配置放进去。比如创建一个文件叫db.propertied,然后写入配置信息：

```
user=root
password=root
driverclass=com.mysql.jdbc.Driver
jdbcurl=jdbc:mysql:///spring
```

那么如何取出这些值呢？通过context命名空间来导入db.properties文件。

```
    <!--导入配置文件-->
    <context:property-placeholder location="classpath:db.properties"/>

    <!--通过${}来读取配置文件中的值-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="user" value="${user}"></property>
        <property name="password" value="${password}"></property>
        <property name="driverClass" value="${driverClass}"></property>
        <property name="jdbcUrl" value="${jdbcUrl}"></property>
    </bean>
```

注意使用这种方式可能会一直警告：

> Establishing SSL connection without server's identity verification is not recommended. According to MySQL 5.5.45+, 5.6.26+ and 5.7.6+ requirements SSL connection must be established by default if explicit option isn't set. For compliance with existing applications not using SSL the verifyServerCertificate property is set to 'false'. You need either to explicitly disable SSL by setting useSSL=false, or set useSSL=true and provide truststore for server certificate verification.

只需要把配置文件中的jdbcurl的值设为：`jdbcUrl=jdbc:mysql://localhost:3306/spring?useSSL=false`

# SpEL
Spring表达式语言，支持运行时查询和操作对象图的强大的表达式语言。

语法：#{...},所有在大括号中的字符都被认为是SqEL.
语法中支持 基本运算符，三目运算符，正则表达式等。
## 引用

通常我们使用ref来引用其他对象，我们也可以通过#{}来引用其他对象，如：
```
    <bean id="person" class="con.qxg.bean.Person" scope="prototype"
          p:age="10"  p:name="haha" p:car="#{car}"/>
```

注意看car的引用变成了`p:car="#{car}"`，原写法是：`p:car-ref="car"`

我们还可以通过#{...}来引用其他对象的属性，比如我想把car2中的name值设置为car中的name值，那么方法就是:

```
    <bean id="car2" class="con.qxg.bean.Car" p:name="#{car.name}" p:size="846"/>
```

并且#{...}还支持调用其他类中的方法，比如toString()等


## 调用静态方法
通过T()调用一个类的静态方法，它返回一个Class Object,然后再调用相应的方法和属性
```
value = "#{T(java.lang.Math).PI}"
```
 
## 三目运算符
```
value ="#{count>30 ? '呵':'哈'}"
```

注意其中的字符串值要用单引号或双引号包括起来。


# Bean生命周期
在bean的配置文件中，制定bean的init-method，和destroy-method，这样bean在初始化的时候会执行init-method，销毁的时候会执行destroy-method,如：

```
 <bean id="car2" init-method="init" destroy-method="destroy" class="con.qxg.bean.Car" p:name="#{car.name}" p:size="846"/>
```
在主代码中调用：
```
ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
Car car = (Car) ctx.getBean("car2");
System.out.println(car);

ctx.close();
```
其打印日志为：

```
init
[xuefufufufuufu,846]
destroy
```

# bean后置处理器
其实作用就是使用getBean取得bean对象的过程中，先对bean进行处理，然后才返回该bean。

比如我getBean("car")取得一个car，我想让所有Car类型的对象，都修改为名为"bmw",那么方法就是配置后置处理器，创建一个类并实现BeanPostProcessor接口，并实现其中的两个方法：
```
public class MyBeanPostProcess implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        System.out.println("postProcessBeforeInitialization");
        if(o instanceof Car){
            ((Car)o).setName("bmw");
            return o;
        }
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        System.out.println("postProcessAfterInitialization");
        return o;
    }
}
```

接着在配置文件中配置，不需要id属性，spring会自动识别：
```
<bean class="con.qxg.bean.MyBeanPostProcess"/>
```

main方法：
```
Car car = (Car) ctx.getBean("car2");
System.out.println(car);

ctx.close();
```

打印日志为：

```
postProcessBeforeInitialization
init
postProcessAfterInitialization
[bmw,846]
destroy
```

可以看到，其创建Car的bean对象的时候，会先调用后置处理器的postProcessBeforeInitialization方法，来进行处理，然后再调用Car的初始化方法，接着再调用后置处理器的postProcessAfterInitialization方法来处理。

方法中传入的对象，第一个就是要取得的bean对象，第二个就是bean的id值。

# 通过工厂方法配置Bean
## 静态工厂方法
调用某个类的静态方法返回实例，就是静态工厂方法.

比如下列静态工厂方法：
```
public class CarFactory {
    private static Map<String,Car> cars;

    static {
        cars = new HashMap<>();
        cars.put("bmw",new Car("bmw"));
        cars.put("ford",new Car("ford"));
    }

    public Car getCar(String name){
        return cars.get(name);
    }
}

```
那么如何配置bean呢，注意配置bean并不是配置CarFactory：
```
    <bean id="car" class="con.qxg.factory.CarFactory" factory-method="getCar">
        <constructor-arg value="bmw"/>
    </bean>
```
class制定工厂类，同时使用factory-method指定工厂方法。
其中constructor-arg表示的是传入的foctory-method中的参数，注意一定要使用constructor-arg来指定。

## 实例工厂方法

实例工厂方法就是先创建实例，然后通过实例的方法来取得某bean对象，不举例子了，直接看代码：
```
<!--先配置工厂实例-->
<bean id="factory" class="com.qxg.factory.InstanceCarFactory"/>

<!--通过factory-bean制定工厂实例，通过factory-method制定工厂方法，该方法不必是静态的。-->
<bean id="car" factory-bean="factory" factory-method="getCar">
    <constructor-arg value="ford"/>
</bean>
```

# 通过FactoryBean配置bean
创建类实现FactoryBean接口。

```
public class CarFactoryBean implements FactoryBean {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    //返回bean对象
    @Override
    public Object getObject() throws Exception {
        Car car = new Car();
        car.setName(name);
        return car;
    }


    //返回对象类型
    @Override
    public Class<?> getObjectType() {
        return Car.class;
    }

    //是否是单例
    @Override
    public boolean isSingleton() {
        return true;
    }
}
```

那么bean的配置就是：
```
    <bean id="car" class="con.qxg.factory.CarFactoryBean">
        <property name="name" value="tmp"/>
    </bean>
```

虽然class指定了CarFactoryBean,但是实际返回的是该类中的getObject返回的对象。

# 基于注解的方式配置bean

使用注解来配置bean，那么spring会进行扫描，如何扫描某文件中的bean呢？

是通过<context:component-scan>来指定扫描的目录：
```
    <context:component-scan base-package="com.qxg.annotation"/>
```


这样就指定只扫描com.qxg.annotation包以及子包中的bean类了。

那么spring扫描类的时候，如何知道该类就是bean呢？

这个时候就需要注解，注解一共分为以下四种：

* @Component :基本注解，标示一个受Spring管理的组件
* @Respository：标示持久层组件
* @Service:标示服务层组件
* @Controller:标示表现层组件

不管注解是什么名字，只要类被以上注解标示，都会被Spring扫描到。

扫描到后，就可以通过getBean方法来取得。

但是getBean中如何传名字呢？通常是将类名第一个字母小写传入即可。

比如class Test,那么传入的参数就是"test"。

如果想要指定名字，可以在注解中指定：

```
@Controller(value = "myTest")
```

这样取得bean的方法就是getBean("myTest");

## resource-pattern

该属性是context:component-scan中的属性，意义为资源匹配，或者叫过滤，配置这个属性后，只会扫描与该属性匹配的类，如:
```
 <context:component-scan base-package="con.qxg.annotation" resource-pattern="pattern/*.class"/>
```

可以看到resource-pattern的值是 pattern/*.class，意思是只会扫描pattern子包中的类文件，其他包中的类文件不会被扫描。

## <context:include-filter> 或 <context:exclude-filter>

这两个标签，也是用于过滤，第一个是包含哪些文件，第二个指定不包含哪些文件：

```
    <context:component-scan base-package="con.qxg.annotation" use-default-filters="false">
        <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

```

对于include，必须要指定use-default-filters的值为false，否则不但include标签下类文件被扫描，base-package中的类文件也被扫描。

context:include-filter下的type指定类型，如上指定的是annotation，那么就会扫描注解类型，然后expression指定注解名称，注意是包全名。这样编写后，只会扫描con.qxg.annotation下的被@Controller注解的类。

对于<context:exclude-filter>就简单多了
```
    <context:component-scan base-package="con.qxg.annotation">
        <context:exclude-filter  type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>
```
不需设置use-default-filters的值，这个标签只是排除某些类不被扫描。

## bean之间的关联关系
比如Person类中有Car的引用，使用注解分别注解了Person类和Car类，那么通过getBean方法取得Person的一个实例后，该实例中并不会包含Car的对象。

如何解决这个问题？

<context:component-scan>元素会自动注册AutowiredAnnotationBeanPostProcessor实例，该实例可以自动装配@Autowired（常用）,@Resource,@Inject注解的属性。

比如：

Person:
```
@Controller
public class Person {
    
    private Car car;
    private String name;
    private int age;

    public Person(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }

    public void say(){
        System.out.println("My car is " + car.toString());
    }

}
```

Car:
```
@Controller
public class Car {

    private String name;
    private int size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(int size) {
        this.size = size;
    }
    

    @Override
    public String toString() {
        return "[" + name + "," + size + "]";
    }
}

```
为了方便起见，将两个类的注解都设为@Controller。

接下来配置配置文件：
```
   <context:component-scan base-package="con.qxg.bean" />
```
这样spring就会自动扫描到Person,Car类了。

然后main方法中测试：
```
Person p = (Person) ctx.getBean("person");
p.say();
```
然后就抛出异常：
> Exception in thread "main" java.lang.NullPointerException

原因就是，Person中Car对象没有被装配，那么如何被装配呢

在Person中的代码中配置@Autowired

```
@Autowired
private Car car;
```

这样就能正常运行了，@Autowired会自动装配兼容类型的对象，其从IOC中取出对象装配进去。如果IOC中没有Car对象，就无法装配，编译时就会出现异常。

那么如果不希望Spring为Person装配，就可以这样写：
```
@Autowired(required = false)
private Car car;
```
这样就不会为其装配了，编译正常，不过car为null。

但是有一点值得注意，使用@Autowired标记的类对象，如果有两个类都是该类型，会匹配哪个呢？

比如@Autowired标记了一个接口类型：
```
@Autowired 
private Animal animal;
```

而被扫描的bean中有两个实现了Animal接口，一个是Dog，一个是Cat，那么装配的应该是哪个呢？

一般情况下，其装配是名为animal的bean，即与属性名一致的bean,这个名可以通过注解指定。比如@Controller(value = "animal"),而如果没有这个名的bean时候，就会抛出异常。

还有一种解决方法是，在装配的时候指定名字：
```
@Autowired
@Qualifier("dog")
private Animal animal;
```

这样就会自动查找名为dog的bean对象，然后装配进去。

值得注意的是：
如果@Autowired放在数组上，集合上，那么Spring会把所有匹配的对象装载进去。

# AOP(面向切面编程)

首先来看一个简单的问题，如果对于一段程序，我想在执行前，插入一段程序，在执行之后，再插入一段程序，按照正常思维模式，就是直接在源代码上编写就好了。但是如果对于每个方法，我都要求这样做，那么改源码的代价就非常昂贵了。

解决这个问题的方法就是动态代理，其思想是使用代理对象，包括住要修改的对象。

假设有这么一个对象：
```
public class Caculator {

    public int add(int a, int b){
        System.out.println("add");

        return a+b;
    }
    
}

```

为简单起见，就写了一个方法，方法有返回值，返回a+b的值。

如果想要在打印前执行某操作，并在打印后执行另一个操作，手下要使用代理类实例化一个对象

```
//第一个参数是由哪个类加载器加载，通常情况下和被加载的对象使用一样的类加载器，使用obj.getClass().getClassLoader()
//第二个参数是Class数组，表示由动态代理产生的对象必须要实现的接口，注意必须是接口！！
//第三个参数表示，当具体调用代理对象的方法时，将产生什么行为。
Proxy.newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h)
```

比如针对Caculator的代理对象，如果要通过Caculator生成代理对象，必须要为其实现接口，因为代理对象返回的值就是接口中的某一个接口型对象。
```
//首先是接口：
public interface Tmp {
    public int add(int a,int b);
}

//接着是Caculator：
public class Caculator implements Tmp{

    @Override
    public int add(int a, int b){
        System.out.println("add");

        return a+b;
    }

}

//最后是生成代理对象的方法:
 Tmp proxy = (Tmp) Proxy.newProxyInstance(Caculator.class.getClassLoader(), new Class[]{Tmp.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("invoke");
                        return 0;
                    }
                });


```

可以看到，其中第一个参数就是要使用代理的类的加载器，第二个是接口的数组，数组内必须是接口，第三个就是Handler，用来处理事务。

注意看，在Caculator的add方法中打印了一条语句，输出"add"，并返回a+b，而在创建代理对象的第三个参数Handler中的invoke方法，打印的是"invoke"，返回的0。

那么此时做测试 ,proxy.add(1,2); 应该会打印"add"，并返回3。但是实际情况是：

```
invoke
0
```

也就是说，其执行的方法是invoke()，并没有执行add()方法。

那么如何执行原方法呢？

原方法就是在invoke()中使用反射去执行，invoke()中传入的参数是代理对象，原方法，以及原参数，还缺一个原对象，那么原对象应该写在哪呢？这个时候，原对象就应该new一个，所以最终的代码应该如下：

```
        final Caculator target = new Caculator();

        Tmp proxy = (Tmp) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("invoke");
                        int result = method.invoke(target,args);
                        return result;
                    }
                });

        int result = proxy.add(1,2);
        System.out.println(result);

        //注意其中第二个参数也可以通过target.getClass().getInterfaces()来获取。使用这个前提就是，代理对象不需要使用其他接口。
```

这样在调用InvocationHandler()中的invoke方法中，先打印语句，然后调用原方法，最后才有返回值，那么在这个方法中，我可以随便操作原方法，我可以在原方法前执行某段代码，也可以在之后执行某段代码，甚至可以修改其返回值。只要通过proxy这个对象去执行的任意方法，都会只调用InvocationHandler中的invoke方法，这点要注意。

那么这就是动态代理。

那么对于AOP来讲，可以实现AOP的方式就是动态代理。

AOP是Aspect Oriented Programming的缩写。按照马士兵老师的说法就是，本来程序是一条线执行的，而面向切面变成就是在这条线上的某处切了一刀，执行我自己的操作，然后继续顺着这条线执行。


## Spring使用AOP

1. 导包：aspectjweaver.jar
2. 配置spring配置文件：
```
    <!--自动为匹配的类生成代理对象-->
    <aop:aspectj-autoproxy></aop:aspectj-autoproxy>
```
3. 创建目标类
```
@Component
public class UserDao implements User {
    @Override
    public void method() {
        System.out.println("UserDao method run");
    }
}
```

4. 创建切面类
```
//@Aspect表示该类是一个切面类
@Aspect
@Component
public class UserAspect {

    //@Before表示该方法会在某个类的方法前执行
    //在execution中，可以通过*来匹配任意方法，任意包等
    //也可以在该方法中加上JoinPoint类对象，来获取链接时候的细节。
    @Before("execution(public void con.qxg.TestAOP.User.method())")
    public void beforeMethod(){
        System.out.println("before");
    }
}
```

5. 测试：
```
ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
//这个地方取得userDao，返回的并不是UserDao类型，而是Proxy类型，即代理对象，所以不能强转为UserDao类型，因为返回的代理对象只能是接口类型，而UserDao只实现了User接口，所以只能是User类型，一定要注意！
User dao = (User) ctx.getBean("userDao");
dao.method();
```

打印结果是：
```
before
UserDao method run
```

### 通知
在前边的示例中，@Before就是标记的前置通知，意义是在方法执行前执行前置通知，当然也有后置通知：@After标记的方法。

后置通知的意义是在方法执行之后执行后置通知，无论是否发生异常。但是后置通知无法取得目标方法的结果。

如果要获取目标方法的结果，需要在返回通知里搞（@AfterReturning）,如何取得返回结果，如下：
```
    @AfterReturning(value = "execution(public boolean con.qxg.TestAOP.User.method())",returning = "result")
    public void afterReturning(boolean result){
        System.out.println("afterReturning,result is " + result);
    }
```

即通过注解中的returning的属性值来指定返回结果的变量名，而返回的类型在execution中已指明，所以以上代码应该很容易理解。

还有一个通知是异常通知，这个就是出现异常的时候会执行的通知@AfterThrowing，这个就不举例了。

通知中最强的一个就是@Around，环绕通知，但并不意味着这个通知是常用的。

其需要携带ProceedingJoinPoint类型的参数，且有返回值，如：
```
    @Around("execution(public boolean con.qxg.TestAOP.User.method())")
    public boolean around(ProceedingJoinPoint proceedingJoinPoint){
        System.out.println("aroud");
        return true;
    }
```

注意，该类型通知，必须携带ProceedingJoinPoint参数，并且如果目标方法有返回值，这个也必须有返回值，否则运行会出异常。
那么以上的环绕通知，会打印什么日志呢？
```
aroud
after
afterReturning,result is true
```

可以看，目标方法并没有执行，而是直接执行环绕通知，后续执行后置通知和返回通知。

这就应该明白了，环绕通知可以决定目标方法是否会被执行，并能决定返回值是什么。那么如何让目标方法恢复执行？

如下：
```
    @Around("execution(public boolean con.qxg.TestAOP.User.method())")
    public boolean around(ProceedingJoinPoint proceedingJoinPoint){
        System.out.println("aroud");

        try {
            proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return true;
    }
```

即使用ProceedingJoinPoint对象的proceed()方法即可恢复执行。恢复执行同样会执行前置通知。

### 切面优先级

既然通过注解来为某个类的方法引入切面，那么如果有两个切面，执行顺序应该是什么呢？
这就涉及到切面的优先级了。

如何设置切面优先级呢？通过`@Order(1)`，值越小，优先级越高

### 重用切点

比如
```
@After("execution(public boolean con.qxg.TestAOP.User.method())")
```

这个方法中的值是 `execution(public boolean con.qxg.TestAOP.User.method())`，那么我好多地方都会用到这个值，如何重用呢？

方法是定义一个方法，用于声明切入点表达式，方法中不需加其他代码，如下：
```
   @Pointcut("execution(public boolean con.qxg.TestAOP.User.method())")
    public void express(){}
```

那么对于@Before等通知的值传入方式就是`@Before("express()")`.
但是如果在其他类中定义的，前面就要加入类的名称，如:`@Before("User.express()")`

### 基于配置文件的AOP

1. 配置bean
2. 配置AOP

如以下配置：
```

    <bean class="con.qxg.TestAOP.UserAspect" id="userAspect"/>
    <bean class="con.qxg.TestAOP.UserDao" id="userDao"/>

    <!-- AOP配置-->
    <aop:config>
        <!--配置切点-->
        <aop:pointcut id="pointcut" expression="execution(public boolean con.qxg.TestAOP.User.method())"/>
        <!--配置切面-->
        <aop:aspect ref="userAspect">
            <aop:before method="beforeMethod" pointcut-ref="pointcut"/>
        </aop:aspect>
    </aop:config>
```

可以看到AOP配置中包含了 切点和切面，其实切点也可以省略，在切面中使用pointcut直接配置。

注意看，切面的几个要素：切面引用，切面通知，切面的切点引用。



# Spring的事务

首先说明什么是事务，事务指的是对于一套代码，要么这些代码都成功执行，要么都不执行，这就是事务。

比如下面这行伪伪伪代码：

```
    buyBook(); //购买书，已出钱，数据库总金额改变
    outBook(); //书库出书，数据库中书个数-1
```

对于上面一套逻辑，必须要用事务，如果不使用事务，假设buyBook()成功运行，而outBook()抛异常，这个时候用户只是出钱，而没有得到书，就很容易出现问题。

而Spring中的事务相当好使

直接一套配置 + 一个注解即可完成

假设以上代码在一个类的方法中：
```
public void method(){
    buyBook();
    outBook();
}
```


首先在配置文件中配置以下代码：
```
    <!--配置事务管理器-->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!--配置dataSource-->
        <property name="dataSource" ref="dataSource"></property>
    </bean>


    <!--启用事务注解-->
    <tx:annotation-driven transaction-manager="transactionManager"/>
```

其中dataSource的配置在前面已经说过，这里再贴出来：

```
    <!--导入资源文件-->
    <context:property-placeholder location="classpath:db.properties"/>
    <!--配置dataSource-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="user" value="${user}"></property>
        <property name="password" value="${password}"></property>
        <property name="driverClass" value="${driverClass}"></property>
        <property name="jdbcUrl" value="${jdbcUrl}"></property>
    </bean>
```

最后就是一个注解：

```
@Transactional
public void method(){
    buyBook();
    outBook();
}
```

这个时候运行这个方法购买书的时候，不论哪个地方抛异常，所有的数据库的操作都不会成功，这就对应了事务的概念，要么都执行，要么都不执行。


## 事务传播
当一个事务被另一个事务调用时，要指定事务的传播行为。

Spring中指定7个传播行为，常用的2个，如下
* REQURED(默认) 如果有事务在运行，当前的方法就在这个事务中运行
* REQURES_NEW当前方法必须开启一个新事务，如果有事务正在运行，应该将它挂起

默认REQURED是什么样子的呢？

比如以下的一个事务：

```
@Transactional
public void buyBook(){
    buyJavaBook();
    buyC艹Book();
}
```

其中buyJavaBook()和buyC艹Book()均有事务注解，默认的REQURED，假设buyJavaBook()成功执行，buyC艹Book()抛出异常，那么两本书，是否都被购买了呢？

如果是默认的REQURED，两本书都不会成功，因为这俩方法使用的是当前的事务，也就是buyBook()的事务，不论哪个方法中抛出异常，整个过程都不会执行，所以C艹没购买成功，那么Java也不会购买成功。


而对于REQURED_NEW就不同，比如buyJavaBook,buyC艹Book的注解为:`@Transactional(propagation = Propagation.REQUIRES_NEW)`,那么在buyJavaBook()开始的时候，会新开一个事务，不论这个方法是否执行成功，都不会影响buyC艹Book()的结果，也就是说这两个方法都会新开事务，互不影响，也不影响buyBook()这个方法。其实也可以简单理解为buyBook()这个事务，并不会搭理buyJavaBook()和buyC艹Book(）

## 事务隔离级别
使用isolation来指定，如`@Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.READ_COMMITTED)`


## noRollbackFor
表示对某异常不进行回滚,如`@Transactional(noRollbackFor = Exception.class)`

## readOnly
表示是否是只读事务，如果是，仅对数据库读操作不能写：`@Transactional(readOnly = true)`

## timeOut
表示事务运行多长时间后要强制回滚，`@Transactional(timeout = 4)`,表示事务如果运行超过4s就进行强制回滚

## 基于xml配置事务

```

    <!--配置事务管理器-->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!--配置dataSource-->
        <property name="dataSource" ref="dataSource"></property>
    </bean>


    <!--配置事务属性-->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <tx:attributes>
            <!--根据方法名指定事务属性-->
            <tx:method name="get*" propagation="REQUIRES_NEW"/>
            <tx:method name="set*" read-only="true"/>
        </tx:attributes>
    </tx:advice>


    <!--配置事务切入点，并关联事务属性-->
    <aop:config>
        <!--配置事务切入点-->
        <aop:pointcut id="txPointcut" expression="execution(* boolean con.qxg.TestAOP.User.*())" />

        <!--关联事务切入点和事务属性-->
        <aop:advisor advice-ref="txAdvice" pointcut-ref="txPointcut"/>
    </aop:config>
```


