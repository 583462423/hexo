---
title: mysql解决插入中文失败问题
date: 2017-05-20 19:25:14
tags: mysql
---

在测试数据库的时候,向表中加入中文的时候,遇到了`Incorrect string value`等错误,主要原因就是字符编码问题,mysql默认的编码不是utf8,所以插入中文会失败.

<!--more-->

解决方法是设置默认编码为utf8.
修改mysql的配置文件,添加`/etc/mysql/conf.d/charset.cnf`文件,
直接键入下列代码:
```
vim /etc/mysql/conf.d/charset.cnf
```
打开文件后,加入:
```
[mysqld]
character-set-server=utf8
[client]
default-character-set=utf8
```
然后保存,接着重启mysql:
```
service mysql restart
```
完了之后,可以通过`show variables like '%char%'`字符编码.
这样还没完,对于已经创建的表,还需要重新设置编码:
```
alter table tablename convert to character set utf8
```

all done.
