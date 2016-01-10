import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PanelPaint extends JPanel implements MouseListener{
	
	Point p0 = null;
	Point p1 = null;
	
	BufferedImage bi = null;
	
	PaintCom m;
	
	float s = 5;
	
	
	Color c = Color.BLACK;
	
	public void setColor(Color c){
		this.c = c;
	}
	
	public void plumaChanged(int val){
		s = val/5f;
		System.out.println(s);
	}
	int aux = 0;
	
	Timer paintTimer= new Timer(16,new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Point p = m.getMousePosition();
			m.pintar(p, c, s);
		}
	});
	
	public void pintar(Point p,Color c,float ancho){
		p0 = p0==null?p:p1;
		p1=p;
		this.c = c;
		this.s = ancho;
		repaint();
	}
	
	/**
	 * Create the panel.
	 */
	public PanelPaint(PaintCom m) {
		this.m = m;
		addMouseListener(this);
		setFocusable(true);
	}
	
	
	public void paint(Graphics gr){
		Graphics2D g;
		
		if(bi==null){
			bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			g = bi.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		g = bi.createGraphics();


		g.setStroke(new BasicStroke(s));
		g.setColor(c);
		if(p0!=null&&p1!=null)
			g.drawLine((int)p0.getX(),(int) p0.getY(),(int) p1.getX(),(int) p1.getY());
		
		g.dispose();
		
		gr.drawImage(bi, 0, 0, null);
	}
	

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		paintTimer.start();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		paintTimer.stop();
		p0=null;
		m.pintar(null, null, 0);
	}
	
}
