package dev.shinyepo.resourcegenerator.util;

import java.text.NumberFormat;
import java.util.Locale;

public class GuiNumericUtil {
    public static final String[] SUFFIXES = {"", "k", "m", "b", "t"};

    public static String abbreviate(long number) {
        number = Math.abs(number);
        if (number < 1000) return String.valueOf(number);

        int exp = (int) (Math.log(number) / Math.log(1000));
        double scaled = number / Math.pow(1000, exp);

        return String.format("%.1f%s", scaled, SUFFIXES[exp]); // removes trailing .0
    }

    public static String format(long number) {
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        return nf.format(number);
    }
}
