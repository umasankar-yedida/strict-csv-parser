package com.strictparser.csv.demo;

import static com.strictparser.csv.CSVConstants.DELIM_COMMA;
import static com.strictparser.csv.CSVConstants.SEPARATOR_DOUBLE_QUOTE;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.strictparser.csv.CSVParser;
import com.strictparser.csv.CSVRecord;
import com.strictparser.csv.CSVWriter;

public class ParseExample {

	public static void main(String[]args)throws Exception{
		readCSV();

		writeCSV();
	}

	public static void readCSV()throws Exception{
		CSVParser parser = new CSVParser(new FileReader(new File("C:\\Users\\uyedida\\Desktop\\test-blru.csv")));
		
		long startTime = System.currentTimeMillis();
		
		List<CSVRecord> records = parser.getRecords();
		
		long endTime = System.currentTimeMillis();
		float result = (endTime - startTime);
		
		System.out.println("Parsed " + records.size() + " records in " + result + " milliseconds");
		System.out.println(records.size());
		
		Iterator<CSVRecord> recordIterator = records.iterator();
		
		startTime = System.currentTimeMillis();
		
		while(recordIterator.hasNext()) {
			CSVRecord record = recordIterator.next();
			System.out.println("Line Number: " + record.getLineNumber() + ", Column count: " + record.size() + ", isValid: " + record.isValidRecord());
		}
		endTime = System.currentTimeMillis();
		
		result = endTime - startTime;
		System.out.println(result);
		parser.close();
	}

	public static void writeCSV()throws Exception{
		String fileName = "C:\\Users\\uyedida\\Desktop\\new.csv";
		FileWriter fileWriter = new FileWriter(fileName);
		Object[] FILE_HEADER = {
			"id",
			"firstName",
			"lastName",
			"gender",
			"age"
		};
		CSVWriter csvFilePrinter = new CSVWriter(fileWriter, ',', '"');
		csvFilePrinter.printRecord(FILE_HEADER);
		String fName = "\"K\" Line";
		//fName = "2";
				
		List<String> studentDataRecord = new ArrayList<String>();
		studentDataRecord.add(new String("1") );
		studentDataRecord.add(fName);
		studentDataRecord.add("\"Y\"");
		studentDataRecord.add("Male");
		studentDataRecord.add("21");
		csvFilePrinter.printRecord(studentDataRecord);
		System.out.println("Hello");
		csvFilePrinter.close();

	}

}
