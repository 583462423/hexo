---
title: Fragment和Activity嵌套的生命周期
date: 2017-03-16 18:14:14
tags:
---

今天携程视频面试,面试虽然很简单,但是第一个问题我就不会= =

第一个问题就是Fragment和Activity嵌套的生命周期.

因为之前只测试过Fragment的生命周期和Activity的生命周期,而Fragment和Activity嵌套情形没测试过,所以一问就懵逼了.下来赶紧测试.

<!--more-->

a 开头的输出表示的是Activity的生命周期

首先打开应用软件时生命周期如下:
```
03-16 21:22:36.632 5284-5284/com.qxg.study.testandroid I/System.out: a onCreate
03-16 21:22:36.685 5284-5284/com.qxg.study.testandroid I/System.out: onAttach
03-16 21:22:36.685 5284-5284/com.qxg.study.testandroid I/System.out: onCreate
03-16 21:22:36.685 5284-5284/com.qxg.study.testandroid I/System.out: onCreateView
03-16 21:22:36.688 5284-5284/com.qxg.study.testandroid I/System.out: a onStart
03-16 21:22:36.688 5284-5284/com.qxg.study.testandroid I/System.out: onViewStateRestored
03-16 21:22:36.688 5284-5284/com.qxg.study.testandroid I/System.out: onStart
03-16 21:22:36.688 5284-5284/com.qxg.study.testandroid I/System.out: a onResume
03-16 21:22:36.688 5284-5284/com.qxg.study.testandroid I/System.out: onResume
```

注意首先一定是调用Activity的onCreate方法,然后调用setContentView后,才开始Fragment的一系列方法,其实可以看成Activity,Fragment的穿插调用,将onAttach,onCreate,onCreateView看成Fragment的create方法,就能看出来,调用完Activity的create方法后,就会调用Fragment的create方法,然后是onStart,F的onStart,A的onResume,F的onResume.

接着是点击home时候生命周期变化:

```
03-16 18:22:05.995 17361-17361/com.qxg.study.testandroid I/System.out: onPause
03-16 18:22:05.995 17361-17361/com.qxg.study.testandroid I/System.out: a onPause
03-16 18:22:06.008 17361-17361/com.qxg.study.testandroid I/System.out: onSaveInstaceState
03-16 18:22:06.009 17361-17361/com.qxg.study.testandroid I/System.out: a onSaveInstaceState
03-16 18:22:06.009 17361-17361/com.qxg.study.testandroid I/System.out: onStop
03-16 18:22:06.009 17361-17361/com.qxg.study.testandroid I/System.out: a onStop
```

可以看到Fragment和Activity的生命周期是交叉性变化的,首先是F的onPause,接着A的onPause,接着...

接着是恢复的时候生命周期变化:
```
03-16 21:26:37.303 5284-5284/com.qxg.study.testandroid I/System.out: a onStart
03-16 21:26:37.303 5284-5284/com.qxg.study.testandroid I/System.out: onStart
03-16 21:26:37.303 5284-5284/com.qxg.study.testandroid I/System.out: a onResume
03-16 21:26:37.303 5284-5284/com.qxg.study.testandroid I/System.out: onResume
```
可以看出,也是交叉变化

接着是旋转屏幕的:
```
03-16 21:27:07.511 5284-5284/com.qxg.study.testandroid I/System.out: a onPause
03-16 21:27:07.511 5284-5284/com.qxg.study.testandroid I/System.out: onPause
03-16 21:27:07.511 5284-5284/com.qxg.study.testandroid I/System.out: a onSaveInstanceState
03-16 21:27:07.511 5284-5284/com.qxg.study.testandroid I/System.out: onSaveInstaceState
03-16 21:27:07.511 5284-5284/com.qxg.study.testandroid I/System.out: a onStop
03-16 21:27:07.511 5284-5284/com.qxg.study.testandroid I/System.out: onStop
03-16 21:27:07.511 5284-5284/com.qxg.study.testandroid I/System.out: a onDestroy
03-16 21:27:07.511 5284-5284/com.qxg.study.testandroid I/System.out: onDestroyView
03-16 21:27:07.511 5284-5284/com.qxg.study.testandroid I/System.out: onDestroy
03-16 21:27:07.511 5284-5284/com.qxg.study.testandroid I/System.out: onDetach


03-16 21:27:07.543 5284-5284/com.qxg.study.testandroid I/System.out: a onCreate
03-16 21:27:07.545 5284-5284/com.qxg.study.testandroid I/System.out: onAttach
03-16 21:27:07.545 5284-5284/com.qxg.study.testandroid I/System.out: onCreate
03-16 21:27:07.545 5284-5284/com.qxg.study.testandroid I/System.out: onCreateView
03-16 21:27:07.547 5284-5284/com.qxg.study.testandroid I/System.out: a onStart
03-16 21:27:07.547 5284-5284/com.qxg.study.testandroid I/System.out: onViewStateRestored
03-16 21:27:07.547 5284-5284/com.qxg.study.testandroid I/System.out: onStart
03-16 21:27:07.547 5284-5284/com.qxg.study.testandroid I/System.out: a onResume
03-16 21:27:07.547 5284-5284/com.qxg.study.testandroid I/System.out: onResume
```

这个过程还是挺让人头疼的.其实Fragment的onDestroyView,onDestroy,onDetach可以看做一个整体的destroy方法,所以转屏的时候,两者生命周期依然是交叉,并且都被销毁.

注意是先调用onPause方法,然后调用onSaveInstanceState.

然后又是一套重绘过程

所以总结下来,其实就是Activity和Fragment有对应的create,start,resume,pause,stop,destroy等方法,其执行顺序就是交叉执行,不过先执行A的方法,再执行F的方法.
