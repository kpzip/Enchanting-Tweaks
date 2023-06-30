package xyz.kpzip.enchantingtweaks.util;

import org.jetbrains.annotations.Nullable;

public abstract class RomanNumerals {
	
	private static final Numeral I = new Numeral("I", 1, Numeral.NONE);
	private static final Numeral IV = new Numeral("IV", 4, I);
	private static final Numeral V = new Numeral("V", 5, IV);
	private static final Numeral IX = new Numeral("IX", 9, V);
	private static final Numeral X = new Numeral("X", 10, IX);
	private static final Numeral XL = new Numeral("XL", 40, X);
	private static final Numeral L = new Numeral("L", 50, XL);
	private static final Numeral XC = new Numeral("XC", 90, L);
	private static final Numeral C = new Numeral("C", 100, XC);
	private static final Numeral CD = new Numeral("CD", 400, C);
	private static final Numeral D = new Numeral("D", 500, CD);
	private static final Numeral CM = new Numeral("CM", 900, D);
	private static final Numeral M = new Numeral("M", 1000, CM);
	
	@Nullable
	public static String getNumeral(int number) {
		Numeral currentNumeral = M;
		StringBuilder str = new StringBuilder();
		if (number > M.value) {
			str.append(M.name.repeat((int)(number/M.value)));
			number %= M.value;
		}
		while (number > 0) {
			if (currentNumeral.value() > number) {
				currentNumeral = currentNumeral.nextLowest();
				continue;
			}
			else {
				number -= currentNumeral.value();
				str.append(currentNumeral.name());
			}
		}
		return str.toString();
	}
	
	private record Numeral(String name, int value, Numeral nextLowest) {
		
		public static final Numeral NONE = (Numeral)null;

	}
}
