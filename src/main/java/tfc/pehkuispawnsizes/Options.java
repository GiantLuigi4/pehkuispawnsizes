package tfc.pehkuispawnsizes;

import com.mojang.datafixers.util.Pair;
import tfc.pehkuispawnsizes.checkers.EntityChecker;
import tfc.pehkuispawnsizes.checkers.NameChecker;
import tfc.pehkuispawnsizes.checkers.TagChecker;
import tfc.pehkuispawnsizes.checkers.WildcardNameChecker;

import java.util.ArrayList;
import java.util.HashMap;

public class Options {
	boolean registerScaleType = false;
	String typeName = "";
	HashMap<String, ArrayList<ScaleInfo>> simpleNames = new HashMap<>();
	ArrayList<Pair<EntityChecker, ArrayList<ScaleInfo>>> nameCheckers = new ArrayList<>();
	
	protected ArrayList<ScaleInfo> get(String name) {
		if (!simpleNames.containsKey(name)) simpleNames.put(name, new ArrayList<>());
		return simpleNames.get(name);
	}
	
	// TODO: clean this up, I guess?
	public Options(String text) {
		boolean inBlock = false;
		for (String s : text.split("\n")) {
			if (inBlock) {
				if (s.startsWith(">")) {
					inBlock = false;
					continue;
				}
				s = s.trim();
				if (s.isEmpty()) continue;
				String[] split = s.split(" ", 2);
				parse(split);
				
				continue;
			}
			
			if (s.trim().startsWith("#")) continue;
			
			if (s.startsWith("register_type")) {
				String str = s.substring("register_type".length()).trim().substring(1).trim();
				registerScaleType = Boolean.parseBoolean(str);
			} else if (s.startsWith("scale_type")) {
				String str = s.substring("scale_type".length()).trim().substring(1).trim();
				typeName = str;
			} else if (s.startsWith("scales")) {
				String str = s.substring("scales".length()).trim().substring(1).trim();
				if (str.equals("<")) {
					inBlock = true;
				}
			}
		}
	}
	
	// mods can mixin to this to add their own prefixes
	public void parse(String[] split) {
		String name = split[0];
		if (name.startsWith("#")) {
			name = name.substring(1);
			nameCheckers.add(Pair.of(new TagChecker(name), ScaleInfo.parseMulti(split[1])));
		}
		else if (name.contains("*") || name.startsWith("!")) {
			if (name.startsWith("!")) name = name.substring(1);
			nameCheckers.add(Pair.of(new WildcardNameChecker(name), ScaleInfo.parseMulti(split[1])));
		} else get(name).addAll(ScaleInfo.parseMulti(split[1]));
	}
}
