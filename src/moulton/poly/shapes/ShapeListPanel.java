package moulton.poly.shapes;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;

import moulton.poly.menu.Menu;
import moulton.scalable.clickables.Button;
import moulton.scalable.clickables.TouchResponsiveComponent;
import moulton.scalable.containers.ListPanel;
import moulton.scalable.containers.Panel;
import moulton.scalable.utils.GridFormatter;

public class ShapeListPanel extends ListPanel {
	private Font font;
	private LinkedList<Shape> shapeList = new LinkedList<>();
	
	private Menu menu;
	private LinkedList<TouchResponsiveComponent> touchTemp = new LinkedList<>();

	public ShapeListPanel(Menu menu, Panel parent, String x, String y, String shownWidth, String shownHeight, Font font, Color color) {
		super("25", parent, x, y, shownWidth, shownHeight, "0", color);
		this.font = font;
		this.menu = menu;
		updateList();
	}
	
	public void addShape(Shape newShape) {
		shapeList.add(newShape);
	}
	
	private void addShapeToGUI(Shape shape, int max) {
		int addHeight = this.grid.getGridHeight();
		Panel newShapePanel = new Panel(this, 0, addHeight, null);
		addTouchComp(new Button("shapeTitle:"+addHeight,shape.getTitle(),newShapePanel,0,0,font,Color.LIGHT_GRAY));
		Button shapeUp = new Button("shapeUp:"+addHeight,"^",newShapePanel,1,0,font,Color.LIGHT_GRAY);
		addTouchComp(shapeUp);
		Button shapeDown = new Button("shapeDown:"+addHeight,"v",newShapePanel,2,0,font,Color.LIGHT_GRAY);
		addTouchComp(shapeDown);
		if(addHeight == 0)
			shapeUp.setEnabled(false);
		if(addHeight + 1 == max)
			shapeDown.setEnabled(false);
		addTouchComp(new Button("shapeDelete:"+addHeight,"X",newShapePanel,3,0,font,Color.LIGHT_GRAY));
		newShapePanel.getGridFormatter().specifyColumnWeight(0, 2.0);
	}
	
	public void removeShape(Shape toRemove) {
		shapeList.remove(toRemove);
	}
	
	public void updateList() {
		//remove all components from the touch list
		clearTouchList();
		
		//throw away the old grid and create a new one
		grid = new GridFormatter();
		for(Shape shape: shapeList)
			addShapeToGUI(shape, shapeList.size());
		
		addTouchComp(new Button("newShape", "New Shape", this, 0, shapeList.size(), font, Color.RED));
	}
	
	public Shape getShape(int index) {
		return shapeList.get(index);
	}
	
	public LinkedList<Shape> getShapes() {
		return shapeList;
	}
	
	public void clear() {
		shapeList.clear();
	}
	
	public void shiftShape(int before, int after) {
		if(after == before)
			return;
		Shape temp = shapeList.remove(before);
		shapeList.add(after, temp);
	}
	
	private void addTouchComp(TouchResponsiveComponent comp) {
		touchTemp.add(comp);
		menu.addTouchComponent(comp);
	}
	
	public void clearTouchList() {
		for(TouchResponsiveComponent comp: touchTemp)
			menu.removeTouchComponent(comp);
		touchTemp.clear();
	}

}
