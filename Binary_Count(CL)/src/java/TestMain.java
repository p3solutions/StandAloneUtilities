import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import net.sf.JRecord.JRecordInterface1;
import net.sf.JRecord.Common.CommonBits;
import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.FieldDetail;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.LayoutDetail;
import net.sf.JRecord.Details.RecordDetail;
import net.sf.JRecord.Details.fieldValue.IFieldValue;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.JRecord.Numeric.ICopybookDialects;
import net.sf.JRecord.def.IO.builders.ICobolIOBuilder;

public class TestMain {

	public static void main(String[] args) throws FileNotFoundException, IOException {	

		ICobolIOBuilder iobCbl = JRecordInterface1.COBOL.newIOBuilder("C:\\Users\\91735\\Downloads\\RKP.CO.STEN09.PCDN.MARKER.INFO\\RKP.CO.STEN09.PCDN.MARKER.INFO\\STEN09C.cbl")
				.setOptimizeTypes(false);
		iobCbl.setFileOrganization(Constants.IO_FIXED_LENGTH).setFont("cp037")
				.setDialect(ICopybookDialects.FMT_MAINFRAME);

		int count = getRecCount(iobCbl.newReader(new FileInputStream(
				"C:\\Users\\91735\\Downloads\\RKP.CO.STEN09.PCDN.MARKER.INFO\\RKP.CO.STEN09.PCDN.MARKER.INFO\\RKP.CO.STEN09.PCDN.MARKER.INFO.bin")));

		copyFileByFieldNumber(
				iobCbl.newReader(new FileInputStream(
						"C:\\Users\\91735\\Downloads\\RKP.CO.STEN09.PCDN.MARKER.INFO\\RKP.CO.STEN09.PCDN.MARKER.INFO\\RKP.CO.STEN09.PCDN.MARKER.INFO.bin")),
				iobCbl.getLayout());

		System.out.println(" count is " + count);

	}

	public static int copyFileByFieldNumber(AbstractLineReader reader, LayoutDetail outSchema) throws IOException {

		AbstractLine inLine = reader.read();
		LayoutDetail inSchema = reader.getLayout();

		if (inSchema.getRecordCount() > 1) {
			throw new RecordException("Only one Record Type is allowed on the Input RecordLayout");
		}
		if (outSchema.getRecordCount() > 1) {
			throw new RecordException("Only one Record Type is allowed on the Output RecordLayout");
		}

		RecordDetail outRecordDef = outSchema.getRecord(0);
		RecordDetail inRecordDef = inSchema.getRecord(0);
		int fieldCount = outRecordDef.getFieldCount();
		int min = Math.min(fieldCount, inRecordDef.getFieldCount());

		ArrayList<FieldDetail> fieldMapping = new ArrayList<FieldDetail>(fieldCount);

		for (int i = 0; i < min; i++) {

			fieldMapping.add(inRecordDef.getField(i));
		}

		for (int i = min; i < fieldCount; i++) {
			fieldMapping.add(null);
		}

		return doCopy(reader, fieldMapping, inLine);
	}

	private static int doCopy(AbstractLineReader reader, List<FieldDetail> fieldMapping, AbstractLine inLine)
			throws IOException {

		int fldNum = 0;
		int lineNumber = 0;
		int fieldCount = fieldMapping.size();
		String v = "";
		try {
			while (inLine != null) {
				lineNumber += 1;

				for (fldNum = 0; fldNum < fieldCount; fldNum++) {
					v = "";
//					IFieldValue fieldValue = outLine.getFieldValue(0, fldNum);

//					if (fieldValue.isFieldInRecord()) {
					if (fieldMapping.get(fldNum) == null) {
						System.out.print(CommonBits.NULL_VALUE+" ");
					} else {
						IFieldValue sfv = inLine.getFieldValue(fieldMapping.get(fldNum));

						v = null;
						if (sfv.isFieldInRecord()) {
							v = sfv.asString();
						}

						if (v == null || v.length() == 0) {
							System.out.print(CommonBits.NULL_VALUE+" ");
						} else {
							System.out.print(v+" ");
						}
					}
//					}

				}
				System.out.println();
				
				inLine = reader.read();
			}
		} catch (Exception e) {
//			System.out.println("Error at Line" + lineNumber + ", field_Number: " + (fldNum + 1) + " Value=" + v + " " + e);
//			System.out.println();
			System.err.println(
					"Error at Line" + lineNumber + ", field_Number: " + (fldNum + 1) + " Value=" + v + " " + e);
			System.err.println();
			System.err.println();
			e.printStackTrace();

			throw new RuntimeException("Error at Line: " + lineNumber, e);
		} finally {
			reader.close();
		}
		System.out.println(lineNumber);
		return lineNumber;

	}

	public static int getRecCount(AbstractLineReader reader) throws IOException {

		AbstractLine inLine = reader.read();

		int lineNumber = 0;
		try {
			while (inLine != null) {

				lineNumber += 1;
				inLine = reader.read();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println();
			e.printStackTrace();

			throw new RuntimeException("Error at Line: " + lineNumber, e);
		} finally {
			reader.close();
		}
		return lineNumber;
	}
	
	public static String updateName(String name) {
		StringBuilder b = new StringBuilder(name.length());
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			
			if (specialChar(ch)) {
				if (b.length() == 0 || b.charAt(b.length() - 1) != '_') {
					b.append('_');
				}
			} else {
				b.append(ch);
			}
		}
		if (b.length() > 1 && b.charAt(b.length()-1) == '_') {
			b.setLength(b.length()-1);
		}
		return b.toString();
	}
	
	private static boolean specialChar(char ch) {
		boolean ret = false;
		switch (ch) {
		case '-':
		case ' ':
		case '(':
		case ')':
		case ',':
		case '.':
			ret = true;
			break;
		}
		return ret;
	}
	
	public static String updateNfame(String name) {
		StringBuilder b = new StringBuilder(name.length());
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (! (ch == '-' || ch == ' ')) {
				b.append(ch);
			}
		}
		return b.toString();
	}
}
