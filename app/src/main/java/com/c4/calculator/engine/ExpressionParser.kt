package com.c4.calculator.engine

enum class AngleMode { DEG, RAD }

class ExpressionParser(private val angleMode: AngleMode = AngleMode.DEG) {

    private sealed class Token {
        data class Number(val value: Double) : Token()
        data class Identifier(val name: String) : Token()
        data class Operator(val op: String) : Token()
        object LParen : Token()
        object RParen : Token()
        object Comma : Token()
        object Eof : Token()
    }

    private var tokens: List<Token> = emptyList()
    private var pos: Int = 0

    fun evaluate(input: String): Double {
        val expr = input.trim()
        if (expr.isEmpty()) return 0.0
        tokens = tokenize(expr)
        pos = 0
        val result = parseExpression()
        if (current() !is Token.Eof) throw IllegalArgumentException("Unexpected token")
        if (result.isNaN()) throw ArithmeticException("NaN")
        if (result.isInfinite()) throw ArithmeticException("Overflow")
        return result
    }

    private fun current(): Token = tokens.getOrElse(pos) { Token.Eof }
    private fun advance() { if (pos < tokens.size) pos++ }
    private fun matchOp(op: String): Boolean {
        val t = current()
        if (t is Token.Operator && t.op == op) { advance(); return true }
        return false
    }
    private fun matchToken(tok: Token): Boolean {
        if (current()::class == tok::class && current() == tok) { advance(); return true }
        return false
    }

    private fun tokenize(s: String): List<Token> {
        val out = mutableListOf<Token>()
        var i = 0
        val str = s.replace(" ", "")
        while (i < str.length) {
            val c = str[i]
            when {
                c.isDigit() || c == '.' -> {
                    if (c == '0' && i + 1 < str.length && (str[i + 1] == 'x' || str[i + 1] == 'X' ||
                            str[i + 1] == 'b' || str[i + 1] == 'B' || str[i + 1] == 'o' || str[i + 1] == 'O')) {
                        val base = when (str[i + 1].lowercaseChar()) {
                            'x' -> 16; 'b' -> 2; 'o' -> 8; else -> 10
                        }
                        i += 2
                        val start = i
                        while (i < str.length && (str[i].isDigit() || (base == 16 && str[i] in "abcdefABCDEF"))) i++
                        val raw = str.substring(start, i)
                        if (raw.isEmpty()) throw IllegalArgumentException("Invalid number")
                        out.add(Token.Number(raw.toLong(base).toDouble()))
                    } else {
                        val start = i
                        while (i < str.length && (str[i].isDigit() || str[i] == '.')) i++
                        if (i < str.length && (str[i] == 'e' || str[i] == 'E')) {
                            val j = i + 1
                            if (j < str.length && (str[j].isDigit() || str[j] == '+' || str[j] == '-')) {
                                i++
                                if (i < str.length && (str[i] == '+' || str[i] == '-')) i++
                                while (i < str.length && str[i].isDigit()) i++
                            }
                        }
                        val raw = str.substring(start, i)
                        if (raw == "." || raw.isEmpty()) throw IllegalArgumentException("Invalid number")
                        out.add(Token.Number(raw.toDouble()))
                    }
                }
                c.isLetter() -> {
                    val start = i
                    while (i < str.length && str[i].isLetter()) i++
                    out.add(Token.Identifier(str.substring(start, i)))
                }
                else -> {
                    when {
                        str.startsWith("<<", i) -> { out.add(Token.Operator("<<")); i += 2 }
                        str.startsWith(">>", i) -> { out.add(Token.Operator(">>")); i += 2 }
                        c == '(' -> { out.add(Token.LParen); i++ }
                        c == ')' -> { out.add(Token.RParen); i++ }
                        c == ',' -> { out.add(Token.Comma); i++ }
                        c in "+-*/^%!" -> { out.add(Token.Operator(c.toString())); i++ }
                        else -> throw IllegalArgumentException("Unexpected character: $c")
                    }
                }
            }
        }
        out.add(Token.Eof)
        return out
    }

    private fun parseExpression(): Double = parseAddSub()

    private fun parseAddSub(): Double {
        var left = parseMulDiv()
        while (true) {
            when {
                matchOp("+") -> { val r = parseMulDiv(); left += r }
                matchOp("-") -> { val r = parseMulDiv(); left -= r }
                else -> break
            }
        }
        return left
    }

    private fun parseMulDiv(): Double {
        var left = parseUnary()
        while (true) {
            when {
                matchOp("*") -> { val r = parseUnary(); left *= r }
                matchOp("/") -> {
                    val r = parseUnary()
                    if (r == 0.0) throw ArithmeticException("Division by zero")
                    left /= r
                }
                else -> break
            }
        }
        return left
    }

    private fun parseUnary(): Double {
        if (matchOp("-")) return -parseUnary()
        if (matchOp("+")) return parseUnary()
        if (matchOp("~")) return notFn(parseUnary())
        return parsePower()
    }

    private fun parsePower(): Double {
        val base = parsePostfix()
        if (matchOp("^")) {
            val exp = parseUnary()
            return Math.pow(base, exp)
        }
        return base
    }

    private fun parsePostfix(): Double {
        var value = parsePrimary()
        while (true) {
            when {
                matchOp("!") -> value = factorial(value)
                matchOp("%") -> value = value / 100.0
                else -> break
            }
        }
        return value
    }

    private fun parsePrimary(): Double {
        val t = current()
        return when (t) {
            is Token.Number -> { advance(); t.value }
            is Token.Identifier -> {
                advance()
                val name = t.name.lowercase()
                when (name) {
                    "pi" -> Math.PI
                    "e" -> Math.E
                    else -> {
                        if (current() !is Token.LParen) throw IllegalArgumentException("Expected ( after $name")
                        advance()
                        val args = mutableListOf<Double>()
                        if (current() !is Token.RParen) {
                            args.add(parseExpression())
                            while (matchToken(Token.Comma)) args.add(parseExpression())
                        }
                        if (current() !is Token.RParen) throw IllegalArgumentException("Expected )")
                        advance()
                        applyFunction(name, args)
                    }
                }
            }
            is Token.LParen -> {
                advance()
                val v = parseExpression()
                if (current() !is Token.RParen) throw IllegalArgumentException("Expected )")
                advance()
                v
            }
            else -> throw IllegalArgumentException("Unexpected token")
        }
    }

    private fun applyFunction(name: String, args: List<Double>): Double {
        fun need(n: Int) { if (args.size != n) throw IllegalArgumentException("Bad arg count for $name") }
        fun rad(x: Double) = if (angleMode == AngleMode.DEG) x * Math.PI / 180.0 else x
        fun deg(x: Double) = if (angleMode == AngleMode.DEG) x * 180.0 / Math.PI else x
        return when (name) {
            "sin" -> { need(1); Math.sin(rad(args[0])) }
            "cos" -> { need(1); Math.cos(rad(args[0])) }
            "tan" -> { need(1); Math.tan(rad(args[0])) }
            "asin" -> { need(1); val v = args[0]; if (v < -1 || v > 1) throw IllegalArgumentException("Domain error"); deg(Math.asin(v)) }
            "acos" -> { need(1); val v = args[0]; if (v < -1 || v > 1) throw IllegalArgumentException("Domain error"); deg(Math.acos(v)) }
            "atan" -> { need(1); deg(Math.atan(args[0])) }
            "sinh" -> { need(1); Math.sinh(args[0]) }
            "cosh" -> { need(1); Math.cosh(args[0]) }
            "tanh" -> { need(1); Math.tanh(args[0]) }
            "log" -> { need(1); val v = args[0]; if (v <= 0) throw IllegalArgumentException("Domain error"); Math.log10(v) }
            "ln" -> { need(1); val v = args[0]; if (v <= 0) throw IllegalArgumentException("Domain error"); Math.log(v) }
            "sqrt" -> { need(1); val v = args[0]; if (v < 0) throw IllegalArgumentException("Domain error"); Math.sqrt(v) }
            "cbrt" -> { need(1); Math.cbrt(args[0]) }
            "abs" -> { need(1); Math.abs(args[0]) }
            "exp" -> { need(1); Math.exp(args[0]) }
            "pow10", "10pow" -> { need(1); Math.pow(10.0, args[0]) }
            "factorial", "fact" -> { need(1); factorial(args[0]) }
            "and" -> { need(2); (toLong(args[0]) and toLong(args[1])).toDouble() }
            "or" -> { need(2); (toLong(args[0]) or toLong(args[1])).toDouble() }
            "xor" -> { need(2); (toLong(args[0]) xor toLong(args[1])).toDouble() }
            "not" -> { need(1); toLong(args[0]).inv().toDouble() }
            "shl" -> { need(2); (toLong(args[0]) shl args[1].toInt()).toDouble() }
            "shr" -> { need(2); (toLong(args[0]) shr args[1].toInt()).toDouble() }
            "pow" -> { need(2); Math.pow(args[0], args[1]) }
            else -> throw IllegalArgumentException("Unknown function: $name")
        }
    }

    private fun factorial(x: Double): Double {
        if (x < 0 || x != Math.floor(x)) throw IllegalArgumentException("Invalid factorial")
        if (x > 170) throw ArithmeticException("Overflow")
        var r = 1.0
        var i = 2
        while (i <= x.toInt()) { r *= i; i++ }
        if (r.isInfinite()) throw ArithmeticException("Overflow")
        return r
    }

    private fun toLong(x: Double): Long {
        if (!x.isFinite() || x != Math.floor(x)) throw IllegalArgumentException("Not an integer")
        return x.toLong()
    }

    private fun notFn(x: Double): Double = toLong(x).inv().toDouble()
}
