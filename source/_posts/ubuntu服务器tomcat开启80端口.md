---
title: ubuntu服务器tomcat开启80端口
date: 2017-05-14 12:37:54
tags: 服务器
---

今天申请了一个新的域名`qxgnote.com`,并绑定了自己的服务器,但是访问的时候,要经常加上8080端口,原因就是tomcat默认端口为8080.

<!--more-->

解决方法就是在tomcat的文件目录中,找到`conf/server.xml`,修改其中connector标签对应的port属性为80即可.然后保存退出,重启tomcat.

但是由于ubuntu默认不会开启1023以下的端口,所以需要借助小工具开启该端口,首先安装authbind工具:`sudo apt install authbind`,然后开放80端口:`sudo touch /etc/authbind/byport/80`,接着重启tomcat.

all done.
