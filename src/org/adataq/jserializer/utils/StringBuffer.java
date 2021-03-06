package org.adataq.jserializer.utils;

import org.adataq.jserializer.exceptions.InvalidCursorPositionException;

public class StringBuffer {
	
	/**
	 * The content of the string buffer
	 */
	private char[] content;
	
	private int cursor = 0;
	
	private int column = 0;
	
	private int line = 1;
	
	
	public StringBuffer(char[] content) {
		this.content = content;
	}
	
	public StringBuffer(String content) {
		this.content = content.toCharArray();
	}
	
	public boolean isOn(char occurrence) {
		return (isOnContent() && current() == occurrence);
	}
	
	public boolean on(char...occurrences) {
		for (char c : occurrences) {
			if(c == current()) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isOnBlank() {
		return (isOnContent() && current() == ' ' || current() == '\n' || current() == '\r' || current() == '\t');
	}
	
	public boolean isOnContent() {
		return (this.cursor >= 0 && this.cursor < this.content.length);
	}
	
	public char current() {
		if(isOnContent()) {
			return this.content[cursor];
		}else{
			throw new InvalidCursorPositionException("The cursor is in an invalid position. Cursor position: " + cursor); 	
		}
	}
	
	public String currentOrEndOfBuffer() {
		if(isOnContent()) {
			return String.valueOf(current());
		}else{
			return "END OF BUFFER";
		}
	}
	
	/**
	 * Move the cursor to the next character
	 * @return
	 */
	public boolean next() {
		cursor++;
		if(cursor < this.content.length) {
			
			//Check if pass an line break
			if(current() == '\n') {
				column = 0;
				line++;
			}else {
				column++;
			}
			
			return true;
		}else {
			return false;
		}
		
	}
	
	/**
	 * Move the cursor to the previous character
	 * @return
	 */
	public boolean previous() {
		cursor--;
		if(cursor >= 0) {
			
			//Check if pass an line break
			if(current() == '\n') {
				column = 0;
				line++;
			}else {
				column++;
			}
			
			return true;
		}else {
			return false;
		}
		
	}
	
	/**
	 * Return the content of the content based on the current position of the cursor
	 * until the value before the given occurrence
	 * @param occurrence
	 * @return
	 */
	public String readUntilBefore(char occurrence) {
		return readUntilBefore(occurrence, false);
	}
	
	/**
	 * Return the content of the content based on the current position of the cursor
	 * until the value before the given occurrence
	 * @param occurrence
	 * @param considerEscaped Flag indicating if when the cursor found the occurrence it will be ignored if is escaped
	 * @return
	 */
	public String readUntilBefore(char occurrence, boolean considerEscaped) {
		StringBuilder strb = new StringBuilder();
		strb.append(current());
		while(next()) {
			
			//If the current character found the occurrence
			if(current() == occurrence) {
				
				//Make the escaped character inclusion algorithm
				if(considerEscaped) {
					
					//If the previous character is an '\' it will be considered escaped
					boolean isEscaped = false;
					if(previous() && current() == '\\') {
						isEscaped = true;
					}
					
					//Return to the current character
					next();
					
					//If found an escape character, break the read
					if(!isEscaped) {
						break;
					}
					
				}else{
					//If the algorithm not consider escaped characters
					break;
				}
			}
			strb.append(current());
		}
		
		
		return strb.toString();
	}
	
	public String readUntil(char occurrence) {
		return this.readUntil(occurrence, false);
	}
	
	public String readUntil(char occurrence, boolean considerEscaped) {
		StringBuilder strb = new StringBuilder();
		strb.append(current());
		while(next()) {
			
			//If the current character found the occurrence
			if(current() == occurrence) {
				
				//Make the escaped character inclusion algorithm
				if(considerEscaped) {
					
					//If the previous character is an '\' it will be considered escaped
					boolean isEscaped = false;
					if(previous() && current() == '\\') {
						isEscaped = true;
					}
					
					//Return to the current character
					next();
					
					//If found an escape character, break the read
					if(!isEscaped) {
						break;
					}
					
				}else{
					//If the algorithm not consider escaped characters
					break;
				}
			}
			strb.append(current());
		}
		
		//Include the current character
		if(isOnContent()) {
			strb.append(current());
		}
		
		
		return strb.toString(); 
	}
	
	public String readUntilBeforeBlankOr(char...occurrences) {
		StringBuilder strb = new StringBuilder();
		strb.append(current());
		while(next() && !isOnBlank() && !on(occurrences)) {
			strb.append(current());
		}
		
		
		return strb.toString();
	}
	
	public String readUntilBlankOr(char... occurrences) {
		
		StringBuilder strb = new StringBuilder();
		strb.append(current());
		while(!isOnBlank() && !on(occurrences) && next()) {
			strb.append(current());
		}
		
		
		return strb.toString();
	}
	
	public String readUntilBlankOrEnd() {
		StringBuilder strb = new StringBuilder();
		strb.append(current());
		while(!isOnBlank() && next()) {
			strb.append(current());
		}
		
		
		return strb.toString();
	}
	
	public boolean skipBlanksUntilFind(char occurrence) {
		while(isOnBlank() && current() != occurrence && next());
		
		return (isOnContent() && current() == occurrence);
	}
	
	public boolean skipUntil(char occurrence) {
		while(isOnContent() && current() != occurrence && next());
		
		return isOnContent();
	}
	
	/**
	 * Advance the cursor while the current char is (' ', '\n', '\r', '\t')
	 * @return Flag indicating if the buffer has more content to read using the StringBuffer.onContent() method
	 */
	public boolean skipBlanks() {
		while(isOnContent() && isOnBlank() && next());
		return isOnContent();
	}
	
	@Override
	public String toString() {
		return "[cursor=" + column + ";line=" + line + ";column=" + column + "]";
	}
}
