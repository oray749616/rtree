package com.weilai.rtree;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RTDirNode
 * @Description: TODO
 */
public class RTDirNode extends RTNode {
    protected List<RTNode> children;

    public RTDirNode(Rtree rTree, RTNode parent, int level) {
        super(rTree, parent, level);
        children = new ArrayList<>();
    }

    /**
     *
     * @param index 索引
     * @return 对应索引下的子节点
     */
    public RTNode getChild(int index) {
        return children.get(index);
    }

    @Override
    protected RTDataNode chooseLeaf(Rectangle rectangle) throws CloneNotSupportedException {
        int index;

        switch (rTree.getTreeType()) {
            case Constants.RTREE_LINEAR:
            case Constants.RTREE_QUADRATIC:
            case Constants.RTREE_EXPONENTIAL:
                // 获得面积增量最小的节点索引
                index = findLeastEnlargement(rectangle);
                break;
            case Constants.RSTAR:
                // 即此结点指向叶节点
                if (level == 1) {
                    // 获得最小重叠面积的结点的索引
                    index = findLeastOverlap(rectangle);
                } else {
                    // 获得面积增量最小的结点的索引
                    index = findLeastEnlargement(rectangle);
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid tree type.");
        }

        // 记录插入路径的索引
        insertIndex = index;

        // 非叶节点的chooseLeaf()实现递归调用
        return getChild(index).chooseLeaf(rectangle);
    }

    @Override
    protected RTDataNode findLeaf(Rectangle rectangle) {
        for (int i = 0; i < usedSpace; i++) {
            if (datas[i].enclosure(rectangle)) {
                deleteIndex = i;
                RTDataNode leaf = children.get(i).findLeaf(rectangle);
                if (leaf != null)
                    return leaf;
            }
        }
        return null;
    }

    /**
     *
     * @param rectangle 外包矩形
     * @return 返回最小重叠面积的节点索引<br/>
     *         1.如果重叠面积相等则选择加入此Rectangle后面积增量更小的<br/>
     *         2.如果面积增量还相等则选择自身面积更小的
     */
    private int findLeastEnlargement(Rectangle rectangle) throws CloneNotSupportedException {
        float overlap = Float.POSITIVE_INFINITY;
        int sel = -1;

        for (int i = 0; i < usedSpace; i++) {
            RTNode node = getChild(i);
            // 用于记录每个子节点的data数据与传入矩形的重叠面积之和
            float ol = 0;

            for (int j = 0; j < node.datas.length; j++) {
                // 将传入矩形与各个矩形重叠的面积累加到ol中，得到重叠的总面积
                ol += rectangle.intersectingArea(node.datas[j]);
            }
            if (ol < overlap) {
                // 记录重叠面积最小的
                overlap = ol;
                // 记录第几个子节点的索引
                sel = i;
            }
            // 如果重叠面积相等则选择加入此Rectangle后面积增量更小的，如果面积增量还相等则选择自身面积更小的
            else if (ol == overlap) {
                double area1 = datas[i].getUnionRectangle(rectangle).getArea() - datas[i].getArea();
                double area2 = datas[sel].getUnionRectangle(rectangle).getArea() - datas[sel].getArea();

                if (area1 == area2) {
                    sel = datas[sel].getArea() <= datas[i].getArea() ? sel : i;
                } else {
                    sel = area1 < area2 ? i : sel;
                }
            }
        }

        return sel;
    }

    /**
     *
     * @param rectangle 矩形
     * @return 返回最小重叠面积的节点索引
     *         如果重叠面积相等则选择加入此Rectangle后面积增量更小的
     *         如果面积增量还相等则选择自身面积更小的
     */
    private int findLeastOverlap(Rectangle rectangle) throws CloneNotSupportedException {
        float overlap = Float.POSITIVE_INFINITY;
        int sel = -1;

        for (int i = 0; i < usedSpace; i++) {
            RTNode node = getChild(i);
            // 用于记录每个子节点的data数据与传入矩形的重叠面积之和
            float ol = 0;

            for (int j = 0; j < node.datas.length; j++) {
                // 将传入矩形与各个矩形重叠的面积累加到ol中，得到重叠的总面积
                ol += rectangle.intersectingArea(node.datas[j]);
            }
            if (ol < overlap) {
                // 记录重叠面积最小的
                overlap = ol;
                // 记录第几个子节点的索引
                sel = i;
            }
            // 如果重叠面积相等则选择加入此Rectangle后面积增量更小的,如果面积增量还相等则选择自身面积更小的
            else if (ol == overlap) {
                double area1 = datas[i].getUnionRectangle(rectangle).getArea() - datas[i].getArea();
                double area2 = datas[sel].getUnionRectangle(rectangle).getArea() - datas[sel].getArea();

                if (area1 == area2) {
                    sel = (datas[sel].getArea() <= datas[i].getArea()) ? sel : i;
                } else {
                    sel = (area1 < area2) ? i : sel;
                }
            }
        }

        return sel;
    }

    /**
     * 插入新的Rectangle后从插入的叶节点开始向上调整RTree，直到根节点
     * @param node1 需要调整的子节点
     * @param node2 分裂的节点，若未分裂则为null
     */
    public void adjustTree(RTNode node1, RTNode node2) throws CloneNotSupportedException {
        // 先要找到指向原来旧的节点（即未添加Rectangle之前）的条目索引
        datas[insertIndex] = node1.getNodeRectangle();
        children.set(insertIndex, node1);

        if (node2 != null) {
            insert(node2);
        } else if (!isRoot()) {
            RTDirNode parent = (RTDirNode) getParent();
            // 向上调整直到根节点
            parent.adjustTree(this, null);
        }
    }

    /**
     * 非叶节点插入
     * @param node
     * @return 如果结点需要分裂则返回true
     */
    protected boolean insert(RTNode node) throws CloneNotSupportedException {
        // 已用节点小于树的节点容量，不需要分裂，只需要插入以及调整树
        if (usedSpace < rTree.getNodeCapacity()) {
            datas[usedSpace++] = node.getNodeRectangle();
            children.add(node);
            node.parent = this;
            RTDirNode parent = (RTDirNode) getParent();
            if (parent != null) {
                parent.adjustTree(this, null);
            }
            return false;
        } else {
            RTDirNode[] a = splitIndex(node);
            RTDirNode n = a[0];
            RTDirNode m = a[1];

            if (isRoot()) {
                // 新建根节点，层数加1
                RTDirNode newRoot = new RTDirNode(rTree, Constants.NULL, level + 1);

                // 把两个分裂的节点n和m添加到根节点
                newRoot.addData(n.getNodeRectangle());
                newRoot.addData(m.getNodeRectangle());

                newRoot.children.add(n);
                newRoot.children.add(m);

                // 设置两个分裂的节点n和m的父节点
                n.parent = newRoot;
                m.parent = newRoot;

                // 设置rtree的根节点
                rTree.setRoot(newRoot);
            } else {
                // 如果不是根节点，向上调整树
                RTDirNode p = (RTDirNode) getParent();
                p.adjustTree(n, m);
            }
        }

        return true;
    }

    /**
     * 非叶节点的分裂
     * @param node
     * @return
     */
    private RTDirNode[] splitIndex(RTNode node) throws CloneNotSupportedException {
        int[][] group = null;
        switch (rTree.getTreeType()) {
            case Constants.RTREE_LINEAR:
            case Constants.RTREE_EXPONENTIAL:
            case Constants.RSTAR:
                break;
            case Constants.RTREE_QUADRATIC:
                group = quadraticSplit(node.getNodeRectangle());
                children.add(node);
                node.parent = this;
                break;
            default:
                throw new IllegalArgumentException("Invalid tree type.");
        }

        // 新建两个非叶节点
        RTDirNode index1 = new RTDirNode(rTree, parent, level);
        RTDirNode index2 = new RTDirNode(rTree, parent, level);

        int[] group1 = group[0];
        int[] group2 = group[1];
        // 为index1添加数据和子节点
        for (int i : group1) {
            index1.addData(datas[i]);
            index1.children.add(this.children.get(i));
            // 让index1成为其父节点
            this.children.get(i).parent = index1;
        }
        for (int j : group2) {
            index2.addData(datas[j]);
            index2.children.add(this.children.get(j));
            // 让index1成为其父节点
            this.children.get(j).parent = index2;
        }

        return new RTDirNode[] {index1, index2};
    }
}
