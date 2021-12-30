package com.p3.archon.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvValidationException;
import com.p3.archon.beans.ArchonInputBean;

public class StartProcess {

	private ArchonInputBean ipargs;

	Map<String, String> fileInfoMap = new HashMap<String, String>();

	Writer out = null;
	File file = null;

	public StartProcess(ArchonInputBean ipargs) throws IOException {
		this.ipargs = ipargs;
		file = new File(new File(ipargs.getCsvPath()).getParent() + File.separator + "ProcessedFileReportInfo.csv");
		out = new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath(), false));
		out.write(
				"\"Folder Path\", \"Original File Name\",\"File Name from CSV\",\"Converted File Name\",\"Converted\"");
		out.write("\n");
	}

	public void start() throws IOException {

		try {
			parseCSV();
		} catch (CsvValidationException | IOException e) {
			e.printStackTrace();
		}

		fileProcessor(ipargs.getInputPath());

		out.flush();
		out.close();
	}

	private void parseCSV() throws CsvValidationException, IOException {
		CSVParser parser = new CSVParserBuilder().withSeparator(',').withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
				.withIgnoreLeadingWhiteSpace(true).withIgnoreQuotations(false).withQuoteChar('"')
				.withStrictQuotes(false).build();
		FileReader filereader = new FileReader(ipargs.getCsvPath());
		CSVReader csvReader = new CSVReaderBuilder(filereader).withCSVParser(parser).build();
		String[] record;
		int iteration = 0;
		while ((record = csvReader.readNext()) != null) {
			iteration++;
			if (iteration <= 1)
				continue;

			String fn = record[ipargs.getOriginalFileName()];
			String ext = FilenameUtils.getExtension(fn);

			if (ext == "" || ext == null) {
				fn = fn +"."+ record[ipargs.getOriginalFileExtention()];
			}

			fileInfoMap.put(record[ipargs.getIdFileName()], fn);
		}
		csvReader.close();
		filereader.close();

	}

	private void fileProcessor(String string) {

		File[] los = new File(string).listFiles();
		for (File file : los) {

			if (file.isDirectory()) {
				fileProcessor(file.getAbsolutePath());
			} else {
				extentionAdder(file.getAbsoluteFile());
			}

		}
	}

	private void extentionAdder(File file) {
		String fileNameFromCSV = fileInfoMap.get(file.getName());
		String formatedFileName = getTextFormatted(fileNameFromCSV);
		String ext = FilenameUtils.getExtension(formatedFileName);
		String finalName = formatedFileName.replace("." + ext, "_" + file.getName() + "." + ext);
		String filePath = file.getParent() + File.separator + finalName;
		boolean flag = file.renameTo(new File(filePath));
		try {
			out.write(file.getParent() + "," + file.getName() + "," + fileNameFromCSV + "," + finalName + "," + flag);
			out.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String getTextFormatted(String string) {
		if (string == "" || string == null) {
			return "";
		}
		string = string.replaceAll("[\\/:?\"><|]", "_");
		return string;

	}
}
