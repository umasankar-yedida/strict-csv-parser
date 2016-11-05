package com.strictparser.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import static com.strictparser.csv.CSVConstants.CR;
import static com.strictparser.csv.CSVConstants.END_OF_STREAM;
import static com.strictparser.csv.CSVConstants.LF;

/**
 * <pre>
 * Helps to read the CSV file record by record on token based.
 * </pre>
 * 
 * @author Uma Sankar [umasankar.yedida@gmail.com]
 * @version 1.0 Sep 1, 2015.
 */

class CSVReader extends BufferedReader {
	
	@SuppressWarnings("unused")
	private long currPosition;
	private long lineCounter;
	private int lastChar;
	private int currPositionInCurrentLine;
	
	public CSVReader(Reader reader) {
		super(reader);
	}
	
	/**
	 * 
	 * @return
	 * 			-1, if EOF reached <br/>
	 * 			integer value for the character at current pointer in file
	 * @throws IOException
	 * 			on file read faiiled
	 */
	public int lookAhead() throws IOException {
		super.mark(1);
		final int c = super.read();
		super.reset();
		
		return c;
	}
	
	/**
	 * @return
	 * 			-1, if EOF reached
	 * 			integer value for the character at current pointer in file
	 * @throws IOException	
	 * 			on file read faiiled
	 */
	public int read() throws IOException {
		int c = super.read();
		if (c == CR || (c == LF && this.lastChar != CR)) {
			this.lineCounter++;
			this.currPositionInCurrentLine = 0;
		}
		this.lastChar = c;
		this.currPosition++;
		this.currPositionInCurrentLine++;
		return this.lastChar;
	}
	
	/**
	 * 
	 * @return
	 * 			integer value of previous character at current pointer in file
	 */
	public int getLastChar() {
		return this.lastChar;
	}
	/**
	 * 
	 * @return
	 * 			current pointer location in current line.
	 */
	public long getCurrPosition() {
		return this.currPositionInCurrentLine;
	}
	/**
	 * @return
	 * 			string - whole liine from the current file pointer position
	 * @throws IOException
	 * 			on failed to read from the current opened InputStream
	 */
	public String readLine() throws IOException {
		String line = super.readLine();
		
		if (line != null) {
			this.lastChar = LF;
			this.lineCounter++;
		} else {
			this.lastChar = END_OF_STREAM;
		}
		return line;
	}
	/**
	 * 
	 * @return
	 * 			current line number
	 */
	public long getCurrentLineNumber() {
		if (this.lastChar == CR || this.lastChar == LF || this.lastChar == END_OF_STREAM)
			return this.lineCounter;
		return this.lineCounter + 1;
	}
}
