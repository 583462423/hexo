---
title: Java PECS
date: 2017-05-24 09:05:14
tags: Java
---

Java PECS(Producer extends Consumer super),即消费者用`extends`,生产者用`super`.

<!--more-->

对于`? extends T`来言,在赋值的时候,该类型的引用可以指向T的类型以及T类型的子类,比如:
```
List<? extends Number> list = new ArrayList<Number>();   //success
List<? extends Number> list = new ArrayList<Integer>();   //success

List<? extends Number> list = new ArrayList<Object>();    //error
```

就是因为其可以指向T类型及其子类,所以如果要往list添加数据,编译器并不知道其指向的到底是什么类型,比如`List<? extends Number> list = new ArrayList<Integer>();`,如果可以添加数据,`list.add(Double.valueOf(1.22))`必然是合法的,但是`Double`是不能强转为`Integer`的,所以就直接限定,不能添加数据.但是可以读取数据,可以保证读取的数据一定是Number类型的.

那么对于`? super T`来言,其可以指向T类型及其父类型,如:
```
List<? super Number> list = new ArrayList<Object>();  //success
List<? super Number> list = new ArrayList<Number>();  //success

List<? super Number> list = new ArrayList<Integer>();  //error
```

而`? super T`类型是允许添加数据的,但是其添加的数据,不能是T类型的父类型,`? super T`只是可以指向`T`的父类型,但是添加数据只能添加`T`类型及其子类型.
但是对于其获取数据而言,可以保证的是,获取到的数据,一定是T类型,但是不能保证具体的类型.

