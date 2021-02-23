package moulton.poly.comps;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import moulton.scalable.clickables.EventAction;
import moulton.scalable.clickables.TouchResponsiveComponent;
import moulton.scalable.containers.MenuManager;
import moulton.scalable.containers.Panel;

public class TouchPanel extends Panel implements TouchResponsiveComponent {
	protected int lastX, lastY;
	protected boolean touched = false;
	protected EventAction touchAction = null;

	public TouchPanel(Panel parent, String x, String y, String shownWidth, String shownHeight, Color color) {
		super(parent, x, y, shownWidth, shownHeight, color);
	}

	public TouchPanel(Panel parent, int x, int y, Color color) {
		super(parent, x, y, color);
	}
	
	@Override
	public void render(Graphics g, int xx, int yy, int ww, int hh) {
		super.render(g, xx, yy, ww, hh);
		
		Rectangle rect = getRenderRect(xx, yy, ww, hh, width, height);
		int x = rect.x;
		int y = rect.y;
		int w = rect.width;
		int h = rect.height;
		if(parent != null) {
			int[][] trueVals = parent.handleOffsets(new int[] {x, x+w, x+w, x}, new int[] {y, y, y+h, y+h}, this);
			lastX = trueVals[0][0];
			lastY = trueVals[1][0];
		} else {
			lastX = x;
			lastY = y;
		}
	}

	@Override
	public boolean isTouchedAt(int x, int y) {
		return ((x >= lastX && x < lastX+lastWidth) && (y >= lastY && y < lastY+lastHeight));
	}

	@Override
	public void setTouched(boolean touched) {
		this.touched = touched;
	}

	@Override
	public boolean isTouched() {
		return touched;
	}

	@Override
	public EventAction getTouchAction() {
		return touchAction;
	}
	public void setTouchAction(EventAction touchAction) {
		this.touchAction = touchAction;
	}

	@Override
	public int getTouchedCursorType() {
		return 0;
	}
	
	@Override
	public void removeTouchResponsiveness(MenuManager manager) {
		manager.removeTouchResponsiveComponent(this);
		super.removeTouchResponsiveness(manager);
	}

}
