package moulton.poly.transformations;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.List;

public class Translate extends Transformation {

	public Translate(double dx, double dy) {
		super(dx, dy);
	}
	public Translate(double dx) {
		super(dx);
	}

	@Override
	public void execute(List<Double> vertices) {
		for(Point2D.Double pt: vertices) {
			pt.x += params[0];
			if(params.length > 1)
				pt.y += params[1];
		}

	}

}
