---
title: Linux 下 mysql导入.sql文件
date: 2017-04-05 19:18:00
tags: mysql
---

最近在看项目,需要导入一些数据库.
之前也考虑过这个事情,因为最近要做一个项目,域名不会变,但是服务器可能需要频繁变,所以需要频繁移植数据库,但是数据库那么多东西应该怎么移植是个问题.
后来想到了使用mysql的导入导出,但是Linux下都是通过命令行来执行mysql的一些指令,如何操作呢?
<!--more-->

1. 导出整个数据库
`mysqldump -u username -p dbname > outfile.sql`

2. 导出一个表
`mysqldump -u username -p dbname tablename > outfile.sql`

3. 导入数据库,首先进入数据库中:`mysql -u root -p`然后输出密码后:`use dbname`,然后使用`source`命令:`source infile.sql`
