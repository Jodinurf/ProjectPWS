/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TugasProject.WSB2022;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.TugasProject.WSB2022.exceptions.NonexistentEntityException;
import com.TugasProject.WSB2022.exceptions.PreexistingEntityException;

/**
 *
 * @author Lenovo
 */
public class DataKtpJpaController implements Serializable {

    public DataKtpJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.TugasProject_WSB2022_jar_0.0.1-SNAPSHOTPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public DataKtpJpaController() {
        
    }

    public void create(DataKtp dataKtp) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dataKtp);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDataKtp(dataKtp.getNik()) != null) {
                throw new PreexistingEntityException("DataKtp " + dataKtp + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

public void edit(DataKtp dataKtp) throws NonexistentEntityException, Exception {
    EntityManager em = null;
    try {
        em = getEntityManager();
        em.getTransaction().begin();

        // Check if the DataKtp with the given nik exists before trying to update it
        DataKtp existingData = em.find(DataKtp.class, dataKtp.getNik());
        if (existingData == null) {
            throw new NonexistentEntityException("The dataKtp with nik " + dataKtp.getNik() + " no longer exists.");
        }

        // If the DataKtp exists, perform the update (merge) operation
        existingData.setAlamat(dataKtp.getAlamat());
        existingData.setNama(dataKtp.getNama());
        existingData.setPhoto(dataKtp.getPhoto());
        existingData.setTglLahir(dataKtp.getTglLahir());

        // Persist the updated entity
        em.merge(existingData);
        em.getTransaction().commit();
    } catch (Exception ex) {
        // Handle any exception that might occur
        throw ex;
    } finally {
        if (em != null) {
            em.close();
        }
    }
}


    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DataKtp dataKtp;
            try {
                dataKtp = em.getReference(DataKtp.class, id);
                dataKtp.getNik();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dataKtp with id " + id + " no longer exists.", enfe);
            }
            em.remove(dataKtp);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DataKtp> findDataKtpEntities() {
        return findDataKtpEntities(true, -1, -1);
    }

    public List<DataKtp> findDataKtpEntities(int maxResults, int firstResult) {
        return findDataKtpEntities(false, maxResults, firstResult);
    }

    private List<DataKtp> findDataKtpEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DataKtp.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public DataKtp findDataKtp(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DataKtp.class, id);
        } finally {
            em.close();
        }
    }

    public int getDataKtpCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DataKtp> rt = cq.from(DataKtp.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
