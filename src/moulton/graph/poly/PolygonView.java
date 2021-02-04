package moulton.graph.poly;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import moulton.scalable.containers.Panel;
import moulton.scalable.visuals.View;

public class PolygonView extends View{
	private static final double SHOULDER_DIVISOR = 5;
	private double lowX=0, lowY=0, hiX=0, hiY=0;
	private double xScale=0, yScale=0;
	private boolean recenter = true;
	private int mouseX = -1, mouseY = -1;
	
	private CoordControl coordControl = null;
	private ShapeListPanel shapes;

	public PolygonView(ShapeListPanel shapes, Panel parent, String x, String y, String w, String h) {
		super(null, parent, x, y, w, h);
		this.shapes = shapes;
	}
	
	public void setCoordControl(CoordControl cc) {
		this.coordControl = cc;
	}
	
	@Override
	public void render(Graphics g, int xx, int yy, int ww, int hh) {
		//create the image
		int[] coords = this.getRectRenderCoords(xx, yy, ww, hh, width, height);
		this.image = new BufferedImage(coords[2],coords[3],BufferedImage.TYPE_INT_RGB);
		Graphics g2 = image.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, coords[2], coords[3]);
		
		LinkedList<Shape> shapeList = shapes.getShapes();
		if(!shapeList.isEmpty()) {
			//find the boundaries, if necessary
			if(recenter) {
				boolean start = true;
				for(Shape shape: shapeList) {
					for(double[] vertex: shape.getVertices()) {
						if(start) { //init the bound values
							lowX = vertex[0];
							lowY = vertex[1];
							hiX = vertex[0];
							hiY = vertex[1];
							start = false;
							continue;
						}
						//otherwise see if the new values are new bounds
						if(vertex[0] < lowX)
							lowX = vertex[0];
						else if(vertex[0] > hiX)
							hiX = vertex[0];
						if(vertex[1] < lowY)
							lowY = vertex[1];
						else if(vertex[1] > hiY)
							hiY = vertex[1];
					}
				}
				//now that we have the exact boundaries, we add a shoulder to all four sides
				//we don't want vertices on the edge of the screen
				double xShoulder = 1;
				double yShoulder = 1;
				if(hiX-lowX != 0)
					xShoulder = (hiX-lowX)/SHOULDER_DIVISOR;
				if(hiY-lowY != 0)
					yShoulder = (hiY-lowY)/SHOULDER_DIVISOR;
				lowX -= xShoulder;
				lowY -= yShoulder;
				hiX += xShoulder;
				hiY += yShoulder;
				recenter = false;
				if(coordControl != null) {
					coordControl.hiX.setMessage(""+hiX);
					coordControl.hiY.setMessage(""+hiY);
					coordControl.lowX.setMessage(""+lowX);
					coordControl.lowY.setMessage(""+lowY);
				}
			}
			
			//now we can start drawing the shapes
			xScale = ww/(hiX-lowX);
			yScale = hh/(hiY-lowY);
			for(Shape shape:shapeList) {
				LinkedList<double[]> vertices = shape.getVertices();
				int[] oldVertex = null;
				int[] firstVertex = null;
				for(double[] vertex:vertices) {
					int x1 = (int)((vertex[0] - lowX)*xScale);
					int y1 = (int)((vertex[1] - lowY)*yScale);
					if(oldVertex != null) { //draw a line from here to the next
						g2.setColor(shape.getColor());
						g2.drawLine(oldVertex[0], oldVertex[1], x1, y1);
					}else
						firstVertex = new int[] {x1, y1};
					g2.setColor(Color.BLACK);
					g2.fillOval(x1-2, y1-2, 4, 4);
					oldVertex = new int[] {x1, y1};
				}
				//only if there are at least 2 vertices
				if(firstVertex != null && (oldVertex[0]!=firstVertex[0] || oldVertex[1]!=firstVertex[1])) {
					g2.setColor(shape.getColor());
					g2.drawLine(oldVertex[0], oldVertex[1], firstVertex[0], firstVertex[1]);
				}
			}
		}
		
		super.render(g, xx, yy, ww, hh); //finally render the image
		
		//write out the mouse coordinates
		if(mouseX > -1 && mouseY > -1 && lowX!=hiX && lowY!=hiY) {
			g.setColor(new Color(100,100,100,150)); //to the translucent gray
			String mx = limitNumber(lowX + mouseX/xScale, 5);
			String my = limitNumber(lowY + mouseY/yScale, 5);
			String mouseXY = "(" + mx + ", " + my + ")";
			FontMetrics fm = g.getFontMetrics();
			g.fillRect(coords[0], coords[1]+coords[3]-fm.getHeight(), fm.stringWidth(mouseXY), fm.getHeight());
			g.setColor(Color.BLACK);
			g.drawString(mouseXY, coords[0], coords[1]+coords[3]-fm.getDescent());
		}
	}
	
	public void setShapes(ShapeListPanel shapes) {
		this.shapes = shapes;
	}
	
	public void recenter() {
		this.recenter = true;
	}
	
	public void informMouseXY(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}
	
	public void setLowX(double lowX) {
		this.lowX = lowX;
	}
	public void setLowY(double lowY) {
		this.lowY = lowY;
	}
	public void setHighX(double highX) {
		hiX = highX;
	}
	public void setHighY(double highY) {
		hiY = highY;
	}
	
	private String limitNumber(double value, int chars) {
		//we want to have a max of 5 characters, unless the numbers are too big
		//we won't count - as a character for this
		if(value < 0)
			chars++;
		String num = doubleToString(value);
		if(num.length() >= chars) {
			//check to cut out digits
			int decimalPlace = num.indexOf('.');
			if(decimalPlace != -1) { //there is a decimal
				if(decimalPlace < chars) { //there are unneeded digits after the decimal
					//begin with the too many, but also remove redundant
					int newEnd = chars-1;
					for(; newEnd>-1; newEnd--) {
						char c = num.charAt(newEnd);
						if(c == '.')
							break;
						if(c > 48 && c < 58) { //a non-zero number
							newEnd++;
							break;
						}
					}
					num = num.substring(0, newEnd); //strip off the too many
				}
			}
		}
		return num;
	}
	
	private String doubleToString(double num) {
		final int PRECISION_OF_DOUBLE = 15;
		String number = "";
		if(num < 0) { //take care of the sign
			number = "-";
			num *= -1;
		}
		//build up to find how large the number is
		int powerOfTen = 0;
		while(num >= Math.pow(10.0, powerOfTen)) {
			powerOfTen++;
		}
		powerOfTen--; //it is too large after the loop
		int beginPwr = 0;
		boolean beginSet = false;
		if(powerOfTen > -1) {
			beginPwr = powerOfTen;
			beginSet = true;
		}
		for(; powerOfTen > -1; powerOfTen--) {
			int multiple = (int)(num / Math.pow(10.0, powerOfTen));
			if(multiple > 0)
				num -= multiple * Math.pow(10.0, powerOfTen);
			number += (char)(multiple + '0');
		}

		//now do less than 0
		if(num > 0) {
			if(number.isEmpty() || number.equals("-"))
				number += "0.";
			else
				number += '.';
		}
		boolean roundingError = false;
		while(num > 0) {
			if(powerOfTen < beginPwr - PRECISION_OF_DOUBLE) { //if there is still more even after precision shouldn't allow
				roundingError = true;
				break;
			}
			int multiple = (int)(num / Math.pow(10.0, powerOfTen));
			if(multiple > 0) {
				if(!beginSet) {
					beginPwr = powerOfTen;
					beginSet = true;
				}
				num -= multiple * Math.pow(10.0, powerOfTen);
			}
			number += (char)(multiple + '0');
			powerOfTen--;
		}

		if(roundingError && number.length() > 1) { //if there was a computational error, round back
			//round the 9s up and remove any zeros
			char lastDigit = number.charAt(number.length() - 1);
			if(lastDigit == '9') {
				int place = number.length() - 2;
				while(place > 0 && (number.charAt(place) == '9'|| number.charAt(place) == '.'))
					place--;
				char thisDigit = number.charAt(place);
				number = number.substring(0, place) + (++thisDigit);
			}else if(lastDigit == '0') {
				int place = number.length() - 2;
				while(place > 0 && number.charAt(place) == '0')
					place--;
				number = number.substring(0, place+1);
			}
		}
		return number;
	}
	
}
