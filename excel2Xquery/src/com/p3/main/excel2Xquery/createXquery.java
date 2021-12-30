package com.p3.main.excel2Xquery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.iterators.ArrayListIterator;

import com.p3.main.bean.inputBean;

import lombok.SneakyThrows;

public class createXquery {
	
	inputBean inputArgs = null;
	int sectionCount = 0;
	Writer out;
	LinkedHashMap<Integer,LinkedHashMap<String,ArrayList<String>>> sectionDetails= null;
	
	public createXquery(inputBean inputArgs,LinkedHashMap<Integer,LinkedHashMap<String,ArrayList<String>>> sectionDetails) throws IOException {
		this.inputArgs =inputArgs;
		this.sectionDetails = sectionDetails;
		String outputFilePath = inputArgs.getOutputPath()+File.separator+"outputQuery.txt";
		new File(outputFilePath).createNewFile();
		out = new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8);
	}
	
	public void startCreatingXquery() throws IOException
	{
		writeStartingContent();
		writeSections();
		writeEndContent();
	}
	
	@SneakyThrows
	private void write(String s) throws IOException {
		out.write(s);
		out.flush();
	}

	@SneakyThrows
	private void writeStartingContent() throws IOException {
		write("declare variable $personId external;\r\n"); 	
		write("<results>\r\n"); 
		write("\t<title>Risk Report</title>\n");
	}

	@SneakyThrows
	private void writeStartSectionTag() throws IOException {
		write("\t<section_"+sectionCount+">\n");
	}

	@SneakyThrows
	private void writeEndSectionTag() throws IOException {
		write("\t</section_"+sectionCount+">\n\n");
	}
	
	@SneakyThrows
	private void writeSections() throws IOException {
		
		  //removing non-relationship column 
		Set relation_set = sectionDetails.entrySet();
		  
		  Iterator relation_iterator = relation_set.iterator();
		  
		  List<String> nonRelationColumnList = new ArrayList<String>();
		  
		  while(relation_iterator.hasNext()) { 
		  
			  writeStartSectionTag();
			  
			  Map.Entry me = (Map.Entry)relation_iterator.next(); 
		  
			  int section = (int) me.getKey(); 
		  
			  System.out.println("section:"+section);
			  
			  LinkedHashMap<String,ArrayList<String>> tableDetails = (LinkedHashMap<String, ArrayList<String>>) me.getValue();

			  writeJoinandResults(tableDetails);
		      
		  writeEndSectionTag();
		  sectionCount++;
		  }
	}
	
	@SneakyThrows
	private void writeJoinandResults(LinkedHashMap<String, ArrayList<String>> tableDetails) throws IOException
	{
		String forContent = "";
		String resultContent = "";
		char tableVariable='A'; 
		String tablePrefix ="";
		Set relation_set1 = tableDetails.entrySet();
		  
	      Iterator relation_iterator1 = relation_set1.iterator();
	      
	      while(relation_iterator1.hasNext()) { 
			  
			  Map.Entry me1 = (Map.Entry)relation_iterator1.next(); 
		  
			  String tableName = (String) me1.getKey(); 
		      System.out.println("table:"+tableName);
		      
			  List<String> columnDetails = (List<String>) me1.getValue();
			  
			  forContent += "\t "+"for $"+tableVariable+" in /DBO/"+tableName.toUpperCase()+"/Row\n";
			  
			  for(String column:columnDetails)
			  {
				  System.out.println("column:"+column);
				  resultContent += "\t <"+column.toLowerCase()+">{$"+tableVariable+"/"+column.toUpperCase()+"/text()}</"+column.toLowerCase()+">\n";
			  }
			  tableVariable++;
	      }
	      
	      writeQueryContent(forContent,resultContent);
	}
	
	@SneakyThrows
	private void writeQueryContent(String forContent,String resultContent) throws IOException {
		write(" {\n"+forContent+"\n\treturn\n\t <Row>\n"+resultContent+"\t </ROW>\n }\n");
	}
	
	@SneakyThrows
	private void writeEndContent() throws IOException {
		write("\n</results>\n");
	}
}
