package moulton.poly.shapes;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;

public class Shape {
	private String title;
	private LinkedList<double[]> vertices = new LinkedList<>();
	private Color color;
	
	public Shape(String title, Color color, double[][] vertices) {
		this.title = title;
		this.color = color;
		if(vertices != null)
			this.vertices.addAll(Arrays.asList(vertices));
	}
	
	private Shape(String title, Color color, LinkedList<double[]> vertices) {
		this.title = title;
		this.color = color;
		this.vertices = vertices;
	}
	
	public Shape clone() {
		LinkedList<double[]> newVertices = new LinkedList<>();
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
	
	
	public LinkedList<double[]> getVertices() {
		return vertices;
	}
}
