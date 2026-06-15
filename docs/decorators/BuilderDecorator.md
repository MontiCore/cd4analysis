# BuilderDecorator

The `BuilderDecorator` implements the Builder Design Pattern by automatically generating a dedicated Builder class for every instantiable class in your class diagram. This is especially useful for classes with numerous attributes, providing a fluent and readable API for object creation instead of relying on massive constructors or numerous setter calls.

## The Core Mechanism

When the generator runs, this decorator processes every class (`ASTCDClass`) in the diagram:

1.  **Target Selection:** It identifies classes that require a builder (typically non-abstract classes, or those specifically tagged).
2.  **Builder Class Generation:** For a class named `X`, it creates a new class `XBuilder`.
3.  **Attribute Duplication:** It duplicates all attributes from the target class into the builder class to hold the intermediate state.
4.  **Fluent Setter Generation:** It generates "set" methods (e.g., `setProjectName(String)`) that return `this.realBuilder` for method chaining, ensuring compatibility with inheritance.
5.  **Validation Method Generation:** It generates a `isValid()` method that checks if all required attributes have been set.
6.  **Build Method:** It generates a `build()` method that validates the state, constructs, and returns an instance of the target class using the accumulated properties.

---

![Figure 1.1 The original class diagramm after applying the mandatory CopyDecorator](../../myOrganizer/img/MyOrganizerNoDecorators.svg)
<figcaption>Figure 1.1 The class diagram after the mandatory CopyDecorator</figcaption>

![Figure 1.2 The original class diagram after applying the BuilderDecorator](../../myOrganizer/img/MyOrganizerOnlyBuilders.svg)
<figcaption>Figure 1.2 The original class diagram after applying the BuilderDecorator</figcaption>

---

## Real-World Breakdown: Generation Strategies

Looking at the `Project` class, which has multiple attributes (`projectName`, `deadline`, `budget`, `tasks`), creating an instance manually might be cumbersome. The `BuilderDecorator` simplifies this by generating a `ProjectBuilder`.

```java
// Example of generated fluent setters in ProjectBuilder
public ProjectBuilder setProjectName(String projectName) {
    this.projectName = projectName;
    return this.realBuilder;
}

public ProjectBuilder setBudget(double budget) {
    this.budget = budget;
    return this.realBuilder;
}

// Example of the final build method
public Project build() {
    if(!isValid()){
      throw new IllegalStateException("build called on an incomplete object of type Project.");
    }
    var v = new Project();
    v.setProjectName(this.projectName);
    v.setBudget(this.budget);
    // ... set other attributes
    return v;
}
```

This allows developers to create instances of `Project` with a more concise syntax:

```java
Project project = new ProjectBuilder()
    .setProjectName("New Website")
    .setBudget(10000.0)
    .build();
```