package com.luismaia.config


import kotlinx.serialization.Serializable

@Serializable
data class CobblemonWikiGuiConfig (
    var basestats: String = "Base Stats",
    var type: String = "Type",
    var effectiveness : String = "Effectiveness",
    var catchrate : String = "CatchRate",
    var spawnbiome : String = "Biome Spawns",
    var spawntime : String = "Time Spawns",
    var spawnlocations : String = "Time Spawns",
    var evolutions : String = "Evolutions",
    var movesbylevel : String = "Moves by level",
    var tmMoves : String = "TM Moves",
    var tutorMoves : String = "Tutor Moves",
    var evolutionMoves : String = "Evolution Moves",
    var eggMoves : String = "Egg Moves",
    var formChangeMoves : String = "Form Changes Moves",
    var abilities : String = "Abilities",
    var baseFriendship : String = "Base Friendship",
    var drops : String = "Drops",
    var eggGroups : String = "Egg Groups",
    var forms : String = "Forms",
    var dynamax : String = "Dynamax",
    var baseExpYield : String = "Base Exp Yield",
    var weakness : String = "Is weak against",
    var resistant : String = "Resistant against:",
    var immune : String = "Immune against:",
    var pokeInfo : String = "Click to get more info",
    var pokewikiErrorNotplayer: String = "This command must be ran by a player.",
    var chatTitle: String = "[Cobblemon Wiki Gui] ",
)

