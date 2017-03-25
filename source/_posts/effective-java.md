---
title: effective java
date: 2017-02-20 09:42:58
tags:
---
effective java 学习笔记。

<!--more-->
# 创建和销毁对象

## 静态工厂方法代替构造器

优点：
* 静态工厂方法与构造器不同的第一大优势在于，它们有名称。
* 不必在每次调用的时候创建一个新对象。针对不可变类。
* 可以返回原返回类型的任何子类型的对象。
* 创建参数化类型实例时，使代码变得简洁

缺点：
* 类如果不含公有的或受保护的构造器，就不能被子类化。意思是静态工厂方法不包含public类型的构造器，所以其他类无法继承于该类，也就不能被子类化。
* 与其他静态方法实际上没有任何区别。

切记第一反应就是提供公有的构造器，而不考虑静态工厂

## 遇到多个参数时要考虑用构建器(Builder)

当一个类有多个属性值，这些属性又是可选的，通常有以下解决思路。

### 使用重叠构造器：
```
public C(int a){
    this(a,0);
}

public C(int a,int b){
    this(a,b,0);
}


public C(int a,int b,int c){
    this(a,b,c,0);
}


public C(int a,int b,int c,int d){
    this(a,b,c,d,0);
}

public C(int a,int b,int c,int d,int e){
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
}
```

注：为了方便起见，上述变量使用abcde代替，编程中杜绝此现象。

缺点：
* 某些参数在构造的时候不得不为他们传递

### 使用JavaBeans设计模式
对于JavaBean我们都不陌生了，其要求是公有的类，公有无参构造器，setter/getter方法，私有属性。

也就是说我们创建对象时，使用new来实例化一个对象，然后使用set方法设置可选的参数。

这种模式弥补了重叠构造器方法的不足。

缺点是：无法使该类变为不可变类。

### 使用Builder模式

如：
```
public class Cat{

    private int a;
    private String b;

    public static class Builder(){
        private int a;
        private String b;        
        public Builder(){}
        
        public Builder a(int a){this.a = a; return this;}
        public Builder b(String b){this.b = b; return this;}

        public Cat build(){
            return new Cat(this);        
        }
    }

    private Cat(Builder builder){
        this.a = builder.a;
        this.b = builder.b;
    }
}

要点是：主类 私有属性，私有构造器，构造器参数传Builder，Builder类设置属性后返回本身(this)。
```

注意，外部类可以访问内部类的任何成员，包括private成员，所以构造器中访问了builder的私有属性，不必惊讶，这是正常的。

这种方式的优点是：链式操作使代码变得灵活，解决了重叠构造器的不必要参数的缺点。但是这种模式的缺点是，创建了额外的构建起，如果参数值过多（4个或以上）才会考虑使用这种方法。

## 用私有构造器或者枚举类型强化Singleton属性

实现singleton有两种方式，懒汉式和饿汉式
饿汉式（在类加载的时候就创建一个单例）：
```
public class Tmp{
    private static final Tmp INSTANCE = new Tmp();
    private Tmp(){ ... }
    public static Tmp getInstance(){
        return INSTANCE;    
    }
}
```
但是注意一点，虽然该类构造方法是私有的，但是通过AccessibleObject.setAccessible()，通过反射机制可调用该私有构造器。如果需要防止该种方式的攻击，让它在要求创建第二个实例的时候抛出异常。

但是注意，这种方式在实现序列化接口的时候就会出问题，因为序列化工具会使用特殊的路径去创建一个新的实例。

解决序列化的问题就是添加readResolve()方法，readresolve()并不是静态的，但是在序列化创建实例的时候被引用,注意该方法并不是重写的：

```
private Object readResolve(){
    return INSTANCE;
}
```


懒汉式（使用的时候才会实例化）：
```
public class MySingleton { 
  
    //设立静态变量 
    private static MySingleton INSTANCE = null; 
  
    private MySingleton(){ 
        //私有化构造函数  
    } 
      
    //开放一个公有方法，判断是否已经存在实例，有返回，没有新建一个在返回 
    public static MySingleton getInstance(){ 
        if(INSTANCE == null){  
            synchronized(MySingleton.class){
                if(INSTANCE == null){
                    INSTANCE = new MySingleton();;                
                }            
            }  
        }
 
        return INSTANCE;  
    } 
} 
```

在Java1.5发型版本后，实现Singleton还有一种方法，就是使用枚举类型，枚举已经自动帮忙处理readResolve()问题了：

```
public enum Tmp{
    INSTANCE;
    public void someMethod(){ ... };
}
```
可查看[这个博文](http://www.cnblogs.com/hyl8218/p/5088287.html)来对枚举进行了解。
单元素的枚举类型已经成为实现Singleton的最佳方式。
其优势在于：
* 自动序列化
* 保证只有一个实例(即使使用反射机制也无法多次实例化一个枚举量)
* 线程安全


## 通过私有构造器强化不可实例化的能力

一些类的设计初衷是该类中包含的属性和方法都是静态的，且这种类不能够被实例化。

但是每个类在设计好后如果没有构造方法会拥有一个默认的无参构造器。

一些人企图使用abstract来使得这种类无法被实例化，这种方式是行不通的。

以上无论哪种方式都可能会被继承并实例化。

所以正确做法是，编写私有的无参构造器：

```
public class Test{
    private Test(){
        //设计目的就是不能被实例化，如果有某些途径实例化该类，就抛异常。
        throw new AssertionError();
    }
}
```

## 避免创建不必要的对象

String的两种创建方式：
```
String s = new String("some string");
String s2 = "some string";
```
但是第一种方式是不被允许的。原因在于，如果有多处地方调用该方法，每处都会创建一个新的实例。
而第二种方式，只要在同一台虚拟机中运行的代码，该实例只会被创建一次。

所以，避免创建重用对象，可把重用对象声明了final类型，并放在static{}代码块中实例化。

优先使用基本类型，而不是装箱基本类型，当心无意识的自动装箱。比如，装箱类型在每次加操作都会创建一个新的装箱类型。

建立对象的代价如果非常昂贵，重用这些对象就有很大的意义。

## 消除过期的对象引用
java 实现的stack栈中的pop()方法
```
public Object pop(){
    if(size == 0)
        thorw new EmptyStackException();
       
    return Elements[size--];
}
```
这段代码可能导致内存泄露，因为对于数组而言，不论其数组内容是有效还是无效的，在java中都是同等的，只要存在就可能会导致内存泄露的问题，以上代码中，被pop掉的对象，虽然显示的是pop掉了，但是Elements数组中依然存在这些对象的引用，这样就导致了内存泄露问题。


对于可能存在内存泄露的程序代码中，一般通过设置null来清空对象间的引用。

修改后的pop
```
    public Object pop(){
        if(size == 0)
            throw new EmptyStackException();
        Object result = Elements[size--];
        Elements[size+1] = null;
        return result;
    }
```

但是清空对象引用应该是一种例外，而不是一种规范行为。

只要类是自己管理内存，程序员就应该警惕内存泄露的问题。

内存泄露另一个常见的来源是缓存。

内存泄露的第三个常见来源是监听器和其他回调。

题外话：

说到内存泄漏，一定要想到有向图，这样才能直观判断是否会导致内存泄露。对于长生命周期引用短生命周期对象，可能会导致内存泄露这句话而言，几行代码更直观理解：
```
public Class A{
    public Object o;
    public void method(){
        Object o = new Object();
        ...
    }
}
```
Object o的实际生命周期应该是在method()调用前后，而A的生命周期肯定比o长，这个时候正确的代码是将Object o放入method总作为局部变量，或在method最后加上o = null，释放其引用。


## 避免使用终结方法
finalizer终结方法通常是不可预测的，也是危险的，一般情况下是不必要的。

Java语言规范不仅不保证终结方法会被及时地执行，而且根本就不保证它们会被执行，所以避免使用终结方法来释放资源。

使用终结方法(finalize)有一个非常严重的性能损失。导致创建和销毁对象的时间增长。

那么如何正确的释放资源呢？提供一个显示的终止方法，要求在其他类中调用的时候不适用该对象中的方法时，调用该显示终止方法来释放资源。

终结(finalize)方法应该用于 ：如果发现资源未被终止，则应该在日志中记录一条警告，后重新执行终止方法。

# 对于所有对象都通用的方法

## 覆盖equals时请遵守通用约定

高质量equals方法诀窍：

* 使用 == 操作符用来检查"参数是否为这个对象的引用"
* 使用instanceof检查"参数是否为正确类型"
* 要把参数转换为正确的类型
* 对于类中关键域，检查参数中的域是否与该对象中对应的域相匹配。
* 当编写完成了equals方法之后，应该问自己三个问题：它们是否对称、传递的、一致的。

根据以上，可以编写这么一套equals方法：

```
public boolean equals(Object o){
    if(o == this){
        return true;    
    }
    if(!(o instanceof MyClass)){
        return false;
    }
    MyClass m = (MyClass)o;
    return this.someVaule == o.someVaule
        && this.otherValue == o.otherValue;
}
```
## 覆盖equals时总要覆盖hashCode

* 两个对象equals方法比较相等时，hashCode方法都必须产生同样的结果。如果没有hashCode，违反的就是该条规定。
* 一个好的散列函数倾向于"为不相等的对象产生不相等的散列码"

配置hashCode()的简单解决方法:

1. 把某个非零的常数值,比如说17,保存在一个名为result的int类型变量中.
2. 对于对象中每个关键域f(指equals方法中涉及的每个域),完成以下步骤,计算出int的hashCode:
 * boolean : (f?1:0)
 * byte,char,short,int:(int)f
 * long : (int)(f^(f>>32))
 * float: Float.floatToIntBits(f)
 * double: Double.doubleToLongBits(f),的到long后根据第三条继续计算
 * 对象引用: 如果equals中也调用了该对象的equals中,则结果也应该为该对象的.hashCode()的返回值
 * 数组: Array.hashCode()
3. 将上述结果计算得到到的散列码合并到result中:result = 31*result + c;
4. 编写单元测试.

如果一个类是不可变的,那么计算散列码就可以采用延时加载的方式:
```
private volatile int hashCode;
@Override
public int hashCode(){

    int result = hashCode;
    if(result == 0){
        result = 17;
        result = 31*result + c1;
        result = 31*result + c2;
        result = 31*result + c3;
    }
    return result;
}
```

## 始终要覆盖toString()

略

## 谨慎地覆盖clone
Cloneable是标示性接口,没有任何方法.

clone有深拷贝和浅拷贝,浅拷贝就是对于基本类型,全部拷贝一遍其值,而对象引用依然指的原对象,即浅拷贝结果,拷贝对象和原对象还有联系.

而深拷贝,对象的拷贝也是拷贝.

该书中最后总结的结果是:不建议使用clone(),因为会出现很多问题.

## 考虑实现Comparable接口

实现该接口注意到需要符合以下规定:
* x.comparable(y)返回异常时,ycomparable(x)也会抛异常
* x.c(y) >0 && y.c(z) >0,则x.c(z) 一定 > 0
* x.c(y)==0 则 x.c(z) == y.c(z)
* 强烈建议 x.c(y) ==0 则x.equals(y)返回true

# 类和接口

## 最小化类和成员访问权限

* 尽可能不被外界访问

## 在公有类中使用访问方法而非公有域

就是setter,getter方法.访问一个成员变量,如果该成员可被访问,应使用getter方法,并将给变量变为私有.

## 使可变性最小化

要使类成为不可变类,要遵循以下规则:
1. 不要提供任何会修改对象的方法
2. 保证类不会被扩展.为防止该类子类化,一般做法使这个类成为final类.
3. 使所有域都是final的
4. 使所有域都是私有的
5. 确保对于任何组件的互斥访问

不可变对象本质是线程安全的,它们不要求同步.

不可变类可以被自由地共享,一个简单的做法,为他们提供静态final常量,例如`public static final Complex ZEOR = new Complex(0,0)`

不可变类不需要拷贝.不需要实现Cloneable接口

不可变类唯一的缺点是:对于每个不同的值都需要一个单独的对象.

如果类不能被做成不可变的,仍然尽可能地限制它的可变性.


## 复合优先于继承

对于继承来的子类,如果父类在以后的工作中进行整改,那么子类就会因为种种问题导致编译出错.

这个时候就出现了复合,或者称做包装,即装饰模式

对于复合来讲,其组成部分有两个,一个是对原本想要继承对象的引用,一个是转发,转发就是该包装类中的方法都要靠引用被包装对象中的方法来取得结果.

包装对象对被包装对象进行了修饰,增加了一些新的特性.

这种方式不能被称做为委托.

有些人担心包装对象造成了额外的内存,会带来性能影响,在实践中,不会带来很大的影响.	编写包装类是有点琐碎,但是实际上只需要编写构造方法,就可以调用其中的新特性.

所以,如果类B和类A是严格的is-a关系,就应该使用继承.每次继承的时候都要问自己:每个B都是A么,如果是否定的,这个时候就要用复合.复合只需要暴露A中一些较小的,较简单的API.

## 要么为继承而设计，并提供文档说明，要么就禁止继承

好的API文档应该描述一个给定的方法做了什么工作，而不是描述它如何做到的。

## 接口优于抽象类


