---
title: nosql之redis学习
date: 2017-07-29 13:15:05
tags: redis
---

项目中要使用redis作缓存，前一段日子学了一下，这里做一个小总结。

<!--more-->

# 前言
当前数据库的演变：单击mysql->缓存+mysql+垂直拆分 -> mysql主从读写分离 -> 分裤分表,水平拆分,mysql集群 

mysql扩展性差，比如我想给某个表加个字段，mysql不好实现。

NoSql = not only sql，泛指非关系型数据库。

# CAP理论
C(Consistency):强一致性
A(Availability):可用性
P(Partition tolerace):分区容错性
CAP理论的核心是：一个分布式系统不可能同时满足一致性，可用性和分区容错性这三个需求，最多只能同时较好的满足两个。
因此根据CAP理论将数据库分为了CA，CP，AP三大类。
CA：单点集群，在可扩展性上不太强大，如：关系型数据库
CP：通常性能不高，如：redis,mongoDB
AP：对一致性要求比较低

对于分布式系统而言，P必占，所以只能从A，C里面挑选一个

# redis

## 是什么
REmote DIctionary Server:远程字典服务器，开源，C语言编写，KV，基于内存，支持持久化。

## 能干嘛
内存存储和持久化，将当前网站的最活跃用户存储在redis中，发布/订阅，定时器计数器等。

## 基础

redis-benchmark 测试性能。

* 单线程：epoll函数，多路IO复用
* 默认16个数据库，初始默认使用0号
* select命令切换数据库：select 9,选择第9个数据库
* Dbsize 查看当前数据库key的数量
* Flushdb 清空当前库，注意是当前
* Flushall 清空所有库
* 16个库都是一个密码
* redis索引从0开始
* 默认端口6379

## 五大常用数据类型
string,list,set,hash,zset(sorted set)

常用操作可参考:redisdoc.com，很详细。


### string
string二进制安全类型，可以包含任意数据，比如jpg序列化的对象等，string是redis最基本的类型，value最多可以是512M。

### hash
类型java中的map

### list
底层通过链表实现，可以插到头部，也可以插到尾部

### set
通过hashtable实现，string类型的无序集合
### zset
和set一样，不过是有序的，不是排序的，他实现有序的方法是让每个元素关联一个double值，double可重复。

## 持久化
redis持久化有两种，rdb和aof

### rdb
Redis Database

在指定的时间间隔内将内存中的数据集快照写入磁盘，恢复的时候是直接将快照文件直接读到内存中

redis会单独创建一个子进程来进行持久化，会先将数据写入到一个临时文件中，等到持久化过程都结束了，再用这个临时文件替换上次持久化好的文件。整个过程中，主进程是不进行任何IO操作的，这就确保了极高的性能，如果需要进行大规模数据恢复，且对于数据恢复的完整性不是非常敏感，那RDB方式要比AOF更加高效。
RDB的缺点是最后一次持久化数据 之后的数据可能会丢失。

RDB触发的条件可以配置->1分钟内改了1W次，5分钟内改了10次，15分钟内改了一次等。

RDB保存的文件是dump.rdb

### aof
Append only file

以日志的形式来记录每个写操作，将redis执行过的所有写指令记录下来(读操作不记录)，只许追加文件但不可以改写文件，redis启动之初会读取改文件重新构建数据，换言之，redis重启的话就根据日志文件的内容将写指令从前到后执行一次以完成数据的恢复工作。

默认是关闭的，并异步操作

保存的文件是appendonly.aof

rdb和aof两者可以同时启用

## 事务
multi开启事务，EXEC进行提交

不保证原子性，redis同一事务中如果有一条命令执行失败，其后的命令仍然执行，没有回滚。


