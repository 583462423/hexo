---
title: Android retrofit 配合服务端nodejs实现文件上传
date: 2017-01-09 11:42:27
tags:
---

做项目的时候遇到了上传文件的难题，项目使用retrofit，那么用retrofit怎么上传文件呢，而上传后，服务端怎么接受呢？

<!--more-->

# retrofit

首先是Service的定义：

```java
 public interface FileUploadService {
     @Multipart
     @POST("/upfile/proj")
     Call<ResponseBody> upload(@Part("description") RequestBody description,
                              @Part MultipartBody.Part file);
}
```
接着就是代码中使用方法：

```
// 创建 RequestBody，用于封装 请求RequestBody
RequestBody requestFile =
        RequestBody.create(MediaType.parse("multipart/form-data"), file);
// MultipartBody.Part is used to send also the actual file name
MultipartBody.Part body =
        MultipartBody.Part.createFormData("filename", file.getName(), requestFile);
// 添加描述
String descriptionString = "hello, 这是文件描述";
RequestBody description =
        RequestBody.create(
                MediaType.parse("multipart/form-data"), descriptionString);
// 执行请求
Call<ResponseBody> call = service.upload(description, body);
call.enqueue(new Callback<ResponseBody>() {
    @Override
    public void onResponse(Call<ResponseBody> call,
                           Response<ResponseBody> response) {
       //执行成功
    }
    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        //执行失败
        Log.e("Upload error:", t.getMessage());
    }
});
```
# 服务端

服务端是使用的是`multer`

首先安装multer和md5
npm install multer
npm install md5


upfile.js
```
var express = require('express');
var router = express.Router();
var pool = require('/www/comproj/mysql/pool');
var upload = require('./fileupload');
router.get('/', function(req, res, next) {
  res.send('upfile');
});
router.post('/proj',upload.single('aFile'),function(req,res,next){
	if(req.file){
		res.send("上传成功");
		console.log(req.file);
		console.log(req.body);
	}
});
module.exports = router;
```

fileupload.js

```
var multer = require('multer');
var md5 = require('md5');
var config = require('./config');
var storage = multer.diskStorage({
	destination:config.upload.path,
	filename:function(req,file,cb){
		var fileFormat = (file.originalname).split(".");
		cb(null,file.fieldname + '-' + md5(file) + "." + fileFormat[fileFormat.length - 1]);//第二个参数就是保存文件的名字，可以自定义，也可以不使用md5
	}
});
//初始化，其中也可以使用limits等，限制上传大小，具体上官网查看
var upload = multer({
	storage:storage
});
module.exports = upload;
```

config.js
```
module.exports = {
	upload:{
		path:process.cwd() + '/uploads' //设置保存路径
	}
}
```
