package com.infmme.readily.xmlparser;

import android.util.Pair;

/**
 * created on 8/26/14 by infm. Enjoy ;)
 */
public class XMLEvent {
	private Pair<Integer, Integer> domain;
	private int type;

	private String content;
	private String tagName;

	public XMLEvent(int type){
		domain = new Pair<Integer, Integer>(-1, -1);
		this.type = type;
	}

	public Pair<Integer, Integer> getDomain() { return domain; }
	public int getEndPosition() { return domain.second; }
	public int getStartPosition() { return domain.first; }
	public int getType() { return type; }
	public String getContent(){	return content; }
	public String getTagName(){	return tagName;	}

	public void setEndPosition(int endPosition) { domain = new Pair<Integer, Integer>(domain.first, endPosition); }
	public void setStartPosition(int startPosition) { domain = new Pair<Integer, Integer>(startPosition, domain.second); }

	public void appendContent(char c) {	content += c; }
	public void appendTagName(char c) { tagName += c; }
}
