---
title: JSP学习
date: 2017-02-05 21:46:54
tags: j2ee
---

开始j2ee的学习之旅，首先要学的就是jsp.

<!--more-->

# JSP页面元素构成
注释 ， 静态内容， 指令， 表达式， 小脚本， 声明

## JSP指令
* page指令： 通常位于JSP页面的顶端，同一个页面可以有多个page指令
* include指令： 将一个外部文件嵌入到当前JSP文件中，同时解析这个页面中的JSP语句
* taglib指令： 使用标签库定义新的自定义标签，在JSP页面中启用定制行为

### PAGE指令
语法： <%@ page value1="" value2="" ...%>

| 属性        | 描述           | 默认值  |
| ------------- |:-------------:| -----:|
| language      | 制定JSP页面使用的脚本语言 | java |
| import      | 通过改属性来引用脚本语言中使用到的类文件      |   无 |
| contentType | 用来指定JSP页面所采用的编码方式      |   text/html,ISO-8859-1 |

## JSP注释
* HTML注释方式，客户端可见
* JSP注释方式：<%-- --%> ,客户端不可见
* 脚本注释： //  /**/

## JSP脚本
即在JSP页面中执行的JAVA代码
语法: <% JAVA代码 %>

## JSP声明
语法：<%! JAVA声明 %>

## JSP表达式
在JSP页面中执行的表达式
语法： <%=表达式 %>


eg:
```
<%!
 /*声明变量*/
 String s = "hah";
%>

<%=s %>
```

# JSP内置对象
## 简介
JSP内置对象是WEB容器创建的一组对象，不适用new关键字就可以使用的内置对象
eg:
```
out.println("hahha");
```

## 常用JSP内置对象
out,request,response,session,application

不常用：Page，pageContext,exception,config

## out对象

JspWriter实例
常用方法:

* void println() 向客户端打印字符串
* void clear()清除缓冲区内容，如果在flush之后调用会抛异常
* void clearBuffer() 清除缓冲区内容
* void flush() 将缓冲区内容输出到客户端
* int getBufferSize() 返回缓冲区以字节数的大小，如不设缓冲区则为0
* int getRemaining() 返回缓冲区还剩余多少可用
* boolean isAutoFlush() 返回缓冲区满时，是自动清空还是抛出异常
* void close() 关闭输出流

注意，如果调用clear()方法之前缓冲区有内容，这个时候缓冲区的内容不被发送到客户端，直接被清除。

## request对象
客户端的请求信息被封装在request对象中，通过它才能了解客户的需求然后做出响应，它是HttpServletRequest类的实例。request对象具有请求域，即完成客户端的请求之前，该对象一直有效。

常用方法如下：

* String getParameter(String name) 返回name制定参数的参数值，说白了，就是获取表单中某一属性值
* String[] getParameterValues(String name)返回包含参数name的所有值的数组，其实就是获取表单某一属性的多值
* void setAttribute(String,Object) 存储此请求中的属性,其实就是修改或添加一个新的表单值
* Object getAttribute(String name) 返回指定属性的属性值，注意和setAttribute配套使用
* String getContentType() 得到请求体的MIME类型
* String getProtocol() 返回请求用的协议类型及版本号q
* String getServerName() 返回接受请求的服务器主机名
* int getServerPort() 返回服务器接收此请求所用的端口号
* String getCharacterEncoding() 返回字符串编码方式
* void setCharacterEncoding() 设置请求的字符串编码方式
* int getContentLength() 返回请求体的长度(以字节数)
* String getRemoteAddr() 返回发送此请求的客户端IP地址
* String getRealPath(String path) 返回一虚拟路径的真实路径
* String getContextPath() 返回上下文路径

如果使用request获取表单值发生乱码，那么在获取前，可以使用request.setCharacterEncoding("utf-8")来设置编码格式。但是如果直接使用uri传参方式，request获取的值是乱码的，该种方式无法解决，这个时候需要配置tomcat服务器，conf/server.xml,修改Connector标签，该标签也可以设置端口号，在该标签后加上 URIEncoding="utf-8"即可，如：
```
<Connector port="8080" protocol="HTTP/1.1"
          connectionTimeout="20000"
          redirectPort="8443" URIEncoding="utf-8"/>
```


## response对象
response对象包含了响应客户端请求的有关信息，但在JSP中很少直接使用。它是HttpServletResponse类的实例。response对象具有页面作用域，即访问一个页面时，改页面内的response对象只能对这次访问有效，其他页面的response对象对当前页面无效。

常用方法：

* String getCharacterEncoding() 返回相应用的是何种字符编码
* void setContentType(String type) 设置响应的MIME类型
* PrintWriter getWriter() 返回可以向客户端输出字符的一个对象
* sendRedirect(String location) 重定向客户端的请求

注：PrintWriter对象向客户端输出的信息总是在内置的out对象前，因为out对象是有缓冲区的，如果想要out对象提前于PrintWriter前，那么可在后边使用out.flush()来清空缓冲区并显示。

### 请求重定向与请求转发
请求重定向：客户端行为，response.sendRedirect(String url),从本质上讲等同于两次请求，前一次的请求对象不会保存，地址栏的URL地址会改变。
请求转发：服务器行为，request.getRequestDispatcher(String url).forward(req,resp) 是一次请求，转发后请求对象会保存，地址栏的URL地址不会改变。

## session对象
什么是session:
* session表示客户端与服务器的一次会话
* Web中的session指的是用户在浏览某个网址时，从进入网站到浏览器关闭所经过的这段时间，也就是用户浏览这个网站所花费的时间
* 从上述定义中可以看到，session实际上是一个特定的时间概念
* 在服务器的内存中保存着不同用户的session

session对象
* session对象是一个JSP内置对象
* session对象在第一个JSP页面被装载时自动创建，注意是自动创建，不是人为创建，完成会话期管理
* 从一个客户打开浏览器并连接到服务器开始，到客户关闭浏览器离开这个服务器结束，被称为一个会话。
* 当一个客户访问一个服务器时，可能会在服务器的几个页面之间切换，服务器应当通过某种办法知道这是一个客户，就需要session对象
* session对象是HttpSession实例。

常用方法：
* long getCreationTime() 返回session创建时间
* String getId() 返回SESSION创建时JSP引擎为它设置的唯一ID号
* Object setAttribute(String name,Object value)使用指定名称将对象绑定到此对话
* Object getAttribute(String name) 返回此会话中的指定名称绑定在一起的对象，如果没有对象绑定在该名称夏，则返回null
* String[] getValueNames() 返回一个包含此SESSION所有可用属性的数组
* int getMaxInactiveInterval 返回两次请求间隔多长时间此SESSION被取消（s）
* setMaxInactiveInterval() 设置失效时间，失效后会重创建，之前所附带的属性也会消失

### session的生命周期
创建：
当客户端第一次访问某个JSP或者serlet时候，服务器会为当前会话创建一个SessionId,每次客户端向服务端发送请求时，都会将此SessionId携带过去，服务端会对此SessionId进行校验。

活动：
* 某次会话当中通过超链接打开的新页面属于同一次会话。
* 只要当前会话页面没有全部关闭，重新打开新的浏览器窗口访问同一项目资源时，属于同一次会话。
* 除非本次会话的所有页面都关闭后再重新访问某个JSP或者Servlet将会创建新的会话。

销毁：
三种：
1. 调用session.invalidate()
2. session过期
3. 服务器重启。
## application对象
* application对象实现了用户间数据的共享，可存放全局变量
* application开始于服务器的启动，终止于服务器的关闭
* 在用户的前后连接或不同用户之间的连接中，可以对application对象的同一属性进行操作
* 在任何地方对application对象属性的操作，都将影响到其他用户对此的访问
* 服务器的启动和关闭决定了application对象的生命。
* 是ServletContext的实例

常用方法：

* public void setAttribute(String name,Object value)使用指定名称将对象绑定到此会话。
* public Object getAttribute(String name) 返回此会话中的指定名称绑定在一起的对象，如果没有对象绑定在该名称下，则返回null
* Enumeration getAttributeNames() 返回所有可用属性名的枚举
* String getServerInfo() 返回JSP(SERVLET)引擎名及版本号

## page对象
page对象就是指当前页面本身，有点类似类中的this指针，它是java.lang.Object类的实例。

常用方法:
* class getClass() 返回此Object的类
* int hashCode() 返回此Object的hash码
* boolean equals(Object obj)
* void copy(Object obj)把此Object拷贝到指定的Object对象中
* Object clone()
* String toString()
* void notify() 唤醒一个等待的线程
* void notifyAll() 唤醒所有等待的线程
* void wait(int timeout) 使一个线程处于等待知道timeout结束或被唤醒
* void wait() 使一个线程处于等待直到被唤醒

##  pageContext对象

* pageContext对象提供了对JSP页面内所有的对象及名字空间的访问
* pageContext对象可以访问到本页所在Session，也可以取本页所在的application的某一属性值
* pageContext对象相当于页面中所有功能的集大成者
* pageContext对象的本类名也叫pageContext

常用方法
* JspWriter getOut()
* HttpSession getSession() 返回当前页面的session对象
* Object getPage()
* ServletRequest getRequest()
* ServletResponse getResponse()
* void setAttribute
* Object getAttribute(String name, int scope)
* int getAttributeScope(String name)返回某一属性的作用范围
* void forward(String relativeUrlPath)使当前页面重导到另一页面
* void include(String relativeUrlPath)在当前位置包含另一文件

## Config对象
config对象是在一个Servlet初始化时，JSP引擎向它传递信息用的，此信息包括Servlet初始化时所要用到的参数(通过属性名和属性值构成)以及服务器的有关信息(通过传递一个ServletContext对象)

常用方法
* ServletContext getServletContext() 返回含有服务器相关信息的ServletContext对象
* String getInitParameter(String name)返回初始化参数的值
* Enumeration getInitParameterNames() 返回Servlet初始化所需所有参数的枚举

## exception对象
异常对象，如果有需要处理的异常，需要在page标签内加入属性 errorPage = "xxx.jsp"来制定哪个页面处理该异常，在xxx.jsp的page标签中，要配置属性isErrorPage = "true"

常用方法：

* String getMessage()返回描述异常的信息
* String toString()
* void printStackTrace()
* Throwable FillInStackTrace()重写异常的执行栈轨迹

# JavaBean

## JavaBean 简介
JavaBeans就是符合某种特定规范的Java类，使用JavaBeans的好处是解决代码重复编写，减少代码冗余，功能区分明确，提高了代码的维护性。

## 设计原则
1. 共有类（public class XXX）
2. 无参的构造方法
3. 属性私有
4. set,get方法获取属性

## Jsp动作元素
(Jsp action elements)动作元素为请求处理阶段提供信息。动作元素遵循XML元素的语法，有一个包含元素名的开始标签，可以有属性，可选的内容，与开始标签匹配的结束标签

### 五类

* 第一类是与存取JavaBean有关的，包括：<jsp:useBean><jsp:setProperty><jsp:getProperty>
* 第二类是JSP1.2就开始有的基本元素，包括6个基本元素：<jsp:include> <jsp:forward><jsp:param><jsp:plugin><jsp:params><jsp:fallback>
* 第三类是JSP2.0新增加的元素，主要与JSP Document有关，包括6个元素：<jsp:root><jsp:declaration><jsp:scriptlet><jsp:expression><jsp:text><jsp:output>
* 第四类是JSP2.0新增的动作元素，主要是用来动态生成XML元素标签的值，包括3个动作元素:<jsp:attribute><jsp:body><jsp:element>
* 第五类是JSP2.0新增的动作元素，主要是用在Tag File中，有2个元素：<jsp:invoke><jsp:dobody>

## 使用

javabean创建在项目文件下的src目录，在JSP中，要使用<%@ page import="com.xx.xxx"%>来导入，然后跟JAVA一样，new一个javabean对象，就可使用。

### useBeans

作用：在jsp页面中实例化或在指定范围内使用javabean

<jsp:useBean id="标识符" class="java类名" scope="作用范围"/>

### setProperty
作用：给已实例化的javabean对象属性赋值，有四种方法：

* <jsp:setProperty name="javabean实例名" property="*" />（跟表单关联，自动创建对应的bean）
* <jsp:setProperty name="javabean实例名" property="javabean属性名" />（跟表单关联，部分匹配）
* <jsp:setProperty name="javabean实例名" property="javabean属性名" value="BeanValue"/>（手工设置，与表单没有关系）
* <jsp:setProperty name="javabean实例名" property="propertyName" param="request对象中的参数名" />(跟request参数关联)

### getProperty
作用：获取指定javabean对象的属性值

<jsp:getProperty name="javabean实例名" property="属性名" />

### javabean四个作用域范围
useBean的时候有个属性是scope就是指定作用域范围，值可取：
* page: 仅在当前页面有效
* request: 可以通过HttpRequest.getAttribute()方法取得javabean对象
* session: 可以通过HttpSession.getAttribute()方法取得javabean对象
* application: 可以通过application.getAttribute()方法取得javabean对象

# Jsp状态管理

## Http协议的无状态性
无状态是指，当浏览器发送请求给服务器的时候，服务器响应客户端请求，但是当浏览器再次发送请求给服务器的时候，服务器并不知道它就是刚才的浏览器。简单的说，服务器不会记住你。

## 保存用户的状态的两大机制
1. Session
2. Cookie

### Cookie
Cookie是服务器保存在客户端的文本信息。

创建Cookie:Cookie newCookie = new Cookie(String key,Object value);
写入Cookie对象：response.addCookie(newCookie);
读取Cookie对象：Cookie[] cookies = request.getCookies();

注意，读取出来的是数组。

常用方法：

* void setMaxAge(int expiry);设置cookie的有效期，以秒为单位
* void setValue(String value)在cookie创建后，对cookie进行赋值
* String getName() 获取cookie名称
* String getValue() 获取cookie的值
* int getMaxAge() 获取cookie的有效时间，以秒为单位


## Session和Cookie的对比
Session:
1. 服务端
2. 保存Object
3. 随会话结束而销毁
3. 保存重要信息

Cookie:
1. 客户端
2. 保存String
3. 长期保存
4. 保存不重要的信息


# JSP指令与动作
## include指令
语法：<%@ include file="URL"%>

## include动作

语法：<jsp:include page="URL" flush="true|false" />
page，表示要包含的页面，flush表示是否使用缓冲区

## include指令与动作的区别

1. 语法不同
2. 指令发生在页面转换期间，jsp动作发生在请求期间
3. 指令的include的内容为文件的实际内容，而动作包含的内容是最终输出的内容
4. 指令 主页面和包含页面转换为一个Servlet,而动作则是转换为独立的Servlet
5. 指令的编译时间慢资源需解析，动作较快
6. 指令执行时间快，动作较慢资源需解析

## <jsp:forward>动作

语法：<jsp:forward page="URL" />
等同于： request.getRequestDispatcher("url").forward(request,response);

## <jsp:param> 动作

语法：<jsp:param name="参数名" value="参数值">
一般与forward动作配合使用，作为其子标签，表示请求转发修改或添加的新参数

