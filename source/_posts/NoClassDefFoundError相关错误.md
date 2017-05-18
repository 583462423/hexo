---
title: NoClassDefFoundError相关错误
date: 2017-05-17 10:59:03
tags: Java
---

.
<!--more-->

今天在做一个有关Tomcat的demo,需要去加载某个Class,但是怎么都加载不成功.

第一次加载错误报的异常是:
`Exception in thread "main" java.lang.NoClassDefFoundError: PrimitiveServlet (wrong name: demo/tomcat/front/PrimitiveServlet)`
该异常表示的意思为,运行了一个带有报名的class.如果想要成功运行该class,其类名就应该加上包名,比如刚开始写的是:`loader.loadClass("PrimitiveServlet")`,修改之后应该为:`loader.loadClass(demo.tomcat.front.PrimitiveServlet)`.


因为报错内容为`(wrong name: demo/tomcat/front/PrimitiveServlet)`,所以在第一次尝试修改的时候,改为了:`loader.loadClass(demo/tomcat/front/PrimitiveServlet)`,那么这样会导致`Exception in thread "main" java.lang.NoClassDefFoundError: IllegalName: demo/tomcat/front/PrimitiveServlet`.所以在指定类名的时候,分隔符应为`.`.



