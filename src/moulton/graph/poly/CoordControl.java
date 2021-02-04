package moulton.graph.poly;

import java.awt.Color;
import java.awt.Font;

import moulton.scalable.containers.Panel;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.TextBox;
import moulton.scalable.texts.TextFormat;

public class CoordControl extends Panel{
	public TextBox lowX, hiX, lowY, hiY;

	public CoordControl(Panel parent, String x, String y, String width, String height, Color color) {
		super(parent, x, y, width, height, color);
		grid.setFrame("width/10", "height/10");
		grid.setMargin("width/20", "height/10");
		Font font = new Font("Arial", Font.PLAIN, 12);
		TextFormat numberForm = new NumberFormat();
		lowX = new TextBox("lowX","0",this,1,0,font,Color.LIGHT_GRAY);
		lowX.setTextFormat(numberForm);
		hiX = new TextBox("hiX","0",this,2,0,font,Color.LIGHT_GRAY);
		hiX.setTextFormat(numberForm);
		lowY = new TextBox("lowY","0",this,1,1,font,Color.LIGHT_GRAY);
		lowY.setTextFormat(numberForm);
		hiY = new TextBox("hiY","0",this,2,1,font,Color.LIGHT_GRAY);
		hiY.setTextFormat(numberForm);
		new Caption("X(",this,0,0,font,Alignment.LEFT_ALIGNMENT);
		new Caption("Y(",this,0,1,font,Alignment.LEFT_ALIGNMENT);
		grid.specifyColumnWeight(0, .15);
		new Caption(")",this,"width-width/12","height/4",font,Alignment.LEFT_ALIGNMENT);
		new Caption(")",this,"width-width/12","3height/4",font,Alignment.LEFT_ALIGNMENT);
		new Caption(",",this,"width/1.9","height/4",font,Alignment.LEFT_ALIGNMENT);
		new Caption(",",this,"width/1.9","3height/4",font,Alignment.LEFT_ALIGNMENT);
	}

}