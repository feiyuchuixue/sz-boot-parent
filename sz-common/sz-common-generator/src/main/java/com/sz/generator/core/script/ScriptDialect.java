package com.sz.generator.core.script;

import java.util.Locale;

/**
 * Supported script SQL dialects.
 */
public enum ScriptDialect {

    MYSQL("mysql"), POSTGRESQL("postgresql");

    private final String code;

    ScriptDialect(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ScriptDialect from(String value) {
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
