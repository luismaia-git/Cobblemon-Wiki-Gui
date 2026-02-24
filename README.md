
# Cobblemon Wiki Gui
An open-source mod for Minecraft Java Edition, written in Kotlin for Fabric and NeoForge.
This is a sidemod for [Cobblemon](https://cobblemon.com/)

- Pokémon Wiki : an interface that allows you to view Pokémon species information (such as pokédex).

#### I strongly recommend that you only use the server side or singleplayer

### Features

The Cobblemon wiki gui allows you to view:
- Pokémon type
- Base stats
- Catch Rate
- Abilities
- Spawn at Biomes
- Spawntime
- Evolutions
- Evolution Requirements
- Moves by level
- TM Moves
- Tutor Moves
- Egg Moves
- Evolution Moves
- Form Change Moves
- Egg Groups
- Forms
- Dynamax
- Drops


## Screenshots
![ImageMain](https://cdn.modrinth.com/data/cached_images/e9ef1affec499acf712b44850f908d1be71a5fb4.png)
![Biomes](https://cdn.modrinth.com/data/cached_images/1a4da83e11d70fe30073a8454c00ac5a81369d60.png)
![Effectiviness](https://cdn.modrinth.com/data/cached_images/bda479d09584c4e95ed619c921a9941d1c53a259.png)


#### Commands

**PokeWiki** — Opens the Pokémon wiki GUI for the executor.

| Permission | Command | Description |
|------------|---------|--------------|
| <code>cobblemon_wiki_gui.command.pwiki</code> | <code>/pwiki &lt;pokemon&gt; [form]</code> | Open the wiki for a species. Optional **form** shows that form only (e.g. <code>/pwiki zorua hisui</code>). |

Aliases: `/pokewiki`, `/pokemonwiki`, `/cobblemonwiki`, `/cobblewiki`, `/cwiki`

**PokeWiki (other player)** — Opens the Pokémon wiki GUI for another player.

| Permission | Command | Description |
|------------|---------|--------------|
| <code>cobblemon_wiki_gui.command.pwikianother</code> | <code>/pwikiother &lt;player&gt; &lt;pokemon&gt; [form]</code> | Open the wiki for a species for the given player. Optional **form** shows that form only (e.g. <code>/pwikiother Steve zorua hisui</code>). |

Aliases: `/pokewikiother`, `/pokemonwikiother`, `/cobblemonwikiother`, `/cobblewikiother`, `/cwikiother`

**Reload** — Reloads the configuration files in-game.

| Permission | Command | Description |
|------------|---------|--------------|
| <code>cobblemon_wiki_gui.command.cwgreload</code> | <code>/cwg reload</code> | Reloads the lang.json configuration file without restarting. |

### Default Configuration File
The lang.json file, located under config/cobblemon_wiki_gui, contains the following default settings:
```json
{
  "useItem": "Use a %s",
  "tradeAny": "Complete any trade or use a %s",
  "tradeSpecific": "Trade for an %s or use a %s",
  "level": "Level: %s",
  "friendship": "Friendship: %s",
  "heldItem": "Pokémon held item: %s",
  "anyRequirement": "Need any of these requirements:",
  "attackDefenceRatioAttackHigher": "Need more attack than defence",
  "attackDefenceRatioDefenceHigher": "Need more defence than attack",
  "attackDefenceRatioEqual": "Need equal value of attack and defence",
  "blocksTraveled": "Need %s blocks traveled",
  "defeat": "Need %s defeats",
  "moveSet": "Need %s move",
  "moveType": "Need any type %s move",
  "recoil": "Need %s recoil",
  "statEqual": "Stats %s and %s need to be equal",
  "statCompare": "Stat %s should be higher than %s",
  "timeRange": "Time of day: %s",
  "useMove": "Need to use %s movement %s times",
  "biomeCondition": "Need biome tag: %s",
  "biomeAntiCondition": "Blacklisted biome tag: %s",
  "structureCondition": "Need structure: %s",
  "structureAntiCondition": "Blacklisted structure: %s",
  "noEvolutionFound": "No evolution found for %s",
  "rightClick": "Right click a %s",
  "goBackClick": "Click to go back",
  "seeEvolutions": "Click to see evolutions",
  "basestats": "Base Stats",
  "type": "Type",
  "effectiveness": "Effectiveness",
  "catchrate": "CatchRate",
  "spawnbiome": "Biome Spawns",
  "spawntime": "Time Spawns",
  "evolutions": "Evolutions",
  "movesbylevel": "Moves by level",
  "tmMoves": "TM Moves",
  "tutorMoves": "Tutor Moves",
  "evolutionMoves": "Evolution Moves",
  "eggMoves": "Egg Moves",
  "formChangeMoves": "Form Changes Moves",
  "abilities": "Abilities",
  "drops": "Drops",
  "eggGroups": "Egg Groups",
  "forms": "Forms",
  "dynamax": "Dynamax",
  "baseFriendship": "Friendship",
  "weakness": "Is weak against:",
  "resistant": "Resistant against:",
  "immune": "Immune against:",
  "pokeInfo": "Click to get more info",
  "noDrops": "No Drops"
}
```

## Installation
- This mod can be [Fabric](https://fabricmc.net/)
- This mod can be [NeoForge](https://neoforged.net/)

- Minecraft Versions: 1.21.1
- [Cobblemon](https://modrinth.com/mod/cobblemon)


## Frequently Asked Questions (FAQ) for Cobblemon and CobblemonWikiGui

#### Q: What is the CobblemonWikiGui?

A: The CobblemonWikiGui adds an interface that allows you to view Pokémon information (such as pokédex). that are focused on Cobblemon servers (I recommend using it on the server)

#### Q: Is it possible to open the Pokemon Wiki gui by command?

A: Yes, just type `/pokewiki <pokemon>` or `/pokewiki <pokemon> <form>` to open a specific form (e.g. `/pokewiki zorua hisui`).

A: You can also open the gui for another player with: `/pokewikiother <player> <pokemon>` or `/pokewikiother <player> <pokemon> [form]`.

#### Q: Is it possible to customize the configuration files?

A: Yes, as long as you keep the standard Json formatting and don't change the file keys.
A: In game, you can type /cwg reload and the modified lang.json file will be loaded
## License

[MIT](https://choosealicense.com/licenses/mit/)

## Author

- [@luismaia-git (github)](https://www.github.com/luismaia-git)
- [@luismaia-git (gitlab)](https://gitlab.com/luismaia-git)