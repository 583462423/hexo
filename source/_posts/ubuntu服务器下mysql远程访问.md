---
title: ubuntu服务器下mysql远程访问
date: 2017-05-20 11:13:33
tags: mysql
---

.
<!--more-->
1.改表
```
mysql -uroot -p
use mysql
update user set host="%" where user='root'
select host,user from user
```

2.授权
```
sudo vim /etc/mysql/mysql.conf.d/mysqld.cnf
```
在`bind-address = 127.0.0.1`上加上#注释.

接着授权:
```
grant all privileges on *.* to root@"%" identified by "pwd" with grant option;
flush privileges;
```
将pwd修改为root对应的密码.

最后重启:
```
sevice mysql restart
```

all done.
