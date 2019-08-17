package com.jianzixing.webapp.service.system;

public enum SystemLevel {
    NORMAL(0), SYSTEM(1), HIDDEN(2);

    private int level;

    SystemLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
