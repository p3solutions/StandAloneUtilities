package com.p3.archon.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import com.p3.archon.beans.ArchonInputBean;

public class StartProcess {

	private ArchonInputBean ipargs;

	public StartProcess(ArchonInputBean ipargs) {
		this.ipargs = ipargs;
	}

	public void start() {

		fileProcessor(ipargs.getInputPath());
	}

	private void fileProcessor(String string) {

		File[] los = new File(string).listFiles();
		for (File file : los) {

			if (file.isDirectory()) {
				fileProcessor(file.getAbsolutePath());
			} else {
				try {
					processFile(file.getAbsoluteFile());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	private void processFile(File file) throws IOException {
		FileInputStream fs = new FileInputStream(file.getAbsolutePath());
		InputStreamReader isr = new InputStreamReader(fs, ipargs.getEncoding());
		BufferedReader br = new BufferedReader(isr);
		String line;

		String op = ipargs.getOutputPath() + File.separator + file.getName();
		new File(op).createNewFile();

		OutputStream outputStream = new FileOutputStream(op, false);
		Writer outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
//		outputStreamWriter.flush();
//		outputStreamWriter.write(
//				"Record Indicator, Issue Branch code,Destination Branch code, Remittance  No,Cheque Doc Type,Cheque Number,Cheque Amount,Cheque Status,Cheque Status description,Cheque Transaction Date,Cheque  Effective date,Cheque Settlement date,Applicant Name,Beneficiary  Name1,Beneficiary  Name2, Additional Field");
//		outputStreamWriter.write("\n");

		System.out.println("Processing " + file.getAbsolutePath());

		while ((line = br.readLine()) != null) {
			System.out.println(line.trim().length() + " " + line.trim());
			String Recored_Indicator = "";
			String Issue_Branch_code = "";
			String Destination_Branch_code = "";
			String Remittance_No = "";
			String Cheque_Doc_Type = "";
			String Cheque_Number = "";
			String Cheque_Amount = "";
			String Cheque_Status = "";
			String Cheque_Status_description = "";
			String Cheque_Transaction_Date = "";
			String Cheque_Effective_date = "";
			String Cheque_Settlement_date = "";
			String Applicant_Name = "";
			String Beneficiary_Name1 = "";
			String Beneficiary_Name2 = "";
			String Additional_Field = "";
			StringBuffer sb = new StringBuffer();

			try {
				Recored_Indicator = line.substring(0, 1);
				sb.append(Recored_Indicator).append(",");
			} catch (Exception e) {
			}
			try {
				Issue_Branch_code = line.substring(1, 5);
				sb.append(Issue_Branch_code).append(",");

			} catch (Exception e) {
			}
			try {
				Destination_Branch_code = line.substring(5, 9);
				sb.append(Destination_Branch_code).append(",");

			} catch (Exception e) {
			}
			try {
				Remittance_No = line.substring(9, 25);
				sb.append(Remittance_No).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Cheque_Doc_Type = line.substring(25, 27);
				sb.append(Cheque_Doc_Type).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Cheque_Number = line.substring(27, 37);
				sb.append(Cheque_Number).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Cheque_Amount = line.substring(37, 52);
				sb.append(Cheque_Amount).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}

			try {
				Cheque_Status = line.substring(52, 54);
				sb.append(Cheque_Status).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Cheque_Status_description = line.substring(54, 104);
				sb.append(Cheque_Status_description).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Cheque_Transaction_Date = line.substring(104, 110);
				sb.append(Cheque_Transaction_Date).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Cheque_Effective_date = line.substring(110, 116);
				sb.append(Cheque_Effective_date).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Cheque_Settlement_date = line.substring(116, 122);
				sb.append(Cheque_Settlement_date).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Applicant_Name = line.substring(122, 162);
				sb.append(Applicant_Name).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Beneficiary_Name1 = line.substring(162, 202);
				sb.append(Beneficiary_Name1).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Beneficiary_Name2 = line.substring(202, 242);
				sb.append(Beneficiary_Name2).append(",");

			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Additional_Field = line.substring(242, 350);
				sb.append(Additional_Field);

			} catch (Exception e) {
				// TODO: handle exception
			}

			outputStreamWriter.write(sb.toString());
			outputStreamWriter.write("\n");
		}
		outputStreamWriter.flush();
		outputStreamWriter.close();
		outputStream.close();
		fs.close();
		isr.close();
		br.close(); // closes the stream and release the resources
		System.out.println("Processed " + file.getAbsolutePath());

	}

}
