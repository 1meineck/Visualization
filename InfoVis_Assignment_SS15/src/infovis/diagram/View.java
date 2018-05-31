package infovis.diagram;

import infovis.diagram.elements.Element;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JPanel;



public class View extends JPanel{
	private Model model = null;
	private Color color = Color.BLUE;
	private double scale = 1;
	private double translateX= 0;
	private double translateY=0;
	private Rectangle2D marker = new Rectangle2D.Double();
	private Rectangle2D overviewRect = new Rectangle2D.Double();
	private double overviewScale = 0.25; 
	private double overviewTranslateX = 0;
	private double overviewTranslateY = 0; 
	
	

	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	
	public void paint(Graphics g) {
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2D.clearRect(0, 0, getWidth(), getHeight());
		g2D.translate(translateX, translateY);
		g2D.scale(scale, scale);
		paintDiagram(g2D);
		
		g2D.scale(1/scale, 1/scale);
		g2D.translate(-translateX, -translateY);
		
		g2D.translate(overviewTranslateX, overviewTranslateY);
		g2D.scale(overviewScale, overviewScale);

		g2D.clearRect(0, 0, getWidth(), getHeight());
		overviewRect.setRect(0, 0, getWidth(), getHeight());
		g2D.draw(overviewRect);
		paintDiagram(g2D);
		
	
		g2D.scale(1/scale, 1/scale);
		g2D.translate(-translateX, -translateY);
		
		// Draw marker
		marker.setRect(0,0, getWidth(), getHeight());
		g2D.draw(marker);		
	}

	private void paintDiagram(Graphics2D g2D){
		for (Element element: model.getElements()){
			element.paint(g2D);
		}
	}
	
	public void setScale(double scale) {
		this.scale = scale;
	}
	public double getScale(){
		return scale;
	}
	public double getTranslateX() {
		return translateX;
	}
	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}
	public double getTranslateY() {
		return translateY;
	}
	public void setTranslateY(double tansslateY) {
		this.translateY = tansslateY;
	}
	public void updateTranslation(double x, double y){
		setTranslateX(x);
		setTranslateY(y);
	}	
	public void updateMarker(int x, int y){
		marker.setRect(x, y, 16, 10);
	}
	public Rectangle2D getMarker(){
		return marker;
	}
	public boolean overviewContains(double x, double y){
		return overviewRect.contains(x, y);
	}

	public double getOverviewScale() {
		return overviewScale;
	}
	
	

	public double toModelX(double x){
		return (x-translateX)/scale;
	}
	public double toModelY(double y){
		return (y-translateY)/scale;
	}
	public double toViewX(double x) {
		return x*scale + translateX;
	}
	public double toViewY(double y) {
		return y*scale + translateY;
	}
	
	
	
	public double getOverviewTranslateX() {
		return overviewTranslateX;
	}
	public void setOverviewTranslateX(double overviewTranslateX) {
		this.overviewTranslateX = overviewTranslateX;
	}
	public double getOverviewTranslateY() {
		return overviewTranslateY;
	}
	public void setOverviewTranslateY(double overviewTranslateY) {
		this.overviewTranslateY = overviewTranslateY;
	}
	
	public double toOverviewX(double x) {
		return (x - overviewTranslateX)/overviewScale;
	}
	public double toOverviewY(double y) {
		return (y - overviewTranslateY)/overviewScale;
	}
	
	
}
 