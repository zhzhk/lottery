package org.crawler.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	public static Matcher regexResult(String pattern,String inputString){
		Pattern rp = Pattern.compile(pattern);
		Matcher rm = rp.matcher(inputString);
		return rm;
	}
}
