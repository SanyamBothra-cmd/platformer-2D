# Asset & Data Pipeline — Recommended Method

Since this is a 2D game, "models" here means sprites/animations rather than 3D meshes. The core idea across all of this: **keep content out of code**. Weapons, enemies, classes, levels — none of it should be hardcoded as Java objects in your source. Everything should be data your code *reads*, so adding a new sword or enemy later is "add a file," not "recompile."

## 1. Folder structure

Keep this inside your `android/assets` (or `core/assets`, depending on your gdx-liftoff layout) — LibGDX expects a single shared `assets/` root:

```
assets/
  data/
    weapons.json
    enemies.json
    classes.json
    items.json
  textures/
    atlases/        <- packed .atlas + .png from TexturePacker
    raw/             <- source PNGs before packing (optional, can live outside assets/)
  maps/
    hub.tmx
    level_01.tmx
    tilesets/
      forest.tsx
  audio/
    sfx/
    music/
  fonts/
```

## 2. Data-driven content (weapons, enemies, classes, items)

Define a plain Java class matching the shape of your data, and load it with LibGDX's built-in `Json` class — no extra library needed:

```java
public class WeaponDef {
    public String id;
    public String name;
    public int damage;
    public float range;
    public String spriteRegion; // key into your texture atlas
}
```

```json
[
  { "id": "sword_iron", "name": "Iron Sword", "damage": 12, "range": 1.2, "spriteRegion": "sword_iron" },
  { "id": "bow_short", "name": "Short Bow", "damage": 8, "range": 6.0, "spriteRegion": "bow_short" }
]
```

Load once at startup into a registry keyed by `id`, and reference weapons/enemies/classes by that string ID everywhere else (inventory, drops, class unlocks) instead of passing around instances. This is the same pattern you'll want for `EnemyDef`, `HeroClassDef`, and `ItemDef` — one JSON file, one loader, one registry per content type.

**Why this matters for your roadmap specifically:** Phase 3 (weapons), Phase 5 (classes), and Phase 7 (enemies) all become "add an entry to a JSON file" instead of "write a new Java class," which will save you a lot of time once you're iterating on balance.

## 3. Sprites & animation

- **Aseprite** (paid, but the industry-standard pixel art tool) or **Krita/GIMP** (free) for drawing sprites and animation frames.
- **TexturePacker** (comes bundled with LibGDX tooling, or use the free `gdx-texturepacker` gradle task) to pack individual frames into a single texture atlas + `.atlas` file. This is a hard requirement for performance — never load loose PNGs at runtime for anything animated.
- Name frames consistently: `player_run_00.png`, `player_run_01.png`, etc. — LibGDX's `Animation` class and atlas region naming both expect this pattern to auto-group frames.

## 4. Levels & map objects

- **Tiled** (free, open source) is the right call — LibGDX has first-party support via `TmxMapLoader`.
- For map objects (spawn points, item pickups, triggers, boss arenas), use Tiled's **Object Layers** with custom properties rather than the tile layer itself. Example: place a rectangle object, tag it `type=enemy_spawn`, `enemyId=goblin_grunt`.
- On the code side, write a small **factory** that reads object layers and spawns the right entity based on the `type` property:

```java
for (MapObject obj : objectLayer.getObjects()) {
    String type = obj.getProperties().get("type", String.class);
    switch (type) {
        case "enemy_spawn" -> spawnEnemy(obj);
        case "item_pickup" -> spawnItem(obj);
        case "boss_arena_bounds" -> registerArena(obj);
    }
}
```

This means level designers (future you) never touch Java to place a new enemy — just drop an object in Tiled and tag it.

## 5. Audio

- Keep SFX short and in `.ogg` or `.wav` (LibGDX supports both; `.ogg` compresses better for music-length files).
- **freesound.org** or **sfxr/jsfxr** (generates retro SFX programmatically, good for prototyping before final audio) are solid free sources while prototyping.

## 6. Suggested order to build this pipeline out

Given where you are (Phase 3), I'd set this up roughly like:
1. Set up the `data/` JSON + registry pattern **now**, while expanding weapons — retrofit your existing single weapon into this system rather than adding more hardcoded ones.
2. Bring in Tiled + TexturePacker at the start of Phase 4, since that phase is literally "replace placeholder rectangles with real map tooling."
3. Extend the same JSON/registry pattern to enemies (Phase 7) and classes (Phase 5) when you get there — by then the pattern will already feel familiar.

## 7. Version control note

Binary assets (art, audio) bloat git history fast. If your repo grows large, look into **Git LFS** (Large File Storage) sooner rather than later — retrofitting it onto an already-bloated repo is painful.
