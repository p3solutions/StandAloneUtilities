package com.p3.archon.dboperations.dbmodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@SuppressWarnings("deprecation")
@Entity
@Table(name = "SALESFORCE_DB_PROFILE", indexes = { @Index(name = "SALESFORCE_db_profile_index", columnList = "DB_ID"),
		@Index(name = "SALESFORCE_db_profile_index1", columnList = "USER_ID"),
		@Index(name = "SALESFORCE_db_profile_index2", columnList = "CONNECTION_URL") })
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class SalesForceDBProfile {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "DB_ID", nullable = false, columnDefinition = "varchar(255) NOT NULL")
	private String dbId;

	@Column(name = "USER_ID", nullable = false, columnDefinition = "varchar(255) NOT NULL")
	String userId;

	@Column(name = "PROFILE_NAME", nullable = false, columnDefinition = "varchar(255) NOT NULL")
	String profileName;

	@Column(name = "CONNECTION_URL", nullable = false, columnDefinition = "varchar(255) NOT NULL")
	String connURL;

	@Column(name = "USER_NAME", nullable = true, columnDefinition = "varchar(255) DEFAULT NULL")
	String userName;

	@Column(name = "PASSWORD", nullable = true, columnDefinition = "varchar(255) DEFAULT NULL")
	String userPwd;

	@Column(name = "SECRET_KEY", nullable = false, columnDefinition = "varchar(255) NOT NULL")
	String secKey;

	@Column(name = "CLIENT_ID", nullable = true, columnDefinition = "varchar(255) DEFAULT NULL")
	String clientID;

	@Column(name = "CLIENT_SECRET", nullable = false, columnDefinition = "varchar(255) NOT NULL")
	String clientSecret;

	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getConnURL() {
		return connURL;
	}

	public void setConnURL(String connURL) {
		this.connURL = connURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getSecKey() {
		return secKey;
	}

	public void setSecKey(String secKey) {
		this.secKey = secKey;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

}
