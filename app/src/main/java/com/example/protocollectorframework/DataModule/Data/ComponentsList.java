package com.example.protocollectorframework.DataModule.Data;

import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComponentsList/*<T>*/ {
    public static final int DEFAULT_SIZE = 10;
    HashMap<Integer, List<Pair<Integer, View>>> mComponentsList;

    public ComponentsList(int size){
        mComponentsList = new HashMap<Integer, List<Pair<Integer, View>>>(size);
        for(int i = 0; i<size;i++){
            mComponentsList.put(i,new ArrayList<Pair<Integer, View>>(DEFAULT_SIZE));
        }
    }

    public HashMap<Integer, List<Pair<Integer, View>>> getMap(){
        return mComponentsList;
    }

    public List<Pair<Integer, View>> getPairsForPoint(int point){
        return mComponentsList.get(point);
    }

    public void insert(View v, int point, int type){
        mComponentsList.get(point).add(new Pair(type,v));
    }

    public void deleteList(int point){
        mComponentsList.remove(point);
        mComponentsList.put(point, new ArrayList<Pair<Integer, View>>(DEFAULT_SIZE));
    }


}
