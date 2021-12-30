package com.p3.archon.dboperations.dbmodel;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.p3.archon.dboperations.dbmodel.enums.DataAnalyticsJobStatus;
import com.p3.archon.dboperations.dbmodel.enums.DataAnalyticsJobType;

@SuppressWarnings("deprecation")
@Entity
@Table(name = "DATA_ANALYTICS_SESSION", indexes = {
		@Index(name = "data_analytics_job_details_index", columnList = "SESSION_ID"),
		@Index(name = "data_analytics_job_details_index1", columnList = "JOB_TYPE"),
		@Index(name = "data_analytics_job_details_index2", columnList = "LAST_ACCESSED_DT") })
@org.hibernate.annotations.Entity(dynamicUpdate = true)

public class DataAnalyticsSession {
	@Id
	@Column(name = "SESSION_ID")
	String sessionId;

	@Column(name = "TASK_NAME")
	String taskName;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "JOB_TYPE")
	DataAnalyticsJobType jobType;

	@Column(name = "CREATED_DT")
	Timestamp createdDt;

	@Column(name = "CREATED_BY")
	String createdBy;

	@Column(name = "LAST_ACCESSED_DT")
	Timestamp lastAccessDt;

	@Column(name = "LAST_ACCESSED_BY")
	String lastAccessedBy;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "JOB_STATUS")
	DataAnalyticsJobStatus jobStatus;

	@Column(name = "SAVED_JOB_DETAILS", columnDefinition = "json DEFAULT NULL")
	String savedJobDetails;

	@Column(name = "TOTAL_CONNECTIONS")
	String totalConnection;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public DataAnalyticsJobType getJobType() {
		return jobType;
	}

	public void setJobType(DataAnalyticsJobType jobType) {
		this.jobType = jobType;
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

	public DataAnalyticsJobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(DataAnalyticsJobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getSavedJobDetails() {
		return savedJobDetails;
	}

	public void setSavedJobDetails(String savedJobDetails) {
		this.savedJobDetails = savedJobDetails;
	}

	public String getTotalConnection() {
		return totalConnection;
	}

	public void setTotalConnection(String totalConnection) {
		this.totalConnection = totalConnection;
	}

}
