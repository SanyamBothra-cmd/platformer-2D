# [Game Name TBD] — A 2D Action Platformer

> A 2D action platformer inspired by *Death's Gambit: Afterlife*, built in Java with LibGDX. Personal solo dev project and a learning ground for Java + game architecture.

![Status](https://img.shields.io/badge/status-in%20development-yellow)
![Platform](https://img.shields.io/badge/platform-desktop-blue)
![Engine](https://img.shields.io/badge/engine-LibGDX-red)
![License](https://img.shields.io/badge/license-TBD-lightgrey)

---

## About

This is a 2D action platformer drawing heavy inspiration from **Death's Gambit: Afterlife** — deliberate, weighty combat, atmospheric world design, and RPG-lite character progression layered on top of tight platforming.

The long-term vision includes:

- **Multiple hero classes**, each with distinct stat blocks (Vitality, Strength, Finesse, etc.) that shape how weapons and abilities behave
- **A weapon selection & inventory system**, with different weapon types offering different combat styles
- **Pets** — companion entities that follow the player and assist in combat or utility roles
- **A shelter/hub area** — a persistent home base for crafting, upgrades, and pet management between runs
- **Raid/boss encounters** — multi-phase boss fights with dedicated arenas
- **Enemy AI and a full combat loop** built around a proper hitbox/hurtbox and animation state machine

This is also a **learning-oriented project** — it's being built incrementally, phase by phase, with an emphasis on clean architecture (composition over inheritance, data-driven content) over speed of delivery. Expect the codebase to reflect a developer actively leveling up their Java and game-dev skills, phase by phase.

---

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Java |
| Framework | [LibGDX](https://libgdx.com/) |
| Project generator | gdx-liftoff |
| Build tool | Gradle |
| JDK | 21 LTS |
| IDE | IntelliJ IDEA |
| Target platform | Desktop (Windows/Mac/Linux) — mobile deferred until desktop build is stable |
| Level editor | [LDtk](https://ldtk.io) |
| Sprite packing | TexturePacker |

---

## Architecture Notes

- **Composition over inheritance**: `Player` (and other entities) own component instances — `PlayerMovement`, `Inventory`, etc. — as fields rather than extending base classes. This keeps behavior modular and easy to test in isolation.
- **Data-driven content**: Weapons, enemies, hero classes, and items are defined as JSON data (loaded via LibGDX's `Json` class into ID-keyed registries) rather than hardcoded as Java classes. Adding new content should mean editing a data file, not writing new code.
- **Package structure**:
  ```
  core/src/main/java/com/sanyam/platformer/
    screens/      -- game screens (menu, gameplay, pause, etc.)
    entities/     -- Player, Enemy, Pet, and other world entities
    components/   -- composable behavior pieces (movement, inventory, AI, etc.)
    items/        -- weapons, items, equipment logic
    data/         -- data definitions and loaders (JSON-backed registries)
    world/         -- level/map handling, collision, camera
    input/        -- input handling and key bindings
  ```

---

## Development Roadmap

The project is being built in **13 phases**. Current phase: **Phase 3 — Weapon & Inventory System**.

| # | Phase | Status |
|---|---|---|
| 1 | Project skeleton — LibGDX/Gradle setup, core game loop, window | ✅ Done |
| 2 | Player backend — movement, wall jump, weapon pickup, single-slot inventory | 🔶 Mostly done (multi-slot pending) |
| 3 | Weapon & inventory system — multi-slot inventory, more weapons, equip/swap | 🔶 In progress |
| 4 | Map/level framework — Tiled integration, real tilemap collision, camera follow | ⬜ Planned |
| 5 | Class system — stat blocks per hero class | ⬜ Planned |
| 6 | Combat & animation state machine — hitboxes/hurtboxes, damage resolution | ⬜ Planned |
| 7 | Enemy AI & basic combat loop | ⬜ Planned |
| 8 | Pets — companion entity, AI, combat/utility roles | ⬜ Planned |
| 9 | Shelter/hub system — persistent base, crafting/upgrades, pet management | ⬜ Planned |
| 10 | Raid/boss encounters — multi-phase bosses, arenas | ⬜ Planned |
| 11 | UI/UX — HUD, inventory screen, menus | ⬜ Planned |
| 12 | Save/load system | ⬜ Planned |
| 13 | Polish pass — juice, sound, particles, balancing | ⬜ Planned |

Full task-level breakdown for every phase lives in [`docs/roadmap-trello-board.md`](docs/roadmap-trello-board.md).

---

## Asset & Data Pipeline

Details on how sprites, maps, audio, and game data (weapons/enemies/classes) are organized and produced are documented in [`docs/asset-and-data-pipeline.md`](docs/asset-and-data-pipeline.md).

Quick summary:
- Sprites are drawn in Aseprite/Krita, packed into atlases via TexturePacker
- Levels are built in Tiled (`.tmx`), loaded via `TmxMapLoader`
- Map objects (spawns, triggers) use Tiled object layers + a factory pattern
- Game content (weapons, enemies, classes, items) is defined in JSON and loaded into registries — never hardcoded

---

## Getting Started

### Prerequisites
- JDK 21
- IntelliJ IDEA (or any IDE with Gradle support)

### Running the game
```bash
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>
./gradlew desktop:run
```

---

## Project Status

This is an actively developed solo project, currently mid-way through Phase 3 of 13. Expect frequent commits, some rough edges, and evolving architecture as new systems come online.

---

## License

*TBD — add a license (MIT is a common default for personal/portfolio game projects) before making the repo public if you want to control reuse terms.*
