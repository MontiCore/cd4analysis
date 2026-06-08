<!-- (c) https://github.com/MontiCore/cd4anaylsis -->
This page is under construction.

# Getting Started with the CD4Code Generator

This page describes the technical installation and usage of the CD4Code Generator for 
language developers. This page inspects a simple example class diagram and the
Java classes and other artifacts that are generated from the decorating CD generator. 
After installing the CD4Code Generator, as described on this page, it can be used to 
automatically generate Java code with additional functionality as described in the 
subsequent chapters.

The decorating CD generator is available as a command line interface (CLI) tool, as a library, and 
can easily be used with Gradle. The Gradle integration enables developers to easily employ
the generator in commonly used integrated developer environments (IDEs), such as 
Eclipse and IntelliJ IDEA. This page contains information about an example
class digram and the generated files and depending on the selected decorators.
(It also shortly explains some key features of the generator.)

(Detailed information about all configuration options that can be used in the Cd4Code 
Generator can be found in the [Configuration](Configuration.md) page.)

## Prerequisites: Installing the Java Development Kit (JDK)

We start with the JDK: Please perform the following steps to install the
Java Development Kit (JDK) and validate that the installation was successful:

- Install a JDK with at least version 21 provided by Oracle or OpenJDK.
- Make sure the environment variable `JAVA_HOME` points to the installed JDK, and
  *not* to the JRE, e.g., the following would be good:
    - `/user/lib/jvm/java-21-openjdk` on UNIX or
    - `C:\Program Files\Java\jdk-21.*` on Windows.
      You will need this in order to run the Java compiler for compiling
      the generated Java source files.
- Also make sure that the system variable is set such that the Java
  compiler can be used from any directory. JDK installations on UNIX
  systems do this automatically. On Windows systems, the `bin`
  directory of the JDK installation needs to be appended to the `PATH`
  variable, e.g. `%PATH%;%JAVA_HOME%`.
- Test whether the setup was successful. Open a command line shell in
  any directory. Execute the command `javac -version`. If this command
  is recognized and the shell displays the version of the installed
  JDK (e.g., `javac 21.0.10`), then the setup was successful.
- *(Optional)* Install [Gradle](https://gradle.org/install/) version 8.14.4.

Now we have the prerequisites to run the CD4Code generator from the command line (CLI)
or alternatively using Gradle.

### Installation

For installing the CD4Code generator for either the CLI or Gradle usage,
select the suitable tab below and perform the following steps:

=== "CLI"
    A ready to use version of the tool can be downloaded in the form of an
    executable JAR file.
    You can use [**this download link**][ToolDownload] for downloading the tool.
    Alternatively, the `wget` command can be used to download the latest version
    into your working directory:
    ```shell
    wget "https://monticore.de/download/MCCD.jar" -O MCCD.jar
    ```
=== "Gradle"
    By adding the `de.rwth.se.cdgen` Gradle plugin to your project,
    all class diagrams in the _cds_ source-directory-set (e.g., _src/main/cds_, _src/test/cds_) are generated to Java code.
    
    ```groovy 
    // build.gradle
    plugins {
        id 'java-library'
        id 'de.rwth.se.cdgen' version '7.9.0-SNAPSHOT'
    }
    
    repositories {
        maven { url 'https://nexus.se.rwth-aachen.de/content/groups/public' }
        mavenCentral()
    }
    
    // settings.gradle
    pluginManagement {
        repositories {
            maven {
                url "https://nexus.se.rwth-aachen.de/content/groups/public"
            }
        }
    }
    ```
    For the main source set, all `.cd` files within the `src/main/cds` directory will be processed.

## Inspect the class diagram

The CD4Code generator helps to generate Java code from class diagrams. It supports easy 
integration within gradle projects, but also as a one-shot generation tool. The CD4Code 
generator processes class diagrams that are stored in files. The CD4Code generator will 
process all `.cd` files in these directories and generate Java code based on the 
class diagrams defined in these files. Each CD contains packages, classes, attributes, 
methods, and associations. The CD4Code generator will generate Java code based on the 
structure of the class diagram and the applied decorators.

It is a *key feature* of the CD4Code generator that the generated Java code can be 
extended with the addition of decorators. These decorators dictate what artifacts 
are generated from the class diagram, or which should not be generated at all.

```cd4code
/* (c) https://github.com/MontiCore/monticore */
import java.util.Date;
import java.util.Optional;

classdiagram MyOrganizer {

  enum Status { PROCESSING, DONE, OPEN; }

  abstract class Asset {
    void process();
  }

  class Task extends Asset {
    Status taskStatus;
    void process();
  }

  class Project extends Asset {
    public String projectName;
    private Optional<Date> deadline;
    protected double budget;
    void process();
  }

  class Day {
    Date date;
  }

  association [1] Day (day) ->  (tasks) Task [*];
  association [*] Task (tasks) <-> (project) Project [1];
}
```
<figcaption>Listing 2.1: The <code>MyOrganizer</code> class diagram</figcaption>

As usual in model-based software engineering, the core of the file is the diagram definition itself.
It begins with the `classdiagram` keyword, followed by the name of the diagram,
which must match the filename. In our example, the diagram is named `MyOrganizer` and
its body is enclosed in curly braces `{ }`.

Class diagrams can have import statements to integrate external types.
Every import is of the form `import` *QualifiedName*. For instance, the `MyOrganizer` class diagram
uses `import java.util.Date;` and `import java.util.Optional;` to make the standard Java `Date` and `Optional` classes available within the model.

Inside the class diagram, various object-oriented constructs can be defined, such as enumerations, 
classes, interfaces, and their relationships. The `MyOrganizer` diagram introduces the enumeration `Status` using 
the `enum` keyword, defining the constants `PROCESSING`, `DONE`, and `OPEN`. 
It also defines several classes, such as `Asset`, `Task`, `Project`, and `Day`. The `abstract` 
keyword can be applied to classes, as seen with `abstract class Asset;`. 
Furthermore, the `extends` keyword is used to establish inheritance. In our example, 
`Task` extends `Asset`, and `Project` extends `Asset`. Equaly, interfaces can be defined 
as well, using the `interface` keyword, and classes can implement interfaces using the `implements` keyword.

Classes typically contain attributes, which consist of a type and a name. The CD4Code 
generator supports standard Java primitive types (like `double budget;`) , imported external types (like `Date date` and `Optional<Date> deadline`), 
and custom types like enums (`Status taskStatus`). Classes and interfaces can also define methods, such as `void process();`. You can also define access modifiers like `public`, `private` and `protected`.

Finally, the class diagram defines how these entities relate to one another using associations and compositions.
While associations define relationships between two entities that simply know about each other, 
compositions define a strong ownership relationship between two entities. 
Standalone relationships use keywords like `association` or `composition`, followed by 
cardinalities (e.g., `[1]`, `[1..*]`, `[*]`), the participating classes, 
and navigation arrows (`<->` for bidirectional, `->` for directional, 
or `--` for unspecified). Relationships can also specify role names in parentheses to clarify the relationship's context, 
such as `(project)` and `(tasks)`.

## Decorators
At the very start of the CD4Code generator, the generator parses the class diagram DSL into the  *CD4C Abstract Syntax Tree (AST)*
which represents the class diagram as an object tree.
Then the CD4Code generator applies the decorators to the AST. 

Decorators are classes that can modify the AST by adding, removing, or changing elements and by adding template 
hooks to objects of the AST. While the modifications on the AST can be visualized and seen directly, the template 
hooks are only processed when the actual code generation takes place. 

The CD4Code generator comes with a set of prewritten decorators which can be applied to the AST. In This chapter, we will 
discuss the decorators in detail.

The Basis of all Decorators is the `CopyDecorator` which is responsible for copying the original `AST` and doing 
some basic transformations on it. After this the CD4Code generator will apply the remaining decorators to the AST.
As some Decorators dependent on other Decorators, all Decorators implement the 
interface `IDecorator<D>` which contains the method `getDependencies()` which returns a list of Decorators that must 
be run before itself. For example, the VisitorDecorator depends on the GetterDecorator,
so the `getDependencies()` method returns `Collections.singletonList(GetterDecorator.class)`. Therefore, the 
CD4Code generator checks the dependencies of the selected Decorators and runs them in the correct order. 
If there is a circular dependency, the CD4Code generator throws an error and does not generate any code.

```java
/** Extend {@link AbstractDecorator} for shared */
public interface IDecorator<D> extends IVisitor {
  
  /**
   * Add your decorator-visitor to the given traverser
   *
   * @param traverser the traverser
   */
  void addToTraverser(CD4CodeTraverser traverser);
  
  void init(DecoratorData util, Optional<GlobalExtensionManagement> glexOpt);
  
  /** @return the list of decorators which MUST traverse the AST before */
  @SuppressWarnings("rawtypes")
  default Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    return Collections.singletonList(ICreator.class);
  }
  
}
```
By default, the [CD2Pojo.ftl](../cdlang/src/main/resources/cd2java/init/CD2Pojo.ftl) template is being used by the CD4Code generator.
It includes the following decorators:

| Decorator                   | Description                                                        | To Enable                | To Disable                                |
|-----------------------------|--------------------------------------------------------------------|--------------------------|-------------------------------------------|
| [CopyDecorator](decorators/CopyDecorator.md)                 | Include all elements of the original CD in the output              | always                   | -                                         |
| [GetterDecorator](decorators/GetterDecorator.md)             | Add Getter Methods                                                 | 🟩  `<<getter>>`         | `<<noGetter>>`                            |
| [SetterDecorator](decorators/SetterDecorator.md)             | Add Setter Methods                                                 | 🟩 `<<setter>>`          | `<<noSetter>>`                            |
| [CardinalityDefaultDecorator](decorators/CardinalityDefaultDecorator.md) | Optional and list attributes are initialized with an empty default | 🟩                       | `<<noDefaultCardinality>>`                |
| [NavigableSetterDecorator](decorators/NavigableSetterDecorator.md)    | Setters of bidirectional associations are also bidirectional       | 🟩   `<<setter>>`        | `<<noSetter>>`                            |
| [AbstractMethodDecorator](decorators/AbstractMethodDecorator.md)     | Defined methods are made abstract                                  | 🟩  `<<abstractMethod>>` | `<<nonAbstractMethod>>`                   |
| [BuilderDecorator](decorators/BuilderDecorator.md)            | Add a builder class                                                | 🟨 `<<builder>>`         | `<<noBuilder>>`                           |
| [ObserverDecorator](decorators/ObserverDecorator.md)           | Turn the class observable                                          | 🟨 `<<observable>>`      | `<<notObservable>>`                       |
| [VisitorDecorator](decorators/VisitorDecorator.md)            | Include a visitor                                                  | 🟨 `<<visitor>>`         | `<<noVisitor>>` or `<<noDefaultVisitor>>` |

In the default configuration,
🟩 means the decorator is applied unless disabled.
🟨 means the decorator is not applied unless enabled.
You can find a more detailed description of the decorators by clicking on the corresponding name.

This means by default that the CD4Code generator will generate getters and setters for all attributes.
Furthermore, it will initialize the cardinality of all optional attributes with an empty default value. Finally, the bidirectional associations between
`Project` and `Task` will be navigable in both directions, meaning that the generated setter methods
will also set the opposite side of the association by default.

### Configuring the CD4Code Generator

While the default `CD2Pojo` configuration is a great starting point, manually adding stereotypes
(like `<<builder>>` or `<<noSetter>>`) directly to every element in a `.cd` file can become
tedious and clutter the model. To solve this, the CD4Code Generator allows you to configure
decorators externally.

Configuration can be applied at two different levels:

1.  **Element-Level Configuration (Tagging):** You can target specific elements inside your class diagram
    (such as a specific class, enum, or attribute) to explicitly enable or disable a decorator.
    This uses a targeting syntax of `<DiagramName>.<ElementName>:<Tag>`. For example, targeting
    `MyOrganizer.Day:noSetter` will prevent the generator from creating setter methods specifically for
    the `Day` class.
2.  **Global-Level Configuration (Templates):** If you need to fundamentally change the default behavior or
    apply your own custom decorators across the entire build, you can supply a custom configuration template
    (e.g., a custom `.ftl` file) to replace the default `CD2Pojo` template.

### Applying Configurations

Depending on how you are running the CD4Code Generator, you can pass these configurations via the command line,
your Gradle build script, or directly through the Java API. Select your environment below:

=== "CLI"
When running the CD4Code generator from the command line, you can pass element-level tags using the `-cliconfig`
parameter. Multiple configurations can be applied by repeating the argument.

    For example, to disable getters and setters specifically for the `Day` class inside the `MyOrganizer` 
    diagram, use the following command:
    
    ```shell
    java -jar MCCD.jar -i src/MyOrganizer.cd -cliconfig "MyOrganizer.Day:noGetter" -cliconfig "MyOrganizer.Day:noSetter"
    ```
    
    To apply a global configuration template, use the `-ct` (config template) argument to specify the 
    template name, and `-fp` (file path) to specify the directory where the custom `.ftl` file is located:
    
    ```shell
    java -jar MCCD.jar -i src/MyOrganizer.cd -ct CD2OwnDecorator -fp src/main/configTemplate
    ```

=== "Gradle"
When using Gradle, element-level configurations can be added directly to the `options` list of the
`generateClassDiagrams` task.

    ```groovy
    // build.gradle
    tasks.named("generateClassDiagrams") {
      // Element-level configuration targeting the Day class
      options.add("MyOrganizer.Day:noGetter")
      options.add("MyOrganizer.Day:noSetter")
      
      // Global-level configuration: Change the config template used by the generator
      // getConfigTemplate().set("CD2OwnDecorator")
      
      // Additional optional configurations:
      // getClass2MC().set(true)
      // getCoCos().set(false) // (Not encouraged!)
      // getOriginalSymbolOutput().set(...)
      // getDecoratedSymbolOutput().set(...)
      // getOutputDir().set(...)
    }
    ```

## Running the CD4Code Generator
The execution of the CD4Code Generator follows a structured pipeline.
First parsing and validating the model, then managing its symbols, and finally transforming the diagram
into executable Java source code.

### 1. Loading, CoCo-Checking, and Symbol Table Creation
The first phase of execution focuses on frontend processing. The generator loads the .cd file, parses its
contents, creates an internal symbol table to resolve types, and runs Context Conditions (CoCos) to
ensure the diagram adheres to all semantic rules of the language.

=== "CLI"
    To parse and validate a class diagram model without generating any code artifacts, pass the input file using the `-i` flag to specify the input file path. By default, basic validation occurs, but you can explicitly enforce full CoCo checks or enable Java type resolution.

    ```shell
    # Basic parse, symbol table creation, and check
    java -jar MCCD.jar -i src/MyOrganizer.cd
    
    # Explicitly check all CD4C Context Conditions (CoCos)
    java -jar MCCD.jar -i src/MyOrganizer.cd --checkcocos
    
    # Enable resolution of standard Java classes (e.g., java.util.List) within the model
    java -jar MCCD.jar -i src/MyOrganizer.cd --class2mc
    ```

=== "Gradle"
    In a standard Gradle setup, the plugin automatically configures these phases as part of its default task execution pipeline. However, you can control CoCo behavior and type resolution directly within the task configuration block.
    
    ```groovy
    // build.gradle
    tasks.named("generateClassDiagrams") {
      // Enables resolving standard Java classes used inside the CD diagram
      getClass2MC().set(true)
      
      // Controls whether CoCo checks are executed (enabled by default)
      getCoCos().set(true)
    }
    ```

### 2. Storing and Exporting Symbols
In a large-scale project, comprehensibility suffers when a single file contains all artifacts of our class
diagram. To address this issue, the CD4Code Generator can serialize its symbol table into a standalone
symbol file, which can then be exported or loaded as a dependency by other models.

=== "CLI"
    Use the `-s` or `--symboltable` flag to specify where the serialized symbol table file should be saved. If your diagram depends on external symbols, use the -path flag to point to the directory containing those symbol files.

    ```shell
    # Export the symbol table to a specific file
    java -jar MCCD.jar -i src/MyOrganizer.cd -s out/symbols/MyOrganizer.cdsym

    # Load external dependencies/symbols while processing a diagram
    java -jar MCCD.jar -i src/MyOrganizer.cd -path dependencies/symbols/
    ```

=== "Gradle"
    The Gradle plugin manages symbol storage and tracking automatically, storing original and decorated symbols in separate build directories. You can customize these locations if your build pipeline requires a non-standard layout.

    ```groovy
    // build.gradle
    tasks.named("generateClassDiagrams") {
      // Customize the output directory for the original symbol table
      getOriginalSymbolOutput().set(file("build/custom-symbols/original"))
    
      // Customize the output directory for the decorated symbol table
      getDecoratedSymbolOutput().set(file("build/custom-symbols/decorated"))
    }
    ```

### 3. Generating Java Code
Once the model is fully validated and its symbols are resolved, the generator can proceed to execute
the decorators and generate the actual Java source files.

=== "CLI"
    To trigger code generation, you must explicitly include the `--gen` flag. You can combine this with the `-o` flag to specify the target directory for the generated code, and `--fieldfromrole` to control how associations are translated into actual class fields.

    ```
    # Generate Java files into a dedicated output directory
    java -jar MCCD.jar -i src/MyOrganizer.cd --gen -o out/generated-sources

    # Generate code while explicitly mapping navigable association roles to Java fields
    java -jar MCCD.jar -i src/MyOrganizer.cd --gen -o out/generated-sources --fieldfromrole navigable
    ```
    
    If your class diagram contains associations (e.g., `association [*] Task (tasks) <-> (project) Project [1];`),
    the basic `--gen` command will not automatically generate the corresponding Java fields to link these objects.
    Instead, you must explicitly tell the generator to map these association roles to fields using the
    `--fieldfromrole` flag.
    
    In our example, the `Project` class has a role named `project` in its association with `Task`.
    This means the generator will create an `project` field inside the generated `Task` Java class to represent 
    the relationship. To generate these fields, use the following command:
    
    ```shell
    java -jar MCCD.jar -i src/MyOrganizer.cd -o out --gen --fieldfromrole navigable
    ```

=== "Gradle"
    Code generation is fully integrated into the standard Gradle lifecycle. Executing the `build` task or the specific `generateClassDiagrams` task automatically processes all source sets and places the output in the configured directory.

    ```groovy
    // build.gradle
    tasks.named("generateClassDiagrams") {
      // Set the target directory for the generated Java files
      getOutputDir().set(file("build/generated/sources/cdgen/main/java"))
    }
    ```

#  === "Gradle"
#  Just like the CLI, the Gradle plugin does not generate fields for associations by default. You must explicitly configure the task to map these roles to Java fields.
#  
#  You can do this by setting the `fieldFromRole` property inside your generation task:
#  
#  ```groovy
#  // build.gradle
#  tasks.named("generateClassDiagrams") {
#    // Set the target directory for the generated Java files
#    getOutputDir().set(file("build/generated/sources/cdgen/main/java"))
#  
#    // Explicitly map navigable association roles to generated Java fields
#    getFieldFromRole().set("navigable")
#  }
#  ```

Running the CD4Code generator tooled into a Gradle build is as simple as executing the Gradle build task.

### Inspecting the Generated Code
The generated code should now be located in the specified directory. Let's take a look at the generated code.
As by default, we use the `CD2Pojo` template, for configuring the Decorators, we are applying the `GetterDecorator`,
`SetterDecorator`, `CardinalityDecorator`, `NavigableSetterDecorator`, and `AbstactMethodDecorator` Decorators. 
Therefore, we also expect the respecitive code artifacts to be generated. In our example, we should find the following files:

```text
my-project/
├── build/
│   ├── cdgensymbols/
│   ├── classes/
│   │   └── java/
│   │       └── main/
│   │           └── MyOrganizer/
│   │               ├── Asset.java
│   │               ├── Day.java
│   │               ├── Project.java
│   │               ├── Status.java
│   │               └── Task.java
│   ├── generated/
│   └── generated-sources/
│       └── cdgen/ 
│           └── sourcecode/
│               └── MyOrganizer/
│                   ├── Asset.java
│                   ├── Day.java
│                   ├── Project.java
│                   ├── Status.java
│                   └── Task.java
├── configTemplate/
│   └── CD2OwnDecorator.ftl
└── build.gradle
 README.md
```