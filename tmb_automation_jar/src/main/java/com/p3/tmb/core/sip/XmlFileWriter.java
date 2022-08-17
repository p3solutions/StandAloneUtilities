package com.p3.tmb.core.sip;

import com.p3.tmb.beans.outputXmlBean;
import com.p3.tmb.beans.propertyBean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class XmlFileWriter {

	private static FileWriter out = null;
	private String outputPath = null;
	private File outFile = null;
	private int rowCount = 0;
	private propertyBean propBean = null;
	private String tableName = null;

	public XmlFileWriter(propertyBean propBean, String fileName) throws IOException {
		this.propBean = propBean;
		outputPath = propBean.getOutputLocation() + File.separator + fileName;
		outFile = new File(outputPath);
		this.tableName = propBean.getTableName().toUpperCase();
		if (outFile.exists())
			outFile.delete();
		outFile.createNewFile();
		out = new FileWriter(outputPath);

	}

	public void writeStartTag() throws IOException {
		out.write("<TABLE_" + tableName + " xmlns=\"urn:x-emc:eas:schema:" + tableName + ":1.0\">\r\n");
		out.flush();
	}

	public void writeEndTag() throws IOException {
		out.write(" </TABLE_" + tableName + ">\n");
		out.flush();
	}

	public void writeRowTag(ArrayList<ArrayList<outputXmlBean>> outputBeanList) throws IOException {
		for (int i = 0; i < outputBeanList.size(); i++) {
			out.write("  <ROW_" + tableName + " REC_ID=\"" + (i + 1) + "\">\n");

			for (int j = 0; j < outputBeanList.get(i).size(); j++) {
				outputXmlBean xmlBean = outputBeanList.get(i).get(j);
				String columnName = xmlBean.getColumnName().toUpperCase();
				String value = xmlBean.getValue();
				out.write("     <" + columnName + ">" + value + "<" + columnName + ">\n");
			}
			out.write("  </ROW_" + tableName + ">\n");
			out.flush();

		}
	}

	public void writeSipXmlFile(int recordCount) throws IOException {
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String currentDateTime = dateFormat.format(currentDate); 
		String holding =propBean.getHolding()+"_"+propBean.getTableName();
		out.write("<sip xmlns=\"urn:x-emc:ia:schema:sip:1.0\">\r\n");
		out.write("  <dss>\r\n");
		out.write("    <holding>" + holding + "</holding>\r\n");
		out.write("    <id>ex6dss1</id>\r\n");
		out.write("    <pdi_schema>urn:x-emc:eas:schema:" + holding + ":1.0</pdi_schema>\r\n");
		out.write("    <production_date>"+currentDateTime+"</production_date>\r\n");
		out.write("    <base_retention_date>"+currentDateTime+"</base_retention_date>\r\n");
		out.write("    <producer>Archon</producer>\r\n");
		out.write("    <entity>Archon</entity>\r\n");
		out.write("    <priority>0</priority>\r\n");
		out.write("    <application>Sample</application>\r\n");
		out.write("  </dss>\r\n");
		out.write("  <production_date>"+currentDateTime+"</production_date>\r\n");
		out.write("  <seqno>1</seqno>\r\n");
		out.write("  <is_last>true</is_last>\r\n");
		out.write("  <aiu_count>"+recordCount+"</aiu_count>\r\n");
		out.write("  <page_count>0</page_count>\r\n");
		out.write("</sip>\r\n");
		out.flush();

	}
//	public void writeRowTagForBlob(outputXMLBean output) throws IOException {
//		out.write("  <ROW REC_ID=\""+(++rowCount)+"\">\n");
//		out.write("     <ID>"+output.getId()+"</ID>\n");
//		out.write("     <FILE_UPLOAD_ID>"+output.getFileUploadId()+"</FILE_UPLOAD_ID>\n");
//		if(!output.getRefAttr().isBlank())
//		out.write("     <FILE_DATA ref=\""+output.getRefAttr()+"\" size=\""+output.getSizeAttr()+"\" status=\""+output.getStatusAttr()+"\" type=\""+output.getTypeAttr()+"\">"+output.getFileData()+"</FILE_DATA>\n");
//		else
//		out.write("     <FILE_DATA null=\"true\">"+output.getFileData()+"</FILE_DATA>\n");
//		out.write("  </ROW>\n");
//		out.flush();
//	}

}
