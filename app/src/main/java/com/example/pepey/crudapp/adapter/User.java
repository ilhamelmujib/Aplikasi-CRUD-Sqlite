package com.example.pepey.crudapp.adapter;

/**
 * Created by pepey on 9/24/17.
 */

public class User {
    private String nis, nama;

    public User(String nama, String nis) {
        this.nama = nama;
        this.nis = nis;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
