---
title: PopupWindow用法
date: 2017-01-09 12:10:47
tags:
---
PopupWindow是一种弹出的框菜单，虽然安卓有自带的弹出框，但是PopupWindow却可以自定义任意位置，所以很方便。后边贴出了教程，该文只介绍用法。

<!--more-->

# 构造函数

```
public PopupWindow()
public PopupWindow(Context context)
public PopupWindow(int width,int height)
public PopupWindow(View contentView)
public PopupWindow(View contentView,int width,int height)
public PopupWindow(View contentView,int width,int height,boolean focusable)
```
# 初始化PopupWindow

```
View contentView = LayoutInflater.from(context).inflate(R.layout.popupwindo,null);
PopupWindow p = new PopupWindow(context);
p.setContentView(contentView);
p.setWidth()
```
# 显示

```
//某view的整左下方显示
showAsDropDown(View anchor)
//正值向左或者向下偏移
showAsDropDown(View anchor,int xoff, int yoff)
//相对于父布局位置显示，如Gravity.CENTER显示在中央，Gravity.BOTTOM显示在底部。
//showAtLocation(View parent,int gravity,int x,int y)
```
# 隐藏

```
dismiss()
```
# 简单用法

首先是PopupWindow的布局文件
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:orientation="vertical" android:layout_width="match_parent"
    android:gravity="center_horizontal"
android:layout_height="match_parent">
    <LinearLayout
        android:id="@+id/share_to_qq"
        android:layout_width="wrap_content"
        android:orientation="horizontal"
        android:gravity="center"
        android:layout_height="36dp">
        <ImageView
            android:src="@drawable/qq"
            android:layout_width="24dp"
            android:layout_height="24dp" />
        <TextView
            android:textSize="10sp"
            android:layout_gravity="center"
            android:text="分享到QQ"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />
    </LinearLayout>
    <View
        android:layout_width="wrap_content"
        android:layout_height="0.5dp"
        android:background="@color/black"
        />
    <LinearLayout
        android:id="@+id/share_to_weixin"
        android:layout_width="wrap_content"
        android:orientation="horizontal"
        android:gravity="center"
        android:layout_height="36dp">
        <ImageView
            android:src="@drawable/weixin"
            android:layout_width="24dp"
            android:layout_height="24dp" />
        <TextView
            android:textSize="10sp"
            android:layout_gravity="center"
            android:text="分享到微信"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />
    </LinearLayout>
</LinearLayout>
```
实例化并且显示代码：

```
//初始化
View contentView = LayoutInflater.from(CommentActivity.this).inflate(R.layout.qq_wei_layout,null);
   PopupWindow mWindow = new PopupWindow(CommentActivity.this);
   mWindow.setContentView(contentView);
   mWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
   mWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
   
   //设置点击外部使得PopupWindow消失
   mWindow.setBackgroundDrawable(new BitmapDrawable()); //必须
mWindow.setOutsideTouchable(true);
//设置点击事件
   View shareQQ = contentView.findViewById(R.id.share_to_qq);
   View shareWinxin = contentView.findViewById(R.id.share_to_weixin);
   shareQQ.setOnClickListener(this);
   shareWinxin.setOnClickListener(this);
   
   //显示
   mWindow.showAsDropDown(share);
```
参考：
[PopUpWindow使用详解](http://blog.csdn.net/harvic880925/article/details/49272285)
