package com.weilai.rtree;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName Rtree
 * @Description: TODO
 */
public class Rtree {
    private RTNode root;            // 根节点
    private int treeType;           // 树类型
    private int nodeCapacity = -1;  // 节点容量
    private float fillFactor = -1;  // 节点填充因子，用于计算每个节点最小条目数
    private int dimension;          // 维度

    public Rtree(int treeType, int nodeCapacity, float fillFactor, int dimension) {
        this.treeType = treeType;
        this.nodeCapacity = nodeCapacity;
        this.fillFactor = fillFactor;
        this.dimension = dimension;
        // 根节点的父节点为NULL
        root = new RTDataNode(this, Constants.NULL);
    }

    /**
     * @return 返回RTree的维度
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * @param root 设置根节点
     */
    public void setRoot(RTNode root) {
        this.root = root;
    }

    /**
     * @return 填充因子
     */
    public float getFillFactor() {
        return fillFactor;
    }

    /**
     * @return 返回节点容量
     */
    public int getNodeCapacity() {
        return nodeCapacity;
    }

    /**
     * @return 返回树的类型
     */
    public int getTreeType() {
        return treeType;
    }

    /**
     * 向Rtree中插入Rectangle
     * 1. 先找到适合的叶节点
     * 2. 再向此叶节点中插入
     *
     * @param rectangle 外包矩阵
     * @return 叶节点
     */
    public boolean insert(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }
        if (rectangle.getHigh().getDimension() != getDimension()) {
            throw new IllegalArgumentException("Rectangle dimension different than RTree dimension.");
        }

        RTDataNode leaf = root.chooseLeaf(rectangle);

        return leaf.insert(rectangle);
    }

    /**
     * 从R树中删除Rectangle
     * 1. 寻找包括记录的节点：调用算法findLeaf()来定位包含此纪录的叶节点L，如果没找到则算法终止
     * 2. 删除记录：将找到的叶节点L中的此记录删除
     * 3. 调用算法condenseTree
     *
     * @param rectangle 外包节点
     * @return 叶节点
     */
    public int delete(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Rectangle can't be null.");
        }
        if (rectangle.getHigh().getDimension() != getDimension()) {
            throw new IllegalArgumentException("Rectangle dimension different than RTree dimension.");
        }

        RTNode leaf = root.findLeaf(rectangle);

        if (leaf != null) {
            return leaf.delete(rectangle);
        }

        return -1;
    }

    /**
     * 从给定的节点root开始遍历所有的节点
     *
     * @param root 根节点
     * @return 所有遍历的节点集合
     */
    public List<RTNode> traversePostOrder(RTNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Node can't be null.");
        }

        List<RTNode> list = new ArrayList<>();
        list.add(root);

        if (!root.isLeaf()) {
            for (int i = 0; i < root.usedSpace; i++) {
                List<RTNode> a = traversePostOrder(((RTDirNode) root).getChild(i));
                for (int j = 0; j < a.size(); j++) {
                    list.add(a.get(j));
                }
            }
        }

        return list;
    }
}
