package moulton.poly.comps;

import moulton.scalable.texts.TextFormat;

public class NumberFormat extends TextFormat{

	@Override
	public boolean isValidChar(char c) {
		return ((c>47 && c<58) || (c>44 && c<47));
	}

	@Override
	public String emptyText() {
		return "0";
	}

}
