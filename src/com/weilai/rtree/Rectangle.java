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
     * @param p1 左下角
     * @param p2 右下角
     */
    public Rectangle(Point p1, Point p2) {

    }

    /**
     * 返回Rectangle左下角的点
     * @return Point
     */
    public Point getLow() {
        return low;
    }

    /**
     * 返回Rectangle右上角的点
     * @return Point
     */
    public Point getHigh() {
        return high;
    }

    /**
     * @param rectangle 外包矩形
     * @return 包围两个rectangle的最小矩形
     */
    public Rectangle getUnionRectangle(Rectangle rectangle) {

    }

    /**
     * @param rectangles 外包矩形
     * @return 包围一系列Rectangle的最小矩形
     */
    public static Rectangle getUnionRectangle(Rectangle[] rectangles) {

    }

    /**
     * @return 返回Rectangle的面积
     */
    public float getArea() {

    }

    /**
     * @return 返回Rectangle的维度
     */
    private int getDimension() {
        return low.getDimension();
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
            if (low.equals(rectangle.getLow()) && high.equals(rectangle.getHigh()) {
                return true;
            }
        }

        return false;
    }
}
