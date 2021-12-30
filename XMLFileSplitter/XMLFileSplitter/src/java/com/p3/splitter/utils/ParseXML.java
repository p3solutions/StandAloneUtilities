package com.p3.splitter.utils;

import java.io.FileReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

/**
 * Class: ParseXML
 *
 * This class is used to parse XML files.
 *
 * @author Malik
 * @version 1.0
 *
 */
public class ParseXML {
	public void ParseXMLFile(String xmlFilePath) throws Exception {
		System.out.print("Parsing XML file : " + FileUtil.getFileNameFromPath(xmlFilePath)+". ");
		XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(xmlFilePath));
        while(eventReader.hasNext()){
            eventReader.nextEvent();
        }
        eventReader.close();
	}
}
