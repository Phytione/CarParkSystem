package com.example.parkingsystem.Interfaces;

public class OtoparkInfo {

    public String aracSayisi;
    public String elektrikliAraclar;
    public String otoparkAdi;
    public String saatlikUcret;
    public String distance;
    public String bosAracSayisi;



    public OtoparkInfo(String aracSayisi,String elektrikliAraclar,String otoparkAdi, String saatlikUcret,String distance,String bosAracSayisi){
        this.otoparkAdi=aracSayisi;
        this.aracSayisi=elektrikliAraclar;
        this.elektrikliAraclar="Elektrikli Araç Sayısı: "+otoparkAdi;
        this.saatlikUcret="Saatlik Ücret: "+saatlikUcret+" TL";
        this.distance=distance+" km";
        this.bosAracSayisi=bosAracSayisi;
    }


    public String getOtoparkAdi(){
        return otoparkAdi;
    }
    public String getSaatlikUcret(){
        return saatlikUcret;
    }

}
