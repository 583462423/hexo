---
title: Java编程语言笔记
date: 2017-03-19 10:18:45
tags: Java
---

有幸得知Java四大名著,所以来尝试阅读一下,肯定会从中发现自身知识的漏洞,这里记录的是,读书后我所不知道的知识.

<!--more-->

类修饰符:public,abstract,final,static只能修饰嵌套类.还有一个比较特殊的是**strictfp** ,这个关键字的含义是,如果被strictfp修饰,该类中的所有运算都会进行精确运算,符合某某标准.

一个域既可以是final,也可以是volatile.

域可以通过类中方法或者其他类中的方法进行初始化,这里不用捕获异常.而一旦创建该实例的时候,创建者处会捕获异常,所以不用担心异常这个问题.

如果域没有被初始化,就会根据类型赋予默认的初始值.

|类型|初始值|
|---|---|
|boolean|false|
|char|'\u0000'|
|byte,short,int,long|0|
|float|+0.0f|
|double|+0.0|
|对象引用|null|


以下演示初始代码块,静态代码块的执行顺序:
```
public class Car {
    char a = g();

    {
        System.out.println("初始化块");
    }


    static{
        System.out.println("静态代码块");
    }

    public Car(){
        System.out.println("构造方法块");
    }

    public char g(){
        System.out.println("域初始化");
        return 'a';
    }
}
```

打印结果:
> 静态代码块
初始化块
域初始化
构造方法块

注意初始化代码块就是用`{}`包裹的代码.

而如果把域和初始化块调换位置,如下:
```
public class Car {
    {
        System.out.println("初始化块");
    }

    char a = g();

    static{
        System.out.println("静态代码块");
    }

    public Car(){
        System.out.println("构造方法块");
    }

    public char g(){
        System.out.println("域初始化");
        return 'a';
    }
}

```

打印结果是:
> 静态代码块
初始化块
域初始化
构造方法块

可以看到,其初始化代码块和域的初始化是同等级别的,谁先谁后是按照从上到下的执行顺序.而static是类加载首先执行的,构造方法最后才会执行.
注意,静态初始化块,只能引用该类中的静态域.如果定义一个final属性,可以在初始化块中或者构造方法中进行初始化.

如果是子类继承父类的时候,首先会调用父类的构造方法,接着才会调用自身的初始化块,以及构造方法,这个一定要注意.

abstact方法不能是staic,final,syn,native,strictfp的.

参数中传入的对象引用时,参数的引用将会指向同一个对象,此时该对象就会存在两条被引用链.注意不是传入的对象,所以将对象=null完全不影响外部对象.


子类覆盖超类方法时,不可降低权限.

覆盖的时候,也可以改变其他修饰符:syn,native,strictfp等.

覆盖抛异常要比父类的少,不能比父类多.

对于引用类型,父类引用指向子类,这个时候调用方法时,会调用真是对象的方法,而调用域时,会根据引用来调用域,如下示例:
```
public class Car {

    public String str = "Car";

    public void show(){
        System.out.println("show Car");
    }
}


public  class ExtendsCar extends Car {

    public String str = "ExtendsCar";

    @Override
    public void show() {
        System.out.println("show ExtendsCar");
    }
}


 public static void main(String[] args){
        ExtendsCar extendsCar = new ExtendsCar();
        Car car = extendsCar;

        extendsCar.show();
        car.show();

        System.out.println(extendsCar.str);
        System.out.println(car.str);
    }

```

其打印日志为:
```
show ExtendsCar
show ExtendsCar
ExtendsCar
Car
```
可以看到,其方法是调用的实际对象的方法,而属性值却是引用对象类型的属性,但是前提是子类覆盖了父类的属性,如果未覆盖,即子类中没有str这个String类型属性值,那么打印结果是一样的.

final类的所有方法实际上都是final,注意是方法.

对于强转,强转时两者的类对象必须有联系,比如一个是父类,一个是子类,又比如,一个是父类实现的接口,一个是子类等等.

同样对于instanceof也是如此,如果两者没有一点联系,编译都通不过.

克隆的默认实现是浅克隆.即不对引用进行克隆,其传递的引用还是指向原对象.


接口内部方法和域默认是public,而方法默认是abstract,这个关键字被省略了.所以实现接口时,方法只能是public,不能是其他权限,而在接口的方法中,不能有其他实现特征的修饰符如:syn,native,strictfp等.

对于接口默认的修饰符是abstract.如果没有使用public修饰接口,只能被其所在的包的成员访问.

考虑这么一种情况,在第一个接口中,定义了方法`void tmp();`,第二个接口中定义了方法`int tmp()`,而第三个接口继承了第一个和第二个接口,这样继承会出现问题么?答案是会的,除非两者接口中同名同参方法返回相同才不会报错.


静态内部类可以访问外部类的所有成员,包括私有成员.

内部接口总是静态的,定义内部接口习惯上省略了static,注意内部接口默认不是public

内部类继承:
```
public class Outer {
    class Inner{

    }
}

public class T extends Outer {
    class InnerT extends Outer.Inner{

    }
}
```
如果非静态内部类,只能先继承外部类.


接口中允许有嵌套类,且嵌套类默认就是public static,所以省略public static也可以.


Java的注释不允许嵌套.

Java的符号化器非常"贪婪",`j+++++i`将被解析为`j++ ++ +i`

Java标识符必须以字母或$以及_开头,后跟字母数字串,而Java中的字母因为是用的unicode编写的,所以字母的范围很宽泛,也可以是中文,或者其他拉丁文啦等等等,当然也包括货币符号`$,¥`,以及`_`等等.所以对于`int 变量 = 1;`这样是合法的.

Java标识符可以有任意长度.

没有任何办法对数组元素添加修饰符(`final`和`volatile`)

对于以下数组
```
  int[][] a = {
            {1},
            {1, 2},
            {1, 3, 4}
    };
```
在Java中是合法的,并且,a数组长度为3,a[1]长度为1,a[2]为2,a[3]为3.

数组是Object的隐式子类.

strictfp是不具备继承性的,一个声明为strictfp的类或接口不会导致他们的继承类或接口为strictfp的.

`x = y = z = 3;`Java中是合法的.

java中有标号,通常和break,continue结合使用
如:
```
    int cnt = 0;
    Label:{
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
               cnt++;
               if(i==5 && j==9)break Label;
            }
        }
    }
    System.out.println(cnt);
```
输出为60,break Label的意思是,退出Label指定的循环块.

同样continue就不举例了.

Throwable是类而不是接口.


String 里面有一个方法是intern();,该方法的含义是:当调用 intern 方法时，如果池已经包含一个等于此 String 对象的字符串（该对象由 equals(Object) 方法确定），则返回池中的字符串。否则，将此 String 对象添加到池中，并且返回此 String 对象的引用。具体内容可查看:http://www.cnblogs.com/wanlipeng/archive/2010/10/21/1857513.html

对每个线程只能调用一次start()方法,再次调用将会抛出IllegalThreadStateException异常.
线程的中止可以通过interrput方法来中止.
main方法本身就是通过运行时系统创建的线程来执行的.
守护线程(Daemon)：http://www.cnblogs.com/super-d2/p/3348183.html
线程分为user线程和daemon线程，user线程的存在保持应用程序的运行。当最后一个user线程运行结束后，所有daemon线程结束，应用程序结束。使用setDaemon可以把线程设置为守护线程。守护线程具有继承性，如果当前线程是守护线程，则当前线程中创建的线程也是守护线程。线程启动后，它的daemon状态就不能再改变，否则抛出异常，也就是说只能再threa.start()方法之前改变线程的状态。


sychronized忘记释放锁是不可能的,不论是抛异常还是return还是正常结束都会释放锁.
之所以不用直接存取而使用setter,getter的方法的原因之一是可以同步对所取数据的访问.
当一个继承类覆盖同步方法时,新方法既可以是同步方法也可以不是,但是调用超类的方法时,依然是同步方法

wait调用的时候,必须在while循环中,如果将while改成if,wait被唤醒的时候可能状态又一次被改变,这个时候再向下运行会导致出错.另外,notify,notifyAll如果被调用的时候没有等待线程,则通知就会被忽略
wait,notify,notifyAll一般是和sychronized配合使用.

线程Thread,可以通过setPriority来改变线程的优先级.
一般来说,用户程序的线程优先级低于稀有事件,如用户键盘输入,点击Esc希望程序退出等.

Thread.sleep()方法休眠的事件并不是精确的,其休眠时间至少是所指定的毫秒数.注意是至少,即无法保证是准确的传入的时间.
调用线程的destroy()方法是比较极端的,它终止线程的执行,不管任务是什么,也不释放任何锁,所以使用该方法可能会导致其他线程永远等待.
线程中的join()方法,意为等待该线程任务结束,类似wait()但不是wait.查看源码可知道其内部是通过wait()来实现的.

包装类中的Byte,Short,Integer,Long,Float,Doublde等都是继承于Number类型
Boolean类型是不能比较的。

ClassLoader中的defineClass方法返回Class对象，仅仅表示一个被载入的类，但是它还没有被链接。可以通过显示调用resolveClass来进行链接。
类的准备一般经过三步：
1. 载入：得到类实现的字节码，并创建Class对象
2. 链接：验证类的字节码是否遵循语言规范，让虚拟机为类的静态域分配空间，并且解析出类中的全部引用(可选)，必要时载入被引用的类。
3. 初始化：先初始化父类(必要时)再执行所有的静态初始化函数和类的静态初始化块。

System.gc()和Runtime.getRuntime().gc()是等效的。也就是说，gc是Runtime中的方法。
Runtime中有freeMemory()方法，用来返回系统中可用内存字节数的估计值，而totalMemory()返回系统全部内存资源的字节值。


IO字节流中的read方法是读取一个字节，虽然返回的是int值，其原因在于需要其他数据来标记文件末尾或者其他标记。
当然字符流中也是返回int，但是其有效值是最后16位char值。
InputStream中的available()方法返回能够读到的字节。
Reader中的ready()方法告诉你是否有数据。

InputStreamReader构造方法可以指定字节编码格式。

SequenceInputStream可以将多个InputStream连接读入





write(int b)只有最后8位能够被写入。

有两种iterator，一种是默认的另一种是ListIterator,ListIterator是操作list对象的，其比iterator多了几个方法如，privious,hasPrivious，以及set,add等方法。

final修饰数组的时候，只能数组引用不可变，而数组内容是可以随便改变的。如果想要数组内容也不可变，需要使用Collections集合。考虑下面一种情形：
```
public final String suits[] = {"a","b","c"};
suits[1] = "e"; //可以
```
那么让其内容变成不可变的方法是：
```
private final String suitNames[] = {"a","b","c"};
public final List suits = Collections.unmodifiableList(Arrays.asList(suitsName));
//suits只能访问成员，不可通过suits修改其中的内容。
```
Collections中还有很多实用的方法，如：
```
min(Collection)//返回集合中的最小元素
public static Comparator reverseOrder() //返回比较其，使它排序得到对象自然顺序的倒序，所以Collections.reverseOrder().compare(o1,o2)返回 o2.compareTo(o1)
reverse(List list)//倒转表种元素的顺序
shuffle(List list)//随机排序表
fill(List list,Object elem)//替换表种每个元素
copy(List dst,List src)//从src向dst复制每一个元素
binarySearch(List list,Object key)//二分查找，List需要实现自然排序
```

相对于Collections,对于数组(Arrays)其也有很多实用方法:
```
sort:给数组排序，算法代价不高于O(nlogn),内部是使用快排的方式，文档中说的是这种快排比传统快排快，所以Java中可以使用这种方式来进行排序。
fill:用指定值填写数组
```

Enumeration与Iterator类似，但只有两个方法：hasMoreElements类似于hasNext, nextElement类似于next，可以通过静态方法Collections.enumeration得到Enumeration列举一个非遗留集合的内容。



# java.util中的一些类

## BitSet
BitSet(int size)创建一个新的BitSet，索引值为 0～size-1,默认每个位置都是false。

BitSet()则创建一个默认大小的BitSet。

有三种方法处理位单位：
`set(int index)`：将index所指的位设置为true。
`clear(int index)`:将index所指的位置设置为false。
`get(index)`取得index所指向位的值


当然其还有一系列位逻辑运算，如`and(BitSet other)`,`xor(BitSet other)`等等

## Observer/Observable

哈哈，原来Java中自带观察者模式，这样以后面试的时候就不用自己写了～～～

Observable是被观察者，其有Observer的列表(Vector)，当Observable改变的时候，会调用列表中的方法进行响应。

Observer是一个借口，其只有一个方法：
```
public void update(Observable obj,Object arg); //当被观察者通知观察者时，调用改方法
```

Observable是一个类，其方法如下：

```
setChanged()：标识这个对象已经被改变，则之后hasChanged方法会返回true，但是不通知观察者
clearChanged():清楚标记
hasChanged()：返回当前是否变化，一般在通知的时候调用
notifyObservers();//通知列表中所有Observer对象发生了改变，并清楚变化标志(clearChanged)，对每个Observer调用其update方法。

addObserver(Observer o)：添加观察者
deleteObserver(Observer):删除观察者
deleteObservers():删除所有观察者
countObservers():返回列表中所有的观察者数目
```


## Random
Math.random()就可以创建一个Random对象（static final形的,并不是每次都创建），并从对象中返回一个随机数。

其构造器如下：
```
Random():创建一个新的随机数发生器，它将会根据当前的时间初始化一个值，及时间是种子
Random(long seed):使用特定的种子产生一个随机数发生器
```

方法如下：
```
setSeed(long seed)设置种子
nextBoolean():返回随机boolean值
nextInt():返回随机int值
nextInt(int ceiling):返回[0,ceiling)的随机值，ceiling不能是负数
nextXXXX..
```


## StringTokenizer

这个方法比较厉害了，平常我们会遇到一些分割字符的题目，一般会使用split，但是这个方法分割是确切分割，比如"s, ,p"，使用split(",")分割后,第二个字符串就是一个空格，但是我们分割的时候想要把空格去掉，或者去掉一些其他的字符，这个时候就可以使用StringTokenizer,比如下面这个例子：

```
String s = "I,m  huang.chun,ning , , h";
StringTokenizer token = new StringTokenizer(s,", .");

while(token.hasMoreTokens()){
    System.out.println(token.nextToken());
}
```
注意，其想要分割的是单词，也就是说把额外的","空格，以及"."都作为分割符，所以传入了", ."，第二个参数是一个字符串，字符串中每个符号都会作为分隔符，这样就能获得正确结果。

其构造方法还有：
```
public StringTokenizer(String str, String delim,boolean returnTokens)://第三个参数标识，是否将分隔符也返回。如果填true，则返回的结果中分隔符也会作为一个Token返回。而上述例子实际上就是调用了StringTokenizer(s,", .",false);

public StringTokenizer(String str)：等同于StringTokenizer(str,"\t\n\r\f")，标识空白符是分隔符
```

## Timer和TimerTask类
每个Timer对象都有一个线程对象，该线程用来调度它的任务执行，可以通过构造方法来设置该线程是守护线程还是user线程，如：
```
public Timer(boolean isDaemon):创建一个新的Timer,它的相关线程是否为守护线程由参数isDaemon决定。
```
而TimerTask类实现了Runnable接口，比如下面的代码实现了每秒打印一次内存：

```
Timer t = new Timer(false);
t.schedule(new TimerTask() {
    @Override
    public void run() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println(runtime.freeMemory());
    }
},0,1000);
```

注意一般会向Timer中传入true，这里为了测试而传入的false。
Timer中有很多调度方法，可以到文档中查看，同样TimerTask也有一些cancel等取消方法。

## Math和StricMath

其实学Java的对Math类再熟悉不过了，但是Math这个方法如果不做另外声明，其所有的参数和返回值都是double。

而StricMath和Math的区别是StricMath的每个方法都是用的指定的算法，所以在不同的虚拟机上返回值都一样。


# 系统编程

## System

System类提供了一系列静态方法来控制系统状态，并作为一个系统资源的仓库。其提供了四个通用领域的功能性：
* 标准I/O流
* 控制系统属性
* 访问当前运行时系统的实用工具和便利方法
* 安全性

标准I/O流其实就是熟知的System.in,System.out,System.err等，其定义如下：
```
public static final InputStream in;
public static final PrintStream out;
public static final PrintStream err;
```

System中也保存着系统属性，其实用Properties进行保存，处理系统属性的System类的方法是：
```
public static Properties getProperties():获得定义所有系统属性的Properties对象
public static String getProperty(String key):返回key中命名的系统属性值
public static String getProperty(String key,String defaultValue)
public static String setProperty(String key,String value)
public static void setProperties(Properties props)
```

那么比如说我想知道当前的操作系统，就可以通过getProperty("os.name")来获取。

System中也包含有大量的工具方法：
currentTimeMillis():1970至今的毫秒时间

System其他一些便利方法其实就是对Runtime.getRuntime()进行操作，如gc,exit,runFinalization,loadLibrary,load等方法


应用程序能通过Runtime.exec方法执行一个新的程序。每一次exec的成功调用都会创建一个新的Process对象，你可以使用该对象询问进程状态，调用方法控制它的进展。
exec的两个基本形式有：
publi Process exec(String[] cmdArray) throws IOException:在当前系统上运行cmdArray里的命令。返回表示它的Process对象，cmdArray[0]是命令名字，后面的所有值都作为命令的参数传递。
public Process exec(String command) throws IOException:等同于exec的数组形式，字符串command被默认的StringTokenizer在空白符出现的地方拆分。

新创建的进程被称为子进程，类似的创建进程的被称为父进程。
创建进程是一个特权操作，如果你没有许可权，则会抛出SecurityException异常。

返回的Process有一系列的方法，比如getInputStream，将返回的数据通过输入流传入，注意这个输入流是相对于子进程而言的。

其中还有一个方法waitFor()该方法无线等待进程结束，最后返回int值,0表示成功，非0表示失败。其实在intellij中运行某个类的main方法时候，最后都会显示这么一行代码：`Process finished with exit code 0`，现在应该明白是什么意思了吧！

比如下列代码运行ls命令：
```
Runtime runtime = Runtime.getRuntime();
try {
    Process p = runtime.exec("ls");
    String result = "";
    InputStreamReader isr = new InputStreamReader(p.getInputStream());
    BufferedReader br = new BufferedReader(isr);
    String tmp;
    while((tmp = br.readLine())!=null){
        result += tmp;
    }

    if(p.waitFor() != 0)throw new RuntimeException("子进程运行出错");
    System.out.println(result);
} catch (IOException e) {
    e.printStackTrace();
} catch (InterruptedException e) {
    e.printStackTrace();
}
```
还可以将命令换成reboot，那么执行后Ubuntu系统就会立刻重启，不信自己去尝试一下试试。


所以到这就应该知道，Java也是可以调用命令行的命令了吧！
如果这样，那么黑客不就肆无忌惮了= =
当然，Runtime.exec也可以制定初始工作目录，这个可以自行看源代码。


Runtime中还有一些特殊的方法，比如addShutdownHook(Thread hook)，该方法标识，程序在最后一个线程结束后，虚拟机会关闭，关闭前会调用hook(Thread)线程。当然其也有对应的remove方法。
一般可以通过添加关闭钩子来结束程序中未关闭的数据库等。



loadLibrary(String libname)装在本地代码库。库与本机文件系统中一个文件相关，系统可以知道它定位在何处，并可通过它寻找库文件。
load(String filename)将filename制定的文件作为动态库装载。相对于loadLibrary,load允许库文件被定位在文件系统的任何地方。


Runtime的另外两种方法用于调试：
public void traceInstructions(boolean on)依据on的值启用或禁止指令的跟踪调试。如果on是true，这个方法就为执行的每条指令产生调试信息。

public void traceMethodCalls(boolean on)为执行的每个方法产生调试信息。






Error异常通常标识非常严重的问题，通常是不可恢复的，并且不可能(很难)被不捕捉的。



# 其他知识
对于以下代码:
```
int a = 1;

for(int j=0; j<=100;j++){
    int a = 2;
} 
```
在外部定义了a后,在for内部又定义了a,在C/C++中是合法的,但是在Java中是不合法的.

`float a = 1e-43f`,f是必须的,不指定会将1e-43作为double处理.如果不指定,将有提示. 

finalize()方法与C++里的析构函数并不是同等概念.为什么要有这个方法,原因在于,Java可能会调用C/C++语言中malloc来分配内存,而释放内存还是通过free(),Java中并没有这种方法,所以需要finalize来调用C/C++的free()来释放内存.这是一种解释.

finalize()方法一般不用于内存清除工作,而是用于最后的检查,比如某一打开的文件是否关闭,某一业务是否完成等等.

垃圾回收机制的引用计数方法管理引用开销不大,但如果对象间互有引用,可能会出现"对象应该被回收,但引用计数器不为0"的情况.
引用计数器常用来说明垃圾收集器的工作方式,但似乎从未应用于任何一种JAVA虚拟机实现中.

垃圾回收机制一般采用的是可达性分析法:对于任何"活"的对象,一定能最终追溯到其存活在堆栈或静态存储区之中的引用.所以,如果从堆栈和静态存储区开始,遍历所有的引用,就能找到所有"活"的对象.

每个成员变量都有初始值,这些初始值在打印的时候会突出其作用,但是如果要使用这些变量的时,必须要初始化这些变量,注意初始化和初始值并不是同一概念.

每个.java文件中只能有一个public类,如果额外定义其他类,将不会被外部看到,这些类只是为public类提供支持.

import static静态导入是JDK1.5中的新特性。一般我们导入一个类都用 import com.....ClassName;而静态导入是这样：import static com.....ClassName.*;这里的多了个static，还有就是类名ClassName后面多了个 .* ，意思是导入这个类里的静态方法。当然，也可以只导入某个静态方法，只要把 .* 换成静态方法名就行了。然后在这个类中，就可以直接用方法名调用静态方法，而不必用ClassName.方法名 的方式来调用。

private方法默认是final形的.

final类中的所有方法,隐式都为final形.


