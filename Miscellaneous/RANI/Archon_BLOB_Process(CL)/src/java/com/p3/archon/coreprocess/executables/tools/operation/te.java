package com.p3.archon.coreprocess.executables.tools.operation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

public class te {

	public static void main(String[] args) throws FileNotFoundException, IOException {
//		for (String fileName : new DirectoryWalker()
//				.walk("/Users/malik/Documents/Work/GitHub/POC-DEV/AS400 Archon with LIC/src"))
//			if (fileName.endsWith(".xml") || fileName.endsWith(".XML"))
//				zipFile(fileName);

		List<String> queueColumnNameholder = new LinkedList<String>();
		List<String> queueValueholder = new LinkedList<String>();
		
		queueColumnNameholder.add("EFFYR");
		queueColumnNameholder.add("EFFMO");
		queueColumnNameholder.add("EFFDA");

		
		queueValueholder.add("2016");
		queueValueholder.add("6");
		queueValueholder.add("24");

		if (queueValueholder.size() == 3) {
			boolean status = checkforDate(queueValueholder, queueColumnNameholder);
			if (status) {
				queueValueholder.clear();
				queueColumnNameholder.clear();
			} else {
				queueValueholder.remove(0);
				queueColumnNameholder.remove(0);
			}
		}
	}

	public static void zipFile(String fileName) throws FileNotFoundException, IOException {
		File file = new File(fileName);

		FileOutputStream fos = new FileOutputStream(file.getParent() + File.separator + file.getName() + ".zip");
		ZipOutputStream zos = new ZipOutputStream(fos);

		System.out.println("Writing '" + file.getName() + "' to zip file");
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(file.getName());
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
		zos.close();
		fos.close();
		file.delete();
	}

	private static boolean checkforDate(List<String> queueValueholder, List<String> queueColumnNameholder) {
		SimilarityStrategy strategy = new JaroWinklerStrategy();
		StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
		double score1 = service.score(queueColumnNameholder.get(0), queueColumnNameholder.get(1));
		double score2 = service.score(queueColumnNameholder.get(0), queueColumnNameholder.get(2));
		double score3 = service.score(queueColumnNameholder.get(1), queueColumnNameholder.get(2));
		System.out.println(score1);
		System.out.println(score2);
		System.out.println(score3);
		if (score1 == score2 && score2 == score3) {
			if (queueValueholder.get(0).length() == 4
					&& (queueValueholder.get(1).length() == 2 || queueValueholder.get(1).length() == 1)
					&& (queueValueholder.get(2).length() == 2 || queueValueholder.get(2).length() == 1)) {
				String text = queueValueholder.get(0)
						+ (queueValueholder.get(1).length() == 2 ? queueValueholder.get(1)
								: "0" + queueValueholder.get(1))
						+ (queueValueholder.get(2).length() == 2 ? queueValueholder.get(2)
								: "0" + queueValueholder.get(1));
				if (isValidDate(text)) {
					String title = longestCommonSubstrings(queueColumnNameholder.get(0), queueColumnNameholder.get(1));
					if (title != null && !title.equals("")) {
						title = title + "_YMD_DT";
						System.out.print("<");
						System.out.print(title);
						System.out.print(">");
						System.out.print(text);
						System.out.print("</");
						System.out.print(title);
						System.out.println(">");
					}
				}
			}
		}
		return false;
	}

	public static boolean isValidDate(String inDate) {
		if (inDate == null)
			return false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	public static String longestCommonSubstrings(String s, String t) {
		int[][] table = new int[s.length()][t.length()];
		int longest = 0;
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < s.length(); i++) {
			for (int j = 0; j < t.length(); j++) {
				if (s.charAt(i) != t.charAt(j)) {
					continue;
				}
				table[i][j] = (i == 0 || j == 0) ? 1 : 1 + table[i - 1][j - 1];
				if (table[i][j] > longest) {
					longest = table[i][j];
					result.clear();
				}
				if (table[i][j] == longest) {
					result.add(s.substring(i - longest + 1, i + 1));
				}
			}
		}
		return result.get(0);
	}

}
