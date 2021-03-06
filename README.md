## R 树

R 树是用来做**空间数据存储的树状数据结构：**

- 给地理位置、矩形和多边形这类多维数据创建索引
- 还可以用来加速使用包括大圆距离在内的q各种距离度量方式的最邻近搜索

B 树是解决低纬度数据（通常一维，也就是一个数据维度上进行比较），R 树很好的解决了这种高维空间搜索问题，它把 B 树的思想扩展到了多维空间，采用了 B 树分割空间的思想，并在添加、删除操作时采用合并、分解节点的方法，保证树的平衡性

### R 树满足的性质

**R 树有两个重要属性：**m 和 M，其中 M 表示一个节点中条目的最大数量，而 m 小于 m / 2，表示一个节点条目的最小数量

1. 每个叶节点若不是根节点，则包含 m 至 M 个索引记录
2. 对于所有在叶子中存储的记录，I 是最小的可以在空间中完全覆盖这些记录所代表的点的矩形
3. 每一个非叶子节点拥有 m 至 M 个子节点
4. 所有叶子节点都位于同一层，因此 R 树为平衡树

### R 树基本结构

R 树是高度平衡的树，在其叶节点的索引记录中包含指向数据对象的指针

叶子节点所保存的数据形式为：`(I, tuple-identifier)`

其中，tuple-identifier 表示的是一个存放于数据库中的 tuple，也就是一条记录，它是 n 维的；I 是一个 n 维空间的矩形，并可以恰好框住这个叶子节点中所有记录代表的 n 维空间的点

![image-20211226192348879](C:\Users\weilai\AppData\Roaming\Typora\typora-user-images\image-20211226192348879.png)

Rectangle 代表可以包裹 E1, E2, E3, E4, E5 的最小限度框

### R 树算法

> E.I 表示索引条目 E 的矩形

#### 1. 搜索算法 Search

> 给定一棵 R 树，其根节点是 T，输入参数为需要搜索的矩形 S，找出其矩形覆盖 S 的所有索引记录

- **[ 搜索子树 ]** 如果 T 不是叶节点，T 所对应的矩形与 S 有重合，那么检查所有 T 中存储的条目，对于这些条目调用 Search 作用在每一个条目所指向的子树的根节点上
- **[ 搜索叶节点 ]** 如果 T 是叶节点，T 所对应的矩形与 S 有重合，那么检查所有 S 所指向的所有记录条目，返回符合条件的记录

#### 2. 插入算法 Insert

*当新的数据记录需要被添加入叶节点时，若叶节点溢出，那么需要对叶节点进行分裂操作，所以插入操作需要一些辅助方法才能完成*

> 把一个新的索引条目 E 插入一个 R 树中

- **[ 为新纪录找到适合插入的叶子节点 ]** 调用 Choose Leaf 选择叶节点 L 放置记录 E
- **[ 添加新记录到叶节点中 ]** 如果叶节点 L 有空间存放新的记录条目，则向 L 中添加 E；否则调用 Split Node 以获得两个节点 L 和 LL，这两个节点包含了所有原来叶节点 L 中的条目和新条目 E
- **[ 向上传递变化 ]** 在节点 L 上调用 Adjust Tree，若完成了分裂，则同时需要对 LL 调用 Adjust Tree
- **[ 对树进行增高操作 ]** 如果节点分裂，且该分裂向上传播导致了根节点的分裂，那么需要生产一个新的根节点，并且让它的两个子节点分别为原来那个根节点分裂后的两个节点

#### 3. 算法 Choose Leaf

> 选择一个叶节点来存放新的索引条目 E

- **[ 初始化 ]** 设置 N 为根节点
- **[ 检查叶节点 ]** 如果 N 为叶节点，返回 N
- **[ 选择子树 ]** 如果 N 不是叶节点，设 F 为 N 中条目，它的矩形 F.I 需要至少放大到包含了 E.I，通过选择有最小区域的矩形条目来重新链接
- **[ 向下进行直至到达一个叶节点 ]** 设 N 为 F，从第二步开始重复操作

#### 4. 算法 Adjust Tree

> 从一个叶节点 L 上升到根节点，调整覆盖的矩形，在传递变换的过程中可能会产生节点的分裂

- **[ 初始化 ]** 将 N 设为 L
- **[ 检查是否完成 ]** 如果 N 为根节点，则停止操作
- **[ 调整父节点条目的最小边界矩形 ]** 设 P 为 N 的父节点，EN 为指向在父节点 P 中指向 N 的条目，调整 EN.I 以保证所有在 N 中的矩形都恰好被包围
- **[ 向上传递节点分裂 ]** 如果 N 又一个刚被分裂产生的节点 NN，则生成一个指向 NN 的条目 ENN，如果 P 有空间来存放 ENN，则将 ENN 添加到 P 中；否则条用 Split Node 来生成 P，PP，ENN  及 P 中 的所有条目
- **[ 移动到下一层 ]** 如果 N 等于 L 且发生了分裂，则把 NN 设置为 PP，从第二步开始重复操作

### 节点分裂

> 为了在一个已包含 M 个条目的已满节点中加入一个新的条目，需要采用分裂算法对节点进行合理的分裂

#### 如何合理地分裂出两个组

**二次方代价算法：**

算法首先从 M+1 个条目中选出两个条目作为两个新组中的第一个成员，选择两个条目的方法是**若两个条目放在同一组内将浪费的面积最大，即覆盖了两个条目的矩形面积减去两个条目的面积最大**，剩余的条目每次分配就是使两个差别最大的那个条目

#### 1. 算法 Quadratic Split

> 把 M+1 个索引条目分成两组

- **[ 从每个组中取第一个条目 ]** 调用算法 Pick Seeds 选出两个条目作为两组中的第一个成员，分配到组中
- **[ 检查是否结束 ]** 若所有的条目都被分配，则停止；若一组中条目很少，则剩余的条目必须分配到这组中，来保证其条目数量达到最小值 m，分配之后停止
- **[ 选择待分配的条目 ]** 调用算法 Pick Next 选择下一个待分配的条目，把它加入到所覆盖矩形在**进行最小扩展就可以容纳它**的组中；**首先考虑面积较小的组，其次是条目最少的组，最后是其他条件**；从第二步重复

#### 2. 算法 Pick Seeds

> 选择两个条目作为组中的新成员 seed

- **[ 计算两个条目的面积对应值 ]** 对于所有条目中的每一对 E1 和 E2，计算出包括E1，E2的最小限定框 `J = MBR(E1, E2)`，然后计算增量 `d = J - E1 - E2`
- **[ 选择增量最大的对 ]** 选择 d 最大的一堆条目返回

#### 3. 算法 Pick Next

> 从剩下的条目中选出一个放到组中

- **[ 判断把一个条目放入一个组中的代价 ]** 对还没放入组中的每个条目 E，计算 `d1 = 第一组包含 E.I 后覆盖矩形增加的面积`，同样的步骤计算第二组 d2
- **[ 找到对于每个组的最佳条目 ]** 选择 d1 与 d2 差别最大的条目

