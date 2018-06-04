package infovis.paracoords;

import infovis.scatterplot.Model;
import infovis.scatterplot.Data; 
import infovis.scatterplot.Range;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class View extends JPanel {
	private Model model = null;
	private Rectangle2D markerRectangle = new Rectangle2D.Double(0,0,0,0);
	private Rectangle2D savedMarker = new Rectangle2D.Double(0,0,0,0);
	private boolean markerActive = true;
	private int lineLength = 0; 
	public int lineDistance = 0;
	public int elementDistance = 0;
	public int borderSize = 100;  
	private int textSpace = 10;
	int[] xPoints; 
	private ArrayList<int[]> yList;
	private boolean changeAxisMode = false;
	private ArrayList<Integer> order;
	private int currentPosition; 

	@Override
	public void paint(Graphics g) {
		calculateLines(); 
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);

		g2d.clearRect(0, 0, getWidth(), getHeight());

		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(Color.BLACK);

		g2d.draw(markerRectangle);

		xPoints = new int[model.getDim()]; 

		if (order==null) {
			order = new ArrayList<Integer>();
			for (int i = 0; i<model.getDim(); i++) {
				order.add(i);
			}
		}

		// draw Coordinate System with Labels
		for (int i = 0; i < model.getDim(); i++) {
			int xPosition = i*lineDistance+borderSize;
			xPoints[i] = xPosition;
			g2d.drawLine((int) xPosition, borderSize, (int) xPosition, getHeight()-borderSize);

			String labelx = model.getLabels().get(order.get(i));
			g2d.drawString(labelx, xPosition-labelx.length()*2, borderSize/2);}

		/*for (int k = 0; k < model.getList().size(); k++ ){
			int yPosition= borderSize + elementDistance -textSpace;
			elementDistance += lineLength/model.getList().size();
			String labely = model.getList().get(k).getLabel();
			g2d.drawString(labely, textSpace,  yPosition);
		}*/

		if (!changeAxisMode ) {
			yList= new ArrayList<int[]>();
			for (int k = 0; k < model.getList().size(); k++ ){
				int[] yPoints = new int[model.getDim()];
				for (int i =0; i<model.getDim(); i++) {
					int yPosition = calculatePosY(i, k);
					yPoints[i] = yPosition;
				}
				yList.add(yPoints);
				g2d.setColor(Color.black);
			}
		} 
		drawParallelCoords(g2d);
	}

	private void changeAxis(int currentLocation, int newLocation) {
		String label = model.getLabels().get(currentLocation); 
		model.getLabels().remove(currentLocation); 
		model.getLabels().add(newLocation, label);

		// TODO recalculate axes
	}
	private void drawParallelCoords(Graphics2D g2d) {
		/*for (int i = 0; i<model.getDim(); i++) {
			int xPosition = i*lineDistance+borderSize;
			g2d.drawLine((int) xPosition, borderSize, (int) xPosition, getHeight()-borderSize);
		}*/

		drawLines(g2d);

	}

	private void drawLines(Graphics2D g2d) {
		for (int i = 0; i < yList.size(); i++) {
			int[] yPoints = yList.get(i);
			g2d.setColor(Color.GRAY);
			Line2D.Double[] lines = new Line2D.Double[xPoints.length-1];
			for (int j = 0; j<yPoints.length-1; j++) {

				Line2D.Double line = new Line2D.Double(xPoints[j], yPoints[order.get(j)], xPoints[j+1], yPoints[order.get(j+1)]);
				lines[j]=line;
				if(markerActive) {
					if(line.intersects(markerRectangle)) {
						g2d.setColor(Color.red);
						System.out.println(j);
					}
				}else {
					if(line.intersects(savedMarker)) {
						g2d.setColor(Color.red);
					}
				}
			}
			for (int j = 0; j<lines.length; j++) {
				g2d.draw(lines[j]);
			}
		}

	}

	private int calculatePosY(int i, int k) {
		double yValue = model.getList().get(k).getValues()[i];
		double maxY = model.getRanges().get(i).getMax();
		double minY = model.getRanges().get(i).getMin();
		int yPosition = (int) (((1 - ((maxY-yValue) /(maxY - minY)))*(getHeight()-2*borderSize)))+borderSize;
		return yPosition;
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	//calculating the axes
	public void calculateLines() {
		lineLength = getHeight()-2*borderSize; 
		lineDistance = (getWidth()-2*borderSize)/(model.getDim()-1);
		elementDistance = lineLength/model.getList().size();
	}
	public Rectangle2D getMarker() {
		return markerRectangle;
	}
	public void setMarkerActive() {
		markerActive = true; 
	}
	public void setMarkerInactive() {
		markerActive = false; 
		savedMarker = new Rectangle2D.Double(markerRectangle.getX(), markerRectangle.getY(), markerRectangle.getWidth(), markerRectangle.getHeight());
	}

	public void changeAxisMode(boolean b, int axis) {
		currentPosition = axis;
		changeAxisMode = b;
	}

	public void changeOrder(int newPosition) {
		int x = order.get(currentPosition);

		order.remove(currentPosition);
		order.add(newPosition, x);
	}

}
