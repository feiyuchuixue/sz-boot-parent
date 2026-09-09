package com.sz.admin.system.script;

import java.util.Locale;

/**
 * Supported script SQL dialects.
 */
public enum AdminScriptDialect {

    MYSQL("mysql"), POSTGRESQL("postgresql");

    private final String code;

    AdminScriptDialect(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AdminScriptDialect from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        if (normalized.contains("postgres")) {
            return POSTGRESQL;
        }
        if (normalized.contains("mysql")) {
            return MYSQL;
        }
        return null;
    }
}
