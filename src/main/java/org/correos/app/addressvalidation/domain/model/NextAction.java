package org.correos.app.addressvalidation.domain.model;

public enum NextAction {
    ACCEPT, REVIEW, CONFIRM, FIX, UNKNOWN;

    public static NextAction fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return NextAction.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
