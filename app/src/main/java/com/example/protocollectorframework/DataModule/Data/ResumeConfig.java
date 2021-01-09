package com.example.protocollectorframework.DataModule.Data;

import java.io.Serializable;

public class ResumeConfig implements Serializable {

    private boolean visit_data;
    private boolean visit_info;
    private boolean complementary_data;
    private boolean complementary_info;
    private boolean plot_data;
    private boolean plot_info;
    private boolean multimedia_count;
    private MethodData[] methods;


    public ResumeConfig(boolean visit_data, boolean visit_info, boolean complementary_data, boolean complementary_info, boolean plot_data, boolean plot_info, boolean multimedia_count, MethodData[] methods) {
        this.visit_data = visit_data;
        this.visit_info = visit_info;
        this.complementary_data = complementary_data;
        this.complementary_info = complementary_info;
        this.plot_data = plot_data;
        this.plot_info = plot_info;
        this.multimedia_count = multimedia_count;
        this.methods = methods;
    }

    public boolean isVisit_data() {
        return visit_data;
    }

    public void setVisit_data(boolean visit_data) {
        this.visit_data = visit_data;
    }

    public boolean isVisit_info() {
        return visit_info;
    }

    public void setVisit_info(boolean visit_info) {
        this.visit_info = visit_info;
    }

    public boolean isComplementary_data() {
        return complementary_data;
    }

    public void setComplementary_data(boolean complementary_data) {
        this.complementary_data = complementary_data;
    }

    public boolean isComplementary_info() {
        return complementary_info;
    }

    public void setComplementary_info(boolean complementary_info) {
        this.complementary_info = complementary_info;
    }

    public boolean isPlot_data() {
        return plot_data;
    }

    public void setPlot_data(boolean plot_data) {
        this.plot_data = plot_data;
    }

    public boolean isPlot_info() {
        return plot_info;
    }

    public void setPlot_info(boolean plot_info) {
        this.plot_info = plot_info;
    }

    public boolean isMultimedia_count() {
        return multimedia_count;
    }

    public void setMultimedia_count(boolean multimedia_count) {
        this.multimedia_count = multimedia_count;
    }

    public MethodData[] getMethods() {
        return methods;
    }

    public void setMethods(MethodData[] methods) {
        this.methods = methods;
    }
}
