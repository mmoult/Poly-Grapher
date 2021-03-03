package moulton.poly.shapes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shape {
	private String title;
	private List<double[]> vertices = new ArrayList<>();
	private Color color;
	
	public Shape(String title, Color color, double[][] vertices) {
		this.title = title;
		this.color = color;
		if(vertices != null)
			this.vertices.addAll(Arrays.asList(vertices));
	}
	
	private Shape(String title, Color color, List<double[]> vertices) {
		this.title = title;
		this.color = color;
		this.vertices = vertices;
	}
	
	public Shape clone() {
		List<double[]> newVertices = new ArrayList<>();
		for(double[] point:vertices)
			newVertices.add(new double[] {point[0], point[1]});
		return new Shape(this.title, this.color, newVertices);
	}
	public void imitate(Shape toImitate) {
		this.title = toImitate.title;
		this.color = toImitate.color;
		this.vertices = toImitate.vertices;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
	
	public List<double[]> getVertices() {
		return vertices;
	}
	
	public double getPerimeter() {
		double perimeter = 0d;
		if(vertices.size() == 0)
			return perimeter;
		
		for(int i=1; i<vertices.size(); i++)
			perimeter += findDistance(i-1, i);
		perimeter += findDistance(vertices.size()-1, 0);
		return perimeter;
	}
	private double findDistance(int vFrom, int vTo) {
		double[] from = vertices.get(vFrom);
		double[] to = vertices.get(vTo);
		return Math.sqrt((Math.pow(to[0]-from[0], 2) + Math.pow(to[1]-from[1], 2)));
	}
}
