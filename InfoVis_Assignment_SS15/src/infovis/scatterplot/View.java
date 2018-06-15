package infovis.scatterplot;

import infovis.debug.Debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

public class View extends JPanel {
	     private Model model = null;
	     private Rectangle2D markerRectangle = new Rectangle2D.Double(0,0,0,0);
	     private int boxSize = 150;
	    
	     // set constant values
	     private int borderSize = 50;
	     private int borderSizeIntern = 10;
	     private int pointSize = 4;
	     private int textSpace = 10;
	     private ArrayList<Point2D> pointList;
	     private ArrayList<Integer> positionList;

		 public Rectangle2D getMarkerRectangle() {
			return markerRectangle;
		}
		 
		@Override
		public void paint(Graphics g) {
			pointList = new ArrayList<Point2D>();
			positionList = new ArrayList<Integer>();
			
	
			//Calculate box size according to the current window size
			calculateBoxSize();
			Graphics2D g2d = (Graphics2D) g;
			
			// reset drawing color before drawing grid
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(1));
			
			//calculate size for the individual points according to box size
			pointSize = (int)(boxSize/30);
			
			// clear drawing space
			g2d.clearRect(0, 0, getWidth(), getHeight());
			
			// Draw grid and points for each combination of dimensions in the model
			for (int i=0; i < model.getDim(); i++) {
					for(int j=0; j<model.getDim(); j++){
						
						
						// calculate current position in the grid
						int posX = boxSize*i+borderSize;
						int posY = boxSize*j+borderSize;
						g2d.setColor(Color.BLACK);
						
						//draw Grid
						g2d.drawRect(posX, posY, boxSize, boxSize);
						
						// draw Labels
						g2d.setStroke(new BasicStroke(1));
						g2d.setColor(Color.BLACK);
						String label = model.getLabels().get(i);
						g2d.drawString(label, posX+textSpace, borderSize - textSpace);
						g2d.rotate(Math.toRadians(90));
						g2d.drawString(label, posX+ textSpace,  -borderSize + textSpace*2);
						g2d.rotate(Math.toRadians(-90)); 
						
						//Calculate Points for current Grid
						for (int k = 0; k < model.getList().size(); k++ )
						{
							// calculate position of the point in the given part of the grid
							Point2D point = calculatePoint(posX, posY, i, j, k);
							// color Point according to marker position
							colorPoint(point, k);
							// add Point to Array
							pointList.add(point);
							// add position in model to array
							positionList.add(k);
							
						}
						
				}
					
			}	
			
			// draw all Points
			for (int k = 0; k < pointList.size(); k++) {
				// draw points with correct color and position
				Point2D point = pointList.get(k);
				int position = positionList.get(k);
				Color color = model.getList().get(position).getColor();
				g2d.setColor(color);
				g2d.drawRect((int)(point.getX()),(int)(point.getY()), pointSize, pointSize);
			}
			
			//draw Marker Rectangle
			g2d.setColor(Color.RED);
			g2d.setStroke(new BasicStroke(2));
			g2d.draw(markerRectangle);
		}
		
		
		public void colorPoint(Point2D point, int k){
			// set Color of entry in the Model to red, 
			//if one of the corresponding points in the grid lies within the marker rectangle
			
			if (markerRectangle.contains(point)){
				model.getList().get(k).setColor(Color.RED);
			}
		}
		
		
		public void resetColor() {
			// set color of all model entries back to black
			for (int k = 0; k < model.getList().size(); k++ ) {
				model.getList().get(k).setColor(Color.BLACK);
			}
		}
		
		
		public void setModel(Model model) {
			this.model = model;
		}
		
		//calculate the appropriate box size for the current window size
		public void calculateBoxSize() {
			int min = Math.min(getWidth(), getHeight());
			int size = (min-2*borderSize)/model.getDim();
			boxSize = size;
			}
		
		
		// Calculate the position of a point within a given grid-cell,
		// 
		public Point2D calculatePoint(int x, int y, int i, int j, int k) {
			
			// get x-value of point from given model-dimension
			double xValue = model.getList().get(k).getValues()[i];
			// get y-value of point from given model-dimension
			double yValue = model.getList().get(k).getValues()[j];
			
			// get maximum values of the current model-dimensions
			double maxX = model.getRanges().get(i).getMax();
			double minX = model.getRanges().get(i).getMin();
			
			// get minimum values of the current model-dimensions
			double maxY = model.getRanges().get(j).getMax();
			double minY = model.getRanges().get(j).getMin();
			
			// calculate point-position within range of the model-dimension
			double xPos = 1- ((maxX-xValue) /(maxX - minX));
			double yPos = 1- ((maxY-yValue) /(maxY - minY));
			
			// calculate point position within given grid-cell
			int xPoint = (int)(x + borderSizeIntern + (boxSize-2*borderSizeIntern)*xPos);
			int yPoint = (int)(y + borderSizeIntern + (boxSize-2*borderSizeIntern)*yPos);
		
			return new Point2D.Double(xPoint, yPoint);		
		}
		
	
			
		
		
}
