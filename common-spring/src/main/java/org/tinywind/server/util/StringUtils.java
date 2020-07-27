package org.tinywind.server.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;

public class StringUtils {
    public static String slice(String str, int end) {
        return slice(str, 0, end);
    }

    public static String slice(final String str, final int begin, final int end) {
        if (end < 0) {
            return slice(str, begin, str.length() + end);
        }
        return str.substring(begin, end);
    }

    public static boolean imatches(String haystack, String regex) {
        return matches(haystack, regex, Pattern.CASE_INSENSITIVE);
    }

    public static boolean matches(String haystack, String regex, int flag) {
        return haystack != null && Pattern.compile(regex, flag).matcher(haystack).find();
    }

    public static String subStringBytes(String str, int byteLength) {
        // String 을 byte 길이 만큼 자르기.
        int retLength = 0;
        int tempSize = 0;
        int asc;
        if (str == null || "".equals(str) || "null".equals(str)) {
            str = "";
        }
        int length = str.length();
        for (int i = 1; i <= length; i++) {
            asc = str.charAt(i - 1);
            if (asc > 127) {
                if (byteLength >= tempSize + 2) {
                    tempSize += 2;
                    retLength++;
                } else {
                    return str.substring(0, retLength) + "..";
                }
            } else {
                if (byteLength > tempSize) {
                    tempSize++;
                    retLength++;
                }
            }
        }
        return str.substring(0, retLength);
    }

    public String makeMoneyType(String s) {
        if (s == null)
            return null;

        DecimalFormat decimalformat = new DecimalFormat();
        DecimalFormatSymbols decimalformatsymbols = new DecimalFormatSymbols();
        decimalformatsymbols.setGroupingSeparator(',');
        decimalformat.setGroupingSize(3);
        decimalformat.setDecimalFormatSymbols(decimalformatsymbols);

        try {
            double double1 = Double.parseDouble(s);
            return decimalformat.format(double1);
        } catch (Exception exception) {
            return s;
        }
    }

    public String makeMoneyType(int value) {
        String str = "-";
        if (value > 0)
            str = makeMoneyType(String.valueOf(value));
        return str;
    }

    public String time2String(long value) {
        if (value == 0)
            return "-";
        int hour = (int) value / 3600;
        int minute = (int) (value % 3600) / 60;
        int sec = (int) (value % 3600) % 60;
        String sHour = String.valueOf(hour);
        String sMinute = String.valueOf(minute);
        String sSec = String.valueOf(sec);
        if (hour < 10) {
            sHour = "0" + sHour;
        }
        if (minute < 10) {
            sMinute = "0" + sMinute;
        }
        if (sec < 10) {
            sSec = "0" + sSec;
        }
        return sHour + ":" + sMinute + ":" + sSec;
    }
}
