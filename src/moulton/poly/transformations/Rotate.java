package moulton.poly.transformations;

import java.awt.geom.Point2D.Double;
import java.util.List;

public class Rotate extends Transformation {
	
	public Rotate(double degrees) {
		super(degrees);
	}
	
	public Rotate(double degrees, double x, double y) {
		super(degrees, x, y);
	}

	@Override
	public void execute(List<Double> vertices) {
		double originX = 0;
		double originY = 0;
		if(params.length > 1) {
			originX = params[1];
			originY = params[2];
		}
		Double origin = new Double(originX, originY);
		
		//for each point
		for(Double pt: vertices) {
			//if there is no distance between the origin and the point, do nothing
			if(pt.x == originX && pt.y == originY)
				continue;
			
			//find the current angle from the origin
			double hyp = Math.sqrt(Math.pow(originX-pt.x, 2) + Math.pow(originY-pt.y, 2));
			double rads = getRadiansSlope(origin, pt);
			double rot = rads + Math.toRadians(params[0]);
			
			//now apply the rotation to x and y
			pt.x = hyp*Math.cos(rot) + originX;
			pt.y = hyp*Math.sin(rot) + originY;
		}

	}
	
	private double getRadiansSlope(Double from, Double to) {
		boolean riseOverRun = from.x != to.x;
		double slope;
		if(riseOverRun)
			slope = (from.y - to.y) / (from.x - to.x); 
		else
			slope = (from.x - to.x) / (from.y - to.y); 
		
		if(riseOverRun) {
			double rads = Math.atan(slope);
			//check for direction
			if(from.x > to.x)
				rads += Math.PI;
			return rads;
		}
		
		//not rise over run, so m=0 is undefined norm slope
		double rads;
		if(slope == 0) {
			rads = -Math.PI/2;
			if(from != null && to != null && from.y < to.y)
				rads = Math.PI/2;
			return rads;
		}else
			rads = Math.atan(1/slope);
		if(from != null && to != null) {
			//check for direction
			if(to.x < from.x)
				rads += Math.PI;
		}
		return rads;
	}

}
