package infovis.paracoords;

import infovis.scatterplot.Model;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class View extends JPanel {
	private Model model = null;
	private Rectangle2D markerRectangle = new Rectangle2D.Double(0,0,0,0);
	private boolean markerActive = false;
	public int lineDistance = 0;
	public int borderSize = 100;  
	int[] xPoints; 
	private ArrayList<int[]> yList;
	private ArrayList<Integer> order;
	private int currentPosition;
	private ArrayList<Line2D.Double[]> lineList; 

	@Override
	public void paint(Graphics g) {
		calculateAxes(); 
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
		drawCoordinateSystem(g2d);
		
		// create yList with all y Positions of each element in the model
		createYPositions();
		

		// calculate the lines for each element in the model and set the color for the lines in the colorList to red, if they were selected by the marker rectangle
		calculateLines(g2d);
		
		//draw the all lines of the parallel coordinate system
		drawLines(g2d);

	}

	/*
	 * Calculates all y-positions for each element of the model and saves them as Array in yList
	 */
	private void createYPositions() {
		yList= new ArrayList<int[]>();
		for (int k = 0; k < model.getList().size(); k++ ){
			int[] yPoints = new int[model.getDim()];
			for (int i =0; i<model.getDim(); i++) {
				int yPosition = calculatePosY(i, k);
				yPoints[i] = yPosition;
			}
			yList.add(yPoints);
		}
	}

	/*
	 * Based on the xPositions, which are defined through the placement of the axes in the Parallel Coordinate System, 
	 * and the yPositions, which are saved in the yList, 
	 * this functions calculates the individual lines for each element of the model and changes the color of the lines to red, if the lines was selected. 
	 */
	private void calculateLines(Graphics2D g2d) {
		lineList = new ArrayList<Line2D.Double[]>(); 
		for (int i = 0; i < yList.size(); i++) {
			int[] yPoints = yList.get(i);
			Color color = Color.gray;
			Line2D.Double[] lines = new Line2D.Double[yPoints.length-1];
			for (int j = 0; j<yPoints.length-1; j++) {

				Line2D.Double line = new Line2D.Double(xPoints[j], yPoints[order.get(j)], xPoints[j+1], yPoints[order.get(j+1)]);
				lines[j]=line;
				if(markerActive) {
					if(line.intersects(markerRectangle)) {
						color = Color.red;
					}
				}
			}
			if(markerActive) {
				model.getList().get(i).setColor(color);
			}
			lineList.add(lines); 
		}

	}

	/*
	 * draws all lines in lineList in the corresponding color found in colorList
	 */
	private void drawLines(Graphics2D g2d) {
		for (int i = 0; i<lineList.size(); i++) {
			g2d.setColor(model.getList().get(i).getColor());
			Line2D.Double[] lines = lineList.get(i);
			for(int j=0; j<lines.length; j++) {
				g2d.draw(lines[j]);
			}
		}

	}

	/*
	 * calculates the actual yPosition for each value found in the model
	 */
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
	public void calculateAxes() {
		lineDistance = (getWidth()-2*borderSize)/(model.getDim()-1);
	}
	
	public Rectangle2D getMarker() {
		return markerRectangle;
	}
	public void setMarkerActive() {
		markerActive = true; 
	}
	public void setMarkerInactive() {
		markerActive = false; 
	}

	/*
	 * draws the coordinate system and assigns the labels for each axis
	 */
	private void drawCoordinateSystem(Graphics2D g2d) {
		for (int i = 0; i < model.getDim(); i++) {
			int xPosition = i*lineDistance+borderSize;
			xPoints[i] = xPosition;
			g2d.drawLine((int) xPosition, borderSize, (int) xPosition, getHeight()-borderSize);

			String labelx = model.getLabels().get(order.get(i));
			g2d.drawString(labelx, xPosition-labelx.length()*2, borderSize/2);}

	}
	
	//Helper functions for changing the order of the axes

	public int getMaxPos() {
		return xPoints.length-1;
	}
	public void changeAxisStart(int axis) {
		currentPosition = axis;
	}

	public void changeOrder(int newPosition) {
		int x = order.get(currentPosition);

		order.remove(currentPosition);
		order.add(newPosition, x);
	}

}
