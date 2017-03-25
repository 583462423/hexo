---
title: Android给按钮添加边框
date: 2017-01-09 12:12:51
tags:
---

安卓的按钮没有自带的属性来设置边框，所以我们只能通过设置自定义背景的方式来设置边框。
<!--more-->
通常设置背景使用的是自定义drawable资源，使用shape标签，所以我们要明白shape标签的用法，如下：

```
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" >
    
    <!-- 圆角 -->
    <corners
        android:radius="9dp"
        android:topLeftRadius="2dp"
        android:topRightRadius="2dp"
        android:bottomLeftRadius="2dp"
        android:bottomRightRadius="2dp"/><!-- 设置圆角半径 -->
    
    <!-- 渐变 -->
    <gradient
        android:startColor="@android:color/white"
        android:centerColor="@android:color/black"
        android:endColor="@android:color/black"
        android:useLevel="true"
        android:angle="45"
        android:type="radial"
        android:centerX="0"
        android:centerY="0"
        android:gradientRadius="90"/>
    
    <!-- 间隔 -->
    <padding
        android:left="2dp"
        android:top="2dp"
        android:right="2dp"
        android:bottom="2dp"/><!-- 各方向的间隔 -->
    
    <!-- 大小 -->
    <size
        android:width="50dp"
        android:height="50dp"/><!-- 宽度和高度 -->
    
    <!-- 填充 -->
    <solid
        android:color="@android:color/white"/><!-- 填充的颜色 -->
    
    <!-- 描边 -->
    <stroke
        android:width="2dp"
        android:color="@android:color/black"
        android:dashWidth="1dp"
        android:dashGap="2dp"/>
    
</shape>
```
如果要设置按钮的自定义边框，只需要在shape中使用stroke标签即可。

如下：
```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!--被选中的样式-->
    <item android:state_selected="true">
        <shape>
            <solid android:color="@color/colorPrimary" />
            <stroke android:width="1dp" />
            <corners android:radius="5dp" />
        </shape>
    </item>
    <!--获取焦点样式-->
    <item android:state_focused="true">
        <shape>
            <solid android:color="@color/colorPrimary" />
            <stroke android:width="1dp" />
            <corners android:radius="5dp" />
        </shape>
    </item>
    <!--点击样式-->
    <item android:state_pressed="true">
        <shape>
            <solid android:color="@color/colorPrimary" />
            <stroke android:width="1dp" />
            <corners android:radius="5dp" />
        </shape>
    </item>
    <!--不可点击样式-->
    <item android:state_enabled="false">
        <shape>
            <solid android:color="@color/colorAccent" />
            <stroke android:width="1dp"
                android:color="#fff"/>
            <corners android:radius="5dp" />
        </shape>
    </item>
    <!--默认显示方式-->
    <item>
        <shape>
        <stroke android:width="1dp"
            android:color="#000" />
        <corners android:radius="5dp" />
     </shape>
    </item>
</selector>
```
默认显示方式，不给其设置solid即可，这样就不会有填充的颜色了，而stroke是边框的样式，我们设置为1dp，颜色为黑色。这样就可以实现边框样式了，当然你也可以更换其他颜色


