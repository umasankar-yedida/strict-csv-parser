package com.strictparser.csv;

import static com.strictparser.csv.Token.Type.INVALID;

/**
 * 
 * @author Uma Sankar [umasankar.yedida@gmail.com]
 * @version 1.0 Sep 1, 2015.
 */

final class Token {
	
	private static final int DEFAULT_LENGTH = 50;

	enum Type {
		INVALID,
		TOKEN,
		EOF,
		EOR
	}
	Token.Type type = INVALID;
	
	StringBuffer content = new StringBuffer(DEFAULT_LENGTH);
	
	boolean isReady;
	
	StringBuffer errorMessage = new StringBuffer(DEFAULT_LENGTH);
	
	void clear() {
		this.type = INVALID;
		this.content.setLength(0);
		this.isReady = false;
		this.errorMessage.setLength(0);
	}
}
