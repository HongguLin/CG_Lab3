import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/*
 * DrawArea - a simple JComponent for drawing.  The "offscreen" BufferedImage is 
 * used to draw to,  this image is then used to paint the component.
 * Eric McCreath 2009 2015, 2017
 */

public class DrawArea extends JComponent implements MouseMotionListener,
		MouseListener {

	private BufferedImage offscreen;
	Dimension dim;
	DrawIt drawit;

	//TODO step1
	int lastX;
	int lastY;

	//TODO step2
	int cX;
	int cY;
	boolean fill=false;
	int bc;
	
	public DrawArea(Dimension dim, DrawIt drawit) {
		this.setPreferredSize(dim);
		offscreen = new BufferedImage(dim.width, dim.height,
				BufferedImage.TYPE_INT_RGB);
		this.dim = dim;
		this.drawit = drawit;
		this.addMouseMotionListener(this);
		this.addMouseListener(this);

		clearOffscreen();
	}

	public void clearOffscreen() {
		Graphics2D g = offscreen.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, dim.width, dim.height);
		repaint();
	}

	public Graphics2D getOffscreenGraphics() {
		return offscreen.createGraphics();
	}

	public void drawOffscreen() {
		repaint();
	}

	protected void paintComponent(Graphics g) {
		g.drawImage(offscreen, 0, 0, null);
	}

	//TODO step2 Fill
	public void myFill(BufferedImage img, int x, int y, int bc, int nc){
		Queue<Point> Q = new LinkedList<Point>();
		Q.add(new Point(x,y));

		while (Q.size()>0){
			Point p = Q.remove();
			if(img.getRGB(p.x,p.y)!=-1) continue;
			img.setRGB(p.x,p.y,nc);
			Point[] pl = {new Point(p.x+1,p.y),new Point(p.x-1,p.y),new Point(p.x,p.y+1),new Point(p.x,p.y-1)};

			for(int i=0;i<4;i++){
				if(pl[i].x>0 && pl[i].x<dim.width-3 && pl[i].y>0 && pl[i].y<dim.height-3 && img.getRGB(pl[i].x,pl[i].y)==-1){
					Q.add(pl[i]);
				}
			}
		}

	}
	
	public void mouseDragged(MouseEvent m) {
		Graphics2D g = offscreen.createGraphics();
		//TODO step1 Transparent
		Color temp = (Color) drawit.colorToolbar.getSelectCommand();
		Color use = new Color(temp.getRed(),temp.getGreen(),temp.getBlue(),drawit.trpSlider.getValue());
		bc = use.getRGB();

		//TODO step3 Mix color
		Color base = new Color(offscreen.getRGB(m.getX(),m.getY()));
		Color mix;
		if(base.getRGB()==-1){
			mix = use;
		}else {
			mix = new Color(temp.getRGB()+base.getRGB());
		}
		g.setColor(mix);

		//g.setColor(base);
		//TODO step1 Thickness
		g.setStroke(new BasicStroke(drawit.thkSlider.getValue()/10));
		// System.out.println("Slide Value is : " + drawit.aSlider.getValue());
		//g.fill(new Ellipse2D.Double(m.getX() - 1.0, m.getY() - 1.0, 2.0, 2.0));

		//TODO step1 draw line
		if(drawit.myToolbar.getSelectCommand()=="line"){
			g.setStroke(new BasicStroke(drawit.thkSlider.getValue()/10));
			g.draw(new Line2D.Double(lastX,lastY,m.getX(),m.getY()));
		}

		//TODO step2 Spray
		if(drawit.myToolbar.getSelectCommand()=="spray"){
			Random random = new Random();

			g.setStroke(new BasicStroke(1));
			for(int i=0;i<drawit.thkSlider.getValue()/10;i++){
				int r1 = random.nextInt(1+drawit.thkSlider.getValue()/5);
				int r2 = random.nextInt(1+drawit.thkSlider.getValue()/5);
				int x = m.getX();
				int y = m.getY();
				int x1 = x + r1;
				int x2 = x - r1;
				int y1 = y + r2;
				int y2 = y - r2;

				if(x2>0 && x1<offscreen.getWidth() && y2>0 && y1<offscreen.getHeight()){
					g.draw(new Line2D.Double(x,y,x,y));
					g.draw(new Line2D.Double(x1,y1,x1,y1));
					g.draw(new Line2D.Double(x2,y2,x2,y2));
					g.draw(new Line2D.Double(x1,y2,x1,y2));
					g.draw(new Line2D.Double(x2,y1,x2,y1));
				}
			}
		}






		lastX=m.getX();
		lastY=m.getY();

		drawOffscreen();
	}

	public void mouseMoved(MouseEvent m) {
	}

	public void mouseClicked(MouseEvent e) {
		//TODO step2 Fill
		if(drawit.myToolbar.getSelectCommand()=="fill"){
			cX=e.getX();
			cY=e.getY();
			fill=true;
		}


		if(fill){
			myFill(offscreen,cX,cY,bc,((Color) drawit.colorToolbar.getSelectCommand()).getRGB());
		}
		drawOffscreen();


	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		//TODO step1 draw line
		lastX=e.getX();
		lastY=e.getY();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void export(File file) {
		try {
			ImageIO.write(offscreen, "png", file);
		} catch (IOException e) {
			System.out.println("problem saving file");
		}
	}
}
