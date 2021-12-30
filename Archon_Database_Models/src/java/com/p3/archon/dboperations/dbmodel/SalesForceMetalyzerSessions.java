package com.p3.archon.dboperations.dbmodel;

import java.sql.Blob;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@SuppressWarnings("deprecation")
@Entity
@Table(name = "SALESFORCE_METALYZER_SESSIONS", indexes = { @Index(name = "ma_index", columnList = "USER_ID"),
		@Index(name = "ma_index1", columnList = "CONNECTION_URL"),
		@Index(name = "ma_index2", columnList = "USER_NAME"), })
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class SalesForceMetalyzerSessions {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "SESSION_ID", nullable = false, columnDefinition = "varchar(255) NOT NULL")
	private String sessonId;

	@Column(name = "USER_ID", nullable = false, columnDefinition = "varchar(255) NOT NULL")
	String userId;

	@Column(name = "CONNECTION_URL", nullable = false, columnDefinition = "varchar(255) NOT NULL")
	String connURL;

	@Column(name = "USER_NAME", nullable = true, columnDefinition = "varchar(255) DEFAULT NULL")
	String userName;

	@Column(name = "BLOB_TABLELIST", nullable = true)
	Blob blobTableList;

	@Column(name = "BLOB_1", nullable = false)
	Blob blob1;

	@Column(name = "BLOB_2", nullable = false)
	Blob blob2;

	@Column(name = "BLOB_3", nullable = false)
	Blob blob3;

	@Column(name = "PROFILE_NAME", nullable = false, columnDefinition = "varchar(255) NOT NULL")
	String profileName;

	@Column(name = "SAVE_DATE", nullable = true, columnDefinition = "datetime(6) DEFAULT NULL")
	Timestamp saveDate;

	public String getConnURL() {
		return connURL;
	}

	public void setConnURL(String connURL) {
		this.connURL = connURL;
	}

	public String getSessonId() {
		return sessonId;
	}

	public void setSessonId(String sessonId) {
		this.sessonId = sessonId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Blob getBlobTableList() {
		return blobTableList;
	}

	public void setBlobTableList(Blob blobTableList) {
		this.blobTableList = blobTableList;
	}

	public Blob getBlob1() {
		return blob1;
	}

	public void setBlob1(Blob blob1) {
		this.blob1 = blob1;
	}

	public Blob getBlob2() {
		return blob2;
	}

	public void setBlob2(Blob blob2) {
		this.blob2 = blob2;
	}

	public Blob getBlob3() {
		return blob3;
	}

	public void setBlob3(Blob blob3) {
		this.blob3 = blob3;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public Timestamp getSaveDate() {
		return saveDate;
	}

	public void setSaveDate(Timestamp saveDate) {
		this.saveDate = saveDate;
	}
}
