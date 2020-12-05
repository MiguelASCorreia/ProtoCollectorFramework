package com.example.protocollectorframework.DataModule.Data;

import org.jetbrains.annotations.NotNull;

public class BluetoothSyncData {
    private String partner;
    private String timestamp;
    private boolean endVisitSync;

    public BluetoothSyncData(String partner, String timestamp, boolean endVisitSync) {
        this.partner = partner;
        this.timestamp = timestamp;
        this.endVisitSync = endVisitSync;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEndVisitSync() {
        return endVisitSync;
    }

    public void setEndVisitSync(boolean endVisitSync) {
        this.endVisitSync = endVisitSync;
    }

    @NotNull
    public String toString() {
        return timestamp + "/Info/BluetoothSync: partner = " + getPartner() + ", lastSync = " + isEndVisitSync();
    }
}
