package com.ayurveda.patient.util;

public final class FullNameSplitter {

    private FullNameSplitter() {
    }

    public static String[] split(String fullName) {
        String trimmed = fullName.trim();
        int spaceIndex = trimmed.indexOf(' ');

        if (spaceIndex == -1) {
            return new String[]{trimmed, trimmed};
        }

        return new String[]{
                trimmed.substring(0, spaceIndex),
                trimmed.substring(spaceIndex + 1).trim()
        };
    }

}
