---
title: 'Android:Looper-Handler-Message源码探究'
date: 2017-01-09 12:16:05
tags:
---
为了探究Handler，Looper,Message,以及MessageQueue之间的关系，专门跑到AndroidStudio去查看代码，本来只是想看看，没想到一看竟然着了迷。哎

<!--more-->
这几个类我们最早接触的是Handler,当然使用的时候一般也是先实例化Handler,所以我们先到Handler源码中瞧瞧去。
刚开始我是从上往下看的，发现其中有一个回调接口Callback。呦呵，可能我用的Handler比较少，竟然不知道Handler中有个回调接口，这就引发我极大的兴趣去研究这个Callback是做什么的了。

```
public interface Callback {
        public boolean handleMessage(Message msg);
    }
```
接着往下看，是他的构造函数，构造函数虽然比较多，但是最终调用的没几个

```
//最终调用Handler(null,false)
public Handler() {
    this(null, false);
}
    
//最终调用Handler(callback,false)
public Handler(Callback callback) {
	this(callback, false);
}
//最终调用Handler(looper,null,false)
public Handler(Looper looper) {
    this(looper, null, false);
}
//最终调用Handler(looper,callback,false)
public Handler(Looper looper, Callback callback) {
    this(looper, callback, false);
}
//最终调用handler(null,boolean)
public Handler(boolean async) {
    this(null, async);
}
public Handler(Callback callback, boolean async) {
    if (FIND_POTENTIAL_LEAKS) {
        final Class<? extends Handler> klass = getClass();
        if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
                (klass.getModifiers() & Modifier.STATIC) == 0) {
            Log.w(TAG, "The following Handler class should be static or leaks might occur: " +
                klass.getCanonicalName());
        }
    }
    mLooper = Looper.myLooper();
    if (mLooper == null) {
        throw new RuntimeException(
            "Can't create handler inside thread that has not called Looper.prepare()");
    }
    mQueue = mLooper.mQueue;
    mCallback = callback;
    mAsynchronous = async;
}
public Handler(Looper looper, Callback callback, boolean async) {
    mLooper = looper;
    mQueue = looper.mQueue;
    mCallback = callback;
    mAsynchronous = async;
}
```
从构造函数中可以看到，一般我们实例化的时候，如果参数中没有looper的最终调用Handler(Callback,boolean)方法，而有looper的最终都是调用Handler(looper,callback,boolean)方法了。

先看两个构造方法中的第一个。
FIND-POTENTIAL_LEAKS是一个标志变量，如果为true,则通过反射检查实例化时是否是成员类，匿名类，本地类或者非static对象，如果是，则打印warning日志，但是不会影响代码的执行，打印日志大意就是我们的对象应该加上static标志，否则可能会存在内存泄漏。至于原因，其实我也不太理解，所以google了一下,有个解释是这样说的，在同一个线程中的handler共享同一个looper,他们向同一个消息队列传递消息，每一个Message都会拥有一个Target引用，这个Target正是handler，如果handler不是静态的，那么消息存在的时候，垃圾回收并不会处理该handler，而同样的，使用handler的activity或service也不会被处理，即便他们调用了onDestroy方法，这样就造成了内存泄漏的问题。当然，这通常不会发生，除非你发送了一个延时很长的消息。

但是当我们给我们的handler加上static时，发现在handler调用外部类的成员或者方法时，需要把它们定义为final，这显然不大可能，所以建议在static Handler中使用若引用，引用可能使用到的activity,或者service
如：
```
static class IncomingHandler extends Handler {
    private final WeakReference<UDPListenerService> mService; 
    IncomingHandler(UDPListenerService service) {
        mService = new WeakReference<UDPListenerService>(service);
    }
    @Override
    public void handleMessage(Message msg)
    {
         UDPListenerService service = mService.get();
         if (service != null) {
              service.handleMessage(msg);
         }
    }
}
static class MHandler extends Handler {  
    WeakReference<OuterClass> outerClass;  
  
    MHandler(OuterClass activity) {  
        outerClass = new WeakReference<OuterClass>(activity);  
    }  
  
    @Override  
    public void handleMessage(android.os.Message msg) {  
        OuterClass theClass = outerClass.get();  
        switch (msg.what) {  
        case 0: {  
            //使用theClass访问外部类成员和方法  
            break;  
        }  
        default: {  
            Log.w(TAG, "未知的Handler Message:" + msg.what);  
        }  
        }  
 
    }  
}
```
好了，即便这样，一般我们在使用的时候不会搞这么复杂，就目前来看，我们发送一个消息后，消息立马就会被消费，所以内存泄漏的几率是比较低的。

接着看下面的源码，因为我们没有传递looper,而对于每个handler，looper对象都是必须的，所以下面执行了mLooper = Looper.myLooper()这个时候，我们进入Looper中查看myLooper()方法

```
public static @Nullable Looper myLooper() {
    return sThreadLocal.get();
}
```
简单理解，这个方法返回的是当前线程中的一个looper实例，那么毫无疑问，在主线程中声明的handler，其使用的looper必然是主线程中的looper。同时，将获取的looper实例中MessageQueue对象传递给Handler中的mQueue,后边的代码就不解释了，再看第二个构造函数，其只是简单的赋值。那么这两个构造函数本质上的区别就是looper和mQueue的不同，第一个构造函数的looper使用的是主线程中的looper,而第二个则使用的是传递过来的looper。

我们平常使用handler的两种类型的方法是handler.post()和handler.sendMessage()；那么我们接着来查看这两种方法的源码，首先看handler.post()等一系列的方法。

```
public final boolean post(Runnable r)
{
   return  sendMessageDelayed(getPostMessage(r), 0);
}
public final boolean postAtTime(Runnable r, long uptimeMillis)
{
    return sendMessageAtTime(getPostMessage(r), uptimeMillis);
}
public final boolean postDelayed(Runnable r, long delayMillis)
{
    return sendMessageDelayed(getPostMessage(r), delayMillis);
}
```
还有一部分post方法没有贴出，因为不经常用，这里可以看到，post和postDelayed方法中最终调用的是sendMessageDeayed方法，其中传递了一个Message对象，这个Message对象是通过getPostMessage(r)来取得的，我们查看下这个方法

```
private static Message getPostMessage(Runnable r) {
    Message m = Message.obtain();
    m.callback = r;
    return m;
}
```
可以发现，通过该方法取得一个消息，会将Runnable对象r赋给该消息中的Callback。即这样得到的一个消息是携带着Runnable对象的。

```
public final boolean sendMessageDelayed(Message msg, long delayMillis)
{
    if (delayMillis < 0) {
        delayMillis = 0;
    }
    return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
}
```
sendMessageDelayed方法最终调用的是sendMessageAtTime方法，所以我们知道了post方法最终调用的是sendMessageAtTime方法，区别在于时间不同。
```
public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
    MessageQueue queue = mQueue;
    if (queue == null) {
        RuntimeException e = new RuntimeException(
                this + " sendMessageAtTime() called with no mQueue");
        Log.w("Looper", e.getMessage(), e);
        return false;
    }
    return enqueueMessage(queue, msg, uptimeMillis);
}
```
我们先忽略掉queue为空的情况，可以看到这个方法又调用了enqueueMessage，该方法从字面意思上来讲，就是把消息入队，其传递的参数是queue，即消息队列，msg，即携带这runnable对象的消息

接着查看enqueueMessage方法

```
private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
    msg.target = this;
    if (mAsynchronous) {
        msg.setAsynchronous(true);
    }
    return queue.enqueueMessage(msg, uptimeMillis);
}
```
在这里，将msg的target设置为handler，并调用了消息队列中的enqueueMessage方法。
跟着程序走，我们去看一下消息队列中的enqueueMessage方法是什么样的。

```
boolean enqueueMessage(Message msg, long when) {
    if (msg.target == null) {
        throw new IllegalArgumentException("Message must have a target.");
    }
    if (msg.isInUse()) {
        throw new IllegalStateException(msg + " This message is already in use.");
    }
    synchronized (this) {
        if (mQuitting) {
            IllegalStateException e = new IllegalStateException(
                    msg.target + " sending message to a Handler on a dead thread");
            Log.w(TAG, e.getMessage(), e);
            msg.recycle();
            return false;
        }
        msg.markInUse();
        msg.when = when;
        Message p = mMessages;
        boolean needWake;
        if (p == null || when == 0 || when < p.when) {
            // New head, wake up the event queue if blocked.
            msg.next = p;
            mMessages = msg;
            needWake = mBlocked;
        } else {
            // Inserted within the middle of the queue.  Usually we don't have to wake
            // up the event queue unless there is a barrier at the head of the queue
            // and the message is the earliest asynchronous message in the queue.
            needWake = mBlocked && p.target == null && msg.isAsynchronous();
            Message prev;
            for (;;) {
                prev = p;
                p = p.next;
                if (p == null || when < p.when) {
                    break;
                }
                if (needWake && p.isAsynchronous()) {
                    needWake = false;
                }
            }
            msg.next = p; // invariant: p == prev.next
            prev.next = msg;
        }
        // We can assume mPtr != 0 because mQuitting is false.
        if (needWake) {
            nativeWake(mPtr);
        }
    }
    return true;
}
```
这个代码比较长。

首先了解一下，MessageQueue中并没有使用ArrayList来维护其队列中的Message对象，而是使用链表的方式，对于每一个Message对象，他都包含有一个成员对象Message next，就是因为这个next使得MessageQueue中的Message构成一条链。而MessageQueue有一个Message对象用来维护当前将要消费的Message对象，如上述代码，即mMessages。所以，我们不用仔细查看上述代码，只需了解，上述操作只是让msg入队。

但是到这好像结束了？？下一步操作在哪呢？

别忘了，我们还有一个类叫Looper，我们知道looper会从MessageQueue循环取出消息对象，交给Handler处理。

所以接着，我们查看Looper中的方法。

首先是构造方法

```
private Looper(boolean quitAllowed) {
    mQueue = new MessageQueue(quitAllowed);
    mThread = Thread.currentThread();
}
```
可以看到构造方法是私有的，那么外部是无法通过构造方法来构造一个Looper对象的，所以一定是在该类的内部的某个地方调用了该构造方法。构造方法中，首先实例化一个消息队列，并设置线程为当前线程。

接着，我们去寻找实例Looper的代码，经查找，实例化Looper的代码只在一个方法中出现

```
private static void prepare(boolean quitAllowed) {
    if (sThreadLocal.get() != null) {
        throw new RuntimeException("Only one Looper may be created per thread");
    }
    sThreadLocal.set(new Looper(quitAllowed));
}
```
可以知道，我们在使用Looper的时候，首先必须要调用的肯定是prepare方法，否则是实例化不到Looper对象的。但是如果调用prepare的时候，sThreadLocal中不为空，那么此方法就会抛出异常，比如如果在我们的主线程中调用prepare()方法，一定会抛异常，因为我们的主线程已经通过此方法实例化了一个Looper对象，其实我们查看ActivityThread的入口函数main就能看到，其已经调用了Looper.prepareMainLooper()方法，该方法会实例化一个不允许退出的Looper对象。ThreadLocal的简单理解，就是维护当前线程中的某个对象，比如在主线程中使用sThreadLoca.get()就是取出主线程中的Looper对象。

既然存入了Looper对象，那么肯定会有取出过程。接着寻找sThreadLocal.get()方法。

```
public static @Nullable Looper myLooper() {
    return sThreadLocal.get();
}
```
到这，我们就知道了，对于一个Looper对象，首先我们要通过prepare()方法来实例化一个Looper对象，如果想要得到该对象，就需要调用Looper.myLooper()方法。这里我们可以大胆的猜测，主线程在运行的时候，一定在某个地方已经调用过Looper.prepare()方法了，否则在前边的源代码中，我们最先接触的是Looper.myLooper()，如果没有实例化，运行Looper.myLooper()方法是一定会报错的。

既然是Looper，那么肯定会循环从自己的消息队列取出消息的，那么我们开始查找取消息队列的方法。其实运用之前接触的知识,循环是在loop()方法中，所以我们查看loop()方法。

```
public static void loop() {
    final Looper me = myLooper();
    if (me == null) {
        throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
    }
    final MessageQueue queue = me.mQueue;
    // Make sure the identity of this thread is that of the local process,
    // and keep track of what that identity token actually is.
    Binder.clearCallingIdentity();
    final long ident = Binder.clearCallingIdentity();
    for (;;) {
        Message msg = queue.next(); // might block
        if (msg == null) {
            // No message indicates that the message queue is quitting.
            return;
        }
        // This must be in a local variable, in case a UI event sets the logger
        Printer logging = me.mLogging;
        if (logging != null) {
            logging.println(">>>>> Dispatching to " + msg.target + " " +
                    msg.callback + ": " + msg.what);
        }
        msg.target.dispatchMessage(msg);
        if (logging != null) {
            logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
        }
        // Make sure that during the course of dispatching the
        // identity of the thread wasn't corrupted.
        final long newIdent = Binder.clearCallingIdentity();
        if (ident != newIdent) {
            Log.wtf(TAG, "Thread identity changed from 0x"
                    + Long.toHexString(ident) + " to 0x"
                    + Long.toHexString(newIdent) + " while dispatching to "
                    + msg.target.getClass().getName() + " "
                    + msg.callback + " what=" + msg.what);
        }
        msg.recycleUnchecked();
    }
}
```
代码首先将自己的Looper对象赋给me，消息队列对象赋给queue。在往后看，这里出现了for(;;)代码，即死循环。

这个时候我们就纳闷了，如果陷入死循环，程序不就卡死了么？

那我们就以主线程中的Looper为例简单说明一下，其实我们的程序都是运行在Looper下的，比如像什么Activity的onCreate方法，在运行前，会有消息传入消息队列中，这个消息会告诉Handler：我需要你执行Activity的onCreate方法，Looper在得到该消息后，就会进入onCreate方法中执行。

我们查看ActivityThread中的main方法，发现他最后两行执行的代码是：

```
Looper.loop();
throw new RuntimeException("Main thread loop unexpectedly exited");
```
这就说的通了，也就是说loop()方法后的任何方法都不会被执行，因为在loop()方法中已经阻塞了进程，或者说程序一直在运行loop()中的for(;;)，根本不可能跳出来。如果被执行，就是抛异常，也就是说跳出了死循环，这个时候程序应该运行什么呢，运行哪个方法呢？如果跳出来就什么也运行不了了，那我就直接让你崩，直接给你抛个RuntimeException。

所以不要怀疑，Looper的loop()方法确实会陷入死循环，在安卓中执行的很多方法都是在死循环下运行的。

这就解释了，程序为什么在死循环却依然能正常运行。
好了回归正题：

接着往下看，首先我们取得消息接着调用msg.target.dispatchMessage(msg)方法。msg.target是一个Handler，那么这段代码调用的就是handler的dispatchMessage(msg)方法。先别急到该方法中去，再往后看，又调用了msg.recycleUnchecked()方法，这个方法就是回收消息的操作，回收完毕就会接着下一个循环。这个时候接着看dispatchMessage方法。
```
public void dispatchMessage(Message msg) {
    if (msg.callback != null) {
        handleCallback(msg);
    } else {
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) {
                return;
            }
        }
        handleMessage(msg);
    }
}
```
看到这就明白了，哦，如果使用post方法，最终会调用handleCallback()方法，而如果使用send方法，那么就会调用handleMessage方法。

在handleCallback()方法中，简单的调用了callback的run()方法。

那么回到开始，当我们使用handler.post(runnable)等一系列方法时，实际上发出的是一个带有runnable对象的消息，接着通过enqueueMessage方法入队，而Looper在取出消息后，会通过消息的target对象来执行dispatchMessage方法，该方法中通过判断消息是否携带runnable，如果携带了就会执行handleCallback方法，而该方法内部只是执行了runnable的run()方法。如果没有携带，首先会判断我们的handler是否含有callback，如果有就会执行callback的handlMessage方法。从这里可以看出，如果我们实例化handler的时候，传入了Callback，那么就不用再重写其中的handleMessage方法了。这样就明白了Callback接口的意义了。

那么同样的我们看handler的一系列的sendMessage方法。

```
public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
    MessageQueue queue = mQueue;
    if (queue == null) {
        RuntimeException e = new RuntimeException(
                this + " sendMessageAtTime() called with no mQueue");
        Log.w("Looper", e.getMessage(), e);
        return false;
    }
    return enqueueMessage(queue, msg, uptimeMillis);
}
```
不用贴太多的代码，只需知道，sendMessage的一系列方法最终调用的是sendMessageAtTime方法，而该方法内部中最终调用enqueueMessage()方法，这里就和post一样了。

那么我们可以知道，其实post和send两种类型的方法，最终都是调用enqueueMessage方法，区别就在于，post方法最终入队的消息，携带一个runnable对象，而send方法没有携带该对象。

如果在主线程，我们不用使用Loopre.prepare()方法来实例化一个Looper对象，因为主线程中已经存在了一个Looper和MessageQueue，如果我们想要在子线程中不使用主线程中的Looper对象，就可以在子线程中先调用Looper.prepare()来实例化自己的looper对象，接着使用Looper.myLooper()来得到该对象。当进行完这些步骤后，我们就可以使用handler来post或者send消息了。当然仅仅这样还是不够的，如果想要looper取得消息，还需要在最后执行looper.loop()方法，这样才会调用内部的for(;;)进行循环取消息操作，才会执行后来的msg.target.dispatchMessage()方法。

光说没用，接下来实践一下，直接贴代码：

```
new Thread(new Runnable() {
    @Override
    public void run() {
        Looper.prepare();
        //如果在这个地方实例化handler，那么该handler就会于该线程中的Looper对象想关联，不用使用Handler(looper)构造，当然使用了也没差
        lHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i("当前线程是:",Thread.currentThread().toString());
                lHandler.sendEmptyMessageDelayed(0,2000); //每隔2秒打印日志
            }
        };
        lHandler.sendEmptyMessage(0);
        //记得调用Looper.loop()方法，否则无法取出消息
        Looper.loop();
    }
}).start();
```
打印的日志为：
```
07-21 10:51:19.621 31391-31521/com.test.testandroid I/当前线程是:: Thread[Thread-1291,5,main]
07-21 10:51:21.621 31391-31521/com.test.testandroid I/当前线程是:: Thread[Thread-1291,5,main]
07-21 10:51:23.621 31391-31521/com.test.testandroid I/当前线程是:: Thread[Thread-1291,5,main]
07-21 10:51:25.621 31391-31521/com.test.testandroid I/当前线程是:: Thread[Thread-1291,5,main]
```
可以看到看到其handleMessage是在子线程中运行的。如果尝试在handleMessage更新主线程UI，是必然报错的。

Only the original thread that created a view hierarchy can touch its views
意思为，View只能通过创造他的进程来更新。

其实我们还有另外一种方法使得handle在子线程中运行，就是借助HandlerThread，用他来取得Looper对象，然后将该对象送入Handler中，这就对应了Handler的第二种构造方法。

如

```
HandlerThread hThread = new HandlerThread("my handler");
Handler handler = new Handler(hThread.getLooper())
{
	handleMessage(Message msg)
    {
    	super.handleMessage(msg);
    	//some code
    }
};
```
好了大概就介绍到这，如果有不明白或者错误的地方，望指出~
