---
title: springMVC学习
date: 2017-04-01 14:52:44
tags:
---


# 学习文档
http://7xvpsh.com1.z0.glb.clouddn.com/publish/21-2/3-dispatcherservlet-processing-sequence.html
<!-- more -->

# springMVC工作流程
来张图片感受一下
![](/images/springMVC工作流程.png)
# 示例
1.首先在web.xml中配置DispatcherServlet：
```
    <!--配置springMVC的前端处理器DispatcherServlet-->
    <servlet>
        <servlet-name>qxg</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>

	<!--初始化beans的配置文件-->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:beansConfig.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup> <!--表示启动容器初始化该Servlet-->
    </servlet>
    <servlet-mapping>
        <servlet-name>qxg</servlet-name>
        <url-pattern>/</url-pattern> <!--哪些请求会交给servlet处理-->
    </servlet-mapping>

```

上面的url-pattern表示，/样式的请求都会交给名为qxg的servlet进行处理。
接着创建一个处理器，并配置RequestMapping,其是一个url的映射关系，如果遇到/index.html就会调用该控制器中的对应方法
```
public class HelloworldController implements Controller {
 
	
	@RequestMapping("/index.html")
    public String hello(){
        return "sucess";
    }
}

```

然后将该控制器配置在上述所说的beansConfig中：
```
   <!-- 控制器 -->
    <bean name="/hello" class="com.qxg.HelloworldController" />
```

接着有了处理器返回数据，如何进行解析视图呢？这个时候要用到视图解析器：

```
    <!-- 专门的试图解析器，会通过prefix + returnVal + suffix得到实际的物理试图，比如上述返回值是sucess，那么其解析为/WEB-INF/jsp/success.jsp,如果使用了该视图解析器，就不会再使用默认的ViewResolver解析器了 -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
```

最后创建一个名为sucess.jsp文件，接着运行，输入localhost:8080/hello即可。

可以看到，上述的第一步骤就是配置DispatherServlet，不仅仅可以在web.xml中进行配置，也可以在代码中进行配置，其配置使用到的类如下：
```
public class InitStartUp implements WebApplicationInitializer {
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("hahha");
    }
}
```
其中WebApplilcationInitializer接口中的onStartup会在服务开启的时候运行，所以可以将DispatherServlet注册到这个方法中，但是该类还要在beans的配置中自动注册。
整体代码如下：
```
public class InitStartUp implements WebApplicationInitializer {
    public void onStartup(ServletContext servletContext) throws ServletException {
        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", new DispatcherServlet());
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }
}
```
在beans的配置中添加:`<bean class="com.qxg.init.InitStartUp"/>`

注意DispatcherServlet会默认寻找/WEB-INF/<servlet-name>-servlet.xml的beans的配置文件，除非指定：
```
<!--初始化beans的配置文件-->
<init-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:beansConfig.xml</param-value>
</init-param>
```

# Controller

@Controller标记一个类为控制器，如果使用该注解，那么可以开启自动扫描，spring扫描到该注解标记的类会自动进行注册，开启自动扫描的方式为:
```
<context:component-scan base-package="com.qxg.controller"/>
```

如果不使用该注解，要注册Controller需要在beans的配置文件中进行指定`<bean class="com.qxg.controller.HelloworldController"/>`

# RequstMapping

翻译过来就是处理器映射，springMVC默认使用RequestMappingHandlerMapping,该类会自动查找@RequestMapping和@Controller的控制器，所有继承AbstractHandlerMapping的处理器方法映射HandlerMapping类都有以下属性：
* 一个interceptors列表，制定拦截器，用于拦截请求，比如：
```
<beans>
    <bean id="handlerMapping"
            class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping">
        <property name="interceptors">
            <list>
                <ref bean="officeHoursInterceptor"/>
            </list>
        </property>
    </bean>

    <bean id="officeHoursInterceptor"
            class="samples.TimeBasedAccessInterceptor">
        <property name="openingTime" value="9"/>
        <property name="closingTime" value="18"/>
    </bean>
</beans>
```
```
package samples;

public class TimeBasedAccessInterceptor extends HandlerInterceptorAdapter {

    private int openingTime;
    private int closingTime;

    public void setOpeningTime(int openingTime) {
        this.openingTime = openingTime;
    }

    public void setClosingTime(int closingTime) {
        this.closingTime = closingTime;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(HOUR_OF_DAY);
        if (openingTime <= hour && hour < closingTime) {
            return true;
        }
        response.sendRedirect("http://host.com/outsideOfficeHours.html");
        return false;
    }
}
```

上述代码实现的是在请求映射前，会查看当前请求的时间，如果时间不在允许范围内，就会进行重定向,在拦截器中有个preHandler方法，该方法是请求前调用。其中返回true表示可以执行请求映射，而返回false则中断其请求。
* defaultHandler,默认处理器
* order,对处理器进行排序。
* alwaysUseFullPath,若值为true，则在上下文中总是使用完整路径查找合适的处理器。
* urlDecode,默认true


## 使用拦截器
拦截器工作流程：
![](/images/interceptor工作流程.png)
如果要使用拦截器，必须要实现HandlerInterceptor接口，实现该接口必须要重写`preHandler`，`postHandler`等多种方法。
`preHandler`会在处理器实际执行之前被执行，`postHandler`会在处理器执行完毕后执行，`afterCompletion`会在整个请求处理完毕后执行。

preHandler在返回true时，处理器会继续执行，而返回false的时候，并不是不执行映射器中的方法了，而是DispatherServlet会认为拦截器已经完成了对请求的处理，其余拦截器以及执行链中的其他处理器就不会再执行了。

如果只想重写其中的一个方法，可以继承HandlerInterceptorAdapter类。如：
```
public class TestInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("preHandler");
        return true;
    }


}
```
但是要注意如果要使用在beans的配置中写下以下代码后：
```
    <bean id="handlerMapping" class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping">
        <property name="interceptors">
            <list>
                <bean class="com.qxg.interceptor.TestInterceptor"/>
            </list>
        </property>
    </bean>
```
还需要配置一个RequestMappingHandlerAdapter适配器，如下：
```
    <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>
```
虽说配置mvc:annotation-driven后会自动装配RequestMappingHandlerAdapter，但是其装配的interceptors中却没有自定义的拦截器，只存在一个ConversionServiceExposingInterceptor，这个拦截器的作用就是实现对传入的数据进行类型转换，但是我想要的是能添加一个interceptor，这个时候应该怎么做？
配置如下：
```
 <mvc:interceptors>
        <bean class="com.qxg.interceptor.TestInterceptor"/>
    </mvc:interceptors>
```

所以在使用mvc:annotation-driven的时候，想要配置自定义拦截器的时候，只需要在mvc的标签中配置即可，既不会和默认的拦截器冲突，又会添加一个新的拦截器。
## @RequestMapping

首先注意的是，如果要使用RequestMapping进行映射，在beans的配置文件中，千万不能配置其他的映射类，比如spring有自带的BeanNameUrlHandlerMapping。所以在beansConfig配置中，只需要以下代码即可：
```
    <!-- ViewResolver -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

    <bean class="com.qxg.Controller.HelloworldController"/>
    
```
RequsetMapping不仅可以修饰方法，也可以修饰类:
```
@Controller
@RequestMapping("springtest")
public class HelloworldController {

    @RequestMapping("/nohello")
    public String hello(){
        return "sucess";
    }
}
```
如果要成功映射到hello()方法，则必须使用/springtest/nohello，这样才能正确访问。

首先来看下RequstMapping的接口定义：
```
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMapping {
    String name() default "";

    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};

    RequestMethod[] method() default {};

    String[] params() default {};

    String[] headers() default {};

    String[] consumes() default {};

    String[] produces() default {};
}
```
一般来讲，常用的是value,method,params,headers.
value是请求时的名字，method是请求方法，params是请求参数，headers就是请求头里的参数。
@RequestMapping("/nohello")比如这个注解，默认的"/nohello"就是value，其中第一个`/`可省略。
而method可指定GET或者POST。
params和headers符合以下规则：
加入指定了params = {"username","type=1","!age","sex!=women"}
那么表示请求中必须要有username参数，必须要有type参数且值为1，必须没有age参数，有sex但是不能为women，所以对于以下访问是合法的：
/springtest/nohello?username=me&type=1&sex=man.
该规则同样试用于headers，这样可以通过headers指定某种特定的浏览器才能访问，或者进行其他过滤。


RequstMapping映射也支持匹配符：
?:匹配文件名中的一个字符
*:匹配文件名中的任意字符
**:匹配多层路径
比如RequestMapping("/springtest/*/abcd")可以匹配/springtest/hahah/abcd等多种url


# PathVariable
占位符，通过该注解，可以向RESTful风格url挺近，如下：
```
    @RequestMapping("testPath/{id}")
    public String haha(@PathVariable("id") int id){
        System.out.println(id);
        return "sucess";
    }
```

在requestMapping中制定id，传到haha()方法中去，这就是PathVariable的作用。

如果不指定PathVariable的值，其就是通过变量名来查找对应的变量值，比如：
```
@RequestMapping(value="test/{id}/{id2}",method = RequestMethod.GET)
    public String test(@PathVariable int id,@PathVariable int id2){
        System.out.println(id +"," + id2);
        return "sucess";
    }
```

@RequestMapping的占位符也可以标记在类上。

也可以使用正则来表示占位符，其方式为{varname:reg}
即使，第一个参数是参数名，第二个是正则表达式，比如`@RequestMapping("springtest/{id3:[a-z]+}")`

## MatrixVariable

如果要允许矩阵变量的使用，你必须把RequestMappingHandlerMapping类的removeSemicolonContent属性设置为false。该值默认是true的。而使用MVC的命名空间配置时，你可以把<mvc:annotation-driven>元素下的enable-matrix-variables属性设置为true。该值默认情况下是配置为false的。
在beans的文件下配置：
```
<mvc:annotation-driven enable-matrix-variables="true"/>

```

其url符合以下模式，就是使用到了矩阵变量：`/cars;color=red;year=2012`，`color=red,green,blue`，`color=red;color=green;color=blue`。
取得矩阵变量值的样例：
```
// GET /pets/42;q=11;r=22

@RequestMapping(path = "/pets/{petId}", method = RequestMethod.GET)
public void findPet(@PathVariable String petId, @MatrixVariable int q) {

    // petId == 42
    // q == 11

}
```
有时候需要矩阵变量的精确定位：
```
// GET /owners/42;q=11/pets/21;q=22

@RequestMapping(path = "/owners/{ownerId}/pets/{petId}", method = RequestMethod.GET)
public void findPet(
    @MatrixVariable(name="q", pathVar="ownerId") int q1,
    @MatrixVariable(name="q", pathVar="petId") int q2) {

    // q1 == 11
    // q2 == 22

}
```

当然也可以定义其不是必须出现的，并给一个默认值：
```
// GET /pets/42

@RequestMapping(path = "/pets/{petId}", method = RequestMethod.GET)
public void findPet(@MatrixVariable(required=false, defaultValue="1") int q) {

    // q == 1

}
```

## 其他
对于RequestMapping还有一些其他的过滤规则，比如consumes对应请求头中的content-type,produces对应请求头的中的Accept，可到最开头给出的文档中进行查看。



# REST
HTTP协议里有四个标识操作方式的动词：GET,POST,PUT,DELETE，分别对应数据库中的查询,新建，更新和删除。
示例：
/order/1 GET :表示查询id=1的order
/order/1 DELETE：表示删除id=1的order
/order/1 PUT: 表删除id=1的order
/order POST:新增order，注意不是表是一条记录。


一般来讲在网页中只有get,post请求，那么如何将get,post请求转换为get,post,put,delete请求呢？
在springMVC有这么一款过滤器叫HiddenHttpMethodFilter，其可以通过过滤请求中的隐藏的input，形式如下：
```
<input type="hidden" name="_method" value="DELETE">
```
该过滤器取得请求中的hidden域后，取得其中的值，然后将post请求转换为对应的DELETE,PUT,POST等请求。
如查看该过滤器的源码即可发现，其中的_method是固定的。

那么如何使用？
首先在web.xml中配置该过滤器。
然后测试代码如下：
```
1.get请求
<a href="springtest/test/1">get</a>
2.post请求
<form method="POST" action="springtest/test/1">
	<input type="submit" value="post请求"/>
</form>
3.delete请求
<form method="POST" action="springtest/test/1">
	<input type="hidden" name="_method" value="DELETE" />
	<input type="submit" value="delete请求"/>
</form>
4.put请求
<form method="POST" action="springtest/test/1">
	<input type="hidden" name="_method" value="PUT" />
	<input type="submit" value="put请求"/>
</form>
```
在每次访问/*类型的网页的时候，服务器都会调用过滤器，将GET,POST等请求过滤一遍

而对于处理器中的写法如下：
```
 @RequestMapping(value="test/{id}",method = RequestMethod.GET)
    public String test(@PathVariable("id") int id){
        System.out.println(id);
        return "sucess";
    }
```
其中method通过RequestMethod枚举来指定对应的方法。


这里要说明一点，如果一个web.xml要配置多个filter的话，每个filter标签是必须在filter-mapping之上的。否则就会报错，解决方法是将顶头的规则删除，但是不建议，那么正确写法如下：
```
    <filter>
        <filter-name>CharacterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>utf-8</param-value>
        </init-param>
    </filter>
    <filter>
        <filter-name>hiddenFilter</filter-name>
        <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
    </filter>
    
    <filter-mapping>
        <filter-name>CharacterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <filter-mapping>
        <filter-name>hiddenFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```

# RequestParam

在前边讲过PathVariable，这个注解是为了和RESTful风格的url相匹配，并不是请求的参数，如果是使用form传递的参数，就可以使用正常的RequestParam来取得参数值，使用方法如下：
```
 @RequestMapping("test")
    public String heihei(@RequestParam("username") String username){
        System.out.println(username);
        return "sucess";
    }
```
上述示例获取名叫username的参数，测试url为：/springtest/test?username=haha
但是这种写法也有坏处，如果不指定参数，就会报错，那么可以这样写：`@RequestParam(value="username",required=fasle)`,表示参数不是必须的，如果我必须要参数，没有参数的时候给个默认值怎么弄？那么可以这样写:`@RequestParam(value = "username",required = false,defaultValue = "0")`,如果不指定默认值，则默认值为"null"那么如果要传入的对象是基本类型，则必然会报错，这点一定要注意。

不仅仅有RequestParam还有一个注解是RequestHeader是用来处理请求头的，这里就不记录了


# CookieValue

用来传递Cookie值

例如`@CookieValue("JSESIONID")`用来获取JSESIONID的cookie值,其参数其实也有required,defautlValue等。

# POJO
使用RequestMapping来进行映射的时候，将传来的参数直接封装为对象，如有以下JavaBean对象：
```
public class Car {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Car{" +
                "name='" + name + '\'' +
                '}';
    }
}

```
以及：
```
public class User {
    private String name;
    private int age;

    private Car car;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", car=" + car +
                '}';
    }
}

```
其中User中有一个Car的引用，那么对于HTML中这样来写：
```
<form method="GET" action="/springtest/user">
    <input type="text" name="name" />
    <input type="number" name="age" />
    <input type="text" name="car.name" />
    <input type="submit" />
</form>
```
接着其映射方法如下：
```
  @RequestMapping("user")
    public String getUser(User user){
        System.out.println(user);
        return "sucess";
    }
```

即使没有制定@RequestParam，也将所传的参数成功封装成了User对象。可以看到其中form中对于级联属性的指定方式为car.name。

# Servlet原生API
可以向RequestMapping指定的方法中传入Servlet原生的API:HttpServletRequest,HttpServletResponse,HttpSession,java.security.Principal,Locale,InputStream,OutputStream,Reader,Writer.等。如：
```
 @RequestMapping("servlet")
    public String servlet(HttpServletResponse response){
        System.out.println(response);
        return "sucess";
    }
```


# 处理模型数据

## ModelAndView
该对象包含数据信息和模型数据。

可以定义一个ModelAndView,添加数据后将来该对象返回，如：
```
   @RequestMapping("modelAndView")
    public ModelAndView modelAndView(){
        ModelAndView mv = new ModelAndView("sucess");
        mv.addObject("name","qxg");
        return mv;
    }
```
其构造的时候，要传入返回的试图，比如sucess，那么就会映射到sucess.jsp中，也可以使用mv.setViewName来指定视图。


## 传入Map
如：
```
   @RequestMapping("map")
    public String map(Map<String,String> map){
        map.put("name","qxg");
        return "sucess";
    }
```
那么其map中的参数，而已在视图中获取到。
 

## ModelAttribute

ModelAttriibute标记的方法会在@RequestMapping之前被调用。
@ModelAttribute方法通常被用来填充一些公共需要的属性或数据。
一个控制器可以拥有数量不限的@ModelAttribute方法。同个控制器内的所有这些方法，都会在@RequestMapping方法之前被调用。


如：
```
    @ModelAttribute
    public void testModelAttribute(Map<String,Object> map){
        User user = new User();
        user.setAge(11);
        user.setName("hah");
        Car car = new Car();
        car.setName("xuefulan");
        user.setCar(car);

        System.out.println("hhh");
        map.put("user",user);
    }


    @RequestMapping("user")
    public String getUser(User user){
        System.out.println(user);
        return "sucess";
    }
```


在调用getUser()方法的时候，首先会调用testModelAttribute方法，并把其user传入到map中，而在getUser方法调用的时候，其user的来源就是在map中取得的，然后调用set方法，来设置从url取得的参数值。
所以，如果url中上传的user的字段缺一段，那么对其在数据库中本来的值是没有影响的。

对于user，其来源：
它可能因为@SessionAttributes注解的使用已经存在于model中
它可能因为在同个控制器中使用了@ModelAttribute方法已经存在于model中
它可能是由URI模板变量和类型转换中取得的（下面会详细讲解）
它可能是调用了自身的默认构造器被实例化出来的



# 视图和视图解析器

在Controller调用任何一个方法，其返回值可以是String,ModelAndView,View等对象，但是不论其是什么对象，最终都会被转换为ModelAndView对象。
SpringMVC通过视图解析器(ViewResolver)得到最终的视图(View)，最终的视图可以是JSP,也可能是Ecel,JFreeChart等各种表现形式的视图。最终调用view的render方法将视图进行渲染。


SpringMVC中视图解析器有多种，比如InternalResourceViewResolver,是JSP最常见的视图技术，其配置如下：
```
    <!-- ViewResolver -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
```

通过该视图解析器可以制定前缀和后缀。


如果不需要将某请求通过一些列的控制器，而是直接转发到某个页面，可以通过<mvc:view-controller>标签，如：
```
<mvc:view-controller path="/sucess" view-name="sucess"/>
```
如果遇到/sucess标签，则直接转发到prefix + "sucess" + suffix视图中。
但是如果使用了该标签，对于其他的处理器中的映射就会出现问题，那么此时需要<mvc:annotation-driven>标签，所最终的写法应该如下：
```
    <mvc:view-controller path="/sucess" view-name="sucess"/>
    <mvc:annotation-driven></mvc:annotation-driven>
```

## 自定义视图解析器
springMVC有一个自带视图解析器：`BeanNameViewResolver`，该解析的作用是，控制器返回一个视图名后，比如返回"user"，那么BeanNameViewResolver解析器就会去查找一个名叫User的类，该类必须实现View接口。然后通过该User类调用render方法进行视图解析。

如果beans配置中配置了多个视图解析器，那么其优先级是通过order属性进行排序，order值越小，优先级越高，对于InternalResourceViewResolver,它的order值是Integer.MAX_VALUE，所以可想而知，其优先级是最低的。

那么如果beans配置中存在InternalResourceViewResolver和BeanNameViewResolver时，在BeanNameViewResolver没有找到对应的视图的时候，其会通过InternalResourceViewResolver来查找prifix + returnValue + suffix视图。

对于自定义的视图，其写法如下：
```
public class HelloView implements View {
    public String getContentType() {
        return "text/html";
    }

    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.getWriter().print("heheh");
    }
}

```
第一个方法返回视图的类型，第二个就是调用responce来打印输出的内容。

那么自定义视图中的render方法其实做的东西有很多，比如可以通过其他插件来写一个Excel等。

## 视图重定向和转发
在控制器中调用某一特定的方法后，会通过视图解析器进行解析，如果返回的String是下面这种：
`redirect:/index.jsp`，那么其就会重定向到根目录下的index.jsp中，同样`forward:/index.jsp`会转发到index.jsp中。

# 数据绑定
流程:
1. springMVC将ServletRequest对象及目标方法的入参实例传递给WebDataBinderFactory实例，以创建DataBinder实例对象
2. DataBinder调用装配在SpringMVC上下文的ConversionService组件进行数据类型转换，数据格式化等工作。将Servlet中的请求信息填充到入参对象中。
3. 调用Validator组件对已经绑定了请求消息的入参对象进行数据合法性校验，并最终生成数据绑定结果BindingData对象。
4. springMVC抽取BindingResult中的入参对象和校验错误对象，将它们赋给处理方法的响应入参。


# 转换器
如果想要将String自动转换为自己的User对象，那么就需要使用Convert接口来实现，并且在beans的配置文件中配置。
如：
```
public class String2UserConvert implements Converter<String,User> {
    public User convert(String s) {
        User user = new User();
        user.setName("convert");
        user.setAge(18);
        return user;
    }
}

```

然后在beans的配置文件中进行配置：
```
   <bean class="org.springframework.context.support.ConversionServiceFactoryBean">
       <property name="converters">
           <set>
               <bean class="com.qxg.convert.String2UserConvert"/>
           </set>
       </property>
   </bean>

```

# springMVC处理静态资源
在视图中可能需要一些静态的js文件，但是由于springMVC会拦截请求，并通过控制器等映射到其他请求，所以这些资源无法被正常获取。

解决方法就是配置<mvc:default-servlet-handler />
配置方法是：
```
    <mvc:default-servlet-handler/>
    <mvc:annotation-driven/> <!--防止RequestMapping失效-->
```

default-servlet-handler会在springMVC上下文中定义一个DefaultServletHttpRequestHandler，它会对进入DispatherServlet的请求进行筛选，如果发现是没有被映射的请求，就会交给服务器默认的Servlet进行处理。

所以对于js文件，DispatherServlet并不会处理。
# mvc:annotation-driven
实际开发中，通常要加入该配置。

mvc:annotation-driven会自动注册RequestMappingHandlerMapping,RequestMappingHandlerAdapter,ExceptionHandlerExceptionResolver三个bean.还将提供以下支持：
* 支持使用`ConversionService`实例对表单参数进行类型转换
* 支持使用@NumberFormatannotataion,@DateTimeFormat注解完成数据类型的格式化
* 支持使用@Valid注解对JavaBean实例进行JSR303 验证
* 支持使用@RequestBody和@ResponseBody注解

如果不加该配置，是没有ConversionService，所以在使用自定义转换器的时候，通常要加上该配置，如果加上<mvc:annotation-driven>会有一个默认的COnversionService的，如：
```
  <bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
       <property name="converters">
           <set>
               <bean class="com.qxg.convert.String2UserConvert"/>
           </set>
       </property>
   </bean>

    <mvc:annotation-driven conversion-service="conversionService"/>
```

如果既没有配置mvc:default-servlet-handler也没有配置mvc:annotation-driven,则springMVC中默认有HttpRequestHandlerAdapter,SImpleControllerHandlerAdapter,AnnotaionMethodHandlerAdapter。
而配置了mvc:default-servlet-handler后，springMVC将不会配置ANnotationMethodHandlerAdapter.
如果两者都配置了，则会多加一个RequestMappingHandlerAdapter。

注意AnnotationMethodHandlerAdapter是spring3.0之前用于支持RequestMapping的类，spring3.0之后过时，替换的是RequestMappingHandlerAdapter.


所以一般在beans的配置中要加上以下的配置：
```
<mvc:default-servlet-handler/>
<mvc:annotation-driven />
```


# InitBinder
由@InitBinder标识的方法，可以对WebDataBinder对象进行初始化。WebDataBinder是DataBinder的子类，用于完成由表单字段到JavaBean属性的绑定。
@InitBinder方法不能有返回值，必须为void
@InitBinder方法的参数通常是WebDataBinder

比如WeDataBinder中有一方法是，w.setDisallowedFields(String ...)该方法是设置某个表单数据，不会被映射到对应的JavaBean中。


# 数据格式化
如果要传入一个日期字符串"1990-11-13"，那么在springMVC中，如何把这个字符串转换为Date类型的数据呢？首先要在JavaBean格式化，比如：
```
@DateTimeFormat(pattern = "yyyy-MM-dd")
private Date date;
```
对于这种格式化的注解还有很多，比如NumberFormat等。

# 数据校验
通过JSR 303进行数据校验，在校验的时候，首先要进行数据格式化，即在JavaBean中添加数据格式化的注解，而在校验的时候，即在入参实例前加上@Valid即可.


# 处理json

1. 加入`jackson-annotation.jar`,`jackson-core.jar`,`jackson-databind.jar`等三个jar包
2. 编写目标方法，使得其返回JSON对应的对象或集合
3. 在方法上添加@ResponseBody注解
如：
```
@ResponseBody
@RequestMapping("/getUser")
public User getUser(){
	User user = userDao.get();
	return user;
}
```




