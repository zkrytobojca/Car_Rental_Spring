package com.carrental.models.enums;

public enum AuthorityType {
    ROLE_ADMIN("ROLE_ADMIN"),

    ROLE_USER("ROLE_USER"),

    ROLE_OWNER("ROLE_OWNER");

    private final String value;

    AuthorityType(String value) {
        this.value = value;
    }

    public static AuthorityType fromValue(String value) {
        for (AuthorityType b : AuthorityType.values()) {
            if (String.valueOf(b.value).equals(value)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
