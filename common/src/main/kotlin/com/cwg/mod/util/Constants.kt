package com.cwg.mod.util

val dayCycleMap = mapOf(
    "Any time" to listOf(0..23999),
    "Day" to listOf(23460 downTo 12541),
    "Night" to listOf(12542..23459),
    "Noon" to listOf(5000..6999),
    "Midnight" to listOf(17000..18999),
    "Dawn" to listOf(22300 downTo 166),
    "Dusk" to listOf(11834..13701),
    "Twilight" to listOf(11834..13701, 22300 downTo 166),
    "Morning" to listOf(0..4999),
    "Afternoon" to listOf(7000..12039),
    "Any time" to listOf(0..23999),
)