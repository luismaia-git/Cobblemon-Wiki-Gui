
# Cobblemon Wiki Gui
An open-source mod for Minecraft Java Edition, written in Kotlin for Fabric.
This is a sidemod for [Cobblemon](https://cobblemon.com/)

- Pokémon Wiki : an interface that allows you to view Pokémon information (such as pokédex).

#### I strongly recommend that you only use the server side

### Features

The Cobblemon wiki gui allows you to view:
- Pokémon type
- Base stats
- Catch Rate
- Abilities
- Spawn at Biomes
- Spawntime
- Evolutions
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

#### Commands:
##### Permission: <code>cobblemon.cobblemon-wiki-gui.user</code>
- /pwiki < pokemon >
- (alias) /pokewiki < pokemon >
- (alias) /pokemonwiki < pokemon >
- (alias) /cobblemonwiki < pokemon >
- (alias) /cobblewiki < pokemon >
- (alias) /cwiki < pokemon >

##### Permission: <code>cobblemon.cobblemon-wiki-gui.reload</code>
- /cobblemonwiki reload

### Default Configuration File
The config.json file, located under config/cobblemon-wiki-gui, contains the following default settings:
```json
{
  "basestats": "Base Stats",
  "type": "Type",
  "effectiveness": "Effectiveness",
  "catchrate": "CatchRate",
  "spawnbiome": "Biome Spawns",
  "spawntime": "Time Spawns",
  "spawnlocations": "Time Spawns",
  "evolutions": "Evolutions",
  "movesbylevel": "Moves by level",
  "tmMoves": "TM Moves",
  "tutorMoves": "Tutor Moves",
  "evolutionMoves": "Evolution Moves",
  "eggMoves": "Egg Moves",
  "formChangeMoves": "Form Changes Moves",
  "abilities": "Abilities",
  "baseFriendship": "Base Friendship",
  "drops": "Drops",
  "eggGroups": "Egg Groups",
  "forms": "Forms",
  "dynamax": "Dynamax",
  "baseExpYield": "Base Exp Yield",
  "weakness": "Is weak against",
  "resistant": "Resistant against:",
  "immune": "Immune against:",
  "pokeInfo": "Click to get more info",
  "pokewikiErrorNotplayer": "This command must be ran by a player.",
  "chatTitle": "[Cobblemon Wiki Gui] ",
  "isEnablePermissionNodes": true
}
```

## Installation
This mod can be [Fabric](https://fabricmc.net/)

- Minecraft Versions: 1.20.1
- [Cobblemon](https://modrinth.com/mod/cobblemon)

## Screenshots
![Image](https://cdn.modrinth.com/data/1KP4CLlU/images/20c2fac9896bc12599ea01e27036600a85b69103.png)
![image2](https://cdn.modrinth.com/data/1KP4CLlU/images/8aae25730d938ed9dce885f158cf0e52275150c8.png)

## Frequently Asked Questions (FAQ) for Cobblemon and CobblemonWikiGui

#### Q: What is the CobblemonWikiGui?

A: The CobblemonWikiGui adds an interface that allows you to view Pokémon information (such as pokédex). that are focused on Cobblemon servers (I recommend using it on the server)

#### Q: Is it possible to open the Pokemon Wiki gui by command?

A: Yes, just type /pokewiki <pokemon>
A: You can also open the gui for a specific player with : /pokewiki <pokemon> <player>

#### Q: Is it possible to customize the configuration files?

A: Yes, as long as you keep the standard Json formatting and don't change the file keys.
A: In game, you can type /cobblemonwiki reload and the modified config.json file will be loaded
## License

[MIT](https://choosealicense.com/licenses/mit/)

## Author

- [@luismaia-git (github)](https://www.github.com/luismaia-git)
- [@luismaia-git (gitlab)](https://gitlab.com/luismaia-git)
