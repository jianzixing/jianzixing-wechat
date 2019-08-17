import org.junit.Test;
import org.mimosaframework.core.utils.calculator.CalcNumber;

import java.math.BigDecimal;

public class TestCalcNumber {
    @Test
    public void t1() {
        BigDecimal[] bigDecimals = CalcNumber.as(new BigDecimal("100.26")).suchAsDividePrice(5);
        for (BigDecimal b : bigDecimals) {
            System.out.println(b.toPlainString());
        }
    }

    @Test
    public void t2() {
        System.out.println(CalcNumber.as(new BigDecimal("100.325345")).toPrice());
    }

    @Test
    public void t3() {
        System.out.println(CalcNumber.as(new BigDecimal("100.325645")).toString(3));
    }
}
