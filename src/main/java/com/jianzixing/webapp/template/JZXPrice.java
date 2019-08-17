package com.jianzixing.webapp.template;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mimosaframework.core.utils.calculator.CalcNumber;

import java.util.List;

@FreemarkerComponent("JZXPrice")
public class JZXPrice implements TemplateMethodModelEx {

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() > 0) {
            String price = String.valueOf(arguments.get(0));
            return new PriceItem(price);
        }
        return new PriceItem("0");
    }

    public static class PriceItem {
        private String price;

        public PriceItem(String price) {
            if (NumberUtils.isNumber(price)) {
                this.price = price;
            }
        }

        public String getYuan() {
            String[] s = price.split("\\.");
            return s[0];
        }

        public String getFen() {
            String[] s = price.split("\\.");
            if (s.length > 1) {
                if (s[1].length() == 0) return "00";
                if (s[1].length() == 1) return s[1] + "0";
                return s[1];
            } else {
                return "00";
            }
        }

        @Override
        public String toString() {
            return CalcNumber.as(price).toPrice();
        }

        public String getNumber() {
            if (price != null) {
                String[] s1 = price.split("\\.");
                if (s1.length > 1 && Integer.parseInt(s1[1]) > 0) {
                    return this.toString();
                } else {
                    if (StringUtils.isNotBlank(s1[0])) {
                        return String.valueOf(Integer.parseInt(s1[0]));
                    }
                }
            }
            return "0";
        }
    }
}
