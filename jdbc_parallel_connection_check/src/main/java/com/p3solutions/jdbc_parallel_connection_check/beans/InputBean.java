package com.p3solutions.jdbc_parallel_connection_check.beans;

import org.kohsuke.args4j.Option;

public class InputBean {

    @Option(name = "-h", aliases = {"--host"}, usage = "Host/Ip Address", required = true)
    private String host;

    @Option(name = "-l", aliases = {"--port"}, usage = "Port Number", required = true)
    private String port;

    @Option(name = "-u", aliases = {"--userName"}, usage = "User Name", required = true)
    private  String userName;

    @Option(name = "-p", aliases = {"--password"}, usage = "Password", required = true)
    private String password;

    @Option(name = "-d", aliases = {"--dbName"}, usage = "DB Name", required = true)
    private String dbName;

    @Option(name = "-s", aliases = {"--schemaName"}, usage = "Schema Name", required = true)
    private String scheamName;

    @Option(name = "-mpp", aliases = {"--maximumParallelProcess"}, usage = "maximumParallelProcess", required = true)
    private Integer mpp;

    @Option(name = "-sl", aliases = {"--showlobs"}, usage = "showlobs", required = false)
    private Boolean sl = false;

    @Option(name = "-dt", aliases = {"--showDateWithTime"}, usage = "showDateWithTime", required = false)
    private Boolean dt = false;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getScheamName() {
        return scheamName;
    }

    public void setScheamName(String scheamName) {
        this.scheamName = scheamName;
    }


    public Integer getMpp() {
        return mpp;
    }

    public void setMpp(Integer mpp) {
        this.mpp = mpp;
    }

    public Boolean getSl() {
        return sl;
    }

    public void setSl(Boolean sl) {
        this.sl = sl;
    }

    public Boolean getDt() {
        return dt;
    }

    public void setDt(Boolean dt) {
        this.dt = dt;
    }

    @Override
    public String toString() {
        return "InputBean{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", dbName='" + dbName + '\'' +
                ", scheamName='" + scheamName + '\'' +
                ", mpp=" + mpp +
                ", sl=" + sl +
                ", dt=" + dt +
                '}';
    }
}
