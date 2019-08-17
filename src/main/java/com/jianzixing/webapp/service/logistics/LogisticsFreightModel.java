package com.jianzixing.webapp.service.logistics;

import java.math.BigDecimal;

public class LogisticsFreightModel {
    private BigDecimal freight = new BigDecimal("0");

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        if (freight != null) {
            this.freight = freight;
        }
    }
}
