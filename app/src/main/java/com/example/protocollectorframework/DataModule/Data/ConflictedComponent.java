package com.example.protocollectorframework.DataModule.Data;

import java.io.Serializable;

public class ConflictedComponent implements Serializable {
    public ComponentData first ;
    public ComponentData second;

    public ConflictedComponent(ComponentData first, ComponentData second){
        this.first = first;
        this.second = second;
    }
}
