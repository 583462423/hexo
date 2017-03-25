---
title: Android重新学习
date: 2017-01-16 11:20:52
tags:
---

记得Android入门的时候，看的是网上的一个pdf，叫第一行代码，前些日子这本书出了第二版，索性直接就下单购买了，质量实在是很好，所以打算重新阅读一遍，把知识巩固一遍，在这过程中，打算做一个APP，功能就是记录我在学习过程中获得的知识以及测试代码。

<!--more-->

项目中的内容，均使用markdown文件记录，最后可以在安卓手机上解析markdown查看。

项目已经起步，本来的打算是用https://github.com/zzhoujay/RichText 这个markdown开源项目进行书写，后来觉得这个只用TextView解析的markdown并不美观，所以就到网上找现成的安卓软件，进行反编译，不过凑巧，正好搜到一款已开源的Markdown编译器：https://github.com/qinci/MarkdownEditors

虽然知道这个是抄袭的MarkdownX,不过是开源的，我就知足了，于是下载了这个开源的代码进行查看。

仔细查看后，发现其实现Markdown解析的思路非常清晰，就是使用webview和js来实现的，使用到的js代码是github上某一大神已实现好了的：https://github.com/chjj/marked

其解析markdown的思路是:首先要加载markdown.html，接着监听webview是否加载完毕，加载完毕后，使用js中的parseMarkdown方法来解析markdown文件中的字符串，这样就OK了。


对于md文件存储，我的想法是把md存储到Assets目录下

Assets和raw目录的说明，可以到http://www.jianshu.com/p/5974fcf88170 查看。
所以对于本项目来说，文件是打算使用assets方式进行存储。


本项目地址：https://github.com/583462423/StudyAndroid

