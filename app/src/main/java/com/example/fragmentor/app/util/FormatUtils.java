package com.example.fragmentor.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.ITALY);

    public static String formatDate(Date date) {
        return dateFormatter.format(date);
    }

}
