package infovis.diagram.layout;

import infovis.debug.Debug;
import infovis.diagram.Model;
import infovis.diagram.View;
import infovis.diagram.elements.Edge;
import infovis.diagram.elements.Vertex;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Iterator;
import java.util.List;

/*
 * 
 */

public class Fisheye implements Layout{
	private Model model; 
	private Model modelFish;
	private double focusX = 200;
	private double focusY = 200;
	private View view; 
	
	private static final int d = 5;
	

	public void setMouseCoords(int x, int y, View view) {
		focusX = view.toModelX(x); 
		focusY = view.toModelY(y);
	}

	public Model transform(Model model, View view) {
		
		this.view = view;
		this.model = model;
		modelFish = new Model();
				
		List<Vertex> vertices = model.getVertices();
		for (int i = 0; i < vertices.size(); i++) {
			Vertex vertex = vertices.get(i);
			Vertex vertexNew = calcVertex(vertex);

			modelFish.addVertex(vertexNew);
		}
		for(int i = 0; i<model.getEdges().size(); i++) {
			Edge edge = model.getEdges().get(i);
			Vertex vStart = calcVertex(edge.getSource()); 
			Vertex vEnd = calcVertex(edge.getTarget());
			
			Edge edgeNew = new Edge(vStart, vEnd);
			
			modelFish.addEdge(edgeNew);
		}
		
		return modelFish;
	}
	
	private Vertex calcVertex(Vertex vertex) {
		Vertex vertexNew = new Vertex(0, 0, 0, 0);

		double x = vertex.getX(); 
		double y = vertex.getY();
		double width = vertex.getWidth(); 
		double height = vertex.getHeight();
		
		Point2D.Double fishPosition = f1(view, modelFish, x, y);
		vertexNew.setX(fishPosition.x);
		vertexNew.setY(fishPosition.y);
		
		double qX = x + width/2;
		double qY = y + height/2; 
		
		Point2D.Double fishQ = f1(view, modelFish, qX, qY);
		double s = 2 * Math.min(Math.abs(fishQ.x-fishPosition.x), Math.abs(fishQ.y-fishPosition.y));
		
		vertexNew.setWidth(width*s*0.02);
		vertexNew.setHeight(height*s*0.02);
		
		return vertexNew;
	}
	
	private Point2D.Double f1(View view, Model model, double y, double x) {
		
		double dMaxX;
		double dMaxY; 

		if(y<=focusX) {
			dMaxX = 0 - focusX;	
		}else {
			dMaxX = view.getWidth() - focusX;
		}
		if(x<=focusY) {
			dMaxY = 0 - focusY;	
		}else {
			dMaxY = view.getWidth() - focusY;
		}
		
		double dNormX = y - focusX; 
		double dNormY = x - focusY;
		
		double fishX = focusX + g(dNormX/dMaxX)*dMaxX;
		double fishY = focusY + g(dNormY/dMaxY)*dMaxY; 
		
		return new Point2D.Double(fishX, fishY);
		
		
	}
	
	private double g(double x) {
		return ((d+1)*x)/(d*x+1);
		
	}
	
}
