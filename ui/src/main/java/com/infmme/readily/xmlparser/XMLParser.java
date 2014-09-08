package com.infmme.readily.xmlparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

/**
 * created on 8/26/14 by infm. Enjoy ;)
 */
public class XMLParser {
	/* Event types */
	public static final int DOCUMENT_START = 0;
	public static final int DOCUMENT_CLOSE = 1;
	public static final int TAG = 665;
	public static final int TAG_START = 666;
	public static final int TAG_CLOSE = 667;
	public static final int TAG_SINGLE = 668;
	public static final int EMPTINESS = 31415;
	public static final int CONTENT = 228;
	private InputStreamReader isr;
	private XMLEvent currentEvent;
	private Stack<XMLEvent> tagStack = new Stack<XMLEvent>();

	private int currentInt = -1, nextInt = -1;
	private long position = 0;

	public static String getTypeName(int type){
		switch (type){
			case DOCUMENT_START:
				return "DOCUMENT_START";
			case DOCUMENT_CLOSE:
				return "DOCUMENT_CLOSE";
			case TAG:
				return "TAG";
			case TAG_START:
				return "TAG_START";
			case TAG_CLOSE:
				return "TAG_CLOSE";
			case TAG_SINGLE:
				return "TAG_SINGLE";
			case EMPTINESS:
				return "EMPTINESS";
			case CONTENT:
				return "CONTENT";
			default:
				return "Illegal argument";
		}
	}

	public void setInput(FileInputStream fis, String encoding){
		try {
			isr = new InputStreamReader(fis, encoding);
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	public void setInput(FileInputStream fis){
		setInput(fis, "UTF-8");
	}

	public long getPosition() {
		return position;
	}

	public XMLEvent next() throws IOException{
		currentEvent = null;
		processEvent();
		return currentEvent;
	}

	private void readNext() throws IOException{
		currentInt = nextInt;
		nextInt = isr.read();
		position++;
	}

	private void processEvent() throws IOException{
		int type;
		if (currentInt == -1){
			currentInt = isr.read();
			nextInt = isr.read();
			position += 2;
		}
		if (Character.isWhitespace(nextInt))
			readNext();
		while (Character.isWhitespace(currentInt))
			readNext();
		if (currentInt == '<'){
			if (nextInt == '/'){
				currentEvent = new XMLEvent(type = TAG_CLOSE);
				readNext();
			} else if (nextInt == '?'){
				currentEvent = new XMLEvent(type = DOCUMENT_START);
				readNext();
			} else {
				currentEvent = new XMLEvent(type = TAG);
			}
		} else { //consider random position as pointing to content of text;
			currentEvent = new XMLEvent(type = CONTENT);
		}
		currentEvent.setStartPosition(position);
/*			!????
		if (tagStack.isEmpty()){
			if (currentInt == '<'){
				if (nextInt == '/') {
					currentEvent = new XMLEvent(type = TAG_CLOSE);
					readNext();
				} else if (nextInt == '?'){
					currentEvent = new XMLEvent(type = DOCUMENT_START);
					readNext();
				} else {
					currentEvent = new XMLEvent(type = TAG);
				}
			} else { //consider random position as pointing to content of text;
				currentEvent = new XMLEvent(type = CONTENT);
			}
		} else {
			int prevType = tagStack.lastElement().getType();
			switch (prevType){
				case DOCUMENT_START:
					currentEvent = new XMLEvent(type = EMPTINESS);
					break;
				case DOCUMENT_CLOSE:
					throw new IllegalArgumentException("WTF, event has appeared after DOCUMENT_CLOSE");
				case TAG_START:
					if (currentInt == '<') {
						if (nextInt == '/')
							currentEvent = new XMLEvent(type = TAG_CLOSE);
						else
							currentEvent = new XMLEvent(type = TAG);
					} else {
						currentEvent = new XMLEvent(type = CONTENT);
					}
					break;
				case TAG_CLOSE:
					throw new IllegalArgumentException("TAG_CLOSE is in tagStack");
				case CONTENT:
					if (currentInt == '<') {
						currentEvent = new XMLEvent(type = TAG);
						break;
					} else {
						throw new IllegalArgumentException("non-TAG after EMPTINESS");
					}
				case EMPTINESS:
					if (currentInt == '<') {
						currentEvent = new XMLEvent(type = TAG);
						break;
					} else {
						throw new IllegalArgumentException("non-TAG after EMPTINESS");
					}
			}
		}
*/
		do {
			readNext();
			updateType(type, currentInt, nextInt);
			type = currentEvent.getType();
			switch (type){
				case DOCUMENT_START:
					currentEvent.appendTagName((char) currentInt);
					break;
				case DOCUMENT_CLOSE:
					currentEvent.appendTagName((char) currentInt);
					break;
				case TAG:
					currentEvent.appendTagName((char) currentInt);
					break;
				case TAG_START:
					currentEvent.appendTagName((char) currentInt);
					break;
				case TAG_CLOSE:
					currentEvent.appendTagName((char) currentInt);
					break;
				case TAG_SINGLE:
					currentEvent.appendTagName((char) currentInt);
					break;
				case CONTENT:
					currentEvent.appendContent((char) currentInt);
					break;
				case EMPTINESS:
					break;
				default:
					throw new IllegalArgumentException("type doesn't exist");
			}
		} while (!breakClause(type, currentInt, nextInt));

		switch (type){
			case CONTENT:
				if (currentEvent.getContent().length() > 1){
					readNext();
					if (!tagStack.isEmpty())
						currentEvent.setContentType(tagStack.lastElement().getTagName());
				} else {
					currentEvent = null;
					processEvent();
				}
				break;
			case TAG:
				currentEvent.clarifyTagType(TAG_START);
				tagStack.push(currentEvent);
				readNext();
				break;
			case TAG_CLOSE:
				if (!tagStack.empty() && tagStack.lastElement().getTagName().equals(currentEvent.getTagName()))
					tagStack.pop();
				readNext();
				readNext();
				break;
			case TAG_START:
				tagStack.push(currentEvent);
				readNext();
				readNext();
				break;
			case DOCUMENT_CLOSE:
				if (!tagStack.empty() && tagStack.lastElement().getTagName().equals(currentEvent.getTagName()))
					tagStack.pop();
				break;
			case DOCUMENT_START:
				currentEvent.cutLastTagChar();
				tagStack.push(currentEvent);
				readNext();
				readNext();
				break;
		}

		currentEvent.setEndPosition(position);
	}

	private boolean breakClause(int type, int currentInt, int nextInt){
		switch (type){
			case CONTENT:
				return currentInt == '<' || nextInt == '<';
			case EMPTINESS:
				return currentInt == '<' || nextInt == '<';
			case TAG:
				return nextInt == '>';
			case TAG_START:
				return nextInt == '>';
			case TAG_CLOSE:
				return nextInt == '>';
			case TAG_SINGLE:
				return currentInt == '/' && nextInt == '>';
			case DOCUMENT_START:
				return currentInt == '?' && nextInt == '>';
			case DOCUMENT_CLOSE:
				return nextInt == '>';
			default:
				throw new IllegalArgumentException("type doesn't exist");
		}
	}

	private void updateType(int type, int currentInt, int nextInt){
		switch (type){
			case TAG:
				if (nextInt == '>' && currentInt == '/')
					currentEvent.clarifyTagType(TAG_SINGLE);
				break;
		}
	}
}
