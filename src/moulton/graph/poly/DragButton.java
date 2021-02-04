package moulton.graph.poly;

import java.awt.Color;

import moulton.scalable.clickables.Button;
import moulton.scalable.containers.Panel;
import moulton.scalable.draggables.DraggableComponent;

public class DragButton extends Button implements DraggableComponent{
	private Menu menu;

	public DragButton(Menu menu, Panel parent, String x, String y, String width, String height, Color color) {
		super("_drag", "", parent, x, y, width, height, null, color);
		this.menu = menu;
	}

	@Override
	public double[] drag(double dx, double dy) {
		dx = menu.movePartition(dx);
		return new double[]{dx, dy};
	}

}
