---
title: 自定义ripple效果并适配——以BUtton举例
date: 2017-01-09 11:46:51
tags:
---

Android5.0以后的按钮点击的时候会有波纹效果，这个效果就是ripple来实现的。

ripple的详细使用可以看这个博文:[Android L Ripple的使用](http://www.cnblogs.com/carlo/p/4795424.html)

<!--more-->
# 自定义ripple

在drawable文件下创建一个文件，命名随意，ripple自定义代码如下：

```
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/colorAccent"
    tools:targetApi="21"
    xmlns:tools="http://schemas.android.com/tools">
    <item android:drawable="@drawable/nav_draw_btn_back"/>
</ripple>
```
其中注明tagetApi，不然Android Studio 会一直出现红色波浪线，强迫症受不了，其实意思就是告诉你，这个ripple使用的API必须在21以上，不然会出错。

代码ripple标签的color属性就是波纹颜色，而item中就是View的background。

如果不想要自定义ripple且直接使用，以TextView来举例，可以直接在TextView的属性中添加下面这一行代码：
`android:background="?android:attr/selectableItemBackgroundBorderless"`或`android:background="?android:attr/selectableItemBackground"`，第一个的波纹无边界，而第二个则有边界。

但是使用这种效果就无法自定义background的颜色，所以自定义ripple使用起来不仅能实现波纹效果，波纹颜色，而且还可以根据情况，自定义background的颜色，点击效果等。

接着就是使用方法，注意，这里是以Button举例，因为给Button适配相对比较复杂。

# 使用

如果要适配Button，不能简单粗暴的在Button的控件上直接加上android:background = "@drawable/xxx_ripple"。这样做的后果是，在API21之前完全没有任何效果。

但是我们想要在API21之前有效果，虽然效果不是波纹效果，但至少有点击效果吧！

所以我们要用style

首先自定义点击效果：

```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <!--被选中的样式-->
    <item android:state_selected="true">
        <shape>
            <solid android:color="@color/ccc" />
            <corners android:radius="0dp" />
        </shape>
    </item>
    <!--获取焦点样式-->
    <item android:state_focused="true">
        <shape>
            <solid android:color="@color/ccc" />
            <stroke android:width="0.2dp" />
            <corners android:radius="0dp" />
        </shape>
    </item>
    <!--点击样式-->
    <item android:state_pressed="true">
        <shape>
            <solid android:color="@color/ccc" />
            <corners android:radius="0dp" />
        </shape>
    </item>
    <!--不可点击样式-->
    <item android:state_enabled="false">
        <shape>
            <solid android:color="@color/white" />
            <stroke android:width="0dp" />
            <corners android:radius="0dp" />
        </shape>
    </item>
    <!--默认显示方式-->
    <item>
        <shape>
            <solid android:color="@color/white" />
            <stroke android:width="0dp" />
            <corners android:radius="0dp" />
        </shape>
    </item>
</selector>
```
接着在style(v21)和style分别设置,但是如果要给Button设置样式，不能在style添加background这个属性，添加了也没效果，只能使用buttonStyle，这样Button才有效果：

```
style(v21).xml
---
<!--NavDrawBtnStyle-->
<style name="NavDrawBtnStyle">
    <item name="android:background">@drawable/nav_draw_btn_ripple</item>
    <item name="android:textAppearance">@color/black</item>
    <item name="android:textSize">14sp</item>
    <item name="android:minHeight">48dip</item>
    <item name="android:minWidth">88dip</item>
    <item name="android:focusable">true</item>
    <item name="android:clickable">true</item>
    <item name="android:gravity">center_vertical|center_horizontal</item>
</style>
```
```
style.xml
---
<style name="NavDrawBtnStyle">
    <item name="android:background">@drawable/nav_draw_btn_back</item>
    <item name="android:textAppearance">@color/black</item>
    <item name="android:textSize">14sp</item>
    <item name="android:minHeight">48dip</item>
    <item name="android:minWidth">88dip</item>
    <item name="android:focusable">true</item>
    <item name="android:clickable">true</item>
    <item name="android:gravity">center_vertical|center_horizontal</item>
</style>
```
上边两个代码除了background不同外，其他全部相同，这样做的目的就是让Button在API21之前依然有点击效果，其实我们也可以把所有相同的属性放在一个新的theme中去，之后让这两个Theme继承那一个。

如果只设置了background，不设置其他属性，虽然能正常运行，但是你会发现，咦，我按钮的文字哪去了？咦，我按钮文字怎么不在中间？咦，我按钮文字怎么这么小。

好了，接着在style.xml添加统一主题,并且将buttonStyle设置成以上的主题样式。

```
<!--NavDrawBtn-->
<style name="NavDrawBtn" parent="Theme.AppCompat">
    <item name="buttonStyle">@style/NavDrawBtnStyle</item>
</style>
```
最后就是在布局文件中添加上主题就行android:theme="@style/NavDrawBtn",完成。


