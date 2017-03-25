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


