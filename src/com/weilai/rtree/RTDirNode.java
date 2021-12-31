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

    private int findLeastEnlargement(Rectangle rectangle) {
    }

    @Override
    protected RTDataNode findLeaf(Rectangle rectangle) {
        for (int i = 0; i < usedSpace; i++) {
            if (datas[i].enclosure(rectangle)) {
                deleteIndex = i;
                RTDataNode leaf = children.get(i).findLeaf(rectangle);
                if (leaf != null) {
                    return leaf;
                }
            }
        }
        return null;
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
}
