---
title: next自定义主题实现
date: 2017-01-09 12:14:10
tags:
---
如果想自定义主题，首先要对CSS有所了解。如果我们只使用Next自带的三个主题，未免有点单调，所以我们可以在这些主题的基础上更改css文件来更改主题样式。

<!--more-->

其实方法很简单，Next已经为什么准备好了一个自定义主题的css文件，位于
`\themes\next\source\css\_custom\`下的`custom.styl`文件。

这个文件后缀是styl，但本质上还是css。所以我们可以在其中更改我们网页中的一系列样式。

当然首先我们要明白，我们的网页各个模块的class是什么。查看方法有很多，最简便的方法就是使用Firefox点击F12进入调试模式来查看。

接着知道clss了之后，就开始编写我们的custome.style文件吧！

比如我刚开始编写好的文件如下(只是为了测试，所以样子有点丑)
```
// Custom styles.
.header{
    background:#333;
    height:200px;
}
.header-inner{
    text-align:center;
    padding-top:70px;
}
//标题上分界线
.logo-line-before i{
    background:red;
}
//标题下分界线
.logo-line-after i{
    background:red;
}
//标题a标签
.brand{
}
.custom-logo-site-title span{
    //网站标题样式
    color:red;
}
.menu .menu-item a{
    //网站导航menu下的item
    color:white;
}
.menu .menu-item a:hover{
    background:red;
}
.footer{
    //底部样式
    background:#333;
}
.footer .footer-inner{
    //底部内容样式
    color:red;
}
.footer .footer-inner a{
    //底部连接样式
    color:red;
}
.sidebar{
    //侧边栏样式
    background:#FBC02D;
    box-shadow:none;
}
.site-author-image{
    //侧边栏图片样式
    border-radius:100px; //给图片添加圆角
}
.sidebar-inner{
    //侧边栏内部内容样式
}
//侧边栏日志数量等等文字的样式
.site-state-item a{
    color:white;
}
.site-state-item a:hover{
    color:black;
}
.links-of-blogroll,.links-of-blogroll a{
    //下部友情链接显示样式
    color:white;
}
.links-of-blogroll-item a{
    //下部友情连接的下划线颜色
    border-bottom:1px solid red;
}
.links-of-author-item a{
    //github的显示样式
    color:white;
    border-bottom:1px solid red;
}
.post{
    //每一篇文章都是post
    border-bottom:1px solid #ccc;
    padding:10px;
    border-top:1px solid #ccc;
}
.sidebar-toggle{
    //侧边栏轮转样式
    background:red;
}
.back-to-top{
    //返回顶部样式
    background:red;
}
.main-inner .content-wrap{
    //文章部分
}
.post-title{
    //文章标题
}
.post-title-link{
    //文章标题的a
}
.post-time{
    //发表时间
    color:red;
}
.post-comments-count,.post-comments-count a{
    //评论次数
    color:blue;
}
.post-category,.post-category a{
    //文章分类
    color:yellow;
}
.leancloud_visitors{
    //阅读次数
    color:green
}
blockquote{
    //引用样式
    border-left:4px solid green;
}
.post-more-link a{
    //阅读全文样式
}
```
而显示出来的网页样式是：


哈哈，是不是很丑，当然自定义方式很多，所以你一定可以设计出来你自己喜欢的样式~

当然，你也可以设置响应布局，如果想要修改在手机上的样式，可以添加下列代码
```
@media screen and (max-width: 766px) {
 //当屏幕尺寸小于766px时，应用下面的CSS样式
  .header {
    //
  }
  .header .header-inner{
    padding-top:50px;
    padding-bottom:50px;
}
  .site-nav{
  }
}
```
好了，提醒一点，这里面有好多样式是冲突，修改的时候一定要注意。

比如如果我把电脑上显示的头部高度，设置为固定值200px,那么在手机上显示就会出错，至于原因，我就不解释了。

好好玩耍吧~
