package liqp;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import liqp.spi.SPIHelper;
public class LValueTest {
    @Test
    public void testIsTemporal() {
        // sorry for side effect, but this is a way the java plugin system works
        SPIHelper.applyCustomDateTypes();
        assertTrue(LValue.isTemporal(new java.util.Date(0l)));
    }
}
