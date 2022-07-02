package moulton.poly.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

import moulton.scalable.clickables.Button;
import moulton.scalable.draggables.ScrollBar;
import moulton.scalable.geometrics.Line;
import moulton.scalable.popups.Popup;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.StaticTextBox;

public class CreditsPopup extends Popup {
	private String creditText =
			"PolyGrapher version 1.1.0\n"
			+ "https://github.com/mmoult/Poly-Grapher\n"
			+ "\n"
			+ "Project Lead: Matthew Moulton\n"
			+ "Project Design: Matthew Moulton\n"
			+ "GUI Format: Matthew Moulton\n"
			+ "Images: Matthew Moulton\n"
			+ "Git Maintainance: Matthew Moulton\n"
			+ "Testing: Matthew Moulton\n"
			+ "\n"
			+ "Made with Moulton Scalable Menus version 1.14\n"
			+ "© 2021- Matthew Moulton. All Rights Reserved";

	public CreditsPopup(String width, String height, Color color) {
		super(width, height, color);
		
		Font font = new Font("Arial", Font.PLAIN, 13);
		base.setOutline(true);
		blanketBackground = new Color(0x33DDDDDD, true);
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		FontMetrics fm = img.getGraphics().getFontMetrics(font);
		int fontHeight = fm.getHeight();
		int doubleHeight = fontHeight * 2;
		
		String title = "  Credits";
		new Caption(title,base,"0",""+fm.getHeight(), font, Alignment.LEFT_ALIGNMENT);
		new Line(base, "1",""+(doubleHeight-1), "width-2", "?", Color.GRAY);
		
		addTouchComponent(new Button("cancel", "X", base, "width-"+(fm.stringWidth("X")*2), "0", "?width", ""+doubleHeight, font, Color.RED));
		Font smallFont = new Font("Arial", Font.PLAIN, 12);
		String boxY = ""+(fontHeight*3);
		String boxHeight = ""+(doubleHeight*5);
		StaticTextBox box = new StaticTextBox("creditsMes", creditText,
				base, ""+fontHeight, boxY, "?width-"+fontHeight, boxHeight, smallFont, Color.WHITE);
		ScrollBar boxBar = new ScrollBar(true, base, "width-10", boxY, "9", boxHeight, Color.LIGHT_GRAY);
		box.setTextScroller(boxBar);
		addTouchComponent(boxBar);
		
		this.width = ""+(doubleHeight*8+10); //doubleHeight serves as padding
		this.height = ""+(doubleHeight*3 + 8*fontHeight);
	}

}
