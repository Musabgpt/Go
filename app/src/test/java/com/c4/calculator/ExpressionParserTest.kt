package com.c4.calculator

import com.c4.calculator.data.settings.AngleMode
import com.c4.calculator.engine.ExpressionParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ExpressionParserTest {
    private fun p(expr: String, deg: Boolean = true): Double =
        ExpressionParser(if (deg) AngleMode.DEG else AngleMode.RAD).evaluate(expr)

    @Test fun add() = assertEquals(5.0, p("2+3"), 1e-9)
    @Test fun sub() = assertEquals(6.0, p("10-4"), 1e-9)
    @Test fun mul() = assertEquals(42.0, p("6*7"), 1e-9)
    @Test fun div() = assertEquals(4.0, p("20/5"), 1e-9)
    @Test fun precedence() = assertEquals(14.0, p("2+3*4"), 1e-9)
    @Test fun parens() = assertEquals(20.0, p("(2+3)*4"), 1e-9)
    @Test fun decimals() = assertEquals(5.0, p("2.5+2.5"), 1e-9)
    @Test fun unary() = assertEquals(-3.0, p("-5+2"), 1e-9)
    @Test fun divZero() = assertThrows(ArithmeticException::class.java) { p("10/0") }
    @Test fun sqrt() = assertEquals(4.0, p("sqrt(16)"), 1e-9)
    @Test fun pow() = assertEquals(1024.0, p("2^10"), 1e-9)
    @Test fun sin() = assertEquals(0.5, p("sin(30)"), 1e-9)
    @Test fun cos() = assertEquals(1.0, p("cos(0)"), 1e-9)
    @Test fun log() = assertEquals(2.0, p("log(100)"), 1e-9)
    @Test fun ln() = assertEquals(1.0, p("ln(e)"), 1e-9)
    @Test fun fact() = assertEquals(120.0, p("5!"), 1e-9)
    @Test fun nested() = assertEquals(8.0, p("2*(3+1)"), 1e-9)
    @Test fun invalid() = assertThrows(Exception::class.java) { p("2++3") }
    @Test fun large() = assertThrows(Exception::class.java) { p("1e308*10") } // overflow -> ArithmeticException? evaluate checks infinite -> ArithmeticException
    @Test fun small() = assertEquals(0.000001, p("0.000001"), 1e-12)
    @Test fun factorialFn() = assertEquals(120.0, p("factorial(5)"), 1e-9)
}
