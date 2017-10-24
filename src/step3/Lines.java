package step3;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class Lines {
    ArrayList<MoveLine> lines;
    public Lines(){
        lines = new ArrayList<MoveLine>();
    }

    public void addLine(MoveLine l){
        lines.add(l);
    }


    public void draw(Graphics2D g, Color c){
        g.setStroke(new BasicStroke(2));
        g.setColor(c);
        double r=2;
        for(MoveLine l:lines){
            g.draw(l.line);
            if(l.select){
                g.setColor(Color.black);
                g.draw(new Ellipse2D.Double(l.line.getX1()-r,l.line.getY1()-r,2*r,2*r));
                g.draw(new Ellipse2D.Double(l.line.getX2()-r,l.line.getY2()-r,2*r,2*r));
            }
            g.setColor(c);

        }
    }

    public void selectLine(double x, double y){
        this.setSelect();
        double min = 20;
        int index = -1;

        for(int i=0;i<lines.size();i++){
            double dis = lines.get(i).line.ptLineDist(x,y);
            if(dis<min){
                min = dis;
                index = i;
            }
        }

        if(index!=-1){
            lines.get(index).select = true;

        }

    }

    public void moveLine(double x,double y){
        for(MoveLine l:lines){
            if(l.select){
                l.line.setLine(l.line.getX1()+x,l.line.getY1()+y,l.line.getX2()+x,l.line.getY2()+y);
            }
        }

    }

    public void setSelect(){
        for(MoveLine l:lines){
            l.select = false;
        }
    }



}
