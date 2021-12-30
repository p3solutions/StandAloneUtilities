package com.p3.archon.dboperations.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.p3.archon.core.ArchonDBUtils;
import com.p3.archon.dboperations.dbmodel.SalesForceMetalyzerSessions;

public class SalesForceMetalyzerSessionsDAO {

	public boolean addRecord(SalesForceMetalyzerSessions ms) {
		Transaction trns = null;
		boolean status = false;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			session.save(ms);
			session.getTransaction().commit();
			status = true;
		} catch (RuntimeException e) {
			if (trns != null)
				trns.rollback();
			status = false;
			throw e;
		} finally {
			session.close();
		}
		return status;
	}

	public boolean updateRecord(SalesForceMetalyzerSessions record) {
		Transaction trns = null;
		boolean status = false;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			session.saveOrUpdate(record);
			session.getTransaction().commit();
			status = true;
		} catch (RuntimeException e) {
			if (trns != null) {
				trns.rollback();
			}
			status = false;
			throw e;
		} finally {
			session.close();
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	public List<SalesForceMetalyzerSessions> getBaseRecords(String connURL, String userName, String userId) {
		List<SalesForceMetalyzerSessions> maSessions = new ArrayList<SalesForceMetalyzerSessions>();
		Transaction trns = null;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();

			maSessions = session.createQuery("from SalesForceMetalyzerSessions where USER_ID = '" + userId
					+ "' and CONNECTION_URL = '" + connURL + "' and USER_NAME = '" + userName + "'").list();

		} catch (RuntimeException e) {
			if (trns != null) {
				trns.rollback();
			}
			throw e;
		} finally {
			session.close();
		}
		return maSessions;
	}
}
