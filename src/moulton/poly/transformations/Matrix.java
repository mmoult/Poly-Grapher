package moulton.poly.transformations;

import java.awt.geom.Point2D.Double;
import java.util.List;

public class Matrix extends Transformation {
	
	public Matrix(double northwest, double north, double northeast, double west, double center, double east) {
		super(northwest, north, northeast, west, center, east);
	}

	@Override
	public void execute(List<Double> vertices) {
		double a = params[0];
		double b = params[3];
		double c = params[1];
		double d = params[4];
		double e = params[2];
		double f = params[5];
		
		for(Double pt: vertices) {
			//[a c e]
			//[b d f]
			//[0 0 1]
			double x = a * pt.x + c * pt.y + e;
			double y = b * pt.x + d * pt.y + f;
			pt.x = x;
			pt.y = y;
		}
	}
	

}
