---
title: servlet学习
date: 2017-02-07 22:35:18
tags:
---

j2ee学习二之 servlet学习

<!--more-->

# 什么是Servlet
先有Servlet，才有JSP，JSP前身就是Servlet.

Servlet是在服务器上运行的小程序。一个Servlet就是一个Java类，并且可以通过“请求-响应”编程模型来访问的这个驻留在服务器内存里的Servlet程序。

# Tomcat容器等级
Tomcat的容器分为4个等级，Servlet的容器管理Context容器，一个Context对应一个Web工程。

# 手工编写第一个Servlet
1. 继承HttpServlet
2. 重写doGet()或者doPost()方法
3. 在Web.xml中注册Servlet

web.xml中添加的配置如下
```
<servlet>
  <servlet-name>HelloServlet</servlet-name>
  <servlet-class>servlet.HelloServlet</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>HelloServlet</servlet-name>
  <url-pattern>/servlet/HelloServlet</url-pattern>
</servlet-mapping>
```
其中，servlet标签中的名字和类就不用说了，第二个servlet-mapping中名字和servlet中的名字一一对应，而url-pagttern则和index.jsp所写的href对应，但是index.jsp所些的url是 servlet/HelloServlet。
注意url-pattern其实可以化简，比如只写为/hello,那么访问HelloServlet的方式就是 servlet/hello,注意servlet是包名而已。

# MyEclipse自动创建

如果使用MyEclipse创建servlet，则不需要在web.xml再注册，因为创建过程，MyEclipse会自动帮你创建好。

# Servlet执行流程
GET请求Servlet-> web.xml寻找对应url地址，寻找对应Servlet名，然后再servlet标签找到对应的servlet类->根据请求方式执行doGet或doPost

# Servlet生命周期
init()，在整个生命周期，该方法只被调用一次->响应客户端请求service()，由service()方法根据提交方式选择执行doGet()或doPost()->服务器关闭时，调用destroy()

# Servlet与JSP内置对象的对应关系
1. out -----> response.getwriter()

2. request -----> service()或doGet,doPost中的传递的参数

3. response -----> 同上

4. session ------>  request.getsession(）

5. application ------> getServletContext()

6. exception ----->  Throwable

7. Page -----> this

8. PageContext -----> PageContext

9. Config -----> getServletConfig


# web配置和servlet的部分关系
servlet有很多类,ServletCofig的实现类是HttpServlet,其中ServletConfig 是一个接口,其对应的操作有:
```
public interface ServletConfig {
    String getServletName();

    ServletContext getServletContext();

    String getInitParameter(String var1);

    Enumeration<String> getInitParameterNames();
}
```
最主要的是`getServletContext`和`getInitParameter`,`getServletContext`就是获取应用对象,即application,该对象在工程初始化的时候存在,一个项目工程对应一个该对象,而`getInitParameter`就与web.xml中的配置相对应,如:
```
<servlet>  
    <servlet-name>name</servlet-name>  
    <servlet-class>com.example.MyServlet</servlet-class>  
    <init-param>  
         <param-name>name</param-name>  
         <param-value>张无忌</param-value>  
    </init-param>  
     <init-param>  
         <param-name>age</param-name>  
         <param-value>20</param-value>  
    </init-param>  
  </servlet>  
```
其`getInitParameter`可以取`<init-param>`的值.

`ServletContext`是一个全局对象,代表了一个应用,服务器启动就创建了该对象,每个web应用都有一个唯一的`ServletContext`对象.获取该对象的方式有多种,一种是通过`ServletConfig`或`HttpServlet`来获取,一种是通过`HttpRequest`来获取`HttpSession`之后通过`HttpSession`来获取.

`ServletContext`接口中有很多内容,不再贴了.其也可以获取一些初始化参数,对应web.xml中的就是:
```
<context-param>  
        <param-name>name</param-name>  
        <param-value>嘿嘿</param-value>  
  </context-param>  
   <context-param>  
        <param-name>age</param-name>  
        <param-value>198</param-value>  
    </context-param>  
```

常用的方法有:
`getAttribute(name)`:获取参数值
`setAttribute(String,Object)`:设置参数
`getContext(String path)`:获得某路径对应的ServletContext
`getContextPath()`:获取当前工程的路径
`getInitParameter(String name)`:获取初始化参数值
`getInitParameterNames()`:获取初始化参数名的集合
`getRealPath(String)`:获取在服务器上的路径
`getRequestDispatcher(String path)`:获取对应路径的转发器

对于转发来说,其应用也很简单,如代码:
```

	RequestDispatcher rd = sc.getRequestDispatcher("/servlet/AnotherServlet") ; 
	//转发请求
	rd.forward(request, response) ; 
```


# 初始化参数
在web.xml对应的servlet标签下，添加<init-param>标签,如：

```
<init-param>
	<param-name>username</param-name>
	<param-value>admin</param-value>
</init-param>
```
该配置方式初始化了参数username其值为admin
,那么在对应的servlet中取得参数的值的方式为：

```
this.getInitParameter("username");

```

