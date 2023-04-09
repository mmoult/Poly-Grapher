package moulton.poly.menu.popups;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

import moulton.poly.menu.NumberFormat;
import moulton.scalable.clickables.Button;
import moulton.scalable.containers.MenuManager;
import moulton.scalable.containers.Panel;
import moulton.scalable.popups.CommonPopUp;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.TextBox;

public class RotatePopup extends CommonPopUp {

	public RotatePopup(MenuManager manager) {
		super("Rotate all shape vertices by some number of degrees.", "Rotate",
				new Font("Arial", Font.PLAIN, 13), "cancel", manager);
		Font font = new Font("Arial", Font.PLAIN, 13);
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		FontMetrics fm = img.getGraphics().getFontMetrics(font);
		int fontHeight = fm.getHeight();
		int doubleHeight = fontHeight * 2;
		
		Panel options = new Panel(base, "CENTERX", "" + (doubleHeight*3), getPopupDefaultWidth(fontHeight)+"/2", ""+(doubleHeight*2.25), null);
		options.getGridFormatter().setMargin("width/20", null);
		NumberFormat nf = new NumberFormat();
		new Caption("rotate angle:", options, 0, 0, font, Alignment.RIGHT_ALIGNMENT);
		TextBox box = new TextBox("angle", "0", options, 1, 0, font, Color.LIGHT_GRAY);
		box.setTextFormat(nf);
		box.setClickSelectsAll(true);
		addTouchComponent(box);
		new Caption("center x:", options, 0, 1, font, Alignment.RIGHT_ALIGNMENT);
		box = new TextBox("centerX", "0", options, 1, 1, font, Color.LIGHT_GRAY);
		box.setTextFormat(nf);
		box.setClickSelectsAll(true);
		addTouchComponent(box);
		new Caption("center y:", options, 0, 2, font, Alignment.RIGHT_ALIGNMENT);
		box = new TextBox("centerY", "0", options, 1, 2, font, Color.LIGHT_GRAY);
		box.setTextFormat(nf);
		box.setClickSelectsAll(true);
		addTouchComponent(box);
		
		Button okButton = new Button("doRotate", "Ok", base, "CENTERX",
				"height-1-"+doubleHeight, "width/5", ""+doubleHeight, font, Color.LIGHT_GRAY);
		addTouchComponent(okButton);
	}
	
	@Override
	public int getPopupExtraHeight(int fontHeight) {
		return (int)(fontHeight * 7.5);
	}
	
	@Override
	public int getPopupDefaultWidth(int fontHeight) {
		return fontHeight * 18;
	}
	
	@Override
	public int getPopupMaxWidth(int fontHeight) {
		return getPopupDefaultWidth(fontHeight);
	}

}
