package com.pierrejacquier.todoboard.commons

object  Colors {
    val LIME = -0x671295
    val SALMON = -0x2797d
    val PEACH = -0x13c89
    val CANARY = -0x71483
    val SLATE = -0x56371d
    val CAFE = -0x2e475c
    val ORCHID = -0x1e551d
    val SILVER = -0x333334
    val CORAL = -0x6778e
    val AMBER = -0x134d1
    val TURQUOISE = -0x86182d
    val AQUA = -0xb92a07
    val RASPBERRY = -0x25ac54
    val CHERRY = -0x55e2c1
    val RUBY = -0x2fb7d2
    val PISTACHIO = -0x7c47de
    val TEAL = -0xe24c4f
    val LAGOON = -0xed7d68
    val SKY = -0x9e4c04
    val SAPPHIRE = -0xee8b3d
    val ONYX = -0x1000000
    val STEEL = -0x888889
    val FOREST = -0xeb6ce0
    val OLIVE = -0x5c63e1
    val TOMATO = -0x1ac1e5
    val MAGENTA = -0x1ae55d
    val GRAPE = -0x67dd1d
    val PEACOCK = -0xdfdf1d
    val SEA = -0xeb7c3d
    val CHARCOAL = -0xaaaaab
    val CRIMSON = -0x55e2c1
    val EMERALD = -0x7c47de
    val NIGHT = -0xeeeeef

    val ORDER = intArrayOf(Colors.LIME, Colors.SALMON, Colors.PEACH, Colors.CANARY, Colors.SLATE, Colors.CAFE, Colors.ORCHID, Colors.SILVER, Colors.CORAL, Colors.AMBER, Colors.TURQUOISE, Colors.AQUA, Colors.RASPBERRY, Colors.CHERRY, Colors.RUBY, Colors.PISTACHIO, Colors.TEAL, Colors.LAGOON, Colors.SKY, Colors.SAPPHIRE, Colors.ONYX, Colors.STEEL)

    fun getColor(colorIndex: Int) = ORDER[colorIndex]
}