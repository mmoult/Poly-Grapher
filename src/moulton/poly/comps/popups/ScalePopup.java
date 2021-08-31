package moulton.poly.comps.popups;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

import moulton.poly.comps.NumberFormat;
import moulton.scalable.clickables.Button;
import moulton.scalable.containers.MenuManager;
import moulton.scalable.containers.Panel;
import moulton.scalable.popups.CommonPopup;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.TextBox;

public class ScalePopup extends CommonPopup {

	public ScalePopup(MenuManager manager) {
		super("Scale the shape by some horizontal and vertical factor from (0,0).", "Scale",
				new Font("Arial", Font.PLAIN, 13), "cancel", manager);
		Font font = new Font("Arial", Font.PLAIN, 13);
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		FontMetrics fm = img.getGraphics().getFontMetrics(font);
		int fontHeight = fm.getHeight();
		int doubleHeight = fontHeight * 2;
		
		Panel options = new Panel(base, "CENTERX", "" + (doubleHeight*3), getPopupDefaultWidth(fontHeight)+"/2", ""+(doubleHeight*1.5), null);
		options.getGridFormatter().setMargin("width/20", null);
		NumberFormat nf = new NumberFormat();
		new Caption("x scale:", options, 0, 0, font, Alignment.RIGHT_ALIGNMENT);
		TextBox delta = new TextBox("scaleX", "1", options, 1, 0, font, Color.LIGHT_GRAY);
		delta.setTextFormat(nf);
		delta.setClickSelectsAll(true);
		addTouchComponent(delta);
		new Caption("y scale:", options, 0, 1, font, Alignment.RIGHT_ALIGNMENT);
		delta = new TextBox("scaleY", "1", options, 1, 1, font, Color.LIGHT_GRAY);
		delta.setTextFormat(nf);
		delta.setClickSelectsAll(true);
		addTouchComponent(delta);
		
		Button okButton = new Button("doScale", "Ok", base, "CENTERX",
				"height-1-"+doubleHeight, "width/5", ""+doubleHeight, font, Color.LIGHT_GRAY);
		addTouchComponent(okButton);
	}
	
	@Override
	public int getPopupExtraHeight(int fontHeight) {
		return fontHeight * 6;
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
