---
title: Struts2学习
date: 2017-02-11 23:44:07
tags:
---

j2ee学习三----Struts2

<!--more-->

# action
首先需要一个简单的例子
1. 创建一个Web project,起名为Strust2_first
2. 接着将自己下载的strust2里面的示例包解压，将来WEB-INF下的lib复制到Strust2_first项目的lib下
3. 将示例文件里的web.xml中的两个filter开头的标签复制到自己的web.xml中如：
```
<filter>
    <filter-name>struts2</filter-name>
    <filter-class>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter</filter-class>
</filter>

<filter-mapping>
    <filter-name>struts2</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
4. 将示例文件下的WEB-INF下的classes下的 strus.xml复制到Strust2_first项目的src下，然后修改后如下：
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE struts PUBLIC
	"-//Apache Software Foundation//DTD Struts Configuration 2.3//EN"
	"http://struts.apache.org/dtds/struts-2.3.dtd">

<struts>

<!--  
    <constant name="struts.enable.DynamicMethodInvocation" value="false" />

-->
    <!-- Add packages here -->
    <constant name="struts.devMode" value="true" />    <!-- 该选项配置的意思是启用开发模式，修改文件后，tomcat服务器可以立即响应 -->

   <package name="default" namespace="/" extends="struts-default">
        <action name="hello">
        	<result>/index.jsp</result>
        </action>
    </package>
</struts>

```


这样配置就完成了，然后开启服务器运行项目，地址栏输入http://localhost:8080/Strust_first/hello 就会发现显示的是index.jsp页面


## namespace
action中的namespace决定action的访问路径，默认为""，可以接受所有路径的action,如上述中的struts.xml中的配置，package中有个属性是namespace，他的值是"/",该namespcae也可以写作/xxx/yyy，则此时对应hello访问的方式就是/xxx/yyy/hello。

如果namespcae为默认值""或者"/"，则无论在地址栏输入/xxx/yyy/hello,还是/hello,还是/xxx/hello，则都会由该package处理，都会返回index.jsp.

## action执行流程
1. 客户端敲入地址
2. 服务端首先读取web.xml，web.xml读取到strust2的配置，然后交给相应的类处理
3. 该类需要读取strus.xml，读取到对应的package，找到package对应action的class,执行相应的方法execute()后取得返回值
4. 找到返回值和resutl标签值相对应的值，比如返回为"sucess"则<result name="sucess">/index.jsp</result>，则就会访问index.jsp文件。

比如访问locahost:8080/Strust2_first/hello，则服务器执行流程为，首先访问web.xml，接着访问strust.xml,在strust.xml找到一个package,该package的namespace为"/"跟当前的路径匹配，接着找到一个name为hello的action,访问该action对应的class类，如果没有指定方法，则执行该类中的execute()方法，该方法返回一个字符串，然后通过该字符串找到对应的result，去访问对应的jsp文件。

## action属性
1. name,表示访问的时候书写的名字，比如name="hello"，则访问该action,就是/hello
2. class，表示访问到该action后，执行该类，比如class="org.xxx.yyy.ActionTmp",则会找到该ActionTmp类
3. method，表示匹配到该action后，执行class类中的某个方法

如：
```
<action name="hello" class="com.tmp.xxx.TmpAction" method ="add">
  <result name="sucess">/index.jsp<result>
</action>
```

如果我想要指定执行class对应的add方法，可以用以上方式，还有一种方式，不用指定method,在访问该action的时候，在后边加上!add，如：locahost8080/Strust2_first/hello!add,这样就能执行对应的add方法了。一般推荐第二种，第二种就叫做DMI，动态方法调用

但是注意，如果在struts.xml中配置
```
<constant name="struts.enable.DynamicMethodInvocation" value="false" /> 
```
这一项后，DMI即动态方法调用会关闭，即使用！将无法调用相应的方法，会显示出错
## 通配符

使用通配符可以将配置量降到最低，如：

```
<action name="Hello*" class="com.tmp.TmpAction" method="{1}">
  <result>/Hello{1}.jsp</result>
</action>
```

其中{1}表示第1个*，这个*就是通配符，如果访问HelloWorld,则*就会对应World,则此时{1}的值就是World,那么method的方法就是World()，同时返回的是HelloWorld.jsp。

同理如：

```
<action name="*_*" class="com.tmp.{1}Action" method="{2}">
  <result>/{1}_{2}.jsp</result>
</action>
```
该种方法对应的则比较灵活。
比如访问Student_add，则对应的类为StudentAction,同时对应的方法是add()方法，那么返回的结果是Student_add.jsp，
同样的访问Teacher_delete，则对应的类为TeacherAction,同时对应的方法是delete()方法，那么返回的结果是Teacher_delete.jsp

## action传参数
action传参接收参数有三种方式

比如访问locahost:8080/Strust2_first/hello?name=a&age=2


### 第一种，用的最多
在action对应的class文件中，添加两个私有类型变量，并且设置对应的set,get方法，如
```
public class FirstAction extends ActionSupport{

	private String name;
	private int age;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		return super.execute();
	}
}

```
注意，其中private String name，这种变量的名字是可以改的，但是setName()以及getName()方法名字是不可以改的，因为获取name变量，是一定会调用setName，getName方法的。

### 第二种DomainModel
首先要设置bean对象，如：

```
public class User{
  private String name;
  private int age;

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

```
接着修改FirstAction,注意也要加上set,get方法

```
public class FirstAction extends ActionSupport{

	private User user;

	public int getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		return super.execute();
	}
}

```
这样访问的方式就变为:locahost:8080/Strust2_first/hello?user.name=a&user.age=2

### 第三种，不常用
ModelDriven
该方式是使Action类实现ModelDriven，如
```
public class FirstAction extends ActionSupport implements ModelDriven<User>{

	private User user = new User(); //注意这里要自己new,系统不会自动给你new的

  @Override
	public User getModel() {
		return user;
	}

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		return super.execute();
	}
}

```
访问方式为：locahost:8080/Strust2_first/hello?name=a&age=2
注意这种方式的执行流程是struts会判断Action是否实现了ModelDriven接口，实现的话就会调用getModel返回对应Model,然后执行Model中对应的set,get方法。

## 简单数据校验

在Action中只能返回String字符串，但是我想要再Action的方法中传递给JSP一些错误信息的时候怎么办呢？

此时需要一个方法this.addFiledError("name","this is why name is error");
这个时候跳转到的jsp怎么获取到错误信息呢？
首先指定标签库，注意其中前缀名是可改的。
```
<%@taglib url="/strust-tags" prefix="s" %>
```
接着获取方法为:
```
<s:fielderror filedName="name" />
```

`<s:debug></s:debug>`该标签可以进行debug，常用
`<s:property value="errors" />`常用，取出debug中的ValueStack中的值

## 访问Web元素，request,session,application

简单来说这个问题就是，如何在Action类中，取得request,session,application对象呢？

### 取得map型对象

在Action中写下面代码：
```
    private Map request;
    private Map session;
    private Map application;

    public MyFirstAction(){
        request = (Map) ActionContext.getContext().get("request");
        session = ActionContext.getContext().getSession();
        application = ActionContext.getContext().getApplication();
    }
```

即在构造函数中，使用ActionContext类来取得上下文并获取request,session,application等Map对象，注意因为获取的是Map对象，所以给某一个对象赋参，就是使用put方法，如request.put("name","msg");

之后，再在.jsp文件中，如何获取在Action中给request传递的参数呢，方法如下：

```
<%@taglib url="/strust-tags" prefix="s" %>

<s:property value="#request.name" />
或
<%=request.getAttribute("name") %>
```

- - -
另一种方式是使用的依赖注入的方式，即不需要自己去获取，而是别人获取后给自己注入进来，需要实现RequestAware,SessionAware,ApplicationAware等接口，并实现其中的setRequest,setSession,setApplication等方法。如代码：

```
public class MyFirstAction extends ActionSupport implements RequestAware,SessionAware,ApplicationAware{

    private Map<String,Object> request;
    private Map<String,Object> session;
    private Map<String,Object> application;

    @Override
    public void setApplication(Map<String, Object> map) {
        this.application = map;
    }

    @Override
    public void setRequest(Map<String, Object> map) {
        this.request = map;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session  map;
    }

    @Override
    public String execute() throws Exception {
        return "add";
    }
}

```
利用依赖注入方式是比较好的，降低耦合，俗称解耦，所以一般建议使用的时候只用该种方式。
### 取得HttpServletRequest，HttpSession,ServletContext等对象方式

其实需要拿到HttpServletRequest,然后通过该对象获取其他两个，如代码
```
public class MyFirstAction extends ActionSupport {


    private HttpServletRequest request;
    private HttpSession session;
    private ServletContext application;

    public MyFirstAction(){
        request = ServletActionContext.getRequest();
        session = request.getSession();
        application = session.getServletContext();
    }

    @Override
    public String execute() throws Exception {
        return "add";
    }

}
```
- - -
同样使用DI的方式是：
```
public class MyFirstAction extends ActionSupport implements ServletRequestAware{


    private HttpServletRequest request;
    private HttpSession session;
    private ServletContext application;

    @Override
    public String execute() throws Exception {
        return "add";
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
        session = request.getSession();
        application = session.getServletContext();
    }
}
```
当然，依然是建议使用第二种DI的方式。

## 默认Action
```
    <package name="default" namespace="/" extends="struts-default">
        <default-action-ref name="hello"></default-action-ref>
        <action name="hello" class="com.qxg.action.MyFirstAction" >
            <result name="me">/index.jsp</result>
            <result name="add">/add.jsp</result>
        </action>
    </package>
```
在上述代码中出现了<default-action-ref name="hello"></default-action-ref>
这句话表示的意思是，如果在地址栏中敲入了一个不存在的地址，或者没有敲下任何的action，这个时候就会交给默认的action处理，在这个地方name="hello"，即会交给hello这个action去处理，或者是说，相当于在地址栏中敲下了/hello

## result配置
如：
```
<result name="sucess">/index.jsp</result>
```
### result类型
什么是result类型呢，其实就是 <result type=“dispatcher”>/index.jsp</result>中的type，如果不写的话默认就是dispatcher，意思就是服务器跳转。
大致分为如下：

1. dispatcher，服务器跳转，只能跳转.jsp，不能跳转到action，但是地址栏上显示的是action的内容
2. redirect ,客户端跳转，同上，但是地址栏上显示.jsp的内容
3. chain， 访问action，服务器跳转
4. redirectAction 访问action,客户端跳转

前两个常用,后两个不常用

```
	<!-- 跳转r1.jsp页面，地址栏显示的是/r1 -->	
        <action name="r1">
            <result type="dispatcher">/r1.jsp</result>
        </action>
		
	<!-- 跳转r2.jsp页面，地址栏显示的是/r2.jsp -->	
        <action name="r2">
            <result type="redirect">/r2.jsp</result>
        </action>

	<!-- 跳转r1.jsp页面，地址栏显示的是/r3 -->
        <action name="r3">
            <result type="chain">/r1</result>
        </action>

	<!-- 跳转r2.jsp页面，地址栏显示的是/r2.jsp -->
        <action name="r4">
            <result type="redirectAction">/r2</result>
        </action>
```

## GlobalResult
顾名思义，全局result，如果很多action公用一个result，那么就可以把这个result写到global-results标签内：

```
<global-results>
	<result name="global">/global.jsp</result>
</global-results>
```

这个是对于相同包的情况，但是如果另一个包也想访问global型的result呢？

这个时候，我们就可以让这个包继承于一个包含global-results的包，即：

```
<package extends="xxx"></package>
```

## DynamicResult（用的不多）
根据Action类文件中赋值的变量不同而跳转到不同页面，直接来看代码：

```
public class MyFirstAction extends ActionSupport{

    private String r;

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    @Override
    public String execute() throws Exception {
        r = "/add.jsp";
        return "success";
    }

}

```

接着是struts.xml配置文件中的action
```
<action name="hello" class="com.qxg.action.MyFirstAction">
     <result>${r}</result>
</action>
```
如上，即要在Action，设置String 变量，并设置set,get方法，这时候，表示此action的属性中有了r，那么在struts.xml中就可以通过${r}来把r值给取出来，可以通过<s:debug></s:debug>可以看到堆栈中存在r的值。

假设地址栏中传入action一些参数，如：hello?name=a,类似这样的方式，传入name参数，然后在Action类中使用set,get方法来取得name,然后就可以在struts.xml中通过${name}，来取得其值。

# OGNL（Object graph navigation language）
我们已经熟知了<s:property value="name">这种取值方式，而OGNL就是value中的内容。
其实就是访问对象中的某值，比如一个Cat对象中有一个Dog变量friend,那么访问该friend的名字的方法就是cat.friend.name，这种方式就是ognl，同样，地址栏传值也是使用这种方式
在ognl同样可以使用对象中的方法。
如
```
<s:property value="xx.toString()" />
```
XXXXX表示包的全名
访问静态方法：@XXXXX@y()
访问静态属性：@XXXXX@Y
访问Math静态方法：@@max(2,3) ,注意@@就是表示Math类，其他不管。
访问构造方法：new XXXXX() 
访问List：直接写名字就List名字就行，如class中ArrayList<String> list; 那么就写list
访问List中某个元素list[1]
访问List中某个属性的集合 list.{age}，表示将list所有的元素取出age之后组成的集合
访问set和List基本一样，但是取不到单个元素，因为无序
访问Map :tmpMap
访问Map单个元素：tmpMap.key,或者tmpMap.['key'],注意要用单引号
访问Map所有的key，tmpMap.keys，同样tmpMap.values，则是所有value的集合

投影，即过滤 ：users.{?#this.age=1}.{age},表示把users里面所有age等于1 的集合，然后再去取出该集合中的所有age的集合。
还有一些写法如下：
users.{^#this.age=1}.{age} 、users.{$#this.age=1}.{age}
其中?#表示所有符合条件的，^#表示第一个符合条件的，$#表示最后一个符合条件的。但是虽然返回一个元素，但是依然是集合。

利用中括号可以访问<s:debug></s:deubg>中的对象，其中Action类对象永远在第一位，即[0]就是一个自己定义的Action对象，可以通过该方法访问其中的method，或者静态变量等。

# struts标签

## 通用标签

### property
```
<s:property value="username" /> 表示取出传入的参数username值
<s:property value="'username'" /> 表示普通的字符串
<s:property value="tmp" default="管理员" /> 表示tmp取不出值是，该值就为管理员
<s:property value="'<hr />'" escape="false" /> escape默认为true,如果为false,表示，输出的结果不会转换为字符串，而可以让浏览器去解析。
```


### set
```
<s:set var="adminName" value="username" />表示设定adminName的值为username,默认会放到request，ActionContext里面。注意不会在浏览器中输出。
取值为：<s:property value="#request.adminName" />
那如果想把值放入其他对象呢，这个时候就要用scope，如：
<s:set var="adminName" value="username" scope="application" />这个时候就只能从application中取值
```

### bean

```
<s:bean name="com.qxg.Dog" var="myDog">
	<s:param name="name" value="'hashiqi'" />
</s:bean>
```
表示创建一个叫myDog的Dog对象，其中param是设置属性，通过set方法。

### include （少用）

```
表示包含一个jsp等页面
<s:include value="myJsp.jsp">
注意，其中的value不论你写什么，都不会被解析，都会当做字符串处理，那么如果是ognl表达式怎么办呢？
比如，value的值要取request中的某值，那么就需要使用%{}来解析，
如<s:include value="%{#request.xxjsp}">,这样就会把request中的xxjsp值取出来，如果不加%{},那么就会寻找/#request.xxjsp，这样一定会出错。

```

## 控制标签

### if

```
其中test数据类型是boolean型，可以直接在里面写 xxx > y ,或xxx == y
<s:if test="%{false}">
    <div>不会显示</div>
</s:if>
<s:elseif test="%{true}">
    <div>会显示</div>
</s:elseif>
<s:else>
    <div>不会显示</div>
</s:else>
```

注意if也可以单独使用

### iterator

```
<s:iterator value="{1,2,3}">
	<s:property>
<s:iterator>
内部会执行3次，每次的值是1,2,3，所以输出的结果是123

<s:iterator value="{'aaa','bbb','ccc'}" var="x">
	<s:property value="#x.toUpperCase()"/>
</s:iterator>
其中var表示的意思是每次取出来的值的名字都是x，那么就可以拿着x进行各种操作了。 
如何取得当前遍历的个数呢？
<s:iterator value="{'aaa','bbb','ccc'}" status="status">
	遍历过的元素总数<s:property value="#status.count"/>
	遍历的元素索引 <s:property value="#status.index" />
	当前是偶数？ <s:property value="#status.even" />
	...
</s:iterator>
```

# 异常处理

我们在写Action类，继承于ActionSupport的时候，重写execute方法，发现该方法会抛出异常，那么抛出的异常由谁类处理呢？这个时候，在配置文件中，某个标签就起到了作用，如代码：
```
<action name="tmp" class="com.qxg.TmpAction">
	<result name="success">/index.jsp</result>
	<exception-mapping result="error" exception="java.sql.SQLException"/>
	<result name="error">/error.jsp</result>
</action>
表示，如果发生了SQLException异常，就会返回error的结果，然后对应就会返回error.jsp页面。

那么如果要配置全局的异常处理，就需要再exception-mapping写在global-exception-mapping标签下，但是global-exception-mapping要写在package标签下。
```

# 自定义拦截器

首先自定义拦截器类，并实现Interceptor接口，注意该接口是xwork中的

```
public class MyInterceptor implements Interceptor {
    @Override
    public void destroy() {
        System.out.println("Interceptor is destroy");
    }

    @Override
    public void init() {
        System.out.println("Interceptor is init");
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        long start = System.currentTimeMillis();
        String r = actionInvocation.invoke();
        long end = System.currentTimeMillis();
        System.out.println("invoke runnig in " + (end - start) + "ms");
        return r;
    }
}

```

接着在struts.xml中进行配置
```
        <interceptors>
            <interceptor name="my" class="com.qxg.interceptor.MyInterceptor"/>
        </interceptors>


        <action name="hello" class="com.qxg.action.MyFirstAction">
            <result>/index.jsp</result>
            <interceptor-ref name="my"/>
            <interceptor-ref name="defaultStack"/>
        </action>
```
可以看到，在package中的interceptors中把该interceptor给写出来，名字叫my，然后在action中使用引用把my拦截器引用过来，注意defaultStack拦截器一定要写在下方。

## 使用token拦截器解决重复提交问题(很少用)
首先说明该问题是什么问题，平常开发的时候经常会碰到，如果通过表单向a.jsp提交数据，然后到a.jsp页面后，我们刷新就会碰到一个提示，说要重新提交数据，是否继续，这个时候，我们就会点继续，然后就会向后台重新提交数据，那么如果碰到网速慢的情况下，后台已经接受到数据，但是因为网速慢，前台一直不显示，这个时候用户就会不停的刷新，不停的提交数据，这个时候，容易导致后台垃圾数据过多，用户阻塞等问题。

对于form提交问题，如果使用get方法，不停刷新，页面不会提示，而使用post，则刷新过程，页面会进行提示，所以一般form都通过post方式提交，这样可以通过在客户端解决重复提交问题，当然，这个也是要取决于用户。

token表示令牌，表示客户端传来数据，如果跟令牌不对应，则不允许通过。

比如下面的例子，在.jsp页面中，写上<s:token></s:token>代码，然后在客户端访问该页面，查看源代码后，就发现客户端多了一处这样的代码：

```
  <input type="hidden" name="struts.token.name" value="token" />
<input type="hidden" name="token" value="KGMG30A8VTBVQ6X4CIH1COUOQVJ67O4A" />
```

注意看其中token这个地方有个value,那么这是什么意思呢？

其实这个token就是解决重复提交的，在客户端访问.jsp界面的时候，服务器会随机生成一个token，然后再写到这个.jsp文件中，当要提交数据时，就会使用该token与服务器进行比较，如果相同，就会将token给删除，表示已提交数据并通过，那么客户端再提交的话，因为token已经没了，就不会再让该客户端提交，这样就防止了重复提交问题。

那么如何在struts中使用呢？

因为struts已经实现了TokenInterceptor，所以就可以通过下列形式
在action中配置下列代码：

```
        <action name="hello" class="com.qxg.action.MyFirstAction">
            <result>/index.jsp</result>
            <interceptor-ref name="my"/>
            <interceptor-ref name="defaultStack"/>
            <interceptor-ref name="token"/>
        </action>
```

但是注意，如果重复提交数据，因为token拦截了，所以result返回的值就是invalid.token，所以还要配置result，所以完整代码如下：

```
        <action name="hello" class="com.qxg.action.MyFirstAction">
            <result>/index.jsp</result>
            <interceptor-ref name="my"/>
            <interceptor-ref name="defaultStack"/>
            <interceptor-ref name="token"/>
            <result name="invalid.token">/error.jsp</result>
        </action>
```
注意一定要加<s:token><s:token>

# 类型转换
简单说明，比如地址栏中传参：/index.jsp?age=6  这个过程已经发生了类型转换，因为地址栏传入的参数一般是string,那么在后台发生了类型转换，把String转换成了int。

## 自定义类型转换器
如果在地址栏中输入了?p=2,3 我们想在后台通过转换器将他转换为Point对象，那么就可以写为下方代码：

```
public class MyConverter extends DefaultTypeConverter {
    @Override
    public Object convertValue(Object value, Class toType) {
        if(toType == Point.class){
            //如果要转换为Point类型，该怎么写..
            Point p = new Point();
            String[] strs = (String[]) value;
            String[] xy = strs[0].split(",");
            p.x = Integer.parseInt(xy[0]);
            p.y = Integer.parseInt(xy[1] );
            return p;
        }
        return super.convertValue(value, toType);
    }
}
```

那么写完注册器，一定要注册，第一种注册方式，是在对应的Action类文件中，创建一个.properties文件
比如在MyAction同包下，要创建MyAction-conversion.properties文件，这个是固定写法，前面是Action类名

然后在该文件中：

```
p=com.qxg.convert.MyConverter
```

这段代码的意思是，如果在地址栏中的参数中碰到了p就交给MyConverter去处理。

第二种注册方式是全局注册，要在src中创建固定名字的文件 xwork-conversion.properties，在内部写上java.awt.Point=com.qxg.convert.MyConverter。

注意为什么要写成Point呢，因为在Action中，对应的p就是Point类型的，而在Action中使用setP的时候，并不知道把地址栏中传入的p的值怎么赋值给Point，这个时候就会到MyCOnverter中执行类型转换。 


