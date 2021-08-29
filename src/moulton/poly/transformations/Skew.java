package moulton.poly.transformations;

import java.awt.geom.Point2D.Double;
import java.util.List;

public class Skew extends Transformation {
	protected boolean xAxis;
	
	public Skew(double skewDeg, boolean xAxis) {
		super(skewDeg);
		this.xAxis = xAxis;
	}

	@Override
	public void execute(List<Double> vertices) {
		Matrix skew;
		if(xAxis)
			// [[ 1	tan(skew)	0]
			//  [ 0		1		0]
			//  [ 0		0		1]]
			skew = new Matrix(1, Math.tan(Math.toRadians(params[0])), 0, 0, 1, 0);
		else
			// [[	1		0	0]
			//  [ tan(skew)	1	0]
			//  [ 	0		0	1]]
			skew = new Matrix(1, 0, 0, Math.tan(Math.toRadians(params[0])), 1, 0);
		skew.execute(vertices);
	}

}
