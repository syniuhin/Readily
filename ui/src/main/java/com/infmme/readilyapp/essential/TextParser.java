package com.infmme.readilyapp.essential;

import android.text.TextUtils;
import android.util.Pair;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.settings.SettingsBundle;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser implements Serializable, Callable<TextParser> {

	public static final int RESULT_CODE_OK = 0;
	public static final int RESULT_CODE_EMPTY_CLIPBOARD = 1;
	public static final int RESULT_CODE_WRONG_EXT = 2;
	public static final int RESULT_CODE_WTF = 3;
	public static final int RESULT_CODE_CANT_FETCH = 4;
	public static final String makeMeSpecial =
			" " + "." + "!" + "?" + "-" + "—" + ":" + ";" + "," + '\"' + "(" + ")";
	private static final int MAX_LEFT_CHARACTER_COUNT = 8;

	static{
		Map<String, Integer> priorityMap = new HashMap<String, Integer>();
		/**
		 a 	b 	c 	d 	e 	f 	g 	h 	i 	j 	k 	l 	m 	n 	o 	p 	q 	r 	s 	t 	u 	v 	w 	x 	y 	z
		 */
		final String englishAlpha = "abcdefghijklmnoprstuvwxyz";
		final int[] englishPriorities =
				{10, 4, 4, 4, 9, 12, 10, 12, 8, 10, 8, 6, 6, 5, 8, 6, 12, 5, 15, 12, 14, 12, 14, 13, 14, 12};
		int i = 0;
		for (char c : englishAlpha.toCharArray()){ priorityMap.put(Character.toString(c), englishPriorities[i++]); }
		/**
		 а  б   в 	г 	д 	е 	ё   ж 	з 	и 	й 	к 	л 	м   н 	о 	п 	р 	с 	т 	у   ф   х 	ц 	ч 	ш 	щ 	ъ
		 ы 	ь 	э 	ю   я
		 */
		final String russianAlpha = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
		final int[] russianPriorities =
				{10, 4, 4, 7, 4, 7, 14, 9, 9, 6, 7, 5, 4, 4, 4, 10, 8, 10, 12, 5, 9, 15, 14, 14, 13, 10, 10, 0, 10, 0,
						10, 12, 11};
		i = 0;
		for (char c : russianAlpha.toCharArray()){ priorityMap.put(Character.toString(c), russianPriorities[i++]); }
		/**
		 ґ  і   ї   є
		 */
		final String uniqueUkrainianChars = "ґіїє";
		final int[] ukrainianPriorities = {15, 14, 18, 12};
		i = 0;
		for (char c : uniqueUkrainianChars.toCharArray()){
			priorityMap.put(Character.toString(c), ukrainianPriorities[i++]);
		}

		PRIORITIES = Collections.unmodifiableMap(priorityMap);
	}

	private static final Map<String, Integer> PRIORITIES;
	private Readable readable;
	private int lengthPreference;
	private List<Integer> delayCoefficients;
	private int resultCode;

	public TextParser(Readable readable){
		this.readable = readable;
		lengthPreference = 13; //TODO:implement it optional
	}

	/**
	 * Need it to get rid of Context, which isn't Serializable
	 *
	 * @param readable       : Readable instance to process
	 * @param settingsBundle : settingsBundle to get some settings.
	 * @return TextParser instance
	 */
	public static TextParser newInstance(Readable readable, SettingsBundle settingsBundle){
		TextParser textParser = new TextParser(readable);
		textParser.setDelayCoefficients(settingsBundle.getDelayCoefficients());
		return textParser;
	}

	public static String findLink(Pattern pattern, String text){
		if (!text.isEmpty()){
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()){ return matcher.group(); }
		}
		return null;
	}

	/**
	 * @return pattern to detect links in text
	 */
	public static Pattern compilePattern(){
		return Pattern.compile(
				"\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" +
						"(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" +
						"|mil|biz|info|mobi|name|aero|jobs|museum" +
						"|travel|edu|[a-z]{2}))(:[\\d]{1,5})?" +
						"(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" +
						"((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
						"([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" +
						"(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
						"([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" +
						"(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b"
							  );
	}

	public int getResultCode(){
		return resultCode;
	}

	public void setResultCode(int resultCode){
		this.resultCode = resultCode;
	}

	public void process(){
		normalize(readable);
		cutLongWords(readable);
		readable.setWordList(Arrays.asList(readable.getText().split(" ")));
		cleanWordList(readable);
		buildDelayList(readable);
		buildEmphasis(readable);
		checkResult();
	}

	public void setDelayCoefficients(List<Integer> delayCoefficients){
		this.delayCoefficients = delayCoefficients;
	}

	public Readable getReadable(){
		return readable;
	}

	public void setReadable(Readable readable){
		this.readable = readable;
	}

	protected void normalize(Readable readable){
		readable.setText(
				handleSpecialCases(
						insertSpacesAfterPunctuation(
								removeSpacesBeforePunctuation(
										clearFromRepetitions(
												readable.getText().replaceAll("\\s+", " ")
															)
															 )
													)
								  )
						);
	}

	/* normalize() auxiliary methods */
	protected String clearFromRepetitions(String text){
		StringBuilder res = new StringBuilder();
		int previousPosition = -1;
		for (char ch : text.toCharArray()){
			int position = makeMeSpecial.indexOf(ch);
			if (position > -1 && position != previousPosition){
				previousPosition = position;
				res.append(ch);
			} else if (position < 0){
				previousPosition = -1;
				res.append(ch);
			}
		}
		return res.toString();
	}

	protected String removeSpacesBeforePunctuation(String text){
		StringBuilder res = new StringBuilder();
		String madeMeSpecial = makeMeSpecial.substring(1, 9) + ")";
		for (char ch : text.toCharArray()){
			if (madeMeSpecial.indexOf(ch) > -1 &&
					res.length() > 0 &&
					" ".equals(res.substring(res.length() - 1))){ res.deleteCharAt(res.length() - 1); }
			res.append(ch);
		}
		return res.toString();
	}

	protected String insertSpacesAfterPunctuation(String text){
		StringBuilder res = new StringBuilder();
		String madeMeSpecial = makeMeSpecial.substring(1, 9) + ")";
		char ch;
		char nextCh;
		for (int i = 0; i < text.length(); ++i){
			ch = text.charAt(i);
			res.append(ch);
			if (i < text.length() - 1){
				nextCh = text.charAt(i + 1);
				if (madeMeSpecial.indexOf(ch) > -1 && Character.isLetter(nextCh))
					res.append(" ");
			}
		}
		return res.toString();
	}

	protected String handleSpecialCases(String text){
		return handleAbbreviations(text); //TODO: implement more cases
	}

	protected String handleAbbreviations(String text){
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < text.length(); ++i){
			if (i > 0 && text.charAt(i - 1) == '.'){
				if (!(i + 2 < text.length() && text.charAt(i + 2) == '.'))
					res.append(text.charAt(i));
			} else {
				res.append(text.charAt(i));
			}
		}
		return res.toString();
	}

	protected void cutLongWords(Readable readable){
		List<String> res = new ArrayList<String>();
		for (String word : readable.getText().split(" ")){
			while (word.length() - 1 > lengthPreference){
				int pos = lengthPreference - 2;
				while (pos > 3 && !Character.isLetter(word.charAt(pos)))
					--pos;
				res.add(word.substring(0, pos) + "-");
				word = word.substring(pos);
			}
			res.add(word);
		}
		StringBuilder sb = new StringBuilder();
		for (String s : res)
			sb.append(s).append(" ");
		readable.setText(sb.substring(0, Math.max(0, sb.length() - 1)));
	}

	protected int measureWord(String word){
		if (word.length() == 0)
			return delayCoefficients.get(0);
		if ((word.length() == 2 && word.equals("не")) ||
				(word.length() == 3 && word.equals("not")))
			return delayCoefficients.get(5);
		int res = 0;
		int tempRes;
		for (char ch : word.toCharArray()){
			tempRes = delayCoefficients.get(0);
			if (Character.isDigit(ch))
				tempRes = delayCoefficients.get(1);
			if (ch == '\t')
				tempRes = delayCoefficients.get(4);
			switch (ch){
				case ',':
					tempRes = delayCoefficients.get(1);
					break;
				case '.':
					tempRes = delayCoefficients.get(2);
					break;
				case '!':
					tempRes = delayCoefficients.get(2);
					break;
				case '?':
					tempRes = delayCoefficients.get(2);
					break;
				case '-':
					tempRes = delayCoefficients.get(3);
					break;
				case '—':
					tempRes = delayCoefficients.get(3);
					break;
				case ':':
					tempRes = delayCoefficients.get(3);
					break;
				case ';':
					tempRes = delayCoefficients.get(3);
					break;
				case '\n':
					tempRes = delayCoefficients.get(4);
			}
			if (tempRes > res)
				res = tempRes;
		}
		return res;
	}

	protected void cleanWordList(Readable readable){
		List<String> wordList = readable.getWordList();
		List<String> res = new ArrayList<String>();
		for (String word : wordList){ if (!TextUtils.isEmpty(word)){ res.add(word); } }
		readable.setWordList(res);
	}

	protected void buildDelayList(Readable readable){
		List<Integer> res = new ArrayList<Integer>();
		for (String word : readable.getWordList()) res.add(measureWord(word));
		readable.setDelayList(res);
	}

	protected void buildEmphasis(Readable readable){
		List<Integer> res = new ArrayList<Integer>();
		for (String word : readable.getWordList()){
			/* some kind of experiment, huh? */
			Map<String, Pair<Integer, Integer>> priorities = new HashMap<String, Pair<Integer, Integer>>();
			int len = word.length();
			for (int i = 0; i < Math.min(MAX_LEFT_CHARACTER_COUNT, len); ++i){
				if (!Character.isLetter(word.charAt(i))) continue;

				String ch = word.substring(i, i + 1).toLowerCase();
				if (PRIORITIES.get(ch) != null &&
						(priorities.get(ch) == null ||
								priorities.get(ch).first < PRIORITIES.get(ch) * 100 / Math.max(1,
																							   Math.abs(len / 2 - i)
																							  ))){
					priorities.put(ch,
								   new Pair<Integer, Integer>(
										   PRIORITIES.get(ch) * 100 / Math.max(1, Math.abs(len / 2 - i)),
										   i)
								  );
				} else { priorities.put(ch, new Pair<Integer, Integer>(0, i)); }
				if (i + 1 < word.length() && word.charAt(i) == word.charAt(i + 1)){
					priorities.put(ch, new Pair<Integer, Integer>(priorities.get(ch).first * 4, i));
				}
			}
			int resInd = word.length() / 2;
			int mmax = 0;
			for (Map.Entry<String, Pair<Integer, Integer>> entry : priorities.entrySet()){
				if (mmax < entry.getValue().first){
					mmax = entry.getValue().first;
					resInd = entry.getValue().second;
				}
			}
			res.add(resInd);
		}
		readable.setEmphasisList(res);
	}

	public void checkResult(){
		int resultCode;
		if (readable != null){
			if (TextUtils.isEmpty(readable.getText()) ||
					readable.getWordList().isEmpty() ||
					readable.isProcessFailed()){
				switch (this.getReadable().getType()){
					case Readable.TYPE_CLIPBOARD:
						resultCode = RESULT_CODE_EMPTY_CLIPBOARD;
						break;
					case Readable.TYPE_FILE:
						resultCode = RESULT_CODE_WRONG_EXT;
						break;
					case Readable.TYPE_TXT:
						resultCode = RESULT_CODE_WRONG_EXT;
						break;
					case Readable.TYPE_EPUB:
						resultCode = RESULT_CODE_WRONG_EXT;
						break;
					case Readable.TYPE_NET:
						resultCode = RESULT_CODE_CANT_FETCH;
						break;
					default:
						resultCode = RESULT_CODE_WTF;
						break;
				}
			} else {
				resultCode = RESULT_CODE_OK;
			}
		} else {
			resultCode = RESULT_CODE_WTF;
		}
		setResultCode(resultCode);
	}


	@Override
	public TextParser call() throws Exception{
		process();
		return this;
	}
}