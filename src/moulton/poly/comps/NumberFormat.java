package moulton.poly.comps;

import moulton.scalable.texts.TextFormat;

public class NumberFormat extends TextFormat{

	@Override
	public boolean isValidChar(char c) {
		return ((c>='0' && c<='9') || (c>='-' && c<='.'));
	}

	@Override
	public String emptyText() {
		return "0";
	}

}
