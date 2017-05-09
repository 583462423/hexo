---
title: LinkedList实现栈或队列
date: 2017-04-19 00:46:17
tags:
---

今天在做层序遍历的时候,看代码逻辑完全正确,但就是结果不对,最后找到原因是,使用LinkedList实现的队列使用错误.

<!--more-->

在Java中是有Stack和Queue,Stack是继承的Vector,完全可用,而Queue是接口,Deque同样是接口即双端队列,而LInkedList实现了Deque,所以LinkedLIst是可以通过Deque实现队列,同样,LinkedLIst作为List,通过链表实现,也可以实现栈.

将链头当做栈顶,那么栈的操作即为:
```
LinkedList<Integer> stack = new LinkedList<>();
//添加操作,注意不要用add(),push内部实现就是addFirst(),
stack.push(1);
//弹出操作
stack.pop();
//栈顶元素
stack.peek();
```

将链头当做队列头,链尾当做队列尾,那么实现的队列操作即为:
```
LinkedList<Integer> queue = new LinkedList<>();
//添加操作,add/offer是在队尾加元素,`offer`是queue独有的操作,而`add`是列表的操作,同样`poll`是Queue独有的操作.push是在队头加元素
queue.add(1);
queue.offer(2);
//弹出操作
queue.pop();
queue.poll();
//队头元素
queue.peek();
//队尾元素
queue.peekLast();
```

也就是说无论栈还是队列,弹出元素都是使用`pop()`方法,而栈顶或队列顶都是`peek()`,那么入栈是`push`,入队列是`add`.



其实LinkedList中还有很多很方便的操作,想要了解的可以查看源码.
