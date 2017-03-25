---
title: Android5.0共享元素使用方法
date: 2017-01-09 11:40:28
tags: Android
---

熟练使用共享元素，可以使得我们的安卓界面动画过度的越来越酷炫。

<!--more-->

有两个Activity，分别名为A，B。
那么从A到B，则为startActivity(this,B.class).

首先在两个Activity的共享元素上加上下列代码:
`android:transitionName="name"`
其中name是自己随便起的名字，后边会用到，注意共享元素并不是说两个元素一样，只是Activity过度的时候，两个元素之间能独立的平滑的过度。假设在A中的共享元素是TextView tA.

则将原来的startActivity(this,B.class)代码更换为:

```
Intent intent = new Intent(this,B.class);
if(BuildUtil.isLargeThanAPI21()){
	//这里的第二个参数是共享元素View,第三个参数就是前边说的transitionName设置的值
    ActivityOptions options = 
        ActivityOptions.makeSceneTransitionAnimation(A.this,tA,"name");
        
    startActivity(intent,options.toBundle());
}else{
	startActivity(intent);
}
```
其中BuildUtil.isLargeThanAPI21()是判断当前的版本是否比5.0高

```
public static boolean isLargeThanAPI21(){
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
}

```
如果共享元素多，可以使用如下代码:

```
ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
        Pair.create(view1, "agreedName1"),
        Pair.create(view2, "agreedName2"));
```
以上。
