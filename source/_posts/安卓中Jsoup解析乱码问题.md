---
title: 安卓中Jsoup解析乱码问题
date: 2017-01-09 11:36:29
tags:
---

在做项目过程中，如果一个网页采用的不是UTF-8编码格式，那么使用Jsoup就会容易出现乱码问题。

如果仅仅是要对url进行解析，并且访问该url的方式是get，那么就可以不用使用OkHttp来访问了，直接Jsoup来搞定。加入指定url采用gb2312的编码格式，那么就可以使用如下代码访问

```
Document doc = Jsoup.parse(new URL(url).openStream(),"gb2312",url);
```

注意，该代码要在子线程中执行。
