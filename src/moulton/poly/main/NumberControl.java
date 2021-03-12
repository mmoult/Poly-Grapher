package moulton.poly.main;

public class NumberControl {
	
	public static String limitNumber(double value, int chars) {
		//we want to have a max of 5 characters, unless the numbers are too big
		//we won't count - as a character for this
		if(value < 0)
			chars++;
		String num = doubleToString(value);
		if(num.length() >= chars) {
			//check to cut out digits
			int decimalPlace = num.indexOf('.');
			if(decimalPlace != -1) { //there is a decimal
				if(decimalPlace < chars) { //there are unneeded digits after the decimal
					//begin with the too many, but also remove redundant
					int newEnd = chars-1;
					for(; newEnd>-1; newEnd--) {
						char c = num.charAt(newEnd);
						if(c == '.')
							break;
						if(c > 48 && c < 58) { //a non-zero number
							newEnd++;
							break;
						}
					}
					num = num.substring(0, newEnd); //strip off the too many
				}
			}
		}
		return num;
	}
	
	public static String doubleToString(double num) {
		final int PRECISION_OF_DOUBLE = 15;
		String number = "";
		if(num < 0) { //take care of the sign
			number = "-";
			num *= -1;
		}
		if(num == 0)
			return "0";
		if(num == Double.POSITIVE_INFINITY)
			return (number + "infinity");
		
		//build up to find how large the number is
		int powerOfTen = 0;
		while(num >= Math.pow(10.0, powerOfTen)) {
			powerOfTen++;
		}
		powerOfTen--; //it is too large after the loop
		int beginPwr = 0;
		boolean beginSet = false;
		if(powerOfTen > -1) {
			beginPwr = powerOfTen;
			beginSet = true;
		}
		for(; powerOfTen > -1; powerOfTen--) {
			int multiple = (int)(num / Math.pow(10.0, powerOfTen));
			if(multiple > 0)
				num -= multiple * Math.pow(10.0, powerOfTen);
			number += (char)(multiple + '0');
		}

		//now do less than 0
		if(num > 0) {
			if(number.isEmpty() || number.equals("-"))
				number += "0.";
			else
				number += '.';
		}
		boolean roundingError = false;
		while(num > 0) {
			if(powerOfTen < beginPwr - PRECISION_OF_DOUBLE) { //if there is still more even after precision shouldn't allow
				roundingError = true;
				break;
			}
			int multiple = (int)(num / Math.pow(10.0, powerOfTen));
			if(multiple > 0) {
				if(!beginSet) {
					beginPwr = powerOfTen;
					beginSet = true;
				}
				num -= multiple * Math.pow(10.0, powerOfTen);
			}
			number += (char)(multiple + '0');
			powerOfTen--;
		}

		if(roundingError && number.length() > 1) { //if there was a computational error, round back
			//round the 9s up and remove any zeros
			char lastDigit = number.charAt(number.length() - 1);
			if(lastDigit == '9') {
				int place = number.length() - 2;
				while(place > 0 && (number.charAt(place) == '9'|| number.charAt(place) == '.'))
					place--;
				char thisDigit = number.charAt(place);
				number = number.substring(0, place) + (++thisDigit);
			}else if(lastDigit == '0') {
				int place = number.length() - 2;
				while(place > 0 && number.charAt(place) == '0')
					place--;
				number = number.substring(0, place+1);
			}
		}
		return number;
	}
}
