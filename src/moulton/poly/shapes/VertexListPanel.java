package moulton.poly.shapes;

import java.awt.Color;
import java.awt.Font;

import moulton.poly.comps.NumberFormat;
import moulton.poly.comps.TouchPanel;
import moulton.poly.main.Menu;
import moulton.scalable.clickables.Button;
import moulton.scalable.clickables.Clickable;
import moulton.scalable.containers.ListPanel;
import moulton.scalable.containers.Panel;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.StaticTextBox;
import moulton.scalable.texts.TextBox;
import moulton.scalable.texts.TextFormat;
import moulton.scalable.utils.GridFormatter;
import moulton.scalable.utils.MenuComponent;

public class VertexListPanel extends ListPanel {
	private final int COMPONENTS_BEFORE_VERTEX_LIST = 3;
	
	private Shape shape; //the shape presently
	private Shape oldShape; //the shape when this was first created
	private Font font = new Font("Arial", Font.PLAIN, 12);
	private Menu menu;
	private StaticTextBox perimeter;

	public VertexListPanel(Menu menu, Shape shape, Panel parent, String x, String y, String shownWidth, String shownHeight, Font font, Color color, boolean defaultShape) {
		super("25", parent, x, y, shownWidth, shownHeight, "0", color);
		this.menu = menu;
		Panel titlePanel = new Panel(this, 0, 0, null);
		new Caption("  Title:",titlePanel,0,0,font,Alignment.LEFT_ALIGNMENT);
		TextBox titleBox = new TextBox("titleBox",shape.getTitle(),titlePanel,1,0,font,Color.LIGHT_GRAY);
		titleBox.setClickSelectsAll(true);
		menu.addTouchResponsiveComponent(titleBox);
		Panel colorPanel = new Panel(this, 0, 1, null);
		new Caption("  Color:",colorPanel,0,0,font,Alignment.LEFT_ALIGNMENT);
		String fullColor = ""+Integer.toHexString(shape.getColor().getRGB());
		if(fullColor.length()>6)
			fullColor = fullColor.substring(2);
		TextBox colorBox = new TextBox("colorBox",fullColor,colorPanel,1,0,font,Color.LIGHT_GRAY);
		colorBox.setTextFormat(new TextFormat() {
			@Override public boolean isValidChar(char c) {
				return ((c>47 && c<58) || (c>64 && c<71) || (c>96 && c<103));
			}
			@Override public String emptyText() {
				return "0";
			}
		});
		colorBox.setClickSelectsAll(true);
		menu.addTouchResponsiveComponent(colorBox);
		Panel perimPanel = new Panel(this, 0, 2, null);
		new Caption("  Perimeter:", perimPanel, 0, 0, font, Alignment.LEFT_ALIGNMENT);
		perimeter = new StaticTextBox("perimBox", "0", perimPanel, 1, 0, font, null);
		menu.addTouchResponsiveComponent(new Button("newVertex", "New Vertex", this, 0, 3, font, Color.RED));
		this.shape = shape;
		this.oldShape = defaultShape? null:shape.clone(); //save the way things are now if the user cancels the edit
		updateVertices();
	}
	
	private void addVertexToGUI(double[] toAdd, int max) {
		int addHeight = this.grid.getGridHeight()-1;
		int vertexNum = addHeight - COMPONENTS_BEFORE_VERTEX_LIST;
		//tell the previous vertex, if any, that its down button should be editable
		TouchPanel newVertex = new TouchPanel(this, 0, addHeight, null);
		menu.addTouchResponsiveComponent(newVertex);
		Button deleteVertex = new Button("deleteVertex:"+vertexNum,"X",newVertex,4,0,font,Color.LIGHT_GRAY);
		Button vertDown = new Button("vertexDown:"+vertexNum,"v",newVertex,3,0,font,Color.LIGHT_GRAY);
		Button vertUp = new Button("vertexUp:"+vertexNum, "^",newVertex,2,0,font,Color.LIGHT_GRAY);
		menu.addTouchResponsiveComponent(deleteVertex);
		menu.addTouchResponsiveComponent(vertDown);
		menu.addTouchResponsiveComponent(vertUp);
		if(vertexNum == 0)
			vertUp.setEnabled(false);
		if(vertexNum == max)
			vertDown.setEnabled(false);
		if(vertexNum == 0 && max < 1)
			deleteVertex.setEnabled(false);
		if(vertexNum > 0) {
			Panel p = (Panel)grid.getAt(0, addHeight-1);
			((Clickable) p.getGridFormatter().getAt(3, 0)).setEnabled(true); //set vertex down
			if(vertexNum != 2)
				((Clickable) p.getGridFormatter().getAt(4, 0)).setEnabled(true); //set delete vertex
		}
		GridFormatter format = newVertex.getGridFormatter();
		format.specifyColumnWeight(0, 2.0);
		format.specifyColumnWeight(1, 2.0);
		TextFormat numberFormat = new NumberFormat();
		new Caption("(",newVertex, "0","height/2",font,Alignment.LEFT_ALIGNMENT);
		new Caption(")",newVertex, "4width/7","height/2",font,Alignment.RIGHT_ALIGNMENT);
		TextBox vertexX = new TextBox("vertexX:"+vertexNum,""+toAdd[0],newVertex,"5","0","2width/7-10","height",font,Color.LIGHT_GRAY);
		vertexX.setTextFormat(numberFormat);
		vertexX.setClickSelectsAll(true);
		menu.addTouchResponsiveComponent(vertexX);
		new Caption(",",newVertex, "2width/7-5","height/2",font,Alignment.CENTER_ALIGNMENT);
		TextBox vertexY = new TextBox("vertexY:"+vertexNum,""+toAdd[1],newVertex,"2width/7","0","2width/7-10", "height", font,Color.LIGHT_GRAY);
		vertexY.setTextFormat(numberFormat);
		vertexY.setClickSelectsAll(true);
		menu.addTouchResponsiveComponent(vertexY);
		
		vertexX.setFormChain(vertexY);
		if(vertexNum > 0) { //link last to now
			Panel last = (Panel)this.getGridFormatter().getAt(0, addHeight-1);
			for(MenuComponent mc : last.getAllHeldComponents()) {
				if(mc instanceof Clickable) {
					Clickable click = (Clickable)mc;
					if(click.getId().indexOf("vertexY:") != -1) {
						click.setFormChain(vertexX);
						break;
					}
				}
			}
		}
		if(vertexNum == max) { //link this to beginning again
			Panel last = (Panel)this.getGridFormatter().getAt(0, COMPONENTS_BEFORE_VERTEX_LIST);
			for(MenuComponent mc : last.getAllHeldComponents()) {
				if(mc instanceof Clickable) {
					Clickable click = (Clickable)mc;
					if(click.getId().indexOf("vertexX:") != -1) {
						vertexY.setFormChain(click);
						break;
					}
				}
			}
		}
		
		newVertex.setTouchAction(() -> {
			if(newVertex.isTouched())
				menu.getPolyView().select(Double.parseDouble(vertexX.getMessage()), Double.parseDouble(vertexY.getMessage()));
			else
				menu.getPolyView().deselect();
			return true;
		});
		
		updatePerimeter();
	}

	public void addVertex(double[] toAdd) {
		shape.getVertices().add(toAdd);
		//update from the panels with the vertices. New vertices added just above the new button
		addVertexToGUI(toAdd, shape.getVertices().size()-1);
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public boolean cancel() {
		if(oldShape == null)
			return false;
		shape.imitate(oldShape);
		return true;
	}
	
	public double[] getVertex(int vertexNum) {
		return shape.getVertices().get(vertexNum);
	}
	
	public void removeVertex(int index) {
		shape.getVertices().remove(index);
		//update from the panels with the vertices
		updateVertices();
	}
	
	public void shiftVertex(int before, int after) {
		if(after == before)
			return;
		double[] temp = shape.getVertices().remove(before);
		shape.getVertices().add(after, temp);
		//update the panels with the vertices
		updateVertices();
	}
	
	private void updateVertices() {
		//remove all the vertices from the GUI, then add back in the ones we need
		while(this.grid.getGridHeight() > COMPONENTS_BEFORE_VERTEX_LIST+1) {
			Panel vertexPanel = (Panel)grid.getAt(0, COMPONENTS_BEFORE_VERTEX_LIST);
			vertexPanel.removeTouchResponsiveness(menu);
			this.removeComponent(COMPONENTS_BEFORE_VERTEX_LIST, true);
		}
		
		//for each vertex in vertex list
		for(double[] vertex: shape.getVertices())
			addVertexToGUI(vertex, shape.getVertices().size()-1);
		
		//update the perimeter field
		updatePerimeter();
	}
	
	public void updatePerimeter() {
		perimeter.setMessage(Double.toString(shape.getPerimeter()));
	}
}