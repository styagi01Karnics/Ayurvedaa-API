package com.ayurveda.appointment.util;

public final class AppRegex {

    private AppRegex() {
    }

    /**
     * Name (Only alphabets and spaces)
     */
    public static final String NAME =
            "^[A-Za-z ]{2,100}$";

    /**
     * Email
     */
    public static final String EMAIL =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * Indian Mobile Number
     */
    public static final String MOBILE =
            "^[6-9]\\d{9}$";

    /**
     * UUID
     */
    public static final String UUID =
            "^[0-9a-fA-F]{8}-"
          + "[0-9a-fA-F]{4}-"
          + "[0-9a-fA-F]{4}-"
          + "[0-9a-fA-F]{4}-"
          + "[0-9a-fA-F]{12}$";

    /**
     * PIN Code
     */
    public static final String PIN_CODE =
            "^[1-9][0-9]{5}$";

}