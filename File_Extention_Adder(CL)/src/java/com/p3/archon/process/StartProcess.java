package com.p3.archon.process;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;

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
				extentionAdder(file.getAbsoluteFile());
			}

		}
	}

	private void extentionAdder(File file) {

		Tika tika = new Tika();
		String type = null;
		try {
			type = tika.detect(file);
		} catch (IOException e) {
			e.printStackTrace();
			type = "";
		}
		System.out.println("Processing ->" + file);
		String filePath = file.getParent() + File.separator + file.getName() + extentionFinder(type);
		file.renameTo(new File(filePath));
		System.out.println("Completed ->" + file);
	}

	private String extentionFinder(String mime) {
		System.out.println(mime);
		switch (mime) {
		case "image/tiff":
			return ".tiff";
		case "audio/aac":
			return ".aac";
		case "application/x-abiword":
			return ".abw";
		case "application/x-freearc":
			return ".arc";
		case "video/x-msvideo":
			return ".avi";
		case "application/vnd.amazon.ebook":
			return ".azw";
		case "application/octet-stream":
			return ".bin";
		case "image/bmp":
			return ".bmp";
		case "application/x-bzip":
			return ".bz";
		case "application/x-bzip2":
			return ".bz2";
		case "application/x-csh":
			return ".csh";
		case "text/css":
			return ".css";
		case "text/csv":
			return ".csv";
		case "application/msword":
			return ".doc";
		case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
			return ".docx";
		case "application/vnd.ms-fontobject":
			return ".eot";
		case "application/epub+zip":
			return ".epub";
		case "application/gzip":
			return ".gz";
		case "image/gif":
			return ".gif";
		case "text/html":
			return ".html";
		case "image/vnd.microsoft.icon":
			return ".ico";
		case "text/calendar":
			return ".ics";
		case "application/java-archive":
			return ".jar";
		case "image/jpeg":
			return ".jpeg";
		case "text/javascript":
			return ".js";
		case "application/json":
			return ".json";
		case "application/ld+json":
			return ".jsonld";
		case "audio/midi audio/x-midi":
			return ".mid";
		case "audio/mpeg":
			return ".mp3";
		case "video/mpeg":
			return ".mpeg";
		case "application/vnd.apple.installer+xml":
			return ".mpkg";
		case "application/vnd.oasis.opendocument.presentation":
			return ".odp";
		case "application/vnd.oasis.opendocument.spreadsheet":
			return ".ods";
		case "application/vnd.oasis.opendocument.text":
			return ".odt";
		case "audio/ogg":
			return ".oga";
		case "video/ogg":
			return ".ogv";
		case "application/ogg":
			return ".ogx";
		case "audio/opus":
			return ".opus";
		case "font/otf":
			return ".otf";
		case "image/png":
			return ".png";
		case "application/pdf":
			return ".pdf";
		case "application/x-httpd-php":
			return ".php";
		case "application/vnd.ms-powerpoint":
			return ".ppt";
		case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
			return ".pptx";
		case "application/vnd.rar":
			return ".rar";
		case "application/rtf":
			return ".rtf";
		case "application/x-sh":
			return ".sh";
		case "image/svg+xml":
			return ".svg";
		case "application/x-shockwave-flash":
			return ".swf";
		case "application/x-tar":
			return ".tar";
		case "video/mp2t":
			return ".ts";
		case "font/ttf":
			return ".ttf";
		case "text/plain":
			return ".txt";
		case "application/vnd.visio":
			return ".vsd";
		case "audio/wav":
			return ".wav";
		case "audio/webm":
			return ".weba";
		case "video/webm":
			return ".webm";
		case "image/webp":
			return ".webp";
		case "font/woff":
			return ".woff";
		case "font/woff2":
			return ".woff2";
		case "application/xhtml+xml":
			return ".xhtml";
		case "application/vnd.ms-excel":
			return ".xls";
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
			return ".xlsx";
		case "application/xml":
		case "text/xml":
			return ".xml";
		case "application/vnd.mozilla.xul+xml":
			return ".xul";
		case "application/zip":
			return ".zip";
		case "video/3gpp":
		case "audio/3gpp":
			return ".3gp";
		case "video/3gpp2":
		case "audio/3gpp2":
			return ".3g2";
		case "application/x-7z-compressed":
			return ".7z";

		default:
			break;
		}
		return "";
	}

}
