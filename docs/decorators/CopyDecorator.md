# CopyDecorator

The `CopyDecorator` is a mandatory, foundational step in the CD4Code generation pipeline. Unlike other decorators 
that add optional features, the `CopyDecorator`'s primary role is to create a clean, unified, and complete 
Abstract Syntax Tree (AST) from the initial parsed class diagram. It normalizes the diagram's structure, 
resolves associations, and sets sensible defaults, preparing the AST for all subsequent decorators.

## The Core Mechanism

The `CopyDecorator` is always the first to run. It takes the raw AST from the parser and performs several crucial transformations:

1.  **AST Unification:** It creates a deep copy of the original AST. This ensures that all subsequent modifications by other decorators do not affect the original, parsed model.
2.  **Package Creation:** If the class diagram is not explicitly defined within a package, the decorator creates a default package to encapsulate all the elements.
3.  **Association Materialization:** It translates abstract `association` and `composition` relationships into concrete class attributes. Based on the defined cardinality, it adds fields to the corresponding classes:
    *   `[*]` or `[0..*]` becomes a `Set<Type>`.
    *   `[1..*]` becomes a `List<Type>`.
    *   `[1]` or `[1..1]` becomes a direct `Type` reference.
    *   `[0..1]` becomes an `Optional<Type>`.
4.  **Default Visibility:** It enforces a "public by default" policy. Any class, attribute, or method without an explicit visibility modifier (`public`, `protected`, `private`) is automatically set to `public`.

---

![Figure 1.1 The original class diagram](../../myOrganizer/img/MyOrganizer.svg)
<figcaption>Figure 1.1 The original class diagram as defined in the `.cd` file</figcaption>

![Figure 1.2 The class diagram after applying the CopyDecorator](../../myOrganizer/img/MyOrganizerNoDecorators.svg)
<figcaption>Figure 1.2 The unified class diagram after the `CopyDecorator` has run</figcaption>

---

## Real-World Breakdown: Generation Strategies

The `CopyDecorator`'s impact is best seen by comparing the user-defined class diagram with the resulting class 
diagram after the `CopyDecorator` modiefied it.

### Association to Attributes

The original diagram defines a bidirectional association:
`association [*] Task (tasks) <-> (project) Project [1];`

The `CopyDecorator` dissolves this relationship and injects corresponding attributes into the classes:
*   The `Project` class gets a `private Set<Task> tasks;` attribute.
*   The `Task` class gets a `private Project project;` attribute.

### Default Visibility

In the original diagram, the `Task` class and its `taskStatus` attribute have no explicit visibility.

```cd4code
class Task extends Asset {
  Status taskStatus;
  void process();
}
```

The `CopyDecorator` automatically promotes them to `public`, making them accessible by default. This unified AST is then passed to other decorators like `GetterDecorator` and `SetterDecorator`, which will later reduce the visibility of the fields to `protected` to enforce encapsulation.