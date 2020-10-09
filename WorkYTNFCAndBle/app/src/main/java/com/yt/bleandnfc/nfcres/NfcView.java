package com.yt.bleandnfc.nfcres;

public interface NfcView {

    void appendResponse(String response);
    void notNfcDevice();
    void notOpenNFC();
    void getNFCStatusOk();
}
