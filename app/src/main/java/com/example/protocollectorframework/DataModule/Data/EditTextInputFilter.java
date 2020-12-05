package com.example.protocollectorframework.DataModule.Data;

import android.text.InputFilter;
import android.text.Spanned;

public class EditTextInputFilter implements InputFilter {

    private double min, max;

    public EditTextInputFilter(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public EditTextInputFilter(String min, String max) {
        this.min = Double.parseDouble(min);
        this.max = Double.parseDouble(max);
    }


    //dest antigo
    //source novo
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {

            try {
                double old = Double.parseDouble(dest.toString());
                if(old == max)
                    return "";
            }catch (Exception ignored){}

            if(source.toString().equals(".") || (min < 0 && source.toString().equals("-")))
                return null;

            double input = Double.parseDouble(dest.toString() + source.toString());

            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}