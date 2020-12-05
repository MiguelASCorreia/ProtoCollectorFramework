package com.example.protocollectorframework.InterfaceModule;

import java.util.ArrayList;

public class ProtocolsListener {
    private static final int DEFAULT_SIZE = 20;
    private ArrayList<String> mProtocols = new ArrayList<>(DEFAULT_SIZE);
    private ChangeListener listener;

    public int getCounter() {
        return mProtocols.size();
    }

    public ArrayList getSelected(){
        return mProtocols;
    }

    public void select(String protocol) {
        mProtocols.add(protocol);
        if (listener != null) listener.onChange();
    }

    public void deselect(String protocol) {
        mProtocols.remove(protocol);
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}