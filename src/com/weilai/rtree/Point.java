package com.weilai.rtree;

/**
 * @ClassName Point
 * @Description: n维空间中的点，所有的维度被存储在一个float数组中
 */
public class Point {
    private float[] data;

    public Point(float[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Coordinates can't be empty.");
        }
        if (data.length < 2) {
            throw new IllegalArgumentException("The dimension of the point must be greater than 1.");
        }

        this.data = new float[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public Point(int[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Coordinates can't be empty.");
        }
        if (data.length < 2) {
            throw new IllegalArgumentException("The dimension of the point must be greater than 1.");
        }

        this.data = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = data[i];
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        float[] copy = new float[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        return new Point(copy);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");

        for (int i = 0; i < data.length - 1; i++) {
            sb.append(data[i]).append(",");
        }
        sb.append(data[data.length - 1]).append(")");

        return sb.toString();
    }

    /**
     * @return 返回Point的维度
     */
    public int getDimension() {
        return data.length;
    }

    /**
     * @param index 下标
     * @return 返回Point坐标的第i位的int值
     */
    public float getFloatCoordinate(int index) {
        return data[index];
    }

    /**
     * @param index 下标
     * @return 返回Point坐标的第i位的int值
     */
    public int getIntCoordinate(int index) {
        return (int) data[index];
    }

    @Override
    public boolean equals(Object obj) {
        // obj是Point的实例
        if (obj instanceof Point) {
            Point point = (Point) obj;

            // 维度相同的点才能比较
            if (point.getDimension() != getDimension()) {
                throw new IllegalArgumentException("Only points with the same dimension can be compared.");
            }

            for (int i = 0; i < getDimension(); i++) {
                if (getFloatCoordinate(i) != point.getFloatCoordinate(i)) {
                    return false;
                }
            }
        }

        return obj instanceof Point;
    }
}
