package tfc.pehkuispawnsizes;

import java.util.ArrayList;
import java.util.Random;

public class ScaleInfo {
	double min = 0, max = 0;
	
	boolean ints = false;
	
	boolean perform = true;
	
	public ScaleInfo(String text) {
		if (text.equals("&")) perform = false;
		if (!perform) return;
		
		String[] split;
		if (text.contains("-")) split = text.split("-");
		else split = new String[]{text, text};
		min = Double.parseDouble(split[0]);
		max = Double.parseDouble(split[1]);
		
		ints = !(split[0].contains(".") || split[1].contains("."));
	}
	
	public boolean shouldPerform() {
		return perform;
	}
	
	public double select(Random rng) {
		return (rng.nextDouble() * (max - min)) + min;
	}
	
	public static ArrayList<ScaleInfo> parseMulti(String s) {
		ArrayList<ScaleInfo> datas = new ArrayList<>();
		for (String s1 : s.split(",")) {
			datas.add(new ScaleInfo(s1.trim()));
		}
		return datas;
	}
}
