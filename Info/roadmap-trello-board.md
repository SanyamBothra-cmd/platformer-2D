# Platformer Dev Roadmap — Trello Board Layout

**How to use this in Trello:** Create one **List** per phase below (13 lists total, or archive completed ones into a "Done" list to keep the board manageable). Each bullet under a phase is one **Card**. Trello lets you paste multi-line text into a list's "Add a card" box and hit enter after each line to auto-create separate cards — copy each phase's bullets in one go to populate a list fast.

Suggested board-level lists: `Backlog` → `Phase In Progress` → `Testing/Polish` → `Done`. Move cards across these as you go, and tag each card with the phase number as a label (Phase 1–13) so you can filter.

---

## Phase 1 — Project Skeleton ✅ DONE
- [x] LibGDX project generated via gdx-liftoff
- [x] Gradle build configured (desktop launcher)
- [x] Core game loop (`ApplicationAdapter` / `create()`, `render()`, `dispose()`)
- [x] Basic window opens with correct resolution/title
- [x] Package structure established under `com.sanyam.platformer`

*Archive this list once confirmed stable — keep as reference only.*

---

## Phase 2 — Player Backend 🔶 MOSTLY DONE
- [x] `Player` class created (composition-based, not inheritance)
- [x] `PlayerMovement` component — run, jump, wall jump
- [x] Weapon throw/pickup logic working
- [x] Basic single-slot inventory working
- [ ] Extend inventory to multi-slot (this bleeds into Phase 3 — keep as the bridge card)
- [ ] Write a quick regression test/checklist for movement edge cases (corner grabs, jump buffering, coyote time) before calling this fully closed

---

## Phase 3 — Weapon & Inventory System 🔶 IN PROGRESS
- [ ] Define multi-slot inventory data structure (array/list of `ItemStack` or slot objects)
- [ ] Equip/unequip logic — swap active weapon without losing held item
- [ ] Add 2–3 more weapon types beyond the current one (melee variant, ranged variant, heavy variant)
- [ ] Weapon stat fields: damage, speed, range, weight (even if unused until Phase 6)
- [ ] Drop/pickup interaction refinement (stacking, ground item despawn/respawn rules)
- [ ] Decide inventory capacity rules (fixed slots vs. weight-based)
- [ ] Basic keybinding for weapon swap/cycle

---

## Phase 4 — Map / Level Framework
- [ ] Install and integrate Tiled map editor into workflow
- [ ] Add `TmxMapLoader` + `OrthogonalTiledMapRenderer` to the project
- [ ] Build one test level in Tiled (ground, platforms, walls)
- [ ] Replace hand-placed `Rectangle` collision solids with real tilemap collision layer
- [ ] Implement camera follow (with bounds clamping to map edges)
- [ ] Define a tileset naming/versioning convention
- [ ] Parachute test: confirm player movement (Phase 2) still behaves correctly on real tile collision vs. old Rectangle stand-ins

---

## Phase 5 — Class System
- [ ] Design stat block schema: Vitality, Strength, Finesse (+ any others you want — Arcane, Vigor, etc.)
- [ ] Create `HeroClass` data structure (base stats + growth curve per level)
- [ ] Hook stats into `Player` (currently stat-less) — HP from Vitality, damage scaling from Strength, etc.
- [ ] Define how class modifies weapon behavior (e.g., Finesse scaling on daggers, Strength scaling on greatswords)
- [ ] Build 2–3 starter classes as data entries (not hardcoded) to validate the schema
- [ ] Class selection stub (even just a debug key to switch class for testing)

---

## Phase 6 — Combat & Animation State Machine
- [ ] Build a generic finite state machine (FSM) framework for the player (Idle, Run, Jump, Attack, Hurt, Dead states)
- [ ] Wire attack states to actual animations
- [ ] Implement hitbox (attack) and hurtbox (vulnerable) shapes, active only during specific animation frames
- [ ] Damage resolution logic — this is where `Weapon.damage` (defined back in Phase 3) finally gets consumed
- [ ] Add hit-stop / knockback feedback on successful hits
- [ ] Add basic invincibility frames after taking damage

---

## Phase 7 — Enemy AI & Basic Combat Loop
- [ ] Create base `Enemy` class (composition: `EnemyMovement`, `EnemyAI`, reuses hurtbox/hitbox from Phase 6)
- [ ] Simple state-based AI: idle/patrol → aggro → attack → recover
- [ ] At least one melee enemy and one ranged enemy to validate combat variety
- [ ] Enemy death → loot drop hook (stub is fine, full loot system can wait)
- [ ] Playtest full loop: player engages enemy, takes/deals damage, enemy dies, player continues

---

## Phase 8 — Pets
- [ ] Design `Pet` entity (composition: owns `PetAI`, follows same movement component pattern as player/enemy)
- [ ] Follow-the-player logic (pathing, catch-up speed, avoid obstacles)
- [ ] Decide pet role(s): combat assist, utility (loot magnet, buff aura), or both
- [ ] Pet summon/dismiss mechanic
- [ ] Basic pet UI indicator (health bar or status icon)

---

## Phase 9 — Shelter / Hub System
- [ ] Build a dedicated hub level (separate from combat levels, safe zone)
- [ ] Crafting system: recipe data structure + UI hook for combining items
- [ ] Upgrade system: weapon/gear upgrade paths, currency or material sink
- [ ] Pet management screen (roster, feeding/bonding if applicable)
- [ ] NPC vendor stub(s) if you want shops
- [ ] Hub ↔ level transition (portal, door, or map select)

---

## Phase 10 — Raid / Boss Encounters
- [ ] Design first boss: multi-phase structure (e.g., phase 1 melee, phase 2 adds ranged attacks at 50% HP)
- [ ] Arena design in Tiled (contained space, no early exits)
- [ ] Phase transition triggers (HP thresholds, timers, or scripted events)
- [ ] Boss-specific attack patterns/telegraphs
- [ ] Reward hook on boss defeat (loot, currency, progression flag)

---

## Phase 11 — UI/UX
- [ ] HUD: health bar, active weapon indicator, resource/mana bar if applicable
- [ ] Inventory screen (visual, drag/swap slots — built on Phase 3's data structure)
- [ ] Pause menu
- [ ] Main menu / title screen
- [ ] Settings screen (audio volume, key rebinding at minimum)
- [ ] Death/game-over screen

---

## Phase 12 — Save/Load System
- [ ] Decide serialization format (LibGDX `Json` class is the natural fit given your data-driven approach)
- [ ] Define what gets saved: player stats, inventory, class, unlocked hub upgrades, level progress
- [ ] Save file versioning (so future changes don't break old saves)
- [ ] Manual save + autosave trigger points (hub entry, checkpoint, etc.)
- [ ] Load flow tested from a cold app start

---

## Phase 13 — Polish Pass
- [ ] Sound effects (attacks, footsteps, pickups, UI)
- [ ] Background music per area
- [ ] Particle effects (hit impacts, dust, weapon trails)
- [ ] Screen shake / camera juice on impactful moments
- [ ] Balancing pass (damage numbers, enemy HP, drop rates)
- [ ] Bug bash / final QA pass
