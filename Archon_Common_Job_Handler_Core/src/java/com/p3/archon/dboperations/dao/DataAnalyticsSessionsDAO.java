package com.p3.archon.dboperations.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.p3.archon.core.ArchonDBUtils;
import com.p3.archon.dboperations.dbmodel.DataAnalyticsSession;
import com.p3.archon.dboperations.dbmodel.enums.DataAnalyticsJobStatus;

public class DataAnalyticsSessionsDAO {

	public DataAnalyticsSession getRecordsOfSession(String sessionId) {
		DataAnalyticsSession dataAnalyticsSession = null;
		Transaction trns = null;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			dataAnalyticsSession = session.get(DataAnalyticsSession.class, sessionId);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			session.flush();
			session.close();
		}
		return dataAnalyticsSession;
	}

	public boolean updateJobStatus(String sessionId, DataAnalyticsJobStatus jobStatus) {
		Transaction trns = null;
		boolean status = false;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			DataAnalyticsSession wd = (DataAnalyticsSession) session.get(DataAnalyticsSession.class, sessionId);
			wd.setJobStatus(jobStatus);
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
