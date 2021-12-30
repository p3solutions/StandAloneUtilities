package com.p3.archon.dboperations.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.p3.archon.core.ArchonDBUtils;
import com.p3.archon.dboperations.dbmodel.MainFrameDB2MetalyzerSessions;

public class MainFrameDB2MetalyzerSessionsDAO {

	public boolean addRecord(MainFrameDB2MetalyzerSessions ms) {
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

	public boolean updateRecord(MainFrameDB2MetalyzerSessions record) {
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
	public List<MainFrameDB2MetalyzerSessions> getBaseRecords(String host, String port, String lib, String username,
			String userId) {
		List<MainFrameDB2MetalyzerSessions> maSessions = new ArrayList<MainFrameDB2MetalyzerSessions>();
		Transaction trns = null;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			maSessions = session.createQuery("from MainFrameDB2MetalyzerSessions where USER_ID = '" + userId + "' and HOST = '"
					+ host + ((port != null && !port.isEmpty()) ? ("' and PORT = '" + port) : "")
					+ ((lib != null && !lib.isEmpty()) ? ("' and LIB = '" + lib) : "") + "' and USER_NAME = '"
					+ username + "' and BASE_IND = '" + 1 + "'").list();
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
