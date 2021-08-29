package moulton.poly.transformations;

import java.awt.geom.Point2D;
import java.util.List;

public abstract class Transformation {
	public final double[] params;
	
	public Transformation(double ...params) {
		this.params = params;
	}
	
	//https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/transform
	public abstract void execute(List<Point2D.Double> vertices);
}
