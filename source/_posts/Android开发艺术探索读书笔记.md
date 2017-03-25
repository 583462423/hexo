---
title: Android开发艺术探索读书笔记
date: 2017-02-28 16:42:19
tags:
---

该书在很早的时候就已经看了一遍,当时看的很头蒙,完全不懂,现在对Android有了很多的了解,所以再来看这本书就感觉恍然大悟了.

<!--more-->

# Activity生命周期和启动模式

## 生命周期

最简单的生命周期就不用说了, 类似于这种:onCreate - onStart - onResume - onPause - onStop - onDestroy.

问题:安卓从A的Activity到B的Activity中,先执行A的onPause方法,还是先执行B的onResume?

答案:先执行A的onPause,其执行顺序是A.onPause - B.onCreate - B.onStart - B.onResume - A.stop

onPause和onStop都不能执行耗时操作,尤其是onPause,这也以为着,我们应尽量在onStop中做操作,从而使新的Activity尽快显示出来并切换到前台.

### 异常情况下的生命周期

屏幕翻转时:

屏幕翻转的时候会销毁当前的Activity,注意是销毁也即调用destroy方法.其调用过程如下:
onSaveInstanceState -> onStop -> onDestroy -> onCreate -> onStart -> onRestoreInstanceState -> onResume
以上是默认情况,那么如何才能保证屏幕旋转的时候当前的Activity不被销毁呢?

方法是在AndroidManifest的配置文件中,配置当前的Activity的configChanges属性,该属性常用的值有:orientataion,locale,keyboardHidden

那么配置如下:

```
<activity android:name=".MainActivity"
            android:configChanges="orientation">
```

这个时候在旋转屏幕时,上边展示的方法都不会重新运行,其只会调用onConfigurationChanged方法.


## 启动模式

* standard : 谁启动了这个Activity,这个Activity就会运行在谁的任务栈中.所以当使用ApplicationContext去启动standard模式的Activity会报错[注意,安卓7.1并不会报错,可能是谷歌已经将这个修复了]
* singleTop : 栈顶复用, 如果该Acitivity位于栈顶,那么Activity就不会被创建,而是直接使用该栈顶的Activity,并且调用其onNewIntent()方法,且onCreate,onStart方法不会被调用.
* singleTask: 栈内复用,如果在该任务站中存在要加载的Activity,就把该Activity移到栈顶(调用onNewIntent方法),同时clearTop即消除之前在该Acitivity上边的活动.
* singleInstance: 单实例模式. 如果所有栈中都没有该活动,那么就重新创建一个栈,把活动添加进去,并且这个栈只会存在这一个活动.

任务栈分为前台任务栈和后台任务栈,后台任务栈中的Acitivity处于暂停状态.

每个Activity都有TaskAffinity,这个属性用来指定该Activity想要运行到哪个任务栈.默认是包名.

# IPC

## 开启多进程

使用Android:process指定进程值.
如:
```
android:process=":remote"
android:process="com.qxg.studyandroid.remote"
```

其中第一种方式中的":"含义是指要在当前的进程名前面附加上当前的包名.并且该进程为当前应用的私有进程,其他应用的组件不可以和他跑在同一个进程中.

## 多进程运行机制

多进程容易导致以下问题:
1. 静态成员和单例模式失效(每个进程中都存在该静态类,操作互不影响)
2. 线程同步机制失效(同上)
3. SharedPreferences的可靠性下降(没有同步机制,所以两个进程可以同时对SharedPreferences读写操作)
4. Application多次创建

不同进程的组件会拥有独立的虚拟机,Application和内存空间.

## IPC基础概念介绍

### Serializable接口

该接口是为实现序列化,实现该接口后需要指定:
```
private static final long serialVersionUID = 456464646L;

```

如:
```
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int age;
    private String name;

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

该值不是必须的,但是如果不指定会影响反序列化.序列化后会将serialVersionUID存到文件中,反序列化时会读取该值并和类中的静态的serialVersionUID值进行比较,如果相同序列化成功,如果不同则失败.如果不指定该属性值,那么序列化的时候会默认计算该类实例的hash值然后赋值给serialVersionUID,那么在反序列化的时候,一旦类的结构改变,就会导致serialVersionUID值的改变,这样就导致反序列化失败.

那么通过ObjectOutputStream和ObjectInputStream可以轻松实现序列化和反序列化:

```
        //序列化
        User user = new User();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("test.txt"));
        out.writeObject(user);
        out.close();

        //反序列化
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("test.txt"));
        User newUser = (User) in.readObject();
        in.close();
```


如果想指定类中的某些成员不被序列化,可以使用`transient`关键字进行修饰.

### Parcelable接口
Parcelable接口是Android中特有的接口.需要实现的方法有
```
public int describeContents();//内容接口描述,默认返回0就好了
public void writeToParcel(Parcel dest, int flags);//将数据存储在Parcel对象中
```
序列化的时候会调用writeToParcel将对象中的属性值存储在Parcel中

而反序列化需要一个静态内部类Creator,反序列化的时候会调用该类中的createFromParcel方法来创建对象.

如:

```
public class User implements Parcelable {


    private int age;
    private String name;

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    private User(Parcel source){
        this.age = source.readInt();
        this.name = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;  //默认返回0就行了
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(age);
        dest.writeString(name);  //注意和read的顺序保持一致
    }

    public static final Creator<User> CREATOR = new Creator<User>() {

        //反序列化调用该方法创建一个新对象
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

```

Parcelable比Serializable效率高,但是因为Parcelable比较复杂,所以一般是建议使用Serializable.



(最近要忙着学习安卓有关面试的内容,所以要把部分的知识梳理一遍,还有个知识点就是View的事件体系,考虑到安卓中的IPC还需要很长一段时间学习,所以先学View的事件体系去了)

# View的事件体系

## View的位置参数

这个位置把我一度搞懵逼,因为其位置有多个,经常把这些位置弄混乱.

left,top,right,bottom这四个位置就是当前view相对于父view的位置,使用getter方法即可获取.

translationX,translationY是偏移量,而x,y则是view的左上角的坐标,所以这些位置的关系是:
```
x = left = translationX
```

注意,view在平移过程中,left,top都表示原始view的左上角信息,值不会改变.

## MotionEvent

一系列点击事件,提供getX(),getRawX()等方法,getX()获取相对于父View左上角x左边,而getRawX()获取屏幕左上角x左边.

## VelocityTracker

该类用于计算速度

使用方法:
1. 首先在View的onTouchEvent方法中追踪当前单击事件的速度
```
VelocityTracker velocityTracker = VelocityTracker.obtain();
velocityTracker = addMovement(event);
```
2. 计算当前速度,传入时间ms
```
velocityTracker.computeCurrentVelocity(1000);
int xVelocity = (int)velocityTracker.getXVelocity(); //X方向的速度
int yVelocity = (int)velocityTracker.getYVelocity(); //Y方向的速度
```

所以大致使用方法如下:

```

   private VelocityTracker tracker;
 
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(tracker == null){
            tracker = VelocityTracker.obtain();
        }
        tracker.addMovement(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                tracker.computeCurrentVelocity(1000);
                Log.i("Down当前X速度",tracker.getXVelocity() + "");
                break;
            case MotionEvent.ACTION_MOVE:
                tracker.computeCurrentVelocity(1000);
                Log.i("MOVE当前X速度",tracker.getXVelocity() + "");
                break;
            case MotionEvent.ACTION_UP:
                tracker.computeCurrentVelocity(1000);
                Log.i("UP当前X速度",tracker.getXVelocity() + "");
                break;
        }

        return true;
    }

```
## GestureDetector

手势检测,书上讲的和现在使用的有很多不一样的地方,所以贴个博客,感兴趣的再看看吧(http://blog.csdn.net/harvic880925/article/details/39520901)

## Scroller
这个之前已经总结过一遍了->http://www.qxgzone.com/2017/01/09/%E5%AF%B9Scroller%E7%9A%84%E7%90%86%E8%A7%A3/



## View滑动
一般来说实现View的滑动有三种实现方式:
1. 通过View自身的scrollTo()/scrollBy()方法
2. 通过动画给View添加平移效果
3. 通过改变View的LayoutParams使得View重新布局

首先是scrollTo()/scrollBy()方法,这个方法所做的内容是将View中的内容进行移动,而不是View自身.

接着是动画实现的平移效果,可以是属性动画或者传统的动画,但是传统动画所产生的平移并不会改变View元素的真正位置,而属性动画可以.

最后就是通过改变LayoutParams参数,这个就是取得LayoutParams对象后,重新设置对象值,最后更新视图就OK了.

例子(使用transalation来实现):

```
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取当前手指点到的位置
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //获取初始位置
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                //移动的时候改变当前view的位置
                int dx = x - mLastX;
                int dy = y - mLastY;
                int translationX = (int) (getTranslationX() + dx);
                int translationY = (int) (getTranslationY() + dy);
                //通过给当前的view设置translation值,来改变其位置.
                setTranslationX(translationX);
                setTranslationY(translationY);
                //重新加载
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        //保存值
        mLastX = x;
        mLastY = y;
        return true;
    }
```

Scroller的弹性滑动,其实就是模拟,来个例子吧:

```
    //开启模拟过程,并重绘
    mScroller.startScroll(0,0,500,500,1000);
    invalidate();

    @Override
    public void computeScroll() {
        //计算当前scroll的值
        if(mScroller.computeScrollOffset()){
            //取得当前模拟位置,设置到当前View上
            setTranslationX(mScroller.getCurrX());
            postInvalidate();
        }
    }
```


# View事件分发机制

这个要重点掌握.

首先是事件分发机制涉及到的方法:

* public boolean dispatcheTouchEvent(MotionEvent ev),该方法是进行事件分发,返回结果受onTouchEvent和下级的dispathcTouchEvent方法的影响,表示是否消耗当前事件
* public boolean onInterceptTouchEvent(MotionEvent event) 用来判断是否拦截某个事件,如果当前View拦截了这个事件,那么在同一个事件序列中,此方法不再被调用
* public boolean onTouchEvent(MotionEvent event) 处理点击事件,返回结果表示是否消耗当前事件,如果不消耗,则在同一个事件序列中,当前View无法再次接受到事件.

用书中的伪代码表示就一目了然:
```
public boolean dispatchToutchEvent(MotionEvent ev){
    boolean consume = false;
    if(onInterceptTouchEvent(ev)){
        consume = onTouchEvent(ev);    
    } else{
        consume = child.dispatchTouchEvent(ev);
    }
    return consume;
}
```
也就是说对于任何一个事件,首先是传递给dispatchTouchEvent,该事件首先会调用onInterceptTouchEvent方法来判断是否拦截这个事件,如果拦截,就执行onTouchEvent方法,如果不拦截,就把事件分发给子View.

当一个点击事件产生后,事件传递顺序Activity->Window->顶级View,到达顶级View后,顶级View收到该事件后就开始进行分发.如果当前View的onInterceptTouchEvent返回true,那么就不会再分发事件了,也就是说这个事件,我自己独吞了,你们爱干嘛干嘛去.否则就会执行子View的dispatchTouchEvent方法.注意View里面是没有onInterceptTouchEvent方法的,其会直接执行onTouchEvent方法.

如果onTouchEvent返回false,则其上层View会调用onTouchEvent方法,而如果返回true,则表示上层View不用处理这个事件.

书中的总结大致如下:
1. 同一个事件序列就是从手指接触屏幕到手指离开.
2. 正常情况下,一个事件序列只能被一个View消耗.(注意是序列,而不是单一的MotionEvent事件)
3. 某个View一旦拦截当前事件序列,那么这个事件序列都只能由它来处理.(即onInterceptTouchEvent只调用一次返回true,而false的情况很特殊见下面的注解)
4. View在事件序列开始的时候,即传入ACTION_DOWN时,如果当前View不处理ACTION_DOWN,即onTouchEvent返回false,那么以后的事件序列都不会交给它处理.
5. ViewGroup默认不拦截任何事件,onInterceptTouchEvent返回false
6. View没有onInterceptTouchEvent方法,一旦有事件传递给他,就会调用onTouchEvent

注解3:当我们在onInterceptTouchEvent（）方法中返回false，且子View的onTouchEvent返回true的情况下，onInterceptTouchEvent方法才会收到后续的事件。

由上可知,在自定义View时候,onTouchEvent中一定要返回true,不然事件序列中的后部分序列不会被执行.

注意默认并不是返回true.

当点击屏幕,事件最先被发送给Activity,Activity会执行dispatchTouchEvent:
```
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onUserInteraction();
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
```
接着调用Window的superDispatchTouchEvent,这个Window就是PhoneWindow.而PhoneWindow中的superDispatchTouchEvent方法直接把事件交给DecorView来处理.DecorView其实就是顶层View,我们平常Activity中setContentView方法就是给DecorView中的一个子View设置的.

view中onTouch的优先级是比onTouchEvent高的,原因就是可以在外部对View进行操作.
