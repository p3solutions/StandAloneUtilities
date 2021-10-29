package com.p3solutions.jdbc_parallel_connection_check;


import com.p3solutions.jdbc_parallel_connection_check.beans.InputBean;
import com.p3solutions.jdbc_parallel_connection_check.process.StartProcess;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.sql.SQLException;
import java.util.Date;

public class JdbcParallelConnectionCheckApplication {

    public static void main(String[] args) {

        System.out.println("Job Started = " + new Date());

        if (args.length == 0) {
            System.err.println("No arguments specified.\nTerminating ... ");
            System.out.println("Job Terminated = " + new Date());
            System.exit(1);
        }

        final InputBean inputBean=new InputBean();
        final CmdLineParser parser = new CmdLineParser((Object) inputBean);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
            System.err.println("Please check arguments specified. \n" + e.getMessage() + "\nTerminating ... ");
            System.out.println("Job Terminated = " + new Date());
            System.exit(1);
        }

        StartProcess startProcess=new StartProcess(inputBean);
        try {
            startProcess.start();
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Job Completed = " + new Date());





    }

}
