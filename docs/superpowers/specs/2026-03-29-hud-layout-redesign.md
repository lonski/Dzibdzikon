# HUD Layout Redesign

**Date:** 2026-03-29

## Goal

Reorganise the bottom HUD so the HP/MP bars move to the bottom centre of the screen, the quickbar slots form a vertical column on the left, and the spell/inventory buttons form a symmetric vertical column on the right.

---

## Layout

```
[Q1] [                         ] [   ]
[Q2] [                         ] [   ]
[Q3] [                         ] [   ]
[Q4] [                         ] [Spl]
[Q5] [  ████ HP bar  ░░ MP bar ] [Inv]
     ^--- centre, bottom-aligned --^
```

All three sections are bottom-anchored. The centre area between the columns is transparent — the game is visible through it.

---

## Sections

### Left column — Quickbar
- 5 quickbar slots (Q1–Q5), stacked vertically, Q5 at the bottom.
- Each slot: **44 × 40 px** (unchanged size).
- Positioned at x=0, bottom of screen.
- Total height: 5 × 44 = 220 px.

### Centre bottom — HP/MP bars
- HP bar stacked above MP bar, both horizontally centred on screen.
- Each bar: **150 × 12 px** (down from 220 × 18 px).
- Small gap (4 px) between bars; total stacked height = 28 px.
- Bottom of screen at `y = 6` — centres the 28 px stack within the 40 px slot row.

### Right column — Action buttons
- Two buttons stacked vertically, bottom-anchored to mirror Q5 and Q4:
  - **Inventory** at bottom (mirrors Q5 position).
  - **Spellbook** just above it (mirrors Q4 position).
- Each button: **44 × 40 px** (unchanged size).
- Positioned at x = 800 − 44 = 756, bottom of screen.

---

## Implementation Notes

- Remove the existing flat `Table bottomBar` and the separately positioned `hpBar` / `mpBar` actors.
- Replace with three independently positioned actors/tables anchored to the bottom:
  - `leftColumn` Table: `setPosition(0, 0)`, grows upward.
  - `rightColumn` Table: `setPosition(756, 0)`, grows upward.
  - `centerBars` Table: centred horizontally (`x = (800 − 150) / 2 = 325`), `y = 0` with small padding.
- Bar sizes updated: `setSize(150, 12)`.
- Bar positions updated to sit at the bottom centre.
- All touch listeners and quickbar update logic remain unchanged.

---

## Out of Scope

- No changes to the targeting overlay, message log, or debug highlight rendering.
- No changes to quickbar slot sizes or button icons.
