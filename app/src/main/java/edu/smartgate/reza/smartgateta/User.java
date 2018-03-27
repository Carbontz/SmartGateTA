package edu.smartgate.reza.smartgateta;

import java.util.ArrayList;

/**
 * Created by Reza on 16-Dec-17.
 */

public class User {

    private int idUser;
    private int idStatus;
    private String nama;
    private String email;
    private String password;
    private String waktu;
    private String foto1;
    private String status;

    String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    private ArrayList<String> riwayat;

    ArrayList<String> getRiwayat() {
        return riwayat;
    }

    void setRiwayat(ArrayList<String> riwayat) {
        this.riwayat = riwayat;
    }

    String getWaktu() {
        return waktu;
    }

    void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    int getIdUser() {
        return idUser;
    }

    void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    String getNama() {
        return nama;
    }

    void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    User() {

    }

}
