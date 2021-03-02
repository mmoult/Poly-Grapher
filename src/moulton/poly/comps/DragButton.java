package moulton.poly.comps;

import java.awt.Color;
import java.awt.Cursor;

import moulton.poly.main.Menu;
import moulton.scalable.clickables.Button;
import moulton.scalable.containers.Panel;
import moulton.scalable.draggables.DraggableComponent;

public class DragButton extends Button implements DraggableComponent{
	private Menu menu;

	public DragButton(Menu menu, Panel parent, String x, String y, String width, String height, Color color) {
		super("_drag", "", parent, x, y, width, height, null, color);
		this.menu = menu;
		this.colorTouched = color;
	}

	@Override
	public double[] drag(double dx, double dy) {
		dx = menu.movePartition(dx);
		return new double[]{dx, dy};
	}
	
	@Override
	public int getTouchedCursorType() {
		return Cursor.W_RESIZE_CURSOR;
	}

}
