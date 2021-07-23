package com.auriga.TTApp1.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

@Service
public class FileImportService {

	/* Function to fetch csv records & return them */
	public List<Map<String, String>> csvToRecords(String[] headers, InputStream is) throws IOException {
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		CSVParser csvParser = new CSVParser(fileReader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		List<Map<String, String>> records = new ArrayList();

		Iterable<CSVRecord> csvRecords = csvParser.getRecords();

		for (CSVRecord csvRecord : csvRecords) {
			Map<String, String> record = new HashMap();
			for (String header : headers) {
				record.put(header, (String) csvRecord.get(header));
			}
			records.add(record);
		}

		return records;
	}
}
