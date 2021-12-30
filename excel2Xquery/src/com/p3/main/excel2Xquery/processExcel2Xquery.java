package com.p3.main.excel2Xquery;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.p3.main.bean.inputBean;

public class processExcel2Xquery {

	inputBean inputArgs = null;
	
	LinkedHashMap<Integer,LinkedHashMap<String,ArrayList<String>>> sectionDetails = new LinkedHashMap<Integer, LinkedHashMap<String,ArrayList<String>>>();
	public processExcel2Xquery(inputBean inputArgs) {
    
		this.inputArgs = inputArgs;
	
	}
	
	public void startProcess() throws IOException
	{
		setExcelValueintoBean();
		createXqueryAsText();
	}
	
	private void setExcelValueintoBean() throws IOException {
  		FileInputStream fis = new FileInputStream(inputArgs.getInputPath());
		XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
		XSSFSheet mySheet = myWorkBook.getSheetAt(0);

		XSSFRow row = mySheet.getRow(0);

		int sectionIndex = -1;
		int tableIndex = -1;
		int columnIndex = -1;
		for (int i = 0; i < row.getLastCellNum(); i++) {

			if (row.getCell(i).getStringCellValue().trim().equals("Section Order"))
				sectionIndex = i;
			if (row.getCell(i).getStringCellValue().trim().equals("Physical Table"))
				tableIndex = i;
			if (row.getCell(i).getStringCellValue().trim().equals("Physical Column"))
				columnIndex = i;

		}

		Iterator<Row> rowIterator = mySheet.iterator();

		int index = 0;
		while (rowIterator.hasNext()) {
			Row rows = rowIterator.next();
    if(index!=0) {
    	    int secNo =-1;
			String tableName = rows.getCell(tableIndex).getStringCellValue();
			String columnName = rows.getCell(columnIndex).getStringCellValue();
			rows.getCell(sectionIndex).setCellType(CellType.STRING);
			if(rows.getCell(sectionIndex).getStringCellValue()!="")
			 secNo = Integer.parseInt(rows.getCell(sectionIndex).getStringCellValue());
		    if(secNo!=-1)
			if(sectionDetails.containsKey(secNo)){
		    	if(!sectionDetails.get(secNo).containsKey(tableName)) {
		    		ArrayList<String> colName = new ArrayList<String>();
		    		colName.add(columnName);
		    		sectionDetails.get(secNo).put(tableName,colName);
		    	}
		    	else
		    	{
		    		ArrayList<String> tableDetails = sectionDetails.get(secNo).get(tableName);
		    		tableDetails.add(columnName);
		    		sectionDetails.get(secNo).put(tableName,tableDetails);
		    	}
		    }
		      else
		      {
		    	  ArrayList<String> columnDetails = new ArrayList<String>();
		    	  columnDetails.add(columnName);
		    	  LinkedHashMap<String,ArrayList<String>> tableDetails = new LinkedHashMap<String, ArrayList<String>>();
		    	  tableDetails.put(tableName,columnDetails);
		    	  sectionDetails.put(secNo,tableDetails);
		      }
    }
    index++;
			}

		System.out.println("linked hash map:"+sectionDetails);
   }
	
  private void createXqueryAsText() {
     try {
    	 createXquery xquery = new createXquery(inputArgs, sectionDetails);
    	 xquery.startCreatingXquery();
     }
     catch(Exception e)
     {
    	 e.printStackTrace();
     }
  }

}
