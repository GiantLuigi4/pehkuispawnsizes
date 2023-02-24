package tfc.pehkuispawnsizes.checkers;

import java.util.regex.Pattern;

public class WildcardNameChecker extends NameChecker {
	Pattern pattern;
	
	public WildcardNameChecker(String regex) {
		// https://stackoverflow.com/questions/12677178/regular-expression-with-wildcards-to-match-any-character
		regex = regex.replace("*", "+([a-zA-Z0-9\\-_.\\/]+)+");
		if (regex.startsWith("+")) regex = regex.substring(1);
		if (regex.endsWith("+")) regex = regex.substring(0, regex.length() - 1);
		pattern = Pattern.compile(regex);
	}
	
	@Override
	public boolean execute(String name) {
		return pattern.matcher(name).matches();
	}
}
