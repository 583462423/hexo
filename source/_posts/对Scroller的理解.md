---
title: 对Scroller的理解
date: 2017-01-09 12:01:39
tags:
---

Scroller类，虽然名字是Scroller，但是实际上内部并没有什么方法能使得某View进行滚动，这个类就是一个数值产生器，他内部只是在某一个时间段，模拟滚动的数值。

<!--more-->

我们来看一下一般Scroller的使用方法，首先初始化`ScrollerScroller mScroller = new Scroller(context)`
接着便是重写View中的`computeScroll()`方法。注意该方法是View中的，而不是Scoller中，刚开始学习的时候还在迷糊怎么重写Scoller中的`computeScroll`方法啊，难道是重写一个类继承么？为什么重写该方法后边会有介绍。

```
@Override
public void computeScroll()
{
	super.computeScroll();
    if(mScroller.computeScrollOffset()){
    	((View)getParent()).scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
        invalidate();
    }
}
```
我们看到了内部又调用了`invalidate()`方法进行重绘，好了，这里说明一点，当系统调用`invalidate`时，会调用view的`draw()`方法，而`draw()`方法又会调用`computeScroll()`方法，即调用顺序为`invalidate()`->`draw()`->`computeScroll()`,这样就形成了循环模式，对Scroller中模拟的数值不断进行变化，就形成了滚动的动画效果。

注意要使用mScroller.computeScrollOffset()来判断Scroller是否模拟完成，如果没有该if语句，这些方法会形成无限循环，后果可想而知。还有一点要说明，computeScroll()方法是View的，而computeScrollOffset()方法是Scroller的。

以上完成后，最后就是调用Scroller的startScroll()方法开启模拟过程：

```
public void startScroll(int startX,int startY,int dx,int dy,int duration)
public void startScroll(int startX,int startY,ind dx,int dy)
```
但是！
如果我们仅仅使用startScroll方法，那么动画效果是不会出现的，原因在于，startScroll方法只是让Scroller开始进行了模拟，而我们的View并没有开始重绘，所以最后还需要使用`invalidate()`方法开始重绘，配合`computeScroll()`形成循环模式，这样就会有滚动的动画效果了。
