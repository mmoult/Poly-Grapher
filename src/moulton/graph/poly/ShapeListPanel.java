package moulton.graph.poly;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;

import moulton.scalable.clickables.Button;
import moulton.scalable.containers.ListPanel;
import moulton.scalable.containers.Panel;
import moulton.scalable.utils.GridFormatter;

public class ShapeListPanel extends ListPanel {
	private Font font;
	private LinkedList<Shape> shapeList = new LinkedList<>();

	public ShapeListPanel(Panel parent, String x, String y, String shownWidth, String shownHeight, Font font, Color color) {
		super("25", parent, x, y, shownWidth, shownHeight, "0", color);
		this.font = font;
		updateList();
	}
	
	public void addShape(Shape newShape) {
		shapeList.add(newShape);
	}
	
	private void addShapeToGUI(Shape shape, int max) {
		int addHeight = this.grid.getGridHeight();
		Panel newShapePanel = new Panel(this, 0, addHeight, null);
		new Button("shapeTitle:"+addHeight,shape.getTitle(),newShapePanel,0,0,font,Color.LIGHT_GRAY);
		Button shapeUp = new Button("shapeUp:"+addHeight,"^",newShapePanel,1,0,font,Color.LIGHT_GRAY);
		Button shapeDown = new Button("shapeDown:"+addHeight,"v",newShapePanel,2,0,font,Color.LIGHT_GRAY);
		if(addHeight == 0)
			shapeUp.setEditable(false);
		if(addHeight + 1 == max)
			shapeDown.setEditable(false);
		new Button("shapeDelete:"+addHeight,"X",newShapePanel,3,0,font,Color.LIGHT_GRAY);
		newShapePanel.getGridFormatter().specifyColumnWeight(0, 2.0);
	}
	
	public void removeShape(Shape toRemove) {
		shapeList.remove(toRemove);
	}
	
	public void updateList() {
		grid = new GridFormatter();
		for(Shape shape: shapeList)
			addShapeToGUI(shape, shapeList.size());
		
		new Button("newShape", "New Shape", this, 0, shapeList.size(), font, Color.RED);
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

}
