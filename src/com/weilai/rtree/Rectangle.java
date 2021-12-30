package com.weilai.rtree;

/**
 * @ClassName Rectangle
 * @Description: TODO
 */
public class Rectangle implements Cloneable {
    private Point low;
    private Point high;

    /**
     * 初始化
     *
     * @param p1 左下角
     * @param p2 右下角
     */
    public Rectangle(Point p1, Point p2) throws CloneNotSupportedException {
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Points can't be null.");
        }
        if (p1.getDimension() != p2.getDimension()) {
            throw new IllegalArgumentException("Points must be of same dimension.");
        }

        // 从左下角到右上角
        for (int i = 0; i < p1.getDimension(); i++) {
            if (p1.getFloatCoordinate(i) > p2.getFloatCoordinate(i)) {
                throw new IllegalArgumentException("The coordinate point is the lower left corner first, then the upper right corner.");
            }
        }

        low = (Point) p1.clone();
        high = (Point) p2.clone();
    }

    /**
     * 返回Rectangle左下角的点
     *
     * @return Point
     */
    public Point getLow() {
        return low;
    }

    /**
     * 返回Rectangle右上角的点
     *
     * @return Point
     */
    public Point getHigh() {
        return high;
    }

    /**
     * @param rectangle 外包矩形
     * @return 包围两个rectangle的最小矩形
     */
    public Rectangle getUnionRectangle(Rectangle rectangle) throws CloneNotSupportedException {
        if (rectangle == null) {
            throw new IllegalArgumentException("Rectangle can't be null.");
        }
        if (rectangle.getDimension() != getDimension()) {
            throw new IllegalArgumentException("Rectangle must be of same dimension.");
        }

        float[] min = new float[getDimension()];
        float[] max = new float[getDimension()];

        for (int i = 0; i < getDimension(); i++) {
            // 第一个参数是当前矩阵的坐标值，第二个参数是传入参数的矩阵的坐标值
            min[i] = Math.min(low.getFloatCoordinate(i), rectangle.low.getFloatCoordinate(i));
            max[i] = Math.max(high.getFloatCoordinate(i), rectangle.high.getFloatCoordinate(i));
        }

        return new Rectangle(new Point(min), new Point(max));
    }

    /**
     * @param rectangles 外包矩形
     * @return 包围一系列Rectangle的最小矩形
     */
    public static Rectangle getUnionRectangle(Rectangle[] rectangles) throws CloneNotSupportedException {
        if (rectangles == null || rectangles.length == 0) {
            throw new IllegalArgumentException("Rectangle array is empty.");
        }

        Rectangle r = (Rectangle) rectangles[0].clone();
        for (int i = 1; i < rectangles.length; i++) {
            r = r.getUnionRectangle(rectangles[i]);
        }

        return r;
    }

    /**
     * @return 返回Rectangle的面积
     */
    public float getArea() {
        float area = 1;
        for (int i = 0; i < getDimension(); i++) {
            area *= high.getFloatCoordinate(i) - low.getFloatCoordinate(i);
        }

        return area;
    }

    /**
     * @return 返回Rectangle的维度
     */
    private int getDimension() {
        return low.getDimension();
    }

    /**
     * @param rectangle 外包矩阵
     * @return 判断两个Rectangle是否相交
     */
    public boolean isIntersection(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }
        if (rectangle.getDimension() != getDimension()) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }

        for (int i = 0; i < getDimension(); i++) {
            // 当前矩阵左下角的坐标值大于传入矩阵右上角的坐标 || 当前矩阵右上角的坐标值小于传入矩阵左下角的坐标值
            if (low.getFloatCoordinate(i) > rectangle.high.getFloatCoordinate(i)
                    || high.getFloatCoordinate(i) < rectangle.low.getFloatCoordinate(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param rectangle 外包矩阵
     * @return 返回两个Rectangle相交的面积
     */
    public float intersectingArea(Rectangle rectangle) {
        if (!isIntersection(rectangle)) {
            return 0;
        }

        float ret = 1;
        // 循环一次，得到一个维度的相交的边，累乘多个维度的相交的边，即为面积
        for (int i = 0; i < rectangle.getDimension(); i++) {
            float l1 = this.low.getFloatCoordinate(i);
            float h1 = this.high.getFloatCoordinate(i);
            float l2 = rectangle.low.getFloatCoordinate(i);
            float h2 = rectangle.high.getFloatCoordinate(i);

            // rectangle_1在rectangle_2的左边
            if (l1 <= l2 && h1 <= h2) {
                ret *= (h1 - l1) - (l2 - l1);
            }
            // rectangle_1在rectangle_2的右边
            else if (l1 >= l2 && h1 >= h2) {
                ret *= (h2 - l2) - (l1 - l2);
            }
            // rectangle_1在rectangle_2的里面
            else if (l1 >= l2 && h1 <= h2) {
                ret *= h1 - l1;
            }
            // rectangle_2在rectangle_1的里面
            else if (l1 <= l2 && h1 >= h2) {
                ret *= h2 - l2;
            }
        }

        return ret;
    }

    /**
     * @param rectangle 外包矩阵
     * @return 判断Rectangle是否被包围
     */
    public boolean enclosure(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Rectangle can't be null.");
        }
        if (rectangle.getDimension() != getDimension()) {
            throw new IllegalArgumentException("Rectangle dimension is different from current dimension.");
        }

        // 只要传入的Rectangle有一个维度的坐标越界就不被包围
        for (int i = 0; i < getDimension(); i++) {
            if (rectangle.low.getFloatCoordinate(i) < low.getFloatCoordinate(i)
                    || rectangle.high.getFloatCoordinate(i) > high.getFloatCoordinate(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Point p1 = (Point) low.clone();
        Point p2 = (Point) high.clone();
        return new Rectangle(p1, p2);
    }

    @Override
    public String toString() {
        return "Rectangle Low:" + low + " High:" + high;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) obj;
            return low.equals(rectangle.getLow()) && high.equals(rectangle.getHigh());
        }

        return false;
    }
}
