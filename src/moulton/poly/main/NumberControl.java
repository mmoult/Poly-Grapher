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
		if(num == 0 || num == -0)
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
				if(number.charAt(place) == '.') {
					//we rounded all the way back to the decimal
					place--;
				}
				number = number.substring(0, place+1);
				if(number.equals("-0"))
					return "0"; //disallow -0
			}
		}
		return number;
	}
	
	/**
	 * Rounds the double value to the specified decimal place and returns the result. Internally, a String
	 * is created and altered to find the solution, then this string is parsed. If a string result is
	 * appropriate, call {@link #roundToString(double, int)} for a faster computation
	 * @param value the number to be rounded
	 * @param powerOfTen the place for value to be rounded to, represented as a power of ten. For example,
	 * rounding value to a whole number would have a powerOfTen of 0, rounding to the nearest 100 would have
	 * a powerOfTen of 2, rounding to the nearest .0001 would have a powerOfTen of -4.
	 * @return the result of value being rounded
	 */
	public static double round(double value, int powerOfTen){
		return Double.parseDouble(roundToString(value, powerOfTen));
	}
	
	/**
	 * Rounds the double value to the specified decimal place and returns the result. If a double result
	 * is needed, use {@link #round(double, int)}.
	 * @param value the number to be rounded
	 * @param powerOfTen the place for value to be rounded to, represented as a power of ten. For example,
	 * rounding value to a whole number would have a powerOfTen of 0, rounding to the nearest 100 would have
	 * a powerOfTen of 2, rounding to the nearest .0001 would have a powerOfTen of -4.
	 * @return the result of value being rounded
	 */
	public static String roundToString(double value, int powerOfTen) {
		boolean negative = value<0;
		double absVal = value;
		if(negative)
			absVal*= -1;
		if(Math.pow(10, powerOfTen-1)>absVal)
			return "0";
		
		char[] num;
		int decimal=-1;
		//if a mathematical integer
		if(value%1==0){
			if(powerOfTen<1)
				return doubleToString(value);
			num = doubleToString(absVal).toCharArray();
			decimal = num.length;
		}else{
			//will contain a decimal
			String number = doubleToString(absVal);
			//subtract one because the decimal won't fit in the new scheme
			num = new char[number.length()-1];
			for(int i=0; i<num.length; i++){
				if(number.charAt(i) == '.')
					decimal = i;
				num[i] = number.charAt(i+(decimal==-1?0:1));
			}
		}
		
		int index = decimal - powerOfTen;
		if(index<0)
			return "0";
		if(index>=num.length)
			return doubleToString(value);
		
		//get the character at the index as a number 0-9.
		byte round = (byte)(num[index]-'0');
		if(round>4){
			//round up
			do{
				if(index>0){
					num[index--] = '0';
					round = (byte)(++num[index]-'0');
				}else{
					//we need to tack a 1 onto the left side of the number
					String number = "";
					if(negative)
						number = "-";
					number += "1";
					num[index]='0';
					int i=0;
					for(; i<index+1; i++) {
						if(i==decimal) number+='.';
						number+=num[i];
					}if(i>=decimal)
						return number;
					//else we need to fill the 0s until the decimal
					for(; i<decimal; i++)
						number+='0';
					return number;
				}
			}while(round>9);
			
			String number = "";
			if(negative)
				number = "-";
			int i=0;
			for(; i<index+1; i++){
				if(i==decimal) number+='.';
				number+=num[i];
			}if(i>=decimal || i>0 && num[i-1]=='0')
				return number;
			//else we need to fill the 0s until the decimal
			for(; i<decimal; i++)
				number+='0';
			return number;
			
		}else{
			//round down
			if(index==0)
				return "0";
			String number = "";
			if(negative)
				number = "-";
			int i=0;
			for(; i<index; i++){
				if(decimal==i)
					number+=".";
				if(num[i]=='0' && i>decimal)
					break;
				number+=num[i];
			}if(i>=decimal || i>0 && num[i-1]=='0')
				return number;
			//else we need to fill the 0s until the decimal
			for(; i<decimal; i++)
				number+='0';
			return number;
		}
	}
}
