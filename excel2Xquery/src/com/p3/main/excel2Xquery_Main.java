package com.p3.main;

import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.p3.main.bean.inputBean;
import com.p3.main.excel2Xquery.processExcel2Xquery;

public class excel2Xquery_Main {

	public static void main(String[] args) throws CmdLineException, IOException {
		inputBean inputArgs = new inputBean();
		CmdLineParser parser = new CmdLineParser(inputArgs);
		parser.parseArgument(args);
		processExcel2Xquery process = new processExcel2Xquery(inputArgs);
		process.startProcess();
	}

}
