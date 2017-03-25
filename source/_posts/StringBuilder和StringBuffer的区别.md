---
title: StringBuilder和StringBuffer的区别
date: 2017-01-10 11:43:52
tags:
---
这个问题应该是Java面试中很基础的问题，所以有必要梳理一下这个知识。

<!--more-->

一般来说，面试的时候会问String,StringBuilder,StringBuffer的区别。

从以下几点来说明

# 速度 
String < StringBuilder < StringBuffer



# 生成对象
String 是字符串常量，StringBuilder和StringBuffer不是常量，是在自己的对象中进行操作。

# 定义
三者都实现了CharSequence的接口。

# StringBuilder与StringBuffer
StringBuffer的一系列方法有Sychronized标识符，即StringBuffer是线程安全的，而StringBuffer是非线程安全的，所以对于执行速度方面，StringBuilder是比StringBuffer快的，一般在编程时，在相对简单的环境下，即不需要考虑线程时，是使用StringBuilder，即大多情况都是推荐使用StringBuilder的。


# 参考

[String、StringBuffer与StringBuilder之间区别](http://www.cnblogs.com/A_ming/archive/2010/04/13/1711395.html)
[从源代码的角度聊聊java中StringBuffer、StringBuilder、String中的字符串拼接](http://www.cnblogs.com/kissazi2/p/3648671.html)
[在Java中连接字符串时是使用+号还是使用StringBuilder](http://www.blogjava.net/nokiaguy/archive/2008/05/07/198990.html)
