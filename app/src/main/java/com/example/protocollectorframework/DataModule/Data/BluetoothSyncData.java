package com.example.protocollectorframework.DataModule.Data;

import org.jetbrains.annotations.NotNull;

/**
 * Information associated with the moment of data exchange via bluetooth
 */
public class BluetoothSyncData {
    private String partner;
    private String timestamp;
    private boolean endVisitSync;

    /**
     * Constructor
     * @param partner: partner identifier
     * @param timestamp: data exchange timestamp
     * @param endVisitSync: last visit sync indicator
     */
    public BluetoothSyncData(String partner, String timestamp, boolean endVisitSync) {
        this.partner = partner;
        this.timestamp = timestamp;
        this.endVisitSync = endVisitSync;
    }

    /**
     * Returns the partner identifier
     * @return partner identifier
     */
    public String getPartner() {
        return partner;
    }

    /**
     * Sets the partner identifier
     * @param partner: partner identifier
     */
    public void setPartner(String partner) {
        this.partner = partner;
    }

    /**
     * Returns the data exchange timestamp
     * @return data exchange timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the data exchange timestamp
     * @param timestamp: data exchange timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Checks if it's the last synchronization moment
     * @return true if th's the last synchronization log, false otherwise
     */
    public boolean isEndVisitSync() {
        return endVisitSync;
    }

    /**
     * Sets the indicator for the last visit sync
     * @param endVisitSync: indicator
     */
    public void setEndVisitSync(boolean endVisitSync) {
        this.endVisitSync = endVisitSync;
    }

    /**
     * Returns the log associated with the data object
     * @return synchronization log
     */
    @NotNull
    public String toString() {
        return timestamp + "/Info/BluetoothSync: partner = " + getPartner() + ", lastSync = " + isEndVisitSync();
    }
}
