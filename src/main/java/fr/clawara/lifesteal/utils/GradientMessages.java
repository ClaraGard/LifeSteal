package fr.clawara.lifesteal.utils;

import net.md_5.bungee.api.ChatColor;

public class GradientMessages {
	public static String getGradientMessage(String message, String color1, String color2) {
		int length = message.length();
		int redInColor1 = Integer.decode("0x"+color1.substring(1,3));
		int redInColor2 = Integer.decode("0x"+color2.substring(1,3));
		int greenInColor1 = Integer.decode("0x"+color1.substring(3,5));
		int greenInColor2 = Integer.decode("0x"+color2.substring(3,5));
		int blueInColor1 = Integer.decode("0x"+color1.substring(5,7));
		int blueInColor2 = Integer.decode("0x"+color2.substring(5,7));
		String output = "";
		for(int i=0;i<length;i++) {
			int coeff1 = i;
			int coeff2 = length-i-1;
			int averageRed = (int) Math.sqrt((redInColor1*redInColor1*coeff1+redInColor2*redInColor2*coeff2)/(coeff1+coeff2));
			int averageGreen = (int) Math.sqrt((greenInColor1*greenInColor1*coeff1+greenInColor2*greenInColor2*coeff2)/(coeff1+coeff2));
			int averageBlue = (int) Math.sqrt((blueInColor1*blueInColor1*coeff1+blueInColor2*blueInColor2*coeff2)/(coeff1+coeff2));
			output+=""+ChatColor.of("#"+Integer.toHexString(averageRed) +Integer.toHexString(averageGreen) +Integer.toHexString(averageBlue))+message.charAt(i);
		}
		return output;
	}
	
	private static ChatColor average(String color1, int coeff1, String color2, int coeff2) {
		int redInColor1 = Integer.decode("0x"+color1.substring(1,3));
		int redInColor2 = Integer.decode("0x"+color2.substring(1,3));
		int averageRed = (int) Math.sqrt((redInColor1*redInColor1*coeff1+redInColor2*redInColor2*coeff2)/(coeff1+coeff2));
		int greenInColor1 = Integer.decode("0x"+color1.substring(3,5));
		int greenInColor2 = Integer.decode("0x"+color2.substring(3,5));
		int averageGreen = (int) Math.sqrt((greenInColor1*greenInColor1*coeff1+greenInColor2*greenInColor2*coeff2)/(coeff1+coeff2));
		int blueInColor1 = Integer.decode("0x"+color1.substring(5,7));
		int blueInColor2 = Integer.decode("0x"+color2.substring(5,7));
		int averageBlue = (int) Math.sqrt((blueInColor1*blueInColor1*coeff1+blueInColor2*blueInColor2*coeff2)/(coeff1+coeff2));
		return ChatColor.of("#"+Integer.toHexString(averageRed)+Integer.toHexString(averageGreen)+Integer.toHexString(averageBlue));
	}
}
