package com.cwg.mod.config

import com.google.gson.GsonBuilder

class CobblemonWikiGuiLang {

    companion object {
        val GSON = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()

        fun fromMap(map: Map<String, String>): CobblemonWikiGuiLang {
            val config = CobblemonWikiGuiLang()
            for ((key, value) in map) {
                try {
                    val field = config.javaClass.getDeclaredField(key)
                    field.isAccessible = true // Permite acesso a campos privados, se houver
                    field.set(config, value)
                } catch (e: NoSuchFieldException) {
                    System.err.println("Campo '$key' não encontrado em CobblemonWikiGuiLang.")
                } catch (e: IllegalAccessException) {
                    System.err.println("Não foi possível acessar o campo '$key': ${e.message}")
                }
            }
            return config
        }
    }

    var useItem: String = "Use a %s"
    var tradeAny: String = "Complete any trade or use a %s"
    var tradeSpecific: String = "Trade for an %s or use a %s"
    //  var damageTaken: String = "Need %s damage taken"
    var level: String = "Level: %s"
    var friendship: String = "Friendship: %s"
    var heldItem: String = "Pokémon held item: %s"
    var anyRequirement: String = "Need any of these requirements:"
    var attackDefenceRatioAttackHigher: String = "Need more attack than defence"
    var attackDefenceRatioDefenceHigher: String = "Need more defence than attack"
    var attackDefenceRatioEqual: String = "Need equal value of attack and defence"
    //    var battleCriticalHits: String = "Need %s battle critical hits"
    var blocksTraveled: String = "Need %s blocks traveled"
    var defeat: String = "Need %s defeats"
    var moveSet: String = "Need %s move"
    var moveType: String = "Need any type %s move"
    //var partyMember: String = "Need a %s in your party"
    //var partyMemberType: String = "%s Pokémon"
    //var playerHasAdvancement: String = "Trainer need %s advancement"
    // var propertyRange: String = "Need %s between %s"
    var recoil: String = "Need %s recoil"
    var statEqual: String = "Stats %s and %s need to be equal"
    var statCompare: String = "Stat %s should be higher than %s"
    var timeRange: String = "Time of day: %s"
    var useMove: String = "Need to use %s movement %s times"
    //var weather: String = "Weather: %s"
    var biomeCondition: String = "Need biome tag: %s"
    var biomeAntiCondition: String = "Blacklisted biome tag: %s"
    //var world: String = "Need World: %s"
    //var moonPhase: String = "Need Moon phase: %s"
    var structureCondition: String = "Need structure: %s"
    var structureAntiCondition: String = "Blacklisted structure: %s"
    //var properties: String = "Need: %s"
    //var defeatSpecific: String = "Need to defeat %s %s times"

    var noEvolutionFound: String = "No evolution found for %s"
    var rightClick: String = "Right click a %s"
    var goBackClick: String = "Click to go back"
    var seeEvolutions : String = "Click to see evolutions"

    var seeCondtions : String = "Click to see spawn conditions"
    var noSpawnConditionFound: String = "No spawn conditions found for %s"

    var basestats: String = "Base Stats"
    var type: String = "Type"
    var effectiveness : String = "Effectiveness"
    var catchrate : String = "CatchRate"
    var spawnbiome : String = "Biome Spawns"
    var spawntime : String = "Time Spawns"
    //var spawnlocations : String = "Location Spawns"
    var evolutions : String = "Evolutions"
    //var evolutionRequirements = "Evolution Requirements"
    var movesbylevel : String = "Moves by level"
    var tmMoves : String = "TM Moves"
    var tutorMoves : String = "Tutor Moves"
    var evolutionMoves : String = "Evolution Moves"
    var eggMoves : String = "Egg Moves"
    var formChangeMoves : String = "Form Changes Moves"
    var abilities : String = "Abilities"
    var drops : String = "Drops"
    var eggGroups : String = "Egg Groups"
    var forms : String = "Forms"
    var dynamax : String = "Dynamax"
    //var baseExpYield : String = "Base Exp Yield"
    var baseFriendship: String = "Friendship"
    var weakness : String = "Is weak against:"
    var resistant : String = "Resistant against:"
    var immune : String = "Immune against:"
    var pokeInfo : String = "Click to get more info"

    var noDrops: String = "No Drops"


    // Novo método para converter a instância em um mapa de strings
    fun toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        this.javaClass.declaredFields.forEach { field ->
            // Garante que não é um campo estático como GSON ou companion object
            if (!java.lang.reflect.Modifier.isStatic(field.modifiers)) {
                field.isAccessible = true // Permite acesso a campos privados
                map[field.name] = field.get(this)?.toString() ?: ""
            }
        }
        return map
    }
}

