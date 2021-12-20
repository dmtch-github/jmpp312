package jm.spring.bootstrap.entity;

public enum Roles {
    ADMIN,
    USER;

    public static final String SUPER_USER_NAME = "admin";
    public static final String SUPER_USER_PASSWORD = "admin";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_ADMIN = ROLE_PREFIX + ADMIN;
    public static final String ROLE_USER = ROLE_PREFIX + USER;
}
