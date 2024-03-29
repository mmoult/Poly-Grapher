package moulton.poly.shapes;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import moulton.poly.menu.CoordControl;
import moulton.poly.menu.Menu;
import moulton.poly.menu.NumberControl;
import moulton.scalable.containers.Panel;
import moulton.scalable.draggables.DraggableComponent;
import moulton.scalable.draggables.ScrollBar;
import moulton.scalable.draggables.ScrollableComponent;
import moulton.scalable.visuals.ImageButton;

public class PolygonView extends ImageButton implements DraggableComponent, ScrollableComponent {
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
	private boolean showAxes = true;
	
	private CoordControl coordControl = null;
	private ShapeListPanel shapes;
	
	private static final int ZOOM_OFFS = 100;
	private static final int ZOOM_INVERSE_SPEED = 200;
	private ScrollBar zoomBar = new ScrollBar(true, null, 0, 0, Color.WHITE);
	public static final int PRECISION_DIGITS = 5;
	

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
		zoomBar.setOffsets(ZOOM_OFFS, 1, ZOOM_OFFS/2);
		//we need to render our zoom bar once so that it has a baseline for scrolling sizes
		Graphics g = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
		zoomBar.render(g, 0, 0, 0, 500);
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
		
		//do some book keeping, like adjusting the scale for drawn size and checking for zoom
		//don't make any scale modifications if re-centering will trash them anyways
		if(!recenter) {
			//check for any scroll zoom
			int diff = zoomBar.getOffset()-(ZOOM_OFFS/2);
			double deltaX = (diff*(hiX-lowX))/ZOOM_INVERSE_SPEED;
			double deltaY = (diff*(hiY-lowY))/ZOOM_INVERSE_SPEED;
			if(diff != 0) {
				if(!fixedXAxis) {
					setHighX(hiX + deltaX);
					setLowX(lowX - deltaX);
				}if(!fixedYAxis) {
					setHighY(hiY + deltaY);
					setLowY(lowY - deltaY);
				}
			}
			
			xScale = fixedXAxis? this.xScale : coords.width/(hiX-lowX);
			yScale = fixedYAxis? this.yScale : coords.height/(hiY-lowY);
		}
		//regardless, reset the zoom
		zoomBar.setOffset(ZOOM_OFFS/2);
		
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
				setHighX(hiX);
				setHighY(hiY);
				setLowX(lowX);
				setLowY(lowY);
				
				//set the scales from the new perspective
				xScale = fixedXAxis? this.xScale : coords.width/(hiX-lowX);
				yScale = fixedYAxis? this.yScale : coords.height/(hiY-lowY);
			}
			
			drawAxes(g2, coords);
			
			//now we can start drawing the shapes
			for(Shape shape: shapeList) {
				List<double[]> vertices = shape.getVertices();
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
		}else
			drawAxes(g2, coords);
		recenter = false;
		
		super.render(g, xx, yy, ww, hh); //finally render the image
		
		//write out the mouse coordinates
		if(mouseX > -1 && mouseY > -1 && lowX!=hiX && lowY!=hiY) {
			if(xScale == 0)
				xScale = coords.width/(hiX-lowX);
			if(yScale == 0)
				yScale = coords.height/(hiY-lowY);
			
			g.setColor(new Color(100,100,100,150)); //to the translucent gray
			mx = NumberControl.limitNumber(lowX + mouseX/xScale, PRECISION_DIGITS);
			if(!invertYAxis)
				my = NumberControl.limitNumber(lowY + mouseY/yScale, PRECISION_DIGITS);
			else
				my = NumberControl.limitNumber(hiY - mouseY/yScale, PRECISION_DIGITS);
			String mouseXY = "(" + mx + ", " + my + ")";
			FontMetrics fm = g.getFontMetrics();
			g.fillRect(coords.x+coords.width-fm.stringWidth(mouseXY), coords.y, fm.stringWidth(mouseXY), fm.getHeight());
			g.setColor(Color.BLACK);
			g.drawString(mouseXY, coords.x+coords.width-fm.stringWidth(mouseXY), coords.y + fm.getHeight()-fm.getDescent());
		}
	}
	
	private void drawAxes(Graphics g, Rectangle coords) {
		if(showAxes) {
			int lineLength = 5;
			
			g.setColor(new Color(90, 90, 90));
			if(lowY <= 0 && hiY >= 0) {
				int y;
				if(!invertYAxis)
					y = (int)((0 - lowY)*yScale);
				else
					y = (int)((hiY - 0)*yScale);
				
				for(int i=0; i<coords.width; i+=lineLength*2)
					g.drawLine(i, y, i+lineLength, y);
			}
			if(lowX <= 0 && hiX >= 0) {
				int x = (int)((0 - lowX)*xScale);
				
				for(int i=0; i<coords.height; i+=lineLength*2)
					g.drawLine(x, i, x, i+lineLength);
			}				
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
				coordControl.lowX.setMessage(NumberControl.doubleToString(lowX));			
		}
	}
	public void setLowY(double lowY) {
		if(!fixedYAxis) {
			this.lowY = lowY;
			if(coordControl != null)
				coordControl.lowY.setMessage(NumberControl.doubleToString(lowY));
		}
	}
	public void setHighX(double highX) {
		if(!fixedXAxis) {
			hiX = highX;
			if(coordControl != null)
				coordControl.hiX.setMessage(NumberControl.doubleToString(hiX));
		}
	}
	public void setHighY(double highY) {
		if(!fixedYAxis) {
			hiY = highY;
			if(coordControl != null)
				coordControl.hiY.setMessage(NumberControl.doubleToString(hiY));
		}
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
	public boolean toggleShowAxes() {
		showAxes = !showAxes;
		return showAxes;
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
	public void select(double[] xy) {
		selected = xy;
	}
	public void deselect() {
		selected = null;
	}

	@Override
	public ScrollBar getWidthScrollBar() {
		return null;
	}

	@Override
	public ScrollBar getHeightScrollBar() {
		return zoomBar;
	}

	@Override
	public int[][] getActiveScrollCoordinates() {
		return clickBoundary;
	}
	
}
