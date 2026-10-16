package com.c4.calculator

import com.c4.calculator.converter.UnitCategory
import com.c4.calculator.converter.UnitConverter
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConverterTest {
    @Test fun length() = assertEquals(1000.0, UnitConverter.convert(UnitCategory.LENGTH, 1.0, "km", "m"), 1e-9)
    @Test fun temp() = assertEquals(32.0, UnitConverter.convert(UnitCategory.TEMPERATURE, 0.0, "C", "F"), 1e-9)
    @Test fun data() = assertEquals(8.0, UnitConverter.convert(UnitCategory.DATA, 1.0, "byte", "bit"), 1e-9)
    @Test fun time() = assertEquals(60.0, UnitConverter.convert(UnitCategory.TIME, 1.0, "minute", "second"), 1e-9)
}
