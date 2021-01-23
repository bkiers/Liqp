package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.filters.Date;
import liqp.spi.SPIHelper;
import org.junit.Test;

import static org.junit.Assert.*;
public class LValueTest {
    @Test
    public void testIsTemporal() {
        // sorry for side effect, but this is a way the java plugin system works
        assertTrue(LValue.isTemporal(new java.util.Date(0l)));
    }
}
