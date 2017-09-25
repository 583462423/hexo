---
title: ubuntu服务器防火墙设置
date: 2017-09-22 20:48:14
tags: linux
---

之前使用域名的方式访问linux服务器的tomcat，后来因为备案的问题，域名无法访问了，只能使用ip访问，但使用ip因为防火墙问题导致无法访问。

<!-- more -->
解决方案是，配置防火墙，查看防火墙配置规则的方法:`iptables -L -n`，
添加指定端口到防火墙中：
```
iptables -I INPUT -p tcp --dport 80 -j ACCEPT
```

摘自:http://www.cnblogs.com/xdp-gacl/p/4097608.html
