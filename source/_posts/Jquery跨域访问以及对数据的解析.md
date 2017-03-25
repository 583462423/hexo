---
title: Jquery跨域访问以及对数据的解析
date: 2017-01-09 11:37:38
tags:
---

在做网站的时候，要访问非本站的内容时，会提醒No ‘Access-Control-Allow-Origin’的错误，该错误表示无权限访问外部网站。

<!--more-->
如果要访问外部网站，可以借助JSONP来实现，代码如下：

```
$.ajax({
    url:url,
    type:'GET',
    dataType:'JSONP',
    success:callback
});
```
其中url就是要访问网站的url，callback是访问成功后的回调，需要传入参数data

比如`function success(data){}`

拿到数据后，需要把该数据转换为JSON，首先是把该数据转换为JSON的字符串，代码如下：

```
var result_string = JSON.stringify(data); //将数据转换为JSON字符串
var result_json = JSON.parse(result_string); //将数据转换为JSON对象
```
接着就可以通过JSON的访问方式来访问该JSON对象了，比如`result_json['city']`，`result_json.city`;
