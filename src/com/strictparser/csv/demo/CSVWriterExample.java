package com.strictparser.csv.demo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.strictparser.csv.CSVParser;
import com.strictparser.csv.CSVRecord;
import com.strictparser.csv.CSVWriter;


class Tester extends Thread {
	
	private String filePath;
	
	public Tester(String filePath) {
		this.filePath = filePath;
	}
	
	private void writeToCSV(String filePath) throws IOException {
		//String fileName = "C:\\Users\\uyedida\\Desktop\\new.csv";
		//FileWriter fileWriter = new FileWriter(filePath);
		String[] FILE_HEADER = {
			"Student_Id",
			"Student_No",
			"Student_Name",
			"Address",
			"Postal_Code",
			"Phone_No",
			"Registration_No",
			"Company",
			"Company_Address",
			"Company_Postal_Code",
		};
		CSVParser parser = new CSVParser(new FileReader(new File("C:\\Users\\uyedida\\Desktop\\Students1.csv")), ',', '"', true, false);
		List<CSVRecord> records = parser.getRecords();
		
		CSVWriter csvFileWriter = new CSVWriter(filePath, ',', '"');
		csvFileWriter.printRecord((Object[])FILE_HEADER);
		
		csvFileWriter.printRecords(records);
		csvFileWriter.close();
		parser.close();
	}
	
	public void run() {
		try {
			writeToCSV(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class CSVWriterExample {
	
	private String folderPath;
	
	public CSVWriterExample(String folderPath) {
		this.folderPath = folderPath;
	}
	
	public String getFolderPath(){
		return this.folderPath;
	}
	
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	
	public void startTest() {
		Tester t1 = new Tester(this.getFolderPath() + "new.csv");
		Tester t2 = new Tester(this.getFolderPath() + "new1.csv");
		Tester t3 = new Tester(this.getFolderPath() + "new2.csv");
		
		t1.start();
		t2.start();
		t3.start();
	}
	
	public static void main(String[] args) {
		CSVWriterExample test = new CSVWriterExample("C:\\Users\\uyedida\\Desktop\\");
		test.startTest();
	}
}
