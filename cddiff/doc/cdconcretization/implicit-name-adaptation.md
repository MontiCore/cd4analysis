# Implicit Name Adaptation

## Overview

CD concretization already supports **explicit** name adaptation via `<<forEach="X">>` annotations:
when a reference method `findEntity(String id)` has `<<forEach="Entity">>`, it gets renamed to
`findPerson(String id)` for a concrete incarnation `Person` of `Entity`. The variable for
substitution comes from the `forEach` stereotype value.

This feature extends that concept to **implicit** dependencies. Rather than requiring an explicit
annotation, the system infers the substitution variable from the *type context* of each element:

| Element       | Implicit dependency (substitution variable/value)                        |
|---------------|--------------------------------------------------------------------------|
| Attribute     | Attribute type → its concrete type incarnation                           |
| Method        | Return type and each parameter type → their respective incarnations      |
| Assoc. role   | The association endpoint type → its concrete type incarnation            |
| Assoc. name   | Either endpoint type → its concrete type incarnation                     |

Classes, interfaces, and enums have no implicit type dependency of this kind and are excluded.

---

## Part 1 — Completion Phase

### Flag

`implicitNameAdaptationEnabled` (boolean, default `true`) on `ConcretizationCompleter`.
Mirrors the existing `forEachNameAdaptationEnabled` flag.

### Adaptation Mechanism

In all cases, `NameUtil.adaptTemplatedName(name, refTypeName, conTypeName)` is the adaptation
function. It tries three substitution patterns (exact, uncapitalized-prefix,
capitalized-infix) and returns `Optional.empty()` when none matches, so an unrelated name is
never accidentally changed.

**Important — use specific pairs, not all context pairs.**
Each element must use only the type pair(s) derived from *its own* type dependencies. Using all
context pairs can cause a chaining bug: if both `Account→BankAccount` and `Bank→SEPABank` are in
context, applying both pairs sequentially turns `sourceAccount` into `sourceBankAccount` and then
into `sourceSEPABankAccount`. A future improvement to handle overlapping names safely is
documented in [name-adaptation-improvements.md](name-adaptation-improvements.md).

---

### Attributes — `BaseAttributeInTypeCompleter`

When an attribute incarnation is created for reference attribute `refAttr` whose type `T_ref` is
incarnated by `T_con`:

```
adaptedName = adaptTemplatedName(attrName, T_ref.getName(), T_con.getName())
```

If adaptation succeeds and the name changed, set the new name and add a stereotype:
```
<<ref="EnclosingType.refAttrName">> adaptedAttrName: T_con;
```

Use the specific `(T_ref, T_con)` pair already available in the loop over `typeIncarnations`.

---

### Methods — `BaseMethodInTypeCompleter`

Two sub-cases arise when iterating over the cartesian product of return-type and parameter-type
incarnations:

#### Method name adaptation

Apply **all** relevant type pairs sequentially: the return type pair followed by each parameter
type pair (for parameters whose type is a CD type with an incarnation):

```
adaptedName = refMethod.getName()
for each (T_ref, T_con) from [returnTypePair, paramTypePairs...]:
    adaptedName = adaptTemplatedName(adaptedName, T_ref.getName(), T_con.getName())
                      .orElse(adaptedName)
```

This naturally handles methods with multiple reference-typed parameters:
`compareInputAndOutput(Input input, Output output)` with `Input→Foo`, `Output→Bar`
→ `compareFooAndBar`.

If the name changes, add:
```
<<ref="EnclosingType.refMethodSignature">> adaptedMethodName(...)
```

#### Parameter name adaptation

For each parameter `p` whose type `T_par_ref` is incarnated by `T_par_con`:

```
adaptedParamName = adaptTemplatedName(p.getName(), T_par_ref.getName(), T_par_con.getName())
```

Set the adapted name if it changed. **No stereotype** is needed for parameter names since they
are not part of any matching key.

> **Open question (see banking2 below):** the current expected output for `banking2` keeps
> `transfer(BankAccount targetAccount, ...)` — i.e., parameter name `targetAccount` is NOT adapted
> to `targetBankAccount` even though `adaptTemplatedName` would produce that. This must be
> clarified: should parameter name adaptation apply here, and if so, the expected output file must
> be updated?

Use the specific return/parameter type pairs available inside the existing incarnation loops.

---

### Associations

#### New (missing) associations — `MissingAssociationsCDCompleter`

Already implemented using specific endpoint type pairs. Role name and association name are adapted
using the left/right endpoint type pairs respectively. **No change needed.**

#### Existing (matched) associations — `DefaultAssocCompleter`

After `assocSideCompleter.completeAssocSide()` has copied role names from the reference:

```
adaptedRightRole = adaptTemplatedName(rightRole, T_rightRef.getName(), T_rightCon.getName())
adaptedLeftRole  = adaptTemplatedName(leftRole,  T_leftRef.getName(),  T_leftCon.getName())

// apply both endpoint type pairs sequentially (same pattern as method parameter types)
adaptedAssocName = assocName
adaptedAssocName = adaptTemplatedName(adaptedAssocName, T_leftRef.getName(),  T_leftCon.getName()).orElse(adaptedAssocName)
adaptedAssocName = adaptTemplatedName(adaptedAssocName, T_rightRef.getName(), T_rightCon.getName()).orElse(adaptedAssocName)
```

The reference endpoint types `T_leftRef` / `T_rightRef` must be resolved from `rAssoc` (which is
passed to `completeAssociation`). The concrete incarnation types `T_leftCon` / `T_rightCon` must
be resolved from `cAssoc`.

Use the specific endpoint type pairs resolved from `cAssoc`/`rAssoc`. The `CDCompletionContext`
is already available in `DefaultAssocCompleter` via the `context` field.

---

## Part 2 — Conformance Phase

### Motivation

When implicit name adaptation is applied, adapted names are *no longer* detected as incarnations
by the existing matching strategies. For example, `findTicket(String id)` is not detected as
an incarnation of `findTask(String id)` by `EqNameMethodIncStrategy` or
`EqSignatureMethodIncStrategy`.

The completers address this by adding `<<ref="...">>`  stereotypes, making
`STAttributeIncStrategy` / `STMethodIncStrategy` the fallback matcher. However, we also want to
support the case where no stereotype is present — i.e., the concrete CD author wrote
`findTicket(String id)` by hand without a stereotype, and the conformance checker should still
recognise it as an incarnation.

### New `CDConfParameter`: `ADAPTED_NAME_MAPPING`

Replaces and generalises the earlier `IMPLICIT_ROLE_NAME_ADAPTATION` parameter (which was
association-only). When this parameter is present in the conformance parameters set, three new
matching strategies are added:

---

### `AdaptedNameAttributeIncStrategy`

Matches a concrete attribute `conAttr` to a reference attribute `refAttr` when:

1. The declaring type of `conAttr` incarnates the declaring type of `refAttr`
   (standard type matching, handled by the surrounding conformance check).
2. The type of `conAttr` incarnates the type of `refAttr`: `typeMatcher.isMatched(T_con, T_ref)`.
3. The name of `conAttr` is the adapted form of `refAttr`'s name:
   ```
   adaptTemplatedName(refAttr.getName(), T_ref.getName(), T_con.getName())
       .map(adapted -> adapted.equals(conAttr.getName()))
       .orElse(false)
   ```

**Base class:** extend `CDAttributeMatchingStrategy` (like `EqNameAttributeIncStrategy`).

**Registration in `DefaultCDConformanceContext.create()`:**
```java
if (conformanceParams.contains(CDConfParameter.ADAPTED_NAME_MAPPING)) {
  compAttributeIncStrategy.addIncStrategy(new AdaptedNameAttributeIncStrategy(compTypeIncStrategy));
}
```

---

### `AdaptedNameMethodIncStrategy`

Matches a concrete method `conMethod` to a reference method `refMethod` when there exists at
least one type pair `(T_ref, T_con)` — derived from the method's parameter types or return type —
such that:

1. `typeMatcher.isMatched(T_con, T_ref)` (the concrete type incarnates the reference type).
2. `adaptTemplatedName(refMethod.getName(), T_ref.getName(), T_con.getName())
       .map(adapted -> adapted.equals(conMethod.getName())).orElse(false)` — adapted names match,
   OR the names are already equal (unchanged by adaptation).
3. Parameter counts are equal.
4. For each parameter pair `(conParam, refParam)`:
   - The parameter type of `conParam` incarnates (or equals) the parameter type of `refParam`
     via the type matcher.
   - *(If parameter name adaptation was applied by the completer)* the parameter name of `conParam`
     is either the adapted form of `refParam`'s name or the same name.

The type pair candidates are: the return type pair and each parameter type pair (only those whose
type is a CD type with an incarnation). All relevant pairs are applied **sequentially** to the
name, so multiple substitutions can take place in one pass:

```
name = refMethod.getName()
for each (T_ref, T_con) from [returnTypePair] + [paramTypePairs...]:
    name = adaptTemplatedName(name, T_ref.getName(), T_con.getName()).orElse(name)
```

Example: `compareInputAndOutput(Input input, Output output)` with `Input→Foo`, `Output→Bar`
→ after `(Input, Foo)`: `compareFooAndOutput`
→ after `(Output, Bar)`: `compareFooAndBar(Foo foo, Bar bar)`

This is safe from the chaining-bug because only the element's *own* type pairs are iterated, not
all unrelated pairs from the context.

> **Banking2 note:** `transfer(BankAccount targetAccount, double amount)` vs
> `transfer(Account targetAccount, double amount)`. The method name `transfer` is unchanged (no
> `Account` substring in `transfer`), so name adaptation alone is not the matching criterion here.
> Instead, `EqSignatureMethodIncStrategy` (with `METHOD_OVERLOADING`) already handles this via
> type-incarnation-aware parameter type matching (`MCTypeMatchingStrategy` + `cdTypeMatcher`).
> The `AdaptedNameMethodIncStrategy` is needed for cases where the METHOD NAME itself changes
> (e.g., `findTask → findTicket`). For the parameter name concern in `banking2`, see the open
> question in Part 1 above.

**Base class:** extend `CDMethodMatchingStrategy`.

**Registration in `DefaultCDConformanceContext.create()`:**
```java
if (conformanceParams.contains(CDConfParameter.ADAPTED_NAME_MAPPING)) {
  compMethodIncStrategy.addIncStrategy(
      new AdaptedNameMethodIncStrategy(compTypeIncStrategy, mcTypeMatcher, conformanceParams));
}
```

---

### `AdaptedRoleNameAssocIncStrategy` (already exists)

Previously gated on `IMPLICIT_ROLE_NAME_ADAPTATION`; re-gate on `ADAPTED_NAME_MAPPING` instead.
`IMPLICIT_ROLE_NAME_ADAPTATION` can be removed (or deprecated) once `ADAPTED_NAME_MAPPING` is in
place.

**Registration:**
```java
if (conformanceParams.contains(CDConfParameter.ADAPTED_NAME_MAPPING)) {
  ExternalCandidatesMatchingStrategy<ASTCDType> typeMatcherForAdapted =
      conformanceParams.contains(CDConfParameter.INHERITANCE)
          ? compSubTypeIncStrategy : compTypeIncStrategy;
  compAssocIncStrategy.addIncStrategy(
      new AdaptedRoleNameAssocIncStrategy(typeMatcherForAdapted, concreteCD, referenceCD));
}
```

---

## Part 3 — Test Configuration

`DEFAULT_CONFORMANCE_PARAMS` in `AbstractCDConcretizationTest` should include `ADAPTED_NAME_MAPPING`
(replacing `IMPLICIT_ROLE_NAME_ADAPTATION` once the rename is done).

Expected output files for test cases where names are adapted (e.g., `task-management`,
potentially `banking2` for parameter names) must be updated to reflect adapted names.

---

## Summary of Work Items

| # | Component                                | Change                                                                 |
|---|------------------------------------------|------------------------------------------------------------------------|
| 1 | `BaseAttributeInTypeCompleter`           | Use specific `(T_ref, T_con)` pair instead of `adaptNameImplicitly`   |
| 2 | `BaseMethodInTypeCompleter`              | Use specific return/parameter type pairs instead of `adaptNameImplicitly` |
| 3 | `DefaultAssocCompleter`                  | Use specific endpoint type pairs instead of `adaptNameImplicitly`      |
| 4 | `CDConfParameter`                        | Add `ADAPTED_NAME_MAPPING`; keep or deprecate `IMPLICIT_ROLE_NAME_ADAPTATION` |
| 5 | `AdaptedNameAttributeIncStrategy`        | New class                                                              |
| 6 | `AdaptedNameMethodIncStrategy`           | New class                                                              |
| 7 | `AdaptedRoleNameAssocIncStrategy`        | Already exists; re-gate on `ADAPTED_NAME_MAPPING`                     |
| 8 | `DefaultCDConformanceContext`            | Register new strategies under `ADAPTED_NAME_MAPPING`                  |
| 9 | `AbstractCDConcretizationTest`           | Update `DEFAULT_CONFORMANCE_PARAMS`                                    |
| 10| Expected output `.cd` files             | Update where names are now adapted                                     |

## Open Questions

1. **Parameter name adaptation in banking2:** ~~Open — resolved.~~ Parameter names ARE adapted.
   `BankingOut.cd` must be updated: `targetAccount → targetBankAccount`,
   `source → source` (no change; `source` contains no `Account`/`BankAccount` substring),
   and `Transaction → (source) BankAccount [1]` role names are unchanged.
   Concretely, `transfer(BankAccount targetBankAccount, double amount)` is the expected output.

2. ~~**Method name with multiple type pairs** — resolved.~~ All type pairs from the method's own
   return/parameter types are applied sequentially. E.g.,
   `compareInputAndOutput(Input input, Output output)` with `Input→Foo`, `Output→Bar` →
   `compareFooAndBar(Foo foo, Bar bar)`.
