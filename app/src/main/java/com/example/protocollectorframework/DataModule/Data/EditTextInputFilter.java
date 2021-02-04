package com.example.protocollectorframework.DataModule.Data;

import android.text.InputFilter;
import android.text.Spanned;

public class EditTextInputFilter implements InputFilter {

    private double min, max;

    /**
     * Constructor that defines the minimum and maximum value
     *
     * @param min: minimum value
     * @param max: maximum value
     */
    public EditTextInputFilter(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Constructor that defines the minimum and maximum value received as a string value
     *
     * @param min: minimum value
     * @param max: maximum value
     */
    public EditTextInputFilter(String min, String max) {
        this.min = Double.parseDouble(min);
        this.max = Double.parseDouble(max);
    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {

            try {
                double old = Double.parseDouble(dest.toString());
                if (old == max)
                    return "";
            } catch (Exception ignored) {
            }

            if (source.toString().equals(".") || (min < 0 && source.toString().equals("-")))
                return null;

            double input = Double.parseDouble(dest.toString() + source.toString());

            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    /**
     * Checks if value c is contained in the interval [a,b]
     *
     * @param a: interval's left value
     * @param b: interval's right value
     * @param c: value to compare
     * @return true if c is bigger or equal than a and smaller or equal than b, false otherwise
     */
    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}