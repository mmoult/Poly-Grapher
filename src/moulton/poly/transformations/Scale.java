package moulton.poly.transformations;

import java.awt.geom.Point2D;
import java.util.List;

public class Scale extends Transformation {
	
	public Scale(double factor) {
		super(factor);
	}
	public Scale(double xFactor, double yFactor) {
		super(xFactor, yFactor);
	}

	@Override
	public void execute(List<Point2D.Double> vertices) {
		double xFactor, yFactor;
		xFactor = params[0];
		if(params.length == 1) {
			yFactor = params[0];
		}else {
			yFactor = params[1];
		}
		
		for(Point2D.Double pt: vertices) {
			pt.x *= xFactor;
			pt.y *= yFactor;
		}
	}

}
