package org.mustream.common.sys;

import java.util.ArrayList;

public class StringUtils {
    public static String[] pointSplit(String str) {
        StringBuilder sbElements = new StringBuilder();
        ArrayList<String> listSplit = new ArrayList<>();
        final char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '.') {
                listSplit.add(sbElements.toString());
                sbElements.delete(0, sbElements.length());
            } else {
                sbElements.append(chars[i]);
            }
        }
        if (sbElements.length() > 0) {
            listSplit.add(sbElements.toString());
        }
        return listSplit.toArray(new String[listSplit.size()]);
    }
}
