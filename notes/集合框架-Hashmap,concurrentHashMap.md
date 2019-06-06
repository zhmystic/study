## 工作中消失而面试却长存的算法与数据结构考点

优秀的算法和数据结构被封装到了Java的集合框架之中

数组结构考点：
* 数组和链表的区别
* 链表的操作，如反转，链表环路监测，双向链表，循环链表等相关操作
* 队列，栈的应用
* 二叉树的遍历方式及其递归和非递归的实现
* 红黑树的旋转

算法考点：

* 内部排序：如递归排序，交换排序（冒泡，快排），选择排序，插入排序
* 外部排序：应掌握如何利用有限的内存配合海量的外部存储来处理超大的数据集，写不出来也要有相关的思路

考点扩展：

* 哪些排序是不稳定的，稳定意味着什么
* 不同数据集，各种排序最好或最差的情况
* 如何优化算法

# Collection

Collection接口是所有集合的根接口，它下面有三个子接口，也是下面典型的三大类集合，分别为：List，Set，Queue
* List：用的相对较多的有序集合
* Set：不允许包含重复元素，适用于需要保证元素唯一的场景，例如把IP放入一个set中，统一同一个IP访问网站的次数。
* Queue：Java提供的一个标准队列结构的实现，除了集合的基本功能，它还支持FIFO先进先出，LIFO后进先出等队列行为。

Queue先不说，先说List和Set
#### List
特点：

* 有序（存储和取出的顺序）
* 可重复，可存重复元素
* 可通过索引值操作元素

分类：

* 底层是数组，查询快，增删慢（因为查询直接通过索引）
    * ~~Vector~~：线程安全，效率低，目前已经基本废用
        1.里面用的synchronized同步锁
    * ArrayList：线程不安全，效率高
        1.因为方法里面既没有用synchronized同步锁，也没有用CAS相关的技术，所以线程是不安全的。
        2.源码里面，它的底层有一个elementData，创建ArrayList的时候，就是new了一个新的数组出来。
        `transient Object[] elementData;`
        
```
    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }
```
        3.它通过grow方法进行动态扩容
        它所谓的动态，就是创建了一个新的数组，赋予新的长度，并覆盖掉原有的数组，即可实现扩容。是原有基础长度的1.5倍+1。
  **JDK1.5的时候，如果不传参数告知容量，那么默认为10；JDK7以及之后，默认为0，只有在第一次添加add或者addAll的时候才进行初始化。**
              
```
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
```
        

* 底层是链表，查询慢，增删快（因为链表的查询需要从头部开始逐个遍历）
    * LinkedList：线程不安全，效率高。


#### Set
特点：

* 无序（存储和取出的顺序）
* 元素是唯一的

分类：

* 底层是哈希表
    * HashSet：保证元素唯一性（hashset的底层就是hashmap,add方法的时候，将元素保存到map的key里面）
        * hashcode()
        * equals()
* 底层是二叉树
    * TreeSet：保证元素排序（如果不关心排序，不需要用treeset，底层是NavigableMap，跟hashset类似）
        * 自然排序，让对象所属的类去实现comparable接口，继承equals,hashcode.compareTo方法。
        * 比较器接口comparator，带参构造。实现Comparator<>接口，重写里面compare方法。
        * 在两个排序都存在的时候，是以后者自定义排序优先的。假如元素相同，那么都会被默认干掉，因为set不支持存储重复。



# Map
Map的key是不允许重复的，Value是可以重复的，为什么呢？原因是，通过源码可以看出，它的Key是通过set来进行组织的，而Value是通过Collection进行组织的。

Map目前活跃的也就三个：

* HashTable
* HashMap
    * LinkedHashmap
* TreeMap

不同之处在于，HashTable继承了Dictionary类，实现了Map接口，源码：
`public class Hashtable<K,V>
    extends Dictionary<K,V>
    implements Map<K,V>, Cloneable, Serializable`
而HashMap和TreeMap都是继承AbstractMap类，然后实现Map接口，源码：
`public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable`

## 经典面试题：HashMap，HashTable，ConcurrentHashMap的区别

### HashMap:
    在Java8以前，底层是数组+链表组成的复合结构。它也是在被首次使用的时候才初始化。但是如果某个数组上面链表的长度太长的话，会影响效率。所以在Java8及8之后，优化为了数组+链表+红黑树。通过一个常量TREEIFY_THRESHOLD来控制长度转换为红黑树的阈值，默认为8.
<u>**源码部分：**</u>
    HashMap的内部结构：
    `transient Node<K,V>[] table;`
    点进去Node之后，可以发现：
    
```
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
```
    发现Node是由哈希值，键值对，以及指向的下一个节点来组成。咱们的数组，会分成一个个bucket桶，通过哈希值寻址，哈希值相同则以链表的形式存储，链表大小超过TREEIFY_THRESHOLD，默认为8时，就会被改成红黑树。低于6的时候，就会被转化为链表。
    源码里面也可以看出，在首次put方法的时候，判断，如果为空的话，会调用resize方法进行初始化长度。
    `if ((tab = table) == null || (n = tab.length) == 0)
     n = (tab = resize()).length;`
    resize方法同样也具备扩容的功能，当长度大于阈值（默认为16的时候，同样会调用resize方法进行扩容。）  
    
```
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```
     
  不为空的话，会通过一个（p = tab[i = (n - 1) & hash]）这么一个hash方法计算出在table中的具体位置，如果通过hash运算得到结果发现该位置并没有元素的话，就会直接new一个该键值对的Node，放在数组的位置中；
  如果发现同样位置已经存在了键值对，且key和传入进来的相等，则直接替换数组里面的元素；如果key不相同，则判断是否为树化了的节点，如果已经树化，那就按树化的方式存储键值对（树化指的就是红黑树方式）；如果没有树化，那么就按照链表的插入方式，在链表后面添加一个元素。同时判断链表的长度，如果超过了树化阈值，那么进行树化转换为红黑树。
  
    简单逻辑：Put方法的逻辑

1.     若Hashmap未被初始化，则进行初始化操作
2.     对key求hash值，依据哈希值计算下标
3.     若未发生哈希碰撞，则直接放入桶中；若发生碰撞，则以链表的形式链接到后面
4.     若链表长度超过树化阈值（默认为8），且HashMap元素超过最低树化容量（默认为64），则将链表转成红黑树。
5.     若节点已经存在，则用新值代替旧值
6.     若桶满了，（默认容量为16 * 扩容因子0.75），就需要进行resize操作（扩容2倍后重排）


* * *

<u>**如何有效减少碰撞：**</u>

1. 扰动函数：促使元素位置分布均匀，减少碰撞几率
2. 使用final对象，并采用合适的equals和hashcode方法



* * *


我们创建hashmap的时候，并不是在构造里面穿多少就是多少容量，而是要经过一个tableSizeFor方法运算，转换成与其最近的2的倍数的值，源码：
```
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }
```


* * *

<u>**扩容的问题**</u>

1. 多线程环境下，调整大小会存在条件竞争，容易造成死锁。
2. rehash是一个比较耗时的操作。


* * *
<u>**如何将HashMap转变成线程安全的？**</u>
    通过Collections.sunchronizedMap方法即可。





### ConcurrentHashMap
  早起的ConsurrentHashMap：通过分段锁Segment来实现的。它使用的是分段锁技术，默认分配了16个Segment，理论上比hashmap提高了16倍。可以发现，相比于早期的hashmap，它只是将hashmap的table数组逻辑上拆分成多个子数组，每个子数组配置一把锁，线程在获取到没把分段锁的时候，比如，获取到编号为7的锁的时候，才能操作里面的子数组。别的线程如果访问别的segment的话，不耽误，但是如果也有线程访问7的话，会阻塞。如图：
  ![9c8e238cc68f434acdeec653fcb065f9.png](evernotecid://0D78A2A4-468C-4036-BC31-3DF94C21D7EF/appyinxiangcom/19899584/ENResource/p438)
  
  后期的ConsurrentHashMap（Java8之后），取消了分段锁，采用了CAS+sunchronized，来使锁更加细化。还做了进一步的优化，数据结构跟hashmap一样，数组+链表+红黑树。
  ConsurrentHashMap是出自J.U.C包的。源码中，有很多地方跟hashmap类似。有一些成员变量是它特有的。
  ![686b4c730e763d3d91fb88b7af418859.png](evernotecid://0D78A2A4-468C-4036-BC31-3DF94C21D7EF/appyinxiangcom/19899584/ENResource/p440)
  

* * *

<u>**查看put方法源码：**</u>
```
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }
```
![75f07d228956221de363d08252fb4272.png](evernotecid://0D78A2A4-468C-4036-BC31-3DF94C21D7EF/appyinxiangcom/19899584/ENResource/p441)
![19084e53bd267f855e335ed889747135.png](evernotecid://0D78A2A4-468C-4036-BC31-3DF94C21D7EF/appyinxiangcom/19899584/ENResource/p442)

    从源码中可以看出，concurrentHashmap是不允许Null键和Null值。接下来，就是计算key的哈希值，之后，一个for循环，先判断数组是否为空，如果为空，初始化；不为空的话，通过哈希值找到f。f表示的是链表或者红黑树的头节点，即我们数组中的元素。根据哈希值定位到的元素，  我们先检查它是否存在，如果没有，尝试通过CAS方法进行添加。如果添加失败，则break掉，进入下次循环；如果我们发现元素已经存在了，由于我们是随时处于多线程，有可能别的线程正在移动元素，那么我们就协助其扩容。
    如果发生了哈希碰撞，首先判断f是否是链表的头节点，如果是的话，咱们就会初始化链表的计数器，遍历链表，每次遍历一次链表，都会使binCount+1。如果节点存在，更新对应value；如果不存在，就在节点尾部添加节点；如果是红黑二叉树节点，那么就调用红黑二叉树的逻辑，添加节点。
    如果链表长度已经达到了临界值8，就需要把链表转化成树结构。
    

* * *


    
<u>**简单逻辑：Put方法的逻辑：**</u>

1. 判断Node[]数组是否初始化，没有则进行初始化操作。
2. 通过hash顶你为数组的索引坐标，是否有node节点，如果没有则使用CAS进行添加（链表的头节点），添加失败则进入下次循环。
3. 检查到内部正在扩容，就帮助它一块扩容。
4. 如果f!=null，则使用synchronized锁住f元素（链表/红黑二叉树的头元素）。
    4.1. 如果是Node链表结构），则执行链表的添加操作。
    4.2. 如果是TrssNode（树型结构），则执行树添加操作。
5. 判断链表长度是否已经达到临界值8，这个8是默认值，可以自行调整，当节点数超过这个值就需要把链表转换为树结构。


* * *

<u>**ConcurrentHashMap总结：**</u>

1. 比起Segment，锁拆的更细。
2. 首先使用无锁操作CAS插入头节点，失败则循环重试。
3. 若头节点已存在，则尝试获取头节点的同步锁，再进行操作。