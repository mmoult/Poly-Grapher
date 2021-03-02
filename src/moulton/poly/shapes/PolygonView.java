package moulton.poly.shapes;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import moulton.poly.comps.CoordControl;
import moulton.poly.main.Menu;
import moulton.scalable.containers.Panel;
import moulton.scalable.draggables.DraggableComponent;
import moulton.scalable.visuals.ImageButton;

public class PolygonView extends ImageButton implements DraggableComponent {
	private static final double SHOULDER_DIVISOR = 5;
	private double lowX=-1, lowY=-1, hiX=1, hiY=1;
	private double xScale=0, yScale=0;
	private boolean recenter = false;
	private int mouseX = -1, mouseY = -1;
	private String mx, my;
	private double[] selected = null;
	
	private boolean fixedXAxis = false;
	private boolean fixedYAxis = false;
	private boolean invertYAxis = false;
	private boolean createOnClick = true;
	
	private CoordControl coordControl = null;
	private ShapeListPanel shapes;

	public PolygonView(Menu menu, ShapeListPanel shapes, Panel parent, String x, String y, String w, String h) {
		super("", null, parent, x, y, w, h, Color.WHITE);
		this.shapes = shapes;
		
		clickAction = () -> {
			if(createOnClick) {
				//use the menu here. Having the shape list is nice, but insufficient. 
				//  we need to know whether the shape list is up or the vertex list, and act accordingly.
				//	only menu would know that.
				menu.createVertexAt(Double.parseDouble(mx), Double.parseDouble(my));
			}
			return true;
		};
	}
	
	public void setCoordControl(CoordControl cc) {
		this.coordControl = cc;
		if(coordControl != null) {
			coordControl.hiX.setMessage(Double.toString(hiX));
			coordControl.hiY.setMessage(Double.toString(hiY));
			coordControl.lowX.setMessage(Double.toString(lowX));
			coordControl.lowY.setMessage(Double.toString(lowY));
		}
	}
	
	@Override
	public void render(Graphics g, int xx, int yy, int ww, int hh) {
		//create the image
		Rectangle coords = this.getRenderRect(xx, yy, ww, hh, width, height);
		this.image = new BufferedImage(coords.width, coords.height, BufferedImage.TYPE_INT_RGB);
		Graphics g2 = image.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, coords.width, coords.height);
		
		LinkedList<Shape> shapeList = shapes.getShapes();
		if(!shapeList.isEmpty()) {
			//find the boundaries, if necessary
			if(recenter) {
				double lowx=this.lowX, lowy=this.lowY, hix=this.hiX, hiy=this.hiY;
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
				//if the axis is fixed, reset this values to how they were before
				if(fixedXAxis) {
					this.lowX = lowx;
					this.hiX = hix;
				}
				if(fixedYAxis) {
					this.lowY = lowy;
					this.hiY = hiy;
				}
				if(coordControl != null) {
					coordControl.hiX.setMessage(Double.toString(hiX));
					coordControl.hiY.setMessage(Double.toString(hiY));
					coordControl.lowX.setMessage(Double.toString(lowX));
					coordControl.lowY.setMessage(Double.toString(lowY));
				}
			}
			
			//now we can start drawing the shapes
			xScale = fixedXAxis? this.xScale : coords.width/(hiX-lowX);
			yScale = fixedYAxis? this.yScale : coords.height/(hiY-lowY);
			for(Shape shape: shapeList) {
				LinkedList<double[]> vertices = shape.getVertices();
				int[] oldVertex = null;
				int[] firstVertex = null;
				for(double[] vertex: vertices) {
					int x1, y1;
					if(!invertYAxis) {
						x1 = (int)((vertex[0] - lowX)*xScale);
						y1 = (int)((vertex[1] - lowY)*yScale);
					}else {
						x1 = (int)((vertex[0] - lowX)*xScale);
						y1 = (int)((hiY - vertex[1])*yScale);
					}
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
			//draw the selected
			if(selected != null) {
				g2.setColor(Color.BLACK);
				int sx = (int)((selected[0] - lowX)*xScale);
				int sy = !invertYAxis? ((int)((selected[1] - lowY)*yScale)): ((int)((hiY - selected[1])*yScale));
				g2.drawOval(sx-5, sy-5, 10, 10);
			}
		}
		recenter = false;
		
		super.render(g, xx, yy, ww, hh); //finally render the image
		
		//write out the mouse coordinates
		if(mouseX > -1 && mouseY > -1 && lowX!=hiX && lowY!=hiY) {
			if(xScale == 0)
				xScale = coords.width/(hiX-lowX);
			if(yScale == 0)
				yScale = coords.height/(hiY-lowY);
			
			g.setColor(new Color(100,100,100,150)); //to the translucent gray
			mx = limitNumber(lowX + mouseX/xScale, 5);
			if(!invertYAxis)
				my = limitNumber(lowY + mouseY/yScale, 5);
			else
				my = limitNumber(hiY - mouseY/yScale, 5);
			String mouseXY = "(" + mx + ", " + my + ")";
			FontMetrics fm = g.getFontMetrics();
			g.fillRect(coords.x+coords.width-fm.stringWidth(mouseXY), coords.y, fm.stringWidth(mouseXY), fm.getHeight());
			g.setColor(Color.BLACK);
			g.drawString(mouseXY, coords.x+coords.width-fm.stringWidth(mouseXY), coords.y + fm.getHeight()-fm.getDescent());
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
		if(!fixedXAxis) {
			this.lowX = lowX;
			if(coordControl != null)
				coordControl.lowX.setMessage(Double.toString(lowX));			
		}
	}
	public void setLowY(double lowY) {
		if(!fixedYAxis) {
			this.lowY = lowY;
			if(coordControl != null)
				coordControl.lowY.setMessage(Double.toString(lowY));
		}
	}
	public void setHighX(double highX) {
		if(!fixedXAxis) {
			hiX = highX;
			if(coordControl != null)
				coordControl.hiX.setMessage(Double.toString(hiX));
		}
	}
	public void setHighY(double highY) {
		if(!fixedYAxis) {
			hiY = highY;
			if(coordControl != null)
				coordControl.hiY.setMessage(Double.toString(hiY));
		}
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
		if(num == 0)
			return "0";
		if(num == Double.POSITIVE_INFINITY)
			return (number + "infinity");
		
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
	
	public boolean toggleXFixed() {
		fixedXAxis = !fixedXAxis;
		return fixedXAxis;
	}
	public boolean toggleYFixed() {
		fixedYAxis = !fixedYAxis;
		return fixedYAxis;
	}
	public boolean toggleInvertYAxis() {
		invertYAxis = !invertYAxis;
		return invertYAxis;
	}
	public boolean toggleClickAction() {
		createOnClick = !createOnClick;
		return createOnClick;
	}
	
	public double[] getPerspective() {
		return new double[]{lowX, lowY, hiX, hiY};
	}

	@Override
	public double[] drag(double dx, double dy) {
		if(!createOnClick) {
			if(xScale != 0 && !fixedXAxis) {
				double change = dx/xScale;
				setLowX(lowX - change);
				setHighX(hiX - change);
			}
			if(yScale != 0 && !fixedYAxis) {
				double change = dy/yScale * (invertYAxis?-1:1);
				setLowY(lowY - change);
				setHighY(hiY - change);
			}
			return new double[] {dx, dy};
		}
		return new double[2];
	}
	
	public void select(double x, double y) {
		selected = new double[] {x, y};
	}
	public void deselect() {
		selected = null;
	}
	
}
