package com.p3.archon.dboperations.dao;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.p3.archon.core.ArchonDBUtils;
import com.p3.archon.dboperations.dbmodel.DataAnalyticsConnections;
import com.p3.archon.dboperations.dbmodel.enums.DataAnalyticsJobStatus;

public class DataAnalysticsConnectionsDAO {

	public boolean addRecord(DataAnalyticsConnections record) {
		Transaction trns = null;
		boolean status = false;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			session.save(record);
			session.getTransaction().commit();
			status = true;
		} catch (RuntimeException e) {
			if (trns != null) {
				trns.rollback();
			}
			status = false;
			e.printStackTrace();
		} finally {
			session.close();
		}
		return status;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public List<DataAnalyticsConnections> getRecordsOfSession(String sessionId) {
		List<DataAnalyticsConnections> connectionInfos = new ArrayList<>();
		Transaction trns = null;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			connectionInfos = (List<DataAnalyticsConnections>) session.createQuery("from DataAnalyticsConnections where SESSION_ID = '" + sessionId
					+ "'").list();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			session.flush();
			session.close();
		}
		return connectionInfos;
	}

	public DataAnalyticsConnections getRecordsOfJob(String connectionId) {
		DataAnalyticsConnections jobDetail = new DataAnalyticsConnections();
		Transaction trns = null;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			jobDetail = (DataAnalyticsConnections) session.get(DataAnalyticsConnections.class, connectionId);
		} catch (RuntimeException e) {
			if (trns != null) {
				trns.rollback();
			}
			e.printStackTrace();
		} finally {
			session.flush();
			session.close();
		}
		return jobDetail;
	}

	public boolean updateJobStatus(String connectionId, DataAnalyticsJobStatus jobStatus, String message, String startTime, String endTime, Blob blob) {
		Transaction trns = null;
		boolean status = false;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			DataAnalyticsConnections wd = (DataAnalyticsConnections) session.get(DataAnalyticsConnections.class, connectionId);
			wd.setJobStatus(jobStatus);
			if (!message.isEmpty())
				wd.setMessage(message);
			wd.setStartTime(startTime);
			wd.setEndTime(endTime);
			if(blob != null)
				wd.setReport(blob);
			session.update(wd);
			session.getTransaction().commit();
			status = true;
		} catch (RuntimeException e) {
			if (trns != null) {
				trns.rollback();
			}
			status = false;
			e.printStackTrace();
		} finally {
			session.close();
		}
		return status;
	}
}
