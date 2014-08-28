package com.infmme.readily.xmlparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;

/**
 * created on 8/26/14 by infm. Enjoy ;)
 */
public class XMLParser {
	/* Event types */
	public static final int DOCUMENT_START = 0;
	public static final int DOCUMENT_CLOSE = 1;
	public static final int TAG_START = 666;
	public static final int TAG_CLOSE = 667;
	public static final int EMPTINESS = 31415;
	public static final int CONTENT = 228;
	private FileInputStream fis;
	private String encoding;
	private XMLEvent currentEvent;
	private Stack<XMLEvent> eventStack;

	public void setInput(FileInputStream fis, String encoding){
		this.fis = fis;
		this.encoding = encoding;
	}

	public XMLEvent next(){
		return currentEvent;
	}

	private void processEvent() throws IOException{
		int type = -1;
		int currentInt = fis.read();
		int nextInt = fis.read();
		if (eventStack.isEmpty()){

		} else {
			int prevType = eventStack.lastElement().getType();
			if (prevType != DOCUMENT_START){
				switch ((char) currentInt){
					case '<':
						if (char)
				}
			} else {
				currentEvent = new XMLEvent(EMPTINESS);
			}
		}
		while (breakClause(type, currentInt, nextInt)){
			switch (type){
				case DOCUMENT_START:
					currentEvent.appendTagName( (char) currentInt);
					break;
				case DOCUMENT_CLOSE:
					currentEvent.appendTagName( (char) currentInt);
					break;
				case TAG_START:
					currentEvent.appendTagName( (char) currentInt);
					break;
				case TAG_CLOSE:
					currentEvent.appendTagName( (char) currentInt);
					break;
				case CONTENT:
					currentEvent.appendContent( (char) currentInt);
					break;
				case EMPTINESS:
					break;
				default:
					throw new IllegalArgumentException("type doesn't exist");
			}
		}
	}

	private boolean breakClause(int type, int currentInt, int nextInt){
		char currentChar = (char) currentInt;
		char nextChar = (char) nextInt;
		switch (type){
			case CONTENT:
				return currentChar == '<';
			case EMPTINESS:
				return currentChar == '<';
			case TAG_START:
				return currentChar == '>';
			case TAG_CLOSE:
				return currentChar == '/' && nextChar == '>';
			case DOCUMENT_START:
				return currentChar == '>';
			case DOCUMENT_CLOSE:
				return currentChar == '/' && nextChar == '>';
			default:
				throw new IllegalArgumentException("type doesn't exist");
		}
	}
}
