package infovis.paracoords;

import infovis.scatterplot.Model;
import infovis.scatterplot.Data; 
import infovis.scatterplot.Range;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class View extends JPanel {
	private Model model = null;
	private Rectangle2D markerRectangle = new Rectangle2D.Double(0,0,0,0);
	private int lineLength = 0; 
	private int lineDistance = 0;
	private int elementDistance = 0;
	private int borderSize = 100;  
	private int textSpace = 10;

	@Override
	public void paint(Graphics g) {
		calculateLines(); 
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);

		g2d.clearRect(0, 0, getWidth(), getHeight());
		int[] yPoints = new int[model.getDim()];
		int[] xPoints = new int[model.getDim()]; 

		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(Color.BLACK);

		// draw Labels
		for (int i = 0; i < model.getDim(); i++) {
			int xPosition = i*lineDistance+borderSize;
			String labelx = model.getLabels().get(i);
			g2d.drawString(labelx, xPosition-labelx.length()*2, borderSize/2);}

		for (int k = 0; k < model.getList().size(); k++ ){
			int yPosition= borderSize + elementDistance -textSpace;
			elementDistance += lineLength/model.getList().size();
			String labely = model.getList().get(k).getLabel();
			g2d.drawString(labely, textSpace,  yPosition);
		}

		//draw Points in current Grid
		for (int k = 0; k < model.getList().size(); k++ ){
			for (int i =0; i<model.getDim(); i++) {
				int xPosition = i*lineDistance+borderSize;
				g2d.drawLine((int) xPosition, borderSize, (int) xPosition, getHeight()-borderSize);


				int yPosition = calculatePosY(i, k);

				xPoints[i] = xPosition; 

				yPoints[i] = yPosition;




				/*Point2D point = calculatePoint(posX, posY, i, j, k);
				//Point2D point = giveMePoint(i, j, k);
				colorPoint(point, k); //TODO: this should probably happen somewhere else
				g2d.setColor(model.getList().get(k).getColor());
				g2d.drawRect((int)(point.getX()),(int)(point.getY()), pointSize, pointSize);*/
			}
			g2d.setColor(Color.GRAY);
			g2d.drawPolyline(xPoints, yPoints, model.getDim());
			g2d.setColor(Color.black);
		}


	}

	private int calculatePosition(int i, int size) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int calculatePosY(int i, int k) {
		double yValue = model.getList().get(k).getValues()[i];
		double maxY = model.getRanges().get(i).getMax();
		double minY = model.getRanges().get(i).getMin();
		//int yPoint = (int)(y + borderSizeIntern + (boxSize-2*borderSizeIntern)*yPos);
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
	//calculating the appropriate BoxSize for the current window Size
	public void calculateLines() {
		lineLength = getHeight()-2*borderSize; 
		lineDistance = (getWidth()-2*borderSize)/(model.getDim()-1);
		elementDistance = lineLength/model.getList().size();
	}
}
