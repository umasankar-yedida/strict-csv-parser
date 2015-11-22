/*
 * Author: Uma Sankar Y 
 * */

package com.strictparser.csv;

import java.util.List;

/**
 * 
 * Datatype to hold a record in CSV file.
 * 
 * @author Uma Sankar [umasankar.yedida@gmail.com]
 * @version 1.0 Sep 1, 2015.
 */

public class CSVRecord {
	
	private List<String> columns;
	private long lineNumber;
	private int recordSize;
	private boolean isValidRecord;
	private String errorMessage;
	
	public CSVRecord(List<String> values, long lineNumber, boolean isValid, String exceptionMessage) {
		if (values == null)
			throw new NullPointerException("First parameter cannot be null");
		this.columns = values;
		this.lineNumber = lineNumber;
		this.recordSize = values.size();
		this.isValidRecord = isValid;
		this.errorMessage = exceptionMessage;
	}
	
	public int size() {
		return this.recordSize;
	}
	
	public List<String> getColumns() {
		return this.columns;
	}
	
	public long getLineNumber() {
		return this.lineNumber;
	}
	
	public void setLineNumber(long number) {
		this.lineNumber = number;
	}
	
	public boolean isValidRecord() {
		return this.isValidRecord;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
}
