package com.weilai.rtree;

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
    }
}
