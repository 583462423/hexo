---
title: 重装系统后Hexo文件恢复方法
date: 2017-01-09 11:31:42
tags:
---

具体步骤如下：
1.安装npm
2.重新部署hexo
3.将原来备份的hexo文件中的部分文件复制到新部署的hexo中
4.部署ssh，完成。

<!--more-->

# 安装npm
安装npm的前提是电脑上已经安装了nodejs和git。而一般安装nodejs的时候默认是直接安装npm了的，所以没有安装nodejs和git的，自行百度安装方法。

不过目前的系统是ubuntu，所以安装方法异常简单，只需要`sudo apt install xxx`即可，目前系统版本是16.04所以不需要apt-get

# 部署hexo
首先要安装hexo，命令如下:
npm install -g hexo

安装完成后，在你想要部署的文件下初始化hexo，比如我的文件为’Hexo’,那么在该文件下,执行下列命令：
hexo init

# 替换文件
上一部完成后，需要把备份的文件中scaffolds, source, themes 和 _config.yml等替换到新部署的hexo文件中。

完成这一步后，先别着急上传，使用hexo s命令打开服务器，浏览器中输入http://localhost:4000查看blog是否显示正确。

如果显示不正确，可能就是前边的步骤有问题，需要重新操作。

# 配置ssh
如果你的Hexo上传使用的ssh方式，你需要重新生成ssh key并添加到github或coding中去，具体方法可以参照:
[Window github ssh配置](http://jingyan.baidu.com/article/a65957f4e91ccf24e77f9b11.html)

coding方式与github基本一样，直接配置即可。

到此大功告成，可以继续玩耍blog了。
