package com.p3.tmb.core.sip;

import com.p3.tmb.beans.propertyBean;
import com.p3.tmb.commonUtils.FileUtil;
import com.p3.tmb.constant.CommonSharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;


public class PdiSchemaGeneration {

	final Logger log = LogManager.getLogger(PdiSchemaGeneration.class.getName());

	private propertyBean propBean;
	private String schemaName;
	private String tableName;
	private String holdingName;
	private String outputPath;
	private Writer out;
	private static final String ENCODING = "UTF-8";
	private int level = 0;
	private String filePath;

	public PdiSchemaGeneration(propertyBean propBean) {
		this.propBean = propBean;
		this.schemaName = propBean.getSchemaName();
		this.outputPath = propBean.getOutputLocation();

	}

	public void startPdiSchema(String tablename, String holding_name, LinkedHashMap<String,String> columnMap) throws Exception {
		this.tableName = tablename;
		this.holdingName = holding_name + "_" + tableName;
		String MetaDataFolderPath = outputPath + File.separator + "PDI_SCHEMA";
		if (!FileUtil.checkForDirectory(MetaDataFolderPath))
			FileUtil.createDir(MetaDataFolderPath);
		filePath = MetaDataFolderPath + File.separator + "pdi-" + schemaName.toUpperCase() + "_"
				+ tableName.toUpperCase() + ".xsd";
		String nameSpace = "urn:x-emc:eas:schema:" + holdingName + ":1.0";
		out = new OutputStreamWriter(new FileOutputStream(new File(filePath)), ENCODING);
		out.write("<?xml version=\"1.0\" encoding=\"");
		out.write(ENCODING);
		out.write("\"?>");
		out.write(getTabSpace(level++)
				+ "<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" ");
		out.write("targetNamespace=\"");
		out.write(nameSpace + "\" ");
		out.write("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");
		out.write(">");
		out.write(getTabSpace(level++) + "<xs:element name=\"TABLE_" + tableName.toUpperCase() + "\">");
		out.write(getTabSpace(level++) + "<xs:complexType>");
		out.write(getTabSpace(level++) + "<xs:sequence>");
		out.write(getTabSpace(level++) + "<xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\""
				+ tableName.toUpperCase() + "_ROW\">");
		out.write(getTabSpace(level++) + "<xs:complexType>");
		out.write(getTabSpace(level++) + "<xs:sequence>");
		for (Map.Entry<String,String> column: columnMap.entrySet()) {
			
			if (column.getValue().equals("DATE") || column.getValue().equals("DATE TIME")) {
				out.write(getTabSpace(level) + "<xs:element name=\"" + column.getKey().toUpperCase()
						+ "\" type=\"" + "xs:date\" minOccurs=\"0\"/>");
			} else {
				out.write(getTabSpace(level) + "<xs:element name=\"" + column.getKey().toUpperCase()
						+ "\" type=\"" + getColumnType(column.getValue()) + "\" minOccurs=\"0\"/>");
//				if(column.getKey().toUpperCase().equals("REPORT")) {
//					out.write(getTabSpace(level) + "<xs:element name=\"REPORT_NAME\" type=\"" + getColumnType(column.getValue()) + "\" minOccurs=\"0\"/>");
//				}
//				else if(column.getKey().toUpperCase().equals("STATEMENT_DATE")) {
//					out.write(getTabSpace(level) + "<xs:element name=\"STATEMENT_DATE_DP\" type=\"string\" minOccurs=\"0\"/>");
//				}
			}
		}

		out.write(getTabSpace(level--) + "</xs:sequence>");
		out.write(getTabSpace(level) + "<xs:attribute name=\"REC_ID\" type=\"xs:long\" use=\"required\" />");
		out.write(getTabSpace(level--) + "</xs:complexType>");
		out.write(getTabSpace(level--) + "</xs:element>");
		out.write(getTabSpace(level--) + "</xs:sequence>");
		out.write(getTabSpace(level--) + "</xs:complexType>");
		out.write(getTabSpace(level--) + "</xs:element>");
		out.write("\n</xs:schema>");
		out.flush();
		out.close();
		log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  PDI schema file created");
		CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  PDI schema file created at " + MetaDataFolderPath + CommonSharedConstants.newLine);
	}

	
	
	private int i = 0;
	private String tabSpace = "\n";

	private String getTabSpace(int tabSize) {
		if (tabSize < 0)
			return "";
		i = 0;
		tabSpace = "\n";
		while (i++ != tabSize) {
			tabSpace += "\t";
		}
		return tabSpace;
	}

	private String getColumnType(String colType) {
		colType = colType.replace("IDENTITY", "").replace("identity", "").replace("Identity", "").trim();
		colType = colType.replace("UNSIGNED", "").replace("unsigned", "").replace("Unsigned", "").trim();
		colType = colType.replace("SIGNED", "").replace("signed", "").replace("Signed", "").trim();
		switch (colType.toUpperCase()) {
		case "CHAR":
		case "VARCHAR":
		case "TEXT":
		case "TINYTEXT":
		case "MEDIUMTEXT":
		case "LONGTEXT":
		case "USERDEFINED":
		case "DATETIME":
        case "TIMESTAMP":
        case "TIMESTAMP_WITH_TIMEZONE":
        case "TIMESTAMP WITH LOCAL TIME ZONE":
        case "SMALLDATETIME":
		case "LONGVARCHAR":
			return "xs:string";
		case "SMALLINT":
        case "TINYINT":
            return "xs:int";
		case "INTEGER":
		case "INT":
		case "AUTONUMBER":
		case "BIGINT":
		case "LONG":
		case "NUMERIC":
		case "NUMBER":	
			return "xs:long";
		case "DOUBLE":
		case "DECIMAL":
		case "MONEY":
		case "DEC":
		case "SMALLMONEY":
		case "BIGMONEY":
		case "CURRENCY":
			return "xs:double";
		case "FLOAT":
		case "REAL":
			return "xs:float";
		case "DATE":
			return "xs:date";
		case "TIME":
			return "xs:time";
		default:
			return "xs:string";
		}
	}

//	public void setValuesIntoTableBean(File file) throws NotesException, Exception {
//
//		log.info(file);
//		columnBean columnBean = null;
//		List<columnBean> columnList = null;
//
//		Iterator<Table> table = DatabaseBuilder.open(file).iterator();
//		while (table.hasNext()) {
//			Table table2 = (Table) table.next();
//			columnList = new ArrayList<columnBean>();
//
//			for (Column column : table2.getColumns()) {
//				columnBean = new columnBean();
//				columnBean.setColumnName(column.getName());
//				columnBean.setColumnType(column.getType().name());
//				columnList.add(columnBean);
//			}
//			startPdiSchema(table2.getName(), columnList);
//
//		}
//
//	}
}
