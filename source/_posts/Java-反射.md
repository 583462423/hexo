---
title: Java 反射
date: 2017-02-22 21:01:11
tags:
---
很早之前就接触过反射，当时觉得反射还挺神奇的，最近因为学习一些新知识要用到反射，所以再来巩固一下。

<!--more-->


# Class
1. Class是一个类，
2. 对于每个类而言，JRE都为其保留一个不变的Class类型的对象，一个Class对象包含了特定某个类的有关信息，比如该类的数据成员名、方法和构造器、实现了什么接口等。
3. Class对象由系统创建
4. 一个类在JVM中只有一个Class实例


## 取得Class对象的方式
* 通过类.class 如:`Person.class`
* 通过对象获取，如：`person.getClass()`
* 通过全类名(常用)，如:`Class.forName("com.qxg.bean.Person")`

注意通过对象来获取这个方法，引用不论是否是多态，最终返回的都是实际类型。
```
Object obj = new Person();
System.out.println(obj.getClass().toString());  //打印的是Person类的全类名
```

## Class的方法
比较重要的方法是：`newInstance()`，一般会通过该方法来创建某类的实例，其调用的是无参的构造器，如果没有无参构造器会抛异常。
```
Class personClasss = Class.forName("con.qxg.bean.Person");
Object object = personClass.newInstance();
```

那么看到这，我们就应该注意，如果要编写类的时候，定义有参构造器，一定要定义一个无参构造器，主要用于反射。

### getMethods
获取类的方法
```
Class clazz = Class.forName("com.qxg.bean.Person");
Method[] methods = clazz.getMethods();
```
注意不能获取私有方法。

### getDeclaredMethods
和getMethods用法一样，能获取私有方法，且只获取当前类声明的方法。

### 获取指定方法

getDeclaredMethod(String name,Class Class<?>... parameterTypes)
第一个为方法名，第二个为重载参数
如:
```
Method method = clazz.getDeclaredMethod("setName",String.class); //获取setName(String);

method = clazz.getDeclaredMethod("setName",String.class,int.class);//获取setName(String,int);

method = clazz.getDeclaredMethod("setName",String.class,int.class，boolean.class);//获取setName(String,int,boolean);
```

### 获取到方法后如何还行
通过Method的invoke方法，第一个参数是某个对象，第二个是该方法的参数`public Object invoke(Object obj, Object... args)`
如：
```
method = clazz.getDeclaredMethod("setName",String.class,int.class);

method.invoke(person,"one",1);
//就相当于
person.setrName("one",1);
```

如果想要执行私有方法，还需要将其私有类型改为公有类型：

```
setAccessible(true);
```

但是一定要注意，使用这个方法来改变方法为公有的话，以后都是公有的。
就是因为反射的这种特性，java中的代码可能就会被别人攻击。
### 获取当前类的父类
clazz.getSuperClass(）

### getFiled()

* getField(String name) 获取单个字段
* getFields(); //获取所有字段，包括私有类型
* getDeclaredField() 获取该类的单个字段
* getDeclaredFields() 获取该类的所有字段，包括私有类型

获取到Filed对象后，可以通过该对象，设置其值：field.set(person,"name") 就类似于 person.name = “name” ，假设field代表String name这个字段。

而如果字段是私有的，就要setAccessible来使其设置为公有，然后在为其设置值。

### getConstructor()
和方法以及字段的套路基本一样，用到的不多

* Constructor<T> getConstructor(Class<?>... parameterTypes) 获取指定的构造器
* Constructor<?>[] getConstructors() 获取所有的构造器

通过构造器来实例化`T newInstance(Object ... initargs)`，看到这就明白前面的为什么用泛型了。

### getAnnotation
通过method的getAnnotation来获取指定的注解，这个注解也可以通过Class对象来获取，但是一般都是通过某个字段，某个方法来获取，比如下面的例子：

注解
```
@Retention(RetentionPolicy.RUNTIME)   //表示运行期也会保留注解，这样反射就可以获取其值
@Target(value = ElementType.METHOD)   //表示针对方法的注解
public @interface MyAnnotation {
    public int min();  //表示设置的值的最小值
    public int max();  //表示设置的值的最大值
}

```

然后将这个注解放在Person类的setAge中
```
    @MyAnnotation(min = 10,max = 30)
    public void setAge(int age) {
        this.age = age;
    }
```

接着进行测试：
```
Class clazz = Class.forName("con.qxg.bean.Person");
Method method = clazz.getDeclaredMethod("setAge",int.class);

//假设age是将要设置进去的值
int age = 10;

// 通过方法来获取注解
MyAnnotation annotation = method.getAnnotation(MyAnnotation.class);
if(annotation != null){
    //通过注解来取得最小值最大值
    int min = annotation.min();
    int max = annotation.max();
    if(min > age || age > max){
        //不符合就抛异常
        throw new Exception("age is ille");
    }
}
//如果符合条件，就调用方法来设置值
Person person = new Person();
method.invoke(person,age);

System.out.println(person);
```
如果age设置为9，就会抛出异常。

# ClassLoader

类加载器，是用来把类装载进JVM的。JVM规范定义了两种类型的类加载器：bootstrap和userdefined。运行时会产生3个：
* Bootstrap ClassLoader:引导类加载器：用C++编写，是JVM自带的类加载器，负责Java平台核心库，用来装载核心类库。因为C++编写，所以无法通过引用来取得该类加载器
* Extension ClassLoader:扩展类加载器：负责jdk_home/lib/ext目录下的jar包或-D java.ext.dirs指定目录下的jar包装入工作库。
* System ClassLoader:系统类加载器:负责java-classpath或java.class.path所指定的目录下的类与jar包装入工作。

获取System ClassLoader:`ClassLoader.getSystemClassLoader();`
获取Extension ClassLoader:`systemClassLoader.getParent()`
测试当前类由哪个类加载器加载:`Class.forName("com.qxg.bean.Test").getClassLoader()`

类加载器之间的父子关系由组合实现，而不是继承。顶层父加载器是Bootstrap ClassLoader,一次类推
## 类加载器的一个主要方法

调用getResourceAsStream()方法来获取src某目录下的输入流。如：
```
this.getClass().getClassLoader().getResourceAsStream("com/qxg/Class/TestClass.java");
```


# 泛型与反射

如果使用泛型的时候，可能就会出现很多问题，比如以下的一个问题：

```
public class Dao<T> {

    Map<String,T> map = new HashMap<String,T>();

    T get(String name){
        return map.get(name);
    }
    
}

```

比如以上情形，我想在get该泛型的时候，对该泛型做一些事情，也就是通过该T对象，来调用该对象中的一些方法，如果按通常的情形的话，可以这么写：

```
T t = map.get(name);
t.xxxx();
```

但是我们都不知道T对象中的任何方法，所以这个时候就要用到反射了，但是通过t如何获取Class对象呢？

首先看下面例子：

```
class UserDao extends Dao<User>{

}
```

默认UserDao有无参构造器，并调用了父类的无参构造器，那么在Dao的构造器中打印这么一条记录：

```
    public Dao(){
        System.out.println(this);
    }
```

那么创建UserDao的时候，看其打印什么：
```
        UserDao userDao = new UserDao();

```

打印结果是：

```
con.qxg.Class.UserDao@816f27d
```

也就是说，Dao中的this指向的并不是Dao，而是UserDao

那么使用this.getClass().getSuperclass()又打印什么呢？
打印的是：class con.qxg.Class.Dao


这时问题来了，如果我想要其打印Dao<User>，这个时候应该怎么办？

方法就是通过this.getClass().getGenericSuperclass()来获取继承有泛型的那个类，不过返回的是Type类型，其打印结果如下：
```
con.qxg.Class.Dao<con.qxg.Class.User>
```
那么如何获取传入的T泛型呢？需要做如下操作

```
Type type = this.getClass().getGenericSuperclass();
//判断是否是带参数的Type类型
if(type instanceof ParameterizedType){
    //如果是就强转
    ParameterizedType parameterizedType = (ParameterizedType) type;
    //获取参数
    Type[] args = parameterizedType.getActualTypeArguments();

    //如果参数不空且有效，那么第一个参数就是对应的泛型，如果泛型是2个，那么args[0]是第一个，args[1]是第二个。
    if(args!=null && args.length>0){
        强转后，就是对应的T的Class对象
        clazz = (Class<T>) args[0];
    }
}
```
这样就OK。
