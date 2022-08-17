package com.p3.tmb.property;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class parsePropertyFile {

	private Properties properties;
	private FileReader reader;

	public parsePropertyFile(String propertyFilePath) throws IOException {
		this.properties = new Properties();
		this.reader = new FileReader(propertyFilePath);
		properties.load(reader); 
	}

	public String getValue(String key) {
		return properties.getProperty(key);
	}
}
