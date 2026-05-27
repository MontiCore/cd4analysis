# Name Adaptation Improvements (Future Work)

## Context

`NameUtil.adaptTemplatedName(String name, String variable, String value)` currently takes a
single (variable, value) substitution pair. In the implicit name adaptation feature, callers
apply multiple pairs sequentially — one call per type pair. This is sufficient for the common
case, but breaks down when the type names overlap.

## The Overlapping-Names Problem

**Example:** Two type pairs in context: `Entity → PersonEntity` and `EntityRepository → PersonRepository`.

Applying pairs naively in an arbitrary order:

1. Apply `(Entity, PersonEntity)` first:
   `findEntityRepository` → `findPersonEntityRepository`
2. Apply `(EntityRepository, PersonRepository)` second:
   `findPersonEntityRepository` → `findPersonPersonRepository`  ← wrong double-substitution

Or in the other order:

1. Apply `(EntityRepository, PersonRepository)` first:
   `findEntityRepository` → `findPersonRepository`  ← correct
2. Apply `(Entity, PersonEntity)` second:
   `findPersonRepository` → no match (no `Entity` substring left)  ← correct

The correct result depends on applying the **longest matching variable first** and then
**protecting already-adapted regions** from further substitution.

## Proposed Solution: Multi-Pair `adaptTemplatedName`

Add an overload (or a new method) to `NameUtil`:

```java
public static String adaptTemplatedName(String name, List<Pair<String, String>> pairs)
```

**Algorithm:**

1. **Sort pairs by decreasing match length** — longer variable names are tried before shorter
   ones. This ensures `EntityRepository` is considered before `Entity` so the longer match wins.

2. **Collect all non-overlapping matches** using a greedy left-to-right scan:
   - For each position in `name`, try all remaining (unsorted-but-prioritised) pairs to find
     the longest match at that position.
   - When a match is found, record the substitution and advance past the matched region.
   - Matched regions are **marked as adapted** and cannot be matched again by any other pair.

3. **Apply substitutions** (right to left to preserve offsets) to produce the final name.

**Example revisited:**
- Input: `findEntityRepository`, pairs: `[(Entity, PersonEntity), (EntityRepository, PersonRepository)]`
- Sorted by length: `[(EntityRepository, PersonRepository), (Entity, PersonEntity)]`
- Scan: at offset 4 (`EntityRepository`) → longest match wins → substitute → `findPersonRepository`
- Region `[4, 20)` is marked adapted; `Entity` at offset 4 is now inside that region → skipped
- Result: `findPersonRepository`  ✓

## Relation to Existing Callers

The current approach (multiple single-pair calls in sequence) in
`BaseAttributeInTypeCompleter`, `BaseMethodInTypeCompleter`, `DefaultAssocCompleter`, and
`MissingAssociationsCDCompleter` is correct as long as the type pairs for a given element do
not overlap. This is typically the case because each element's own type pairs are independent.

The chaining bug that motivated using element-specific pairs (instead of all context pairs) would
**also** be addressed by this algorithm, since each region can only be adapted once. When this
improved API is available, it would be safe to pass all context type pairs — not just the
element-specific ones — and the algorithm would still produce correct results.

## Implementation Note

This is **not required** for the current implicit name adaptation feature. The element-specific
pair approach is sufficient. Implement this when overlapping type names become a real concern in
practice.
