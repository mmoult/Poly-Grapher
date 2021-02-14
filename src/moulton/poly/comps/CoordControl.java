package moulton.poly.comps;

import java.awt.Color;
import java.awt.Font;

import moulton.scalable.containers.Panel;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.TextBox;
import moulton.scalable.texts.TextFormat;

public class CoordControl extends Panel {
	public TextBox lowX, hiX, lowY, hiY;

	public CoordControl(Panel parent, String x, String y, String width, String height, Color color) {
		super(parent, x, y, width, height, color);
		grid.setFrame("width/10", "height/10");
		grid.setMargin("width/20", "height/10");
		Font font = new Font("Arial", Font.PLAIN, 12);
		TextFormat numberForm = new NumberFormat();
		lowX = new TextBox("lowX","0",this,1,0,font,Color.LIGHT_GRAY);
		lowX.setTextFormat(numberForm);
		lowX.setClickSelectsAll(true);
		hiX = new TextBox("hiX","0",this,2,0,font,Color.LIGHT_GRAY);
		hiX.setTextFormat(numberForm);
		hiX.setClickSelectsAll(true);
		lowY = new TextBox("lowY","0",this,1,1,font,Color.LIGHT_GRAY);
		lowY.setTextFormat(numberForm);
		lowY.setClickSelectsAll(true);
		hiY = new TextBox("hiY","0",this,2,1,font,Color.LIGHT_GRAY);
		hiY.setTextFormat(numberForm);
		hiY.setClickSelectsAll(true);
		new Caption("X(",this,"8","8",font,Alignment.LEFT_ALIGNMENT);
		new Caption("Y(",this,"8","26",font,Alignment.LEFT_ALIGNMENT);
		grid.specifyColumnWeight(0, .01);
		new Caption(")",this,"width-width/12","8",font,Alignment.LEFT_ALIGNMENT);
		new Caption(")",this,"width-width/12","26",font,Alignment.LEFT_ALIGNMENT);
		new Caption(",",this,"width/1.9","8",font,Alignment.LEFT_ALIGNMENT);
		new Caption(",",this,"width/1.9","26",font,Alignment.LEFT_ALIGNMENT);
	}

}