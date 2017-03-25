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

