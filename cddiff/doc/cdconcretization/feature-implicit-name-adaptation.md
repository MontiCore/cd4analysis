# Feature: Implicit Name Adaptation

## Overview

When the completer creates a new element (attribute, method, or association role) in the
concrete CD, it copies the name from the reference model by default.
**Implicit name adaptation** improves this by automatically renaming the element whenever its
name contains a reference type name as a recognisable substring — replacing it with the
corresponding concrete type name.

The adaptation recognises three patterns. Using `Task → Ticket` as an example:

| Pattern | Reference name | Adapted name |
|---|---|---|
| Exact match | `task` | `ticket` |
| Capitalised infix | `assignedTask` | `assignedTicket` |
| Capitalised prefix | `taskList` | `ticketList` |

If none of the patterns match, the name is left unchanged and no adaptation is applied.

The feature is enabled by default and can be disabled on `ConcretizationCompleter` via
`setImplicitNameAdaptationEnabled(false)`.

---

## Affected Elements

### Attributes

An attribute's name is adapted using the type pair of its own declared type. If the attribute
type `T_ref` is incarnated by `T_con`, the completer tries to replace occurrences of
`T_ref`'s name in the attribute name with `T_con`'s name.

**Example** — reference attribute `assignedTasks: Task`, concrete incarnation `Ticket` of `Task`:

```
// Reference CD
class Project {
  Task assignedTasks;
}

// Concrete CD (input)
<<ref="Task">>    class Ticket;
<<ref="Project">> class Sprint;

// Completed output
class Sprint {
  Ticket assignedTickets;   // "Tasks" → "Tickets"
}
```

### Methods

A method's name is adapted using the type pairs of its return type and all parameter types,
applied in sequence. This means a method whose name references multiple types can be fully
adapted in a single pass. Parameter names are adapted the same way, each using its own
parameter type's pair.

**Example 1** — adaptation via return type:

```
// Reference CD
class TaskRepository {
  Task findTask(String id);
}

// Concrete incarnation: Ticket → Task

// Completed output
class TicketRepository {
  Ticket findTicket(String id);   // method name and return type both adapted
}
```

**Example 2** — adaptation via multiple parameter types:

```
// Reference CD
interface Comparator {
  int compareInputAndOutput(Input a, Output b);
}

// Concrete incarnations: Foo → Input, Bar → Output

// Completed output
interface ConcreteComparator {
  int compareFooAndBar(Foo a, Bar b);
}
```

### Association Roles and Names

Each role name is adapted using the type pair of its own endpoint. The association name, if
present, is adapted using both endpoint type pairs in sequence.

**Example**:

```
// Reference CD
association [*] (managedAccounts) Account <-> Bank;

// Concrete incarnations: BankAccount → Account, SEPABank → Bank

// Completed output
association [*] (managedBankAccounts) BankAccount <-> SEPABank;
```

---

## Interaction with `ADAPTED_NAME_MAPPING`

The `ADAPTED_NAME_MAPPING` conformance parameter controls how the conformance checker
recognises implicitly adapted names as valid incarnations. This in turn affects whether the
completer adds a `<<ref="...">>` stereotype to adapted elements.

### Without `ADAPTED_NAME_MAPPING`

When this parameter is not set, the conformance checker has no strategy to recognise, for
example, `assignedTickets` as an incarnation of `assignedTasks`. To ensure conformance still
passes, the completer adds a `<<ref>>` stereotype to every element whose name was changed by
implicit adaptation. The stereotype-based matcher (`STAttributeIncStrategy` /
`STMethodIncStrategy`) then identifies it as a valid incarnation.

```
// Completed output — no ADAPTED_NAME_MAPPING
class Sprint {
  <<ref="Project.assignedTasks">> Ticket assignedTickets;
}
```

### With `ADAPTED_NAME_MAPPING` (recommended)

When this parameter is set, the conformance checker includes `AdaptedNameAttributeIncStrategy`
and `AdaptedNameMethodIncStrategy`, which recognise adapted names directly by applying the
same substitution patterns used during completion. No stereotype is needed, producing cleaner
output:

```
// Completed output — with ADAPTED_NAME_MAPPING
class Sprint {
  Ticket assignedTickets;
}
```

This is the recommended configuration for auto-completed CDs. It also means a hand-written
concrete CD that follows the adapted naming convention will pass conformance checks without
requiring manual stereotype annotations.

> **Note:** The `<<ref>>` stereotype is still added even with `ADAPTED_NAME_MAPPING` when the
> name was changed by the **multi-incarnation suffix** (e.g., `sourceAccount_BankAccount`),
> because that suffix is not derivable by name pattern matching alone.

### Summary

| Scenario | `<<ref>>` stereotype added? | Matched by |
|---|---|---|
| Name unchanged (single incarnation) | No | `EqNameAttributeIncStrategy` / `EqSignatureMethodIncStrategy` |
| Name unchanged (multiple incarnations, suffix added) | Yes | `STAttributeIncStrategy` / `STMethodIncStrategy` |
| Name adapted, `ADAPTED_NAME_MAPPING` **not** set | Yes | `STAttributeIncStrategy` / `STMethodIncStrategy` |
| Name adapted, `ADAPTED_NAME_MAPPING` set | No | `AdaptedNameAttributeIncStrategy` / `AdaptedNameMethodIncStrategy` |

---

## Complete Example: Task Management

**Reference CD:**

```
classdiagram TaskRef {
  class Task {
    String title;
    Task assignedTask;
  }
  association Project -> (assignedTasks) Task [*];
}
```

**Concrete CD (input):**

```
classdiagram TaskConc {
  <<ref="Task">>    class Ticket { String title; }
  <<ref="Project">> class Sprint;
}
```

**Completed output** (with `ADAPTED_NAME_MAPPING`):

```
classdiagram TaskConc {
  <<ref="Task">> class Ticket {
    String title;
    Ticket assignedTicket;
  }
  <<ref="Project">> class Sprint;
  association Sprint -> (assignedTickets) Ticket [*];
}
```

Both the attribute name (`assignedTask` → `assignedTicket`) and the association role name
(`assignedTasks` → `assignedTickets`) are adapted automatically. No `<<ref>>` stereotypes are
added to these adapted elements because `ADAPTED_NAME_MAPPING` is set and the conformance
checker can identify them by their adapted names directly.
