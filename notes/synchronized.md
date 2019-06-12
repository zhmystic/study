## 线程安全问题的主要诱因

* 存在共享数据（也称临界资源）
* 存在多条线程共同操作这些共享数据

## 解决问题的根本办法：
同一时刻，有且只有一个线程在操作共享数据，其它线程必须等到该线程处理完数据后再对共享数据进行操作。

## 互斥锁
互斥锁的特性：

* 互斥性：即在同一时间只允许一个线程持有某个对象锁，通过这种特性来实现多线程的协调机制，这样在同一时间只有一个线程对需要同步的代码块进行访问。互斥性也称为操作的原子性。
* 可见性：必须确保锁在释放之前，对共享变量所做对的修改，对于随后获得该锁的另一个线程是可见的（即在获得锁时应获得最新共享变量的值），否则另一个线程可能是在本地缓存的某个副本上继续操作，从而引起不一致。

对于java来讲，关键字Synchroized实现了上述特性。
Synchroized锁的不是代码，而是对象。

## Synchronized
根据获取锁的分类：获取对象锁和获取类锁

* 获取对象锁的两种用法：
    * 同步代码块（synchronized(this),synchronized(类实例对象)），锁是小括号中的实例对象
    * 同步非静态方法（synchronized method），锁是当前对象的实例对象

* 获取类锁的两种用法：
    * 同步代码块（synchronized(类.class)），锁是()中的类的对象（Class对象）
    * 同步静态方法（synchroized static method），锁是当前对象的类对象（class对象）


#### synchronized底层实现原理

实现synchronized的基础：

* Java对象头
* Monitor

对象在内存中的布局：

* 对象头
* 实例数据
* 对齐填充

一般而言，synchronized使用的锁对象是存储在Java对象头里面的，其主要结构是由Mark Word和Class Metadata Address组成。其中：

* Class Metadata Address是指类型指针指向对象的类元数据，JVM通过这个指针确定该对象是哪个类的实例。
* Mark Word用于存储对象自身的运行时数据，它是实现轻量级锁和偏向锁的关键。Mark Word存储着对象的hashcode，分代年龄，锁类型，锁标志等。

<u>**Monitor:**</u>
Moitor对象存在于每个对象的对象头中，synchronized锁便是通过这种方式去获取锁的，这也是为什么java中的任何对象都能作为锁的原因。
Monitor：每个Java对象天生自带了一把看不见的锁。里面有计数器count，它是在源码里面的ObjectMonitor类里面，是C++写的。它里面有两个队列，一个是waitset，一个是EntryList。它们就是用来保存ObjectMonitor对象的列表。每个对象锁的线程都会被封装成ObjectMonitor保存到里面。有个字段为owner，它是指向持有ObjectMonitor对象的线程。当多个线程同时访问同一段代码的时候，首先会进入到EntryList集合里面，当获取到对象的Moitor后，就会进入到Owner区域，并把Monitor中的Owner变量设置为当前线程，同时Monitor中的计数器count就会加1。当调用线程的wait方法时，就会释放当前持有的Monitor，owner变量也会被恢复成Null，count也会减1，同时该线程及ObjectMonitor实例就会被计入到waitset集合中，等待被唤醒。若当前线程执行完毕，它也将释放Monitor锁，并复位对应变量的值，以便其它线程进入获取Monitor锁。如图：
![f254d477ceb97b796923c33aab3c93b5](synchronized.resources/81364F9B-3777-4F75-9668-0F970AEC4D81.png)

<u>**synchronized的字节码文件分析：**</u>
1.作用于同步代码块的时候：
    可以看出是通过monitorenter和monitorexit执行的，monitorenter指令指向同步代码块开始的位置，当monitor的计数器count为0时，线程就可以成功获得monitor，owner会指向当前线程，并将计数器加1。monitorexit指执行完毕了代码块。
2.作用于同步方法的时候：
    字节码并没有monitorenter和monitorexit，但是其实方法级的同步是隐式的，无需通过字节码来控制。有一个标志叫ACC_SYNCHRONIZED，当方法调用时，调用指令会检查这个标志是否被设置，如果被设置了，会自动持有monitor。
    
<u>**锁消除：**</u>
更彻底的优化，JDK1.6之后就有了
JIT编译时，对运行上下文进行扫描，去除不可能存在竞争的锁。
例如：在方法里面用StringBuffer。因为在方法里面本身就是线程安全的，StringBuffer本身也是线程安全的，所以这个时候，即使调用了append方法，虽然append方法是synchronized修饰的，但是会自动锁消除。减少没必要的这种时间。

<u>**synchronized锁的四种状态：**</u>
* 无锁
* 偏向锁
* 轻量级锁
* 重量级锁

锁膨胀方向：无锁-->偏向锁-->轻量级锁-->重量级锁

偏向锁：减少同一线程获取锁的代价
大多数情况下，锁不存在多线程竞争，总是由同一线程多次获得。
核心思想：如果一个线程获得了锁，那么锁就进入了偏向模式，此时Mark Word的结构也变成了偏向锁结构，当该线程再次请求锁时，无需再做任何同步啊哦做，即获取锁的过程只需要检查Mark Word的锁标记为偏向锁以及当前线程ID等于Mark Word的线程ID即可，省去了大量锁申请的操作；但是不适用于锁竞争比较激烈的多线程场合。

轻量级锁：由偏向锁升级来的
偏向锁运行在一个线程进入同步块的情况下，当第二个线程加入锁争用的时候，偏向锁就会升级为轻量级锁。适用于线程交替执行同步块的时候。若存在多线程同一时间访问同一锁的情况，就会导致轻量级锁膨胀为重量级锁。




