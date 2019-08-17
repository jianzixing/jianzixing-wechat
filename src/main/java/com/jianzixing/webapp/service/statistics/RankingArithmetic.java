package com.jianzixing.webapp.service.statistics;

public interface RankingArithmetic {
    void run();

    String getName();

    /**
     * 小于0表示算法全部都适用
     *
     * @return
     */
    int getType();

    boolean canRun();
}
