package moulton.poly.menu.popups;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

import moulton.poly.menu.NumberFormat;
import moulton.scalable.clickables.Button;
import moulton.scalable.clickables.RadioGroup;
import moulton.scalable.containers.MenuManager;
import moulton.scalable.containers.Panel;
import moulton.scalable.popups.CommonPopup;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.TextBox;

public class SkewPopup extends CommonPopup {

	public SkewPopup(MenuManager manager) {
		super("Skew the shape by some number of degrees.", "Skew",
				new Font("Arial", Font.PLAIN, 13), "cancel", manager);
		Font font = new Font("Arial", Font.PLAIN, 13);
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		FontMetrics fm = img.getGraphics().getFontMetrics(font);
		int fontHeight = fm.getHeight();
		int doubleHeight = fontHeight * 2;
		
		Panel options = new Panel(base, "CENTERX", "" + (doubleHeight*2.5), getPopupDefaultWidth(fontHeight)+"/2", ""+(doubleHeight*1.5), null);
		options.getGridFormatter().setMargin("width/20", null);
		RadioGroup skewType = new RadioGroup();
		Button skewBut = new Button("horizSkew", "horizontal", options, 0, 0, font, Color.LIGHT_GRAY);
		skewType.addButton(skewBut);
		skewType.select(skewBut);
		addTouchComponent(skewBut);
		skewBut = new Button("vertSkew", "vertical", options, 1, 0, font, Color.LIGHT_GRAY);
		skewType.addButton(skewBut);
		addTouchComponent(skewBut);
		new Caption("skew angle:", options, 0, 1, font, Alignment.RIGHT_ALIGNMENT);
		NumberFormat nf = new NumberFormat();
		TextBox delta = new TextBox("skewAngle", "0", options, 1, 1, font, Color.LIGHT_GRAY);
		delta.setTextFormat(nf);
		delta.setClickSelectsAll(true);
		addTouchComponent(delta);
		
		Button okButton = new Button("doSkew", "Ok", base, "CENTERX",
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
