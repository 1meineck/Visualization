package infovis.scatterplot;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseController implements MouseListener, MouseMotionListener {

	private Model model = null;
	private View view = null;
	private int xStart = 0;
	private int yStart = 0;

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		//Remove Rectangle and re-color Points on Mouse Pressed
		view.getMarkerRectangle().setRect(0,0,0,0);
		view.resetColor();
		view.repaint();
		
		// set starting position for Mouse dragging
		xStart = arg0.getX();
		yStart = arg0.getY();
		
	}

	public void mouseReleased(MouseEvent arg0) {
		
	}

	public void mouseDragged(MouseEvent arg0) {

		// Calculate Position of the marker rectangle according to 
		//start and end position of the cursor
		
		int xBegin;
		int xLength;
		int yBegin;
		int yLength;
		
		if(arg0.getX()>=xStart) {
			xLength = arg0.getX()- xStart;
			xBegin = xStart;
		}
		else{
			xLength = xStart - arg0.getX();
			xBegin = arg0.getX();
		}
		
		if(arg0.getY()>=yStart) {
			yLength = arg0.getY()- yStart;
			yBegin = yStart;
		}
		else{
			yLength = yStart - arg0.getY();
			yBegin = arg0.getY();
		}
	
		// set Marker Rectangle at calculated Position	
		
		view.getMarkerRectangle().setRect(xBegin, yBegin,xLength,yLength);
		view.resetColor();
		view.repaint();
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void setModel(Model model) {
		this.model  = model;	
	}

	public void setView(View view) {
		this.view  = view;
	}

}
