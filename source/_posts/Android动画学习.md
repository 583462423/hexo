---
title: Android动画学习
date: 2017-01-09 12:03:38
tags:
---

动画有视图动画和属性动画，视图动画提供了`AlphaAnimation`,`RotateAnimation`,`TranslateAnimation`,`ScaleAnimation`等四种动画方式，并提供了AnimationSet动画集合。但是视图动画缺陷是不具有交互性，即视图动画只是展示了效果，但是实际位置还是在原位置。

<!--more-->

# 视图动画

这些动画使用方法基本上如出一辙，先初始化并设置其属性后，再调用某View的startAnimation方法即可。

## AlphaAnimation

透明度动画

```
AlphaAnimation aa = new AlphaAnimation(1,0);
aa.setDuration(1000);
//注意setFillAfter，这个方法很重要，如果设置为true，则动画执行后，view的显示状态为执行后的状态，否则恢复为动画执行前的状态
aa.setFillAfter(true);
myView.startAnimation(aa);
```
## RotateAnimation
旋转动画

```
RotateAnimation ra = new RotateAnimation(0,360,100,100);
ra.setDuration(1000);
ra.setFillAfter(true);
myView.startAnimation(ra);
```
参数中前两个是起始的角度，后两个是旋转中心。也可以设置为自身中心点，方法如下

```
RotateAnimation ra = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5F,RotateAnimation.RELATIVE_TO_SELF,0.5F);
```

## TranslateAnimation
位移动画

```
TranslateAnimation ta = new TranslateAnimation(0,200,0,300);
ta.setDuration(1000);
ta.setFillAfter(true);
myView.startAnimation(ta);
```
位移动画的参数比较特殊，前两个参数表示X坐标移动的起点和终点，同样，后两个参数为Y坐标从起点移动到终点。

## ScaleAnimation

缩放动画

```
ScaleAnimation sa = new ScaleAnimation(0,2,0,2);
sa.setDuration(1000);
sa.setFillAfter(true);
myView.startAnimation(sa);
```
其参数类似位移动画的参数，前两个参数为X轴缩放倍数的开始与结束值，同样，后两个参数为Y轴的。这种初始化方法的默认缩放中心点为(0,0)，当然我们可以自定义缩放中心点，类似旋转，如下：

```
ScaleAnimation sa = new ScaleAnimation(0,1,0,1,ScaleAnimation.RELATIVE_TO_SELF,0.5F,ScaleAnimation.RELATIVE_TO_SELF,0.5F);
```
## AnimationSet


动画集合

```
 AnimationSet as = new AnimationSet(true);
AlphaAnimation aa = new AlphaAnimation(1,0);
RotateAnimation ra = new RotateAnimation(0,360,100,100);
TranslateAnimation ta = new TranslateAnimation(0,200,0,300);
ScaleAnimation sa = new ScaleAnimation(0,1,0,1,ScaleAnimation.RELATIVE_TO_SELF,0.5F,ScaleAnimation.RELATIVE_TO_SELF,0.5F);
as.addAnimation(aa);
as.addAnimation(ra);
as.addAnimation(ta);
as.addAnimation(sa);
as.setDuration(1000);
as.setFillAfter(true);
myView.startAnimation(as);
```
当然，我们可以单独给其中的动画设置持续时间等。

## 动画的监听

动画的监听实现也比较简单，只需要给相应的动画对象设置监听事件即可，如下

```
as.setAnimationListener(new Animation.AnimationListener() {
    @Override
    public void onAnimationEnd(Animation animation) {
		//动画结束时运行
    }
    @Override
    public void onAnimationRepeat(Animation animation) {
		//动画重复时运行
    }
    @Override
    public void onAnimationStart(Animation animation) {
		//动画开始时运行
    }
});
```
# 属性动画

属性动画与视图动画的区别是，属性动画更改的是View的属性，比如一个按钮，如果使用视图动画移动了其位置，但是实际位置还是在原来的位置上，这个时候点击移动后的位置处是没有效果的，但是属性动画却有效果。
另一个区别是，视图动画如果使用startAnimation方法开始动画的时候，他会不管View的动画运行在什么地方了，而是直接重新开始运行该动画。但是属性动画不一样，如果属性动画没有设置开始值，则属性动画运行结束，无论你怎么开始动画，他都不会重新运行。除非你设置的动画值，包含开始值和结束值。

还有一个比较小的区别，不知道大家注意到没有，视图动画的后缀是Animation,而属性动画的后缀则为Animator

## ObjectAnimator

首先查看示例代码：
```
ObjectAnimator objA = ObjectAnimator.ofFloat(myView,"translationX",300);
objA.setDuration(1000);
objA.start();
```
首先通过静态工厂方法返回一个ObjectAnimator对象，其中第一个参数是targetView，第二个参数是属性名，其中属性名是需要该View中有相应的set和get方法，安卓会通过反射机制找到相应的方法给其设置相应值的，如果没有，是运行不成功的，第三个属性则为属性值。

以下为常用的可以直接使用的属性值：

* translationX和translationY：这个就是坐标值
* rotation,rotationX,rotationY：旋转角度，第一个rotation是在2D画面上进行旋转，而rotationX,rotationY比较神奇，是在3D图形上进行的旋转，比如rotationX是绕着X进行旋转。
* scaleX,scaleY:控制图形的缩放
* pivotX,pivotY:控制View的支点位置，默认情况，View的旋转缩放等都是围绕着该支点进行处理的。
* x，y：这两个值与translationX,translationY很相似，但是x,y描述的是最终坐标值，计算方法如下：x = View的最初x坐标 + translationX
* alpha:透明值，1是默认值，0是完全透明
以上是View自带有get,set方法的情况下可以使用的属性值，但是如果没有get,set方法怎么用呢?答案是，我们可以使用包装类，为该类添加get,set方法。如下所示：

```
private static class WrapperMyView
{
    View target;
    public WrapperMyView(View view)
    {
        target = view;
    }
    public int getWidth()
    {
        return target.getLayoutParams().width;
    }
    public void setWidth(int width)
    {
        target.getLayoutParams().width = width;
        target.requestLayout();
    }
}
```
注意在setXX方法中使用requestLayout方法重绘。这里说明下invalidate和requestLayout方法的区别，invalidate方法是重新调用onDraw方法，而requestLayout方法重新调用onMeasure,onLayout,onDraw方法，这个才是真正意义上的重绘。

这样，想要使用动画，就可以使用如下代码：

```
WrapperMyView wrapper = new WrapperMyView(targetView);
ObjectAnimator.ofInt(wrapper,"width",200).setDuration(5000).start();
我们也可以不使用这种方法，还有一种方法是使用ValueAnimator，后边会有介绍。
```
## ValueAnimator

ObjectAnimator就是继承自ValueAnimator，ValueAnimator本身不产生任何动画效果，只是一个数值发生器，类似Scroller，只是定期产生某一种有规律的数字，就是因为这些数字，我们可以使用监听方法来给view设置这些不断变化的值，从而达到动画的效果。

使用方法如下：

```
ValueAnimator valueAnimator = ValueAnimator.ofInt(0,100);
valueAnimator.setTarget(targetView);
valueAnimator.setDuration(1000).start();
valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        int value = (int) animation.getAnimatedValue();
        //拿到改变的值来做其他操作
    }
});
```
## 动画的监听事件

```
ObjectAnimator anim = ObjectAnimator.ofFloat(view,"alpha",0.5f);
anim.addListener(new AnimatorListener(){
	@Override
    public void onAnimationStart(Animator animation)
    {
    
    }
    @Override
    public void onAnimationRepeat(Animator animation)
    {
    
    }
    @Override
    public void onAnimationEnd(Animator animation)
    {
    
    }
    @Override
    public void onAnimationCancel(Animator animation)
    {
    
    }
})
```
我们也可以添加监听适配器

```
anim.addListener(new AnimatorListenerAdapter(){
	@Override
    public void onAnimationEnd(Animator animation)
    {
	}
})
```
## PropertyValuesHolder

如果我们想要使用视图动画中类似AnimationSet的效果怎么办呢？其实PropertyValuesHolder实现的效果就类似AnimationSet实现的效果。

直接看代码：

```
PropertyValuesHolder p1 = PropertyValuesHolder.ofFloat("translationX",300f);
PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("scaleX",1f,0,1f);
PropertyValuesHolder p3 = PropertyValuesHolder.ofFloat("scaleY",1f,0,1f);
ObjectAnimator.ofPropertyValuesHolder(myView,p1,p2,p3).setDuration(1000).start();
```
可以看到，`PropertyValuesHolder`不用设置目标View，初始化只需要属性名，和属性值，第一种传入一个值，则默认开始值为当前目标View的值，而第二种方式，传入多个值，第一个为动画开始属性值，后边为属性值的过度值。接着使用`ObjectAnimator.ofPropertyValuesHolder(target,p1,p2,p3)`,第一个参数为View，后边加上`PropertyValuesHolder`对象即可，有多少加多少。

## AnimatorSet

类似`AnimationSet`的效果，不仅可以使用`PropertyValueHolder`来实现，也可以使用`AnimatorSet`,注意，不是`AnimationSet`哦。

使用`AnimatorSet`的好处是，我们可以控制动画集中动画的展示顺序，展示时间，从而对动画达到一种精确的控制。
如上边的`PropertyValuesHolder`，使用`AnimatorSet`的实现方式是：

```
ObjectAnimator anim1 = ObjectAnimator.ofFloat(target,"translationX",300F);
ObjectAnimator anim2 = ObjectAnimator.ofFloat(target,"scaleX",1F,0F,1F);
ObjectAnimator anim2 = ObjectAnimator.ofFloat(target,"scaleY",1F,0F,1F);
AnimatorSet set = new AnimatorSet();
set.setDuration(1000);
set.playTogether(ani1,anim2,anim3);
set.start();
```
AnimatorSet的使用方式与AnimationSet略有不同，他可以设置动画的展示方式，如playTogether表示动画同时间开始展示，当然还有其他设置方式，如palySequentially则为按顺序执行，而play只传入一个动画，但是如果我们要使用play()方法，一般还会在后边跟上with()，或者after(),before等方法，这几种方式可以使用于只有两个动画效果的时候。

## 属性动画简写方式

Android3.0以后，View中增加了animate方法直接操作属性动画。

```
myView.animate()
    .alpha(0)
    .y(300)
    .setDuration(300)
    .start();
```
## 插值器

直接参考文档:[Interpolator](https://my.oschina.net/banxi/blog/135633)

# 最后

更多知识请参考Hongyang大神文章:
[Android 属性动画（Property Animation） 完全解析 （上）](http://blog.csdn.net/lmj623565791/article/details/38067475)
[Android 属性动画（Property Animation） 完全解析 （下）](http://blog.csdn.net/lmj623565791/article/details/38092093)

其中第二篇文章涉及内容有：
1、xml文件创建属性动画
2、布局动画
3、View的animate方法等。
如果对以上感兴趣的直接点进去即可。
