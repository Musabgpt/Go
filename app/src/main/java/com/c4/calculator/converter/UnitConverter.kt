package com.c4.calculator.converter

enum class UnitCategory { LENGTH, WEIGHT, TEMPERATURE, AREA, VOLUME, DATA, TIME }

data class UnitDef(val name: String, val factorToBase: Double)

object UnitConverter {
    val categories: Map<UnitCategory, List<UnitDef>> = mapOf(
        UnitCategory.LENGTH to listOf(
            UnitDef("mm", 0.001), UnitDef("cm", 0.01), UnitDef("m", 1.0),
            UnitDef("km", 1000.0), UnitDef("inch", 0.0254), UnitDef("foot", 0.3048),
            UnitDef("yard", 0.9144), UnitDef("mile", 1609.344)
        ),
        UnitCategory.WEIGHT to listOf(
            UnitDef("mg", 0.000001), UnitDef("g", 0.001), UnitDef("kg", 1.0),
            UnitDef("oz", 0.028349523125), UnitDef("lb", 0.45359237)
        ),
        UnitCategory.TEMPERATURE to listOf(
            UnitDef("C", 1.0), UnitDef("F", 1.0), UnitDef("K", 1.0)
        ),
        UnitCategory.AREA to listOf(
            UnitDef("m2", 1.0), UnitDef("km2", 1e6), UnitDef("ft2", 0.09290304), UnitDef("acre", 4046.8564224)
        ),
        UnitCategory.VOLUME to listOf(
            UnitDef("ml", 0.001), UnitDef("liter", 1.0), UnitDef("gallon", 3.785411784)
        ),
        UnitCategory.DATA to listOf(
            UnitDef("bit", 0.125), UnitDef("byte", 1.0), UnitDef("KB", 1024.0),
            UnitDef("MB", 1024.0 * 1024.0), UnitDef("GB", 1024.0 * 1024.0 * 1024.0),
            UnitDef("TB", 1024.0 * 1024.0 * 1024.0 * 1024.0)
        ),
        UnitCategory.TIME to listOf(
            UnitDef("second", 1.0), UnitDef("minute", 60.0), UnitDef("hour", 3600.0),
            UnitDef("day", 86400.0), UnitDef("week", 604800.0)
        )
    )

    fun units(cat: UnitCategory): List<UnitDef> = categories[cat] ?: emptyList()

    fun convert(cat: UnitCategory, value: Double, from: String, to: String): Double {
        if (cat == UnitCategory.TEMPERATURE) {
            val celsius = when (from) {
                "C" -> value
                "F" -> (value - 32) * 5 / 9
                "K" -> value - 273.15
                else -> value
            }
            return when (to) {
                "C" -> celsius
                "F" -> celsius * 9 / 5 + 32
                "K" -> celsius + 273.15
                else -> celsius
            }
        }
        if (cat == UnitCategory.DATA) {
            // base byte
            val toByte = mapOf(
                "bit" to 0.125, "byte" to 1.0, "KB" to 1024.0,
                "MB" to 1024.0 * 1024.0, "GB" to 1024.0 * 1024.0 * 1024.0,
                "TB" to 1024.0 * 1024.0 * 1024.0 * 1024.0
            )
            val base = value * (toByte[from] ?: 1.0)
            return base / (toByte[to] ?: 1.0)
        }
        val list = units(cat)
        val f = list.first { it.name == from }
        val t = list.first { it.name == to }
        return value * f.factorToBase / t.factorToBase
    }
}
