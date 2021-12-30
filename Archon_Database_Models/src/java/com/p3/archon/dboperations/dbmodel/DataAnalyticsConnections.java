package com.p3.archon.dboperations.dbmodel;

import java.sql.Blob;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.p3.archon.dboperations.dbmodel.enums.DataAnalyticsJobStatus;

@SuppressWarnings("deprecation")
@Entity
@Table(name = "DATA_ANALYTICS_CONNECTION_SESSION", indexes = {
		@Index(name = "data_analytics_job_details_index", columnList = "CONNECTION_ID"),
		@Index(name = "data_analytics_job_details_index2", columnList = "LAST_ACCESSED_DT") })
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class DataAnalyticsConnections {
	
	@Id
	@Column(name = "CONNECTION_ID")
	String connectionId;

	@Column(name = "SESSION_ID")
	String sessionId;

	@Column(name = "APP_NAME")
	String appName;

	@Column(name = "APP_ID")
	String appId;

	@Column(name = "HOST_NAME")
	String hostName;

	@Column(name = "PORT_NO")
	String portNo;

	@Column(name = "USER_NAME")
	String userName;

	@Column(name = "PASSWORD")
	String password;

	@Column(name = "DATABASE_NAME")
	String databaseName;

	@Column(name = "SCHEMA_NAME")
	String schemaName;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "JOB_STATUS")
	DataAnalyticsJobStatus jobStatus;
	

	@Column(name = "START_TIME")
	String startTime;

	@Column(name = "END_TIME")
	String endTime;

	@Column(name = "ERROR_MESSAGE")
	String errorMessage;

	@Column(name = "CREATED_DT")
	Timestamp createdDt;

	@Column(name = "CREATED_BY")
	String createdBy;

	@Column(name = "LAST_ACCESSED_DT")
	Timestamp lastAccessDt;

	@Column(name = "LAST_ACCESSED_BY")
	String lastAccessedBy;

	@Column(name = "SAVED_JOB_DETAILS", columnDefinition = "json DEFAULT NULL")
	String savedJobDetails;

	@Column(name = "MESSAGE")
	String message;
	
	@Column(name = "REPORT")
	Blob report;

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getPortNo() {
		return portNo;
	}

	public void setPortNo(String portNo) {
		this.portNo = portNo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public DataAnalyticsJobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(DataAnalyticsJobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Timestamp getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Timestamp createdDt) {
		this.createdDt = createdDt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getLastAccessDt() {
		return lastAccessDt;
	}

	public void setLastAccessDt(Timestamp lastAccessDt) {
		this.lastAccessDt = lastAccessDt;
	}

	public String getLastAccessedBy() {
		return lastAccessedBy;
	}

	public void setLastAccessedBy(String lastAccessedBy) {
		this.lastAccessedBy = lastAccessedBy;
	}

	public String getSavedJobDetails() {
		return savedJobDetails;
	}

	public void setSavedJobDetails(String savedJobDetails) {
		this.savedJobDetails = savedJobDetails;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public Blob getReport() {
		return report;
	}

	public void setReport(Blob report) {
		this.report = report;
	}

}
