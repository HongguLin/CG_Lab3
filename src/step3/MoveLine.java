package step3;

import java.awt.geom.Line2D;

public class MoveLine {
    Line2D line;
    boolean select;

    public MoveLine(int x1,int y1, int x2, int y2){
        line = new Line2D.Double(x1,y1,x2,y2);
        select = false;
    }
}
