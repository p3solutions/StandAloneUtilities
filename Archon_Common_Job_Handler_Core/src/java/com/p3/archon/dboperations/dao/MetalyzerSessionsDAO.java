package com.p3.archon.dboperations.dao;

import java.sql.Blob;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.p3.archon.core.ArchonDBUtils;
import com.p3.archon.dboperations.dbmodel.MetalyzerSessions;

public class MetalyzerSessionsDAO {

	public String addRecord(MetalyzerSessions ms) {
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
		return ms.getSessonId();
	}

	public MetalyzerSessions getAllRecordsByBlobColumn(String sessionId) {
		MetalyzerSessions jobs = new MetalyzerSessions();
		Transaction trns = null;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			Criteria crit = session.createCriteria(MetalyzerSessions.class);
			ProjectionList projList = Projections.projectionList();
			projList.add(Projections.property("blob1"));
			projList.add(Projections.property("blob2"));
			projList.add(Projections.property("blob3"));
			projList.add(Projections.property("blob4"));
			projList.add(Projections.property("blob5"));
			projList.add(Projections.property("blob6"));
			projList.add(Projections.property("blob7"));
			crit.setProjection(projList);
			crit.add(Restrictions.eq("sessonId", sessionId));
			List<String> rows = crit.list();
			for (Object r : rows) {
				Object[] row = (Object[]) r;
				jobs.setBlob1((Blob) row[0]);
				jobs.setBlob2((Blob) row[1]);
				jobs.setBlob3((Blob) row[2]);
				jobs.setBlob4((Blob) row[3]);
				jobs.setBlob5((Blob) row[4]);
				jobs.setBlob6((Blob) row[5]);
				jobs.setBlob7((Blob) row[6]);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return jobs;
	}

	public void deleteRecord(String maId) {
		Transaction trns = null;
		Session session = ArchonDBUtils.getSessionFactory().openSession();
		try {
			trns = session.beginTransaction();
			MetalyzerSessions maProfile = (MetalyzerSessions) session.load(MetalyzerSessions.class,
					maId);
			session.delete(maProfile);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			if (trns != null) {
				trns.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

}
