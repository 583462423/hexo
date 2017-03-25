---
title: Activity转换为Dialog
date: 2017-01-09 11:39:03
tags: Android
---

将Activity伪装成Dialog还是有好处的，比如我们想通过共享元素的方式打开一个Dialog，如果仅仅是Dialong这个组件，是不好实现的，而如果是Activity就很好实现了。

<!--more-->
  
将Activity伪装为Dialong只需要自定义一个主题即可，并且该主题继承Theme.AppCompat.Dialog,该主题默认背景是夜间模式，并且显示标题的，非常难看，所以我们自定义某些属性，比如背景颜色，无标题等等设置，代码如下：

```
<style name="DialogActivity" parent="@style/Theme.AppCompat.Dialog">
    <item name="windowNoTitle">true</item>
    <item name="android:colorBackground">@color/white</item>
</style>
```
接着把该主题应用在Activity中就行了。

如果希望外部点击之后不消失，只需要在`onCreate`中添加`setFinishOnTouchOutside(false)`;
