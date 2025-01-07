package com.TugasProject.WSB2022;

import java.util.Date;

public class DataKtpResponse {
    private String nik;
    private String nama;
    private Date tglLahir;
    private String alamat;
    private String photo; // Base64 encoded photo

    public DataKtpResponse(String nik, String nama, Date tglLahir, String alamat, String photo) {
        this.nik = nik;
        this.nama = nama;
        this.tglLahir = tglLahir;
        this.alamat = alamat;
        this.photo = photo;
    }

    // Getters and setters
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
