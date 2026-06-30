package com.ayurveda.appointment.common;

public final class RegexConstants {

    private RegexConstants() {
    }

    public static final String NAME =
            "^[A-Za-z ]{2,100}$";

    public static final String MOBILE =
            "^[6-9][0-9]{9}$";

    public static final String EMAIL =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static final String ALPHA_NUMERIC =
            "^[A-Za-z0-9\\- ]+$";

}
