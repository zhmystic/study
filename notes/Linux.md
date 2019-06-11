## 如何查找特定的文件?

find path [options] params
作用：在指定目录下查找文件

在当前目录及子目录下查找：
find -name "aaa.txt";

如果在别的目录下的话，全局搜索：
find / -name "aaa.txt"；
因为/是linux的根目录

支持模糊查询，查询所有以aaa打头的文件：
find ~ -name "aaa*";
如果想把大写的，例如Aaa也找到,不区分文件名大小写的话：
find ~ -iname "aaa*";

man find；  查看更多关于find的指令


## 检索文件内容

grep [options] pattern file
作用：查找文件里符合条件的字符串

查找文件里面有haha字符串的，以aaa开头的文件：
grep "haha" aaa* ;


管道操作符 | 
作用：可将指令连接起来，前一个指令的输出作为后一个指令的输入

* 管道命令只能处理前一个命令正确的输出，不能处理后面的输出。
* 管道右边命令必须能够接收标准输入命令，否则传递过程中数据会被抛弃
* 常用来接收数据管道的命令有：
    * sed,awk,grep,cut,head,top,less,more,wc,join,sort,split等等


* * *

在aaa.log日志里面呢查询partial为true的行:
<u>grep 'partial\[true\]' aaa.log;</u>
以前面指令作为管道，传给后面，做进一步筛选:
grep -o 进一步筛选出想要展示的内容
<u>grep 'partial\[true\]' aaa.log | grep -o 'eneige\[[0-9a-z] * \]';</u>


* * *

grep -v 过滤掉指定要过滤的
过滤掉本身进程：
ps -ef | grep tomcat | grep -v 'grep' 


* * *

在内容里面查找包含某个字段的文件，并将相关行展示出来。
例如：<u>grep 'partial\[true\]' aaa.log；</u>
筛选出符合正则表达式的内容
例如：<u>grep -o 'eneige\[[0-9a-z] * \]'；</u>
过滤掉包含相关字符串的内容
例如：<u>grep -v 'grep'；</u>


## 对文件内容做统计
awk [options] 'cmd' file
特别适合处理表格化，格式化的数据
* 一次读取一行文本，按输入分隔符进行切片，切成多个组成部分
* 将切片直接保存在内建的变量中，$1,$2...($0表示行的全部)
* 支持对单个切片的判断，支持循环判断，默认分隔符为空格

举例：假如我有一个文件，aaa.txt，里面格式也是很规范的。
我现在想要查看它的第一列和第四列的数据
筛选出文件内容里某些列的数据
<u>awk '{print $1,$4}' aaa.txt;</u>

我要查看第一行为name，并且第二行为0的数据
依据一定的条件，去筛选文件内容里面某些列的数据
<u>awk '$1=="name" && $2==1{print $0}' aaa.txt；</u>

将一个用,连接的文本的第二列筛出来
指定默认分隔符
<u>awk -F "," '{print $2}' aaa.txt;</u>

对内容逐行去做统计操作，并列出对应的统计结果
<u>awk '{enginearr[$1]++}END{for(i in enginearr)print i "\t" enginearr[i]}'</u>


## 批量替换文档内容
sed [option] 'sed command' filename 
它是一个流编辑器，适合用于对文本的行内容进行处理；
默认情况只是把修改完的数据打印到终端，如果想永久保存，需要加-i

对aaa.java文件中，将以Str打头的字符串替换成String：
^标识 以...打头

<u>sed -i 's/^Str/String/' aaa.java;</u>

以.结尾的，替换为;
$标识 以...结尾：

<u>sed -i 's/\.$/\;/' aaa.java;</u>

将所有Jack替换成me,第三个/后面的g代表一行里面所有Jack，不加的话，仅替换该行第一个：

<u>sed -i 's/Jack/me/g'；</u>

