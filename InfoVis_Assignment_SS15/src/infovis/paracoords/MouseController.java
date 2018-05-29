package infovis.paracoords;

import infovis.scatterplot.Model;
import infovis.scatterplot.Data; 
import infovis.scatterplot.Range;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseController implements MouseListener, MouseMotionListener {
	private View view = null;
	private Model model = null;
	Shape currentShape = null;
	
	private int xStart = 0;
	private int yStart = 0;
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		view.getMarker().setRect(0,0,0,0);
		view.setMarkerActive();
		view.repaint();
		
		xStart = e.getX();
		yStart = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		view.setMarkerInactive();
		System.out.println(view.getMarker().getHeight());
		view.getMarker().setRect(0,0,0,0);
		view.repaint();
		
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX()- xStart;
		int y = e.getY() - yStart;
		view.getMarker().setRect(xStart, yStart,x,y);
		//TODO: this should be calculated correctly in view
		view.repaint();
	}

	public void mouseMoved(MouseEvent e) {

	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
