package moulton.poly.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import moulton.scalable.containers.Panel;
import moulton.scalable.texts.LineBreak;
import moulton.scalable.utils.MenuComponent;
import moulton.scalable.visuals.ImageButton;

public final class CaptionImageButton extends ImageButton {
	private String caption;
	private Panel root;
	private long timeTouched;
	private long timeDelay = 500;
	private BoxCaption box = null;
	
	public CaptionImageButton(String id, BufferedImage img, String caption, Panel parent, int x, int y, Color background, Panel root) {
		super(id, img, parent, x, y, background);
		this.caption = caption;
		this.root = root;
	}
	
	@Override
	public void render(Graphics g, int xx, int yy, int ww, int hh) {
		super.render(g, xx, yy, ww, hh);
		Rectangle rect = getRenderRect(xx, yy, ww, hh, this.width, this.height);
		
		//If this has been touched for a bit, then it will display the caption
		if(touched && System.currentTimeMillis() - timeTouched > timeDelay) {
			//For this application, we will have the caption rendered above
			if(box == null)
				box = new BoxCaption(root, ""+xx, "height", caption, rect.width * 2);
		}else if(box != null) {
			root.removeFreeComponent(box);
			box = null;
		}
	}
	
	@Override
	public void setTouched(boolean touched) {
		boolean wasTouched = this.touched;
		super.setTouched(touched);
		if(touched && !wasTouched)
			timeTouched = System.currentTimeMillis();
	}
	
	private static final class BoxCaption extends MenuComponent {
		private String text;
		private int width;

		public BoxCaption(Panel parent, String x, String y, String text, int width) {
			super(parent, x, y);
			this.text = text;
			this.width = width;
		}

		@Override
		public void render(Graphics g, int xx, int yy, int ww, int hh) {
			Rectangle rect = getRenderRect(xx, yy, ww, hh, "100", "100");
			// the parent button is in a container with an x offset of 20
			final int xOffs = 20;
			// and has a height of 40 
			final int yOffs = 40;
			
			//We must determine how long the text will be, which will determine how we split it,
			// and how many lines we split it into.
			g.setFont(new Font("Arial", Font.PLAIN, 12));
			FontMetrics fm = g.getFontMetrics();
			int buffer = fm.stringWidth("_");
			
			String[] lines = LineBreak.lines(text, width - buffer, fm, false);
			int maxWidth = 0;
			for(String line: lines) {
				int thisWidth = fm.stringWidth(line) + buffer;
				if (thisWidth > maxWidth)
					maxWidth = thisWidth;
			}
			int captionHeight = fm.getHeight() * lines.length;
			
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(rect.x + xOffs, rect.y - yOffs - captionHeight, maxWidth, captionHeight);
			g.setColor(Color.BLACK);
			for(int i = 0; i < lines.length; i++) {
				String line = lines[i];
				g.drawString(line, rect.x + xOffs + buffer/2,
						rect.y - yOffs - fm.getDescent() - fm.getHeight()*(lines.length - 1 - i));				
			}
		}
		
	}

}
