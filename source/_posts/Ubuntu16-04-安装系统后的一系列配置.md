---
title: Ubuntu16.04 安装系统后的一系列配置
date: 2017-01-09 11:28:26
tags:
---




# 升级软件
sudo apt update
sudo apt upgrade

<!--more-->

# adobe flash
随便点开个视频，会提示安装，之后让打开某一软件，确定后使用终端更新
接着在去flash官网安装一遍

# inode客户端
1.检查本机是64位还是32位
```
$ dpkg --print-architecture
```
然后可以看到amd64，就说明当前系统是64位。
2.检查是否开启32位支持--输入：
```
$ dpkg --print-foreign-architectures
```
看到i386，就说明已开启32位支持，如果没有显示i386，则输入：
```
$ sudo dpkg --add-architecture i386
```
打开i386的支持，然后输入：
```
$ sudo apt-get update
```
进行更新，然后输入：
```
$ sudo apt-get dist-upgrade
```
对支持库进行更新
3.安装兼容包：
```
$ sudo apt-get install libncurses5:i386
$ sudo apt-get install libgtk2.0-0:i386
$ sudo apt-get install libpangoxft-1.0-0:i386
$ sudo apt-get install libpangox-1.0-0:i386
$ sudo apt-get install libxxf86vm1:i386
$ sudo apt-get install libsm6:i386
$ sudo apt-get install libjpeg62:i386
```
4.cd到iNode解压目录下，安装iNode：
解压方法: tar -zxvf xxxx.tar.gz
```
$ sudo ./install
```
安装完成后，输入下面命令启动即可：
```
$ sudo ./iNodeClient.sh
```

# 安装SS
添加 PPA 源 :sudo add-apt-repository ppa:hzwhuang/ss-qt5
更新软件列表 :sudo apt-get update
安装 shadowsocks:sudo apt-get install shadowsocks-qt5
配置 shadowsocks 帐号信息
# 安装搜狗等deb包时的问题
在第一次更新的时候使用自带的应用软件来安装deb会产生安装不上的问题，

首先使用 sudo dpkg -i packgename.deb来安装，这个时候会有报错，不用管，继续sudo apt-get -f install，这样就安装好了，接着重启，重启后，自带的应用软件就可以安装deb包了

# 安装桌面环境
http://blog.csdn.net/terence1212/article/details/52270210

# 配置jdk以及Android studio
http://blog.csdn.net/w965440884/article/details/51498556
注意在安装android-studio的时候，会有一个选项是选择标准的还是自定义的，这个应该选自定义的，不然默认下载的sdk是在root下的。
