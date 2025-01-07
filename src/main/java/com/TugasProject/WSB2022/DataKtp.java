/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TugasProject.WSB2022;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "data_ktp")
@NamedQueries({
    @NamedQuery(name = "DataKtp.findAll", query = "SELECT d FROM DataKtp d"),
    @NamedQuery(name = "DataKtp.findByNik", query = "SELECT d FROM DataKtp d WHERE d.nik = :nik"),
    @NamedQuery(name = "DataKtp.findByNama", query = "SELECT d FROM DataKtp d WHERE d.nama = :nama"),
    @NamedQuery(name = "DataKtp.findByTglLahir", query = "SELECT d FROM DataKtp d WHERE d.tglLahir = :tglLahir")})
public class DataKtp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "nik")
    private String nik;
    @Basic(optional = false)
    @Column(name = "nama")
    private String nama;
    @Basic(optional = false)
    @Column(name = "tgl_lahir")
    @Temporal(TemporalType.DATE)
    private Date tglLahir;
    @Basic(optional = false)
    @Lob
    @Column(name = "alamat")
    private String alamat;
    @Basic(optional = false)
    @Lob
    @Column(name = "photo")
    private byte[] photo;

    public DataKtp() {
    }

    public DataKtp(String nik) {
        this.nik = nik;
    }

    public DataKtp(String nik, String nama, Date tglLahir, String alamat, byte[] photo) {
        this.nik = nik;
        this.nama = nama;
        this.tglLahir = tglLahir;
        this.alamat = alamat;
        this.photo = photo;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Date getTglLahir() {
        return tglLahir;
    }

    public void setTglLahir(Date tglLahir) {
        this.tglLahir = tglLahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nik != null ? nik.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataKtp)) {
            return false;
        }
        DataKtp other = (DataKtp) object;
        if ((this.nik == null && other.nik != null) || (this.nik != null && !this.nik.equals(other.nik))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.TugasProject.WSB2022.DataKtp[ nik=" + nik + " ]";
    }    
}
