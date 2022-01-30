package com.example.e_v1;


import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties

public class Nfc_tag {
    public String nfctag;
    public int booth_num;

    public Nfc_tag(){

    }

    public Nfc_tag(String nfctag){
        this.nfctag = nfctag;
    }

    public Nfc_tag(String nfctag, int booth_num){
        this.nfctag = nfctag;
        this.booth_num = booth_num;
    }

}
