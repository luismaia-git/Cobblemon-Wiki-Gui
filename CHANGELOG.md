# Changelog

## 2.6.0

**Compatible with Cobblemon 1.8.1 "Make Your Move"**

### ✨ New
- **`/pwikidex`** — a paginated browser listing every implemented Pokémon. Don't know the exact name? No problem, flip through the whole dex right in-game. *(own permission node — server owners decide who gets it)*
- **"Rideable" tile** — instantly see if a Pokémon can be ridden (Pidgeot, Crobat, Skarmory, Milotic and friends now show up correctly in the wiki)

### 🎨 Visual
- **Spawn Conditions GUI got a full color makeover** — biomes, fluids, items, structures, weather, and time of day each get their own color instead of one flat yellow wall of text. Much easier to scan at a glance.
- Readable biome/tag names (`cobblemon:is_beach` is now **Beach** — no more raw ids on screen)

### 🐛 Fixes
- Trade-evolution partner name showed up untranslated in requirement text
- Structure/biome evolution requirements showed raw text instead of a proper name
- Item-interaction and held-item evolutions showed no requirement at all (now they do)
- Fixed a version-drift bug in the server startup log

### ⚙️ Under the hood
- Spawn pool lookup is now cached (faster GUI opens on servers with lots of addons)
- Expanded test coverage — less chance of silent breakage on the next Cobblemon update
