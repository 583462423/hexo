---
title: KotLin学习
date: 2017-05-18 13:26:38
tags: Java
---

这几天一直在想,有没有一种脚本语言能和Java兼容,且学习跨度不大呢?一直在考虑,到底用python,php,还是go作为自己主要脚本语言.但是突然今天的Google IO大会宣布,Android将使用KotLin作为官方语言,遂赶紧去瞧瞧这门语言是什么样的.

<!--more-->

虽然说KotLin并不是脚本语言,但是其用法与脚本语言一样的简洁,并能兼容java,可以完全正常使用Java的类库,并可被编译为JVM字节码或JavaScript.在看到其简介语法,以及对Java的兼容性,就瞬间喜欢上这门语言,所以打算用上一点时间,来学学.


学习笔记参考官方学习文档:http://kotlinlang.org/docs/reference/basic-syntax.html


# 基础

## 基础类型
在Kotlin中,所有事物都是对象,所以我们可以将成员方法和属性赋值给任何变量.

## Numbers数字类型
KotLin中处理数字类型和Java非常相似,但并不完全相同.
KotLin提供了以下几种自带数字类型:

|类型| 位数 |
|-|-|
|Double | 64|
|Float| 32|
|Long| 64 |
|Int| 32|
|Short| 16 |
|Byte|8|
可以看到,和Java是几乎一样的.

创建数字变量,在没有指明类型的时候,会根据字面量来指定,如:
```
	val a = 100
	val b = 10.0
	val c = 10.0F
	val d = 100L
	println(a.javaClass)
	println(b.javaClass)
	println(c.javaClass)
	println(d.javaClass)
```
打印结果为:
```
int
double
float
long
```

如果要指明类型,需要在变量名后加`:type`,如:
```
    val a :Int= 100
    val b :Double= 10.0
    val c :Double= 10.0
    val d :Long= 100L
```
但注意,赋值时并没有强制类型转换:
```
	val a:Double = 10F //error
	val b:FLoat = 10.0 //error
```

而如果想要使用封箱类型,需要在类型后加上`?`,如:
```
	val a:Int? = 100  //a is a boxed int type
```

虽然不能强制类型转换,但是可以通过该类型中的某方法来转换为其他类型,如:
```
val a :Int = 1
val b : Byte = a.toByte()
```

而如果使用包装类型的时候,其转换方式为:
```
    val a :Int? = 1
    val b : Byte? = a?.toByte()
```
`?.`表示非空对象才能调用其后的方法.

## Characters

字符类型通过`Char`来表示，但是与Ｊava中不同的是，Char类型的变量,不允许和数字类型进行比较,比如:
```
	val a:Char = 'a'
	if(a == 1)//error
```
但是可以通过`toInt()`,`toLong()`等方法来将Char类型转换为数字类型

## Booleans
通过`Boolean`来表示,其中`&&`,`||`,`!`等逻辑操作,仅能用来操作Boolean类型.
## 操作符
KotLin中没有`<<`,`>>`,`&`,`|`等操作,取代的是:

|KotLin | Java|
|-|-|
|shl|<<|
|shr|>>|
|ushr|>>>|
|and|&|
|or|||
|xor | ^|
|inv|!|


## 数组类型
数组通过`Array`类型来指定,查看KotLin中`Array`类型的源码:
```
public class Array<T> {
    public inline constructor(size: Int, init: (Int) -> T)
    public operator fun get(index: Int): T
    public operator fun set(index: Int, value: T): Unit
    public val size: Int
    public operator fun iterator(): Iterator<T>
}
```
可发现其构造函数只有一个,并有一个Int属性Size,以及3个方法.

创建数组可通过`arrayOf()`和`arrayOfNulls`方法,以及`Array`构造方法.
如:
```
val arrs = arrayOf("1","2")         //arrs为String数组类型,大小为2
val arrs = arrayOfNulls<String>(5)  //arrs为String数组类型,且被Null填充,大小为5
val arrs = Array(5,{i->i})          //通过构造器创建的数组,其中5表大小,后边为一个生成函数
```
同样数组可以通过`[]`来获取值,也可以通过get()方法来获取值,但是通过get方法来获取的时候,编译器会推荐使用`[]`.也就是说,完全可以把`get`,`set`遗忘.

与Java不同的是,KotLin中的数组类型,在指定数组中项目的类型后,就不能再改变其类型,比如`Array<String>`不能转换为Array<Any>.KotLin同样提供了一系列的原始数据类型的数组,比如`ByteArray`,`IntArray`等

## String

String是不可变类型,通过`[]`来获取String中某位置上的字符,同样,String也可以通过迭代器进行遍历:
```
for (c in str) {
	println(c)
}
```

KotLin中有两种类型的String,查看样例就明白了:
```
val s1 = "Hellow,world\n"
var s2 = """
	for (c in "foo")
		print(c)
"""
```

可通过`trimMargin()`方法移除`|`符号外的多余空格,如:
```
	val text = """
    |Tell me and I forget.
    |Teach me and I remember.
    |Involve me and I learn.
    |(Benjamin Franklin)
    """.trimMargin()
    println(text)
```
将打印:
```
Tell me and I forget.
Teach me and I remember.
Involve me and I learn.
(Benjamin Franklin)

```
而如果给`trimMargin`传递参数,则会移除该参数类型外的空格,如:
```
    val text = """
    >Tell me and I forget.
    |Teach me and I remember.
    |Involve me and I learn.
    |(Benjamin Franklin)
    """.trimMargin(">")
    println(text)
```
则打印:
```
Tell me and I forget.
    |Teach me and I remember.
    |Involve me and I learn.
    |(Benjamin Franklin)
```

String中如果要引用其他String变量,可以通过以下方式:
```
val i = 10
val s = "i = $i"

s = "abc"
val str = "$s.length is ${s.length}"
```

而如果要表示一个`$`符号,则可以通过`var price = "${'$'} 9.99"`

# Packages

在KotLin中,每个.kt文件都会被自动导入以下包:
* kotlin.*
* kotlin.annotation.*
* kotlin.collections.*
* kotlin.comparisons.*
* kotlin.io.*
* kotlin.ranges.*
* kotlin.sequences.*
* kotlin.text.*

而针对不同的平台,会导入不同的包:
--JVM: 
* java.lang.*
* kotlin.jvm.*

--JS:
* kotlin.js.*

import和java中类似,但KotLin中的import还有一个特点,可以在import的时候重命名,这样做可以用于解决重名冲突比如:
```
import foo.Bar
import bar.Bar as bBar  //bBar表示bar.Bar
```

与java不同的是,KotLin没有`import static`这种语法.

# 流程控制

## if
在KotLin中没有三木运算符,原因是if已经可以工作的很好了,如果要用三木运算符,可以这样:
```
val max = if(a > b) a else b
```
还有一种比较奇葩的写法:
```
val max = if (a > b) {
	print("Choose a")
	a
} else {
	print("Choose b")
	b
}
```

## when
when对应java/c中的switch,如:
```
when (x) {
	1 -> print("x==1")
	2 -> print("x==2")
	else -> {
		print("x is other")
	}
}
```

但是不同的是,碰到一个分支就只运行该分支,不用break语句.
同样,when也可以作为一个表达式,如:
```
    val s = 1
    val t = when (s) {
        10 -> {
            1
        }
        else -> {
            println("haha")
            2
        }
    }
```
但是这种写法要在分支的最后写上其对应的值.

如果有多种分支对应同一种操作,那么可以简化写法:
```
when (x) {
	0, 1 -> print("x == 0 or x == 1")
	else -> print("otherwise")
}
```
条件中也可以使用任意表达式,如:
```
when (x) {
    parseInt(s) -> print("s encodes x")
    else -> print("s does not encode x")
}
```
条件也可以通过`in`或`!in`判断是否在某个范围或者不在某一范围:
```
when (x) {
    in 1..10 -> print("x is in the range")
    in validNumbers -> print("x is valid")
    !in 10..20 -> print("x is outside the range")
    else -> print("none of the above")
}
```
可以通过`is`或`!is`来判断变量是否是某种类型:
```
val hasPrefix = when(x) {
    is String -> x.startsWith("prefix")
    else -> false
}
```


## For循环
for-each和java中类似,不过可以省略类型,:
```
    val arrs = arrayOf("a","b","c")
    for (item in arrs) println(item)
```
如果要指明类型,需要这么写:
```
for (item: Int in ints) {
    // ...
}
```

如果对于数组而言,想获取下标,可以通过:
```
for (i in arrs.indices)
```

也有这种操作,同时获取下标和值:
```
for ((index, value) in array.withIndex()) {
    println("the element at $index is $value")
}
```

## while
while循环和传统的一样,所以就不贴代码

# 跳转

## Break和Continue
循环语句中可以有一个label,以`@`命名结尾,和Java中的一样,不过Java中命名并没有`@`
```
loop@ for (i in 1..100) {
    for (j in 1..100) {
        if (...) break@loop
    }
}

# Class

## Constructors
一个类有一个主要的构造方法,和多个次要的构造方法,主要的构造方法是类头的一部分,它编写在类名的后边:
```
class Person constructor(firstName: String) {

}
```
如果主构造器没有注解或修饰符,就可以将`constructor`省略:
```
class Person(firstName: String) {
}
```
在构造器中不能有任何代码,初始化的代码可以在init体内:
```
class Customer(name: String) {
	init {
		println("${name}")
	}
}
```

其中`${name}`和`$name`是一样的.

但是,构造器的方法,不但能用到init块中,也可以用于属性赋值,如:
```
class Person(name: String) {
    val myName = name
}
```

事实上,除了构造器获取参数后初始化属性方式外,在KotLin中,还有一种更简洁的声明方式:
```
class Person(val firstName: String, val lastName: String) {
	//...
}
```
即在构造方法中,参数前加上val即可.

如果构造器里有注解,或者修饰符,构造关键字就是必须的,如:
```
class Customer public @Inject constructor(name: String) { ... }
```

如果有第二个构造器,就可以通过constructor进行指明:
```
class Person {
    constructor(parent: Person) {
        //parent.children.add(this)
    }
}
```
而如果其余构造器使用该类的某种构造器,就可以使用`this`关键字:
```
class Person(val name: String) {
    constructor(name: String, parent: Person) : this(name) {
        parent.children.add(this)
    }
}
```

如果想要自己的类不被公开,即不能被实例化,就可以将构造器变为似有的:
```
class DontCreateMe private constructor () {
}
```

如果想要创建一个类实例,与Java类似,但是并不需要new关键字,并且KotLin中不存在new关键字:
```
val invoice = Invoice()
```

## 类成员
类成员包括
* 构造器和init块
* 函数
* 属性
* 内嵌类
* 对象声明

## 继承
在KotLin中,所有类的父类不叫`Object`而是`Any`,但是`Any`并不等同于`java.lang.Object`.
创建一个拥有父类的类,可以通过`:`指明父类,如:
```
open class Base(p: Int)

class Derived(p: Int): Base(p)
```
如果两个类都没有主要构造器,但次要构造器需要指明调用父类或该类的某构造方法:
```
class MyView : View {
    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
}
```
open关键字和java中final正好相反,open表示允许其他类继承该类.默认的,KotLin中所有的类都是final型的.

## 重写方法
open同样用于方法,如果没有指明是open类型的,那么就是final型的,不可被重写,重写方法,需要使用override关键字,如:
```
open class Base {
    open fun v() {}
    fun nv() {}
}
class Derived() : Base() {
    override fun v() {}
}
```

如果子类中,override仅仅重写了方法,并没有加final关键字时,那么该方法依然是可被重写的,如果想要将其变为final,就需要明确指定其为final型的.:
```
open class AnotherDerived() : Base() {
    final override fun v() {}
}
```

## 重写属性
重写属性和重写方法有相似之处,父类要明确指定为open类型的,如果重写的时候,可以通过get()来将其重写为父类的属性值:
```
open class Person(name: String) {
    open val myName = name
}

class Student(name: String) : Person(name) {
    override val myName: String
        get() = super.myName
}
```
当然初始化的方式有很多,比如:
```
override val myName: String = "bb"

override val myName: String get() = "cc"

override val myName: String get() = {return "dd"}
```
## 抽象类型
抽象类不需要指定其成员为open,因为open是默认的.如:
```
abstract class AbastractPerson {
    abstract fun f()
}
```
抽象类也可从open 类中继承,并重写某一方法为抽象方法:
```
open class OpenClass {
    open fun f(){
        println("a")
    }
}

abstract class AbastractPerson : OpenClass() {
    override abstract fun f()
}
```

# 属性和域
## 声明属性
通过`val`或`var`来进行声明属性,但是`var`声明的属性是可变的,`val`声明的属性是不可变的.

## Getters和Setters
声明一个属性完整的语法是:
```
var <propertyName>[: <PropertyType>] [= <property_initializer>]
    [<getter>]
    [<setter>]
```


