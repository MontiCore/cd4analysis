<!-- (c) https://github.com/MontiCore/cd4analysis -->
This page is under construction.

# Getting Started with the CD4Code Generator

This page describes the technical installation and usage of the CD4Code Generator for 
language developers. This page inspects a simple example class diagram and the
Java classes and other artifacts that are generated from the decorating CD generator. 
After installing the CD4Code Generator, as described on this page, it can be used to 
automatically generate Java code with additional functionality as described in the 
later chapters.

The decorating CD generator is available as a command line interface (CLI) tool, as a library, and 
can easily be used with Gradle. The Gradle integration enables developers to easily employ
the generator in commonly used integrated developer environments (IDEs), such as 
Eclipse and IntelliJ IDEA. This page contains information about an example
class diagram and the generated files and depending on the selected decorators.
(It also shortly explains some key features of the generator.)

(Detailed information about all configuration options that can be used in the Cd4Code 
Generator can be found in the [Configuration](CDGen.md) page.)

## Prerequisites: Installing the Java Development Kit (JDK)

We start with the JDK: Please perform the following steps to install the
Java Development Kit (JDK) and validate that the installation was successful:

- Install a JDK with at least version 21 provided by Oracle or OpenJDK.
- Make sure the environment variable `JAVA_HOME` points to the installed JDK, and
  *not* to the JRE, e.g., the following would be good:
    - `/user/lib/jvm/java-21-openjdk` on UNIX or
    - `C:\Program Files\Java\jdk-21.*` on Windows.
      You will need this to run the Java compiler for compiling
      the generated Java source files.
- Also, make sure that the system variable is set such that the Java
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
integration within Gradle projects, but also as a one-shot generation tool. The CD4Code 
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
`Task` extends `Asset`, and `Project` extends `Asset`. Equally, interfaces can be defined 
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
By default, the [CD2Pojo.ftl](../cdlang/src/main/resources/cd2java/init/CD2Pojo.ftl) template is being used by the CD4Code generator. It defines which decorators are applied to the AST.
It uses the `decConfig` variable which is an instance of the `DecoratorConfig` class. To apply a decorator to the AST, 
we simply need to add the decorator to the `decConfig` variable. For example, to apply the `GetterDecorator` to the AST, we can add the following line to the `CD2Pojo.ftl` template:

```ftl
<#-- Apply the GetterDecorator to the AST -->
${decConfig.withGetters().applyOnName("getter").ignoreOnName("noGetter").defaultApply()}
```

The `.applyOnName("getter")` defines that the `GetterDecorator` should only be applied to classes or attributes which have the stereotype `<<getter>>`.
The `.ignoreOnName("noGetter")` defines that the `GetterDecorator` should not be applied to classes or attributes which have the stereotype `<<noGetter>>`.
The `.defaultApply()` defines that the `GetterDecorator` should be applied to all classes or attributes which do not have the stereotype `<<getter>>` or `<<noGetter>>`.
The [CD2Pojo.ftl](../cdlang/src/main/resources/cd2java/init/CD2Pojo.ftl) includes decorators with the following configurations:


| Decorator                                                                | Description                                                        | To Enable                | To Disable                                |
|--------------------------------------------------------------------------|--------------------------------------------------------------------|--------------------------|-------------------------------------------|
| [CopyDecorator](decorators/CopyDecorator.md)                             | Include all elements of the original CD in the output              | always                   | -                                         |
| [GetterDecorator](decorators/GetterDecorator.md)                         | Add Getter Methods                                                 | 🟩  `<<getter>>`         | `<<noGetter>>`                            |
| [SetterDecorator](decorators/SetterDecorator.md)                         | Add Setter Methods                                                 | 🟩 `<<setter>>`          | `<<noSetter>>`                            |
| [CardinalityDefaultDecorator](decorators/CardinalityDefaultDecorator.md) | Optional and list attributes are initialized with an empty default | 🟩                       | `<<noDefaultCardinality>>`                |
| [NavigableSetterDecorator](decorators/NavigableSetterDecorator.md)       | Setters of bidirectional associations are also bidirectional       | 🟩   `<<setter>>`        | `<<noSetter>>`                            |
| [AbstractMethodDecorator](decorators/AbstractMethodDecorator.md)         | Defined methods are made abstract                                  | 🟩  `<<abstractMethod>>` | `<<nonAbstractMethod>>`                   |
| [BuilderDecorator](decorators/BuilderDecorator.md)                       | Add a builder class                                                | 🟨 `<<builder>>`         | `<<noBuilder>>`                           |
| [ObserverDecorator](decorators/ObserverDecorator.md)                     | Turn the class observable                                          | 🟨 `<<observable>>`      | `<<notObservable>>`                       |
| [VisitorDecorator](decorators/VisitorDecorator.md)                       | Include a visitor                                                  | 🟨 `<<visitor>>`         | `<<noVisitor>>` or `<<noDefaultVisitor>>` |

In the default configuration,
🟩 means the decorator is applied unless disabled.
🟨 means the decorator is not applied unless enabled.
You can find a more detailed description of the decorators by clicking on the corresponding name.

This means by default that the CD4Code generator will generate getters and setters for all attributes.
Furthermore, it will initialize the cardinality of all optional attributes with an empty default value. Finally, the bidirectional associations between
`Project` and `Task` will be navigable in both directions, meaning that the generated setter methods
will also set the opposite side of the association by default.

## Designing a custom Decorator

To design a new decorator, we need to first implement the new Decorator itself, and then add it to the `DecoratorConfig` class. 

### Implementing a new Decorator 
The different Decorator classes are located in the `cdlang/src/main/java/de/monticore/cd/codegen/decorators/` folder. 

As mentioned above, all Decorators extend the `AbstractDecorator` class. This class provides some basic functionality 
for all Decorators, such as the `init()` method which is called by the CD4Code generator before the actual code generation takes place.
Furthermore, it provides the `addToTraverser()` method which is used to include the decorator in the traverser.

Let’s imagine we want to create a `ToStringDecorator` which will add a `toString()` method to all classes.
As our new decorator does not specify any additional data for other decorators, we can set the generic 
attribute of the `AbstractDecorator` class to `NoData`. Keep in mind that we still need to implement the `addToTraverser()` and `getMustRunAfter()` methods.

To later add functionality to the `ToStringDecorator`, we need to implement the visitor methods for the AST elements 
we want to modify. To add them, the class needs to implement the `CDBasisVisitor2` interface. This interface contains 
visitor methods for all AST elements of the class diagram.

```java
public class ToStringDecorator extends AbstractDecorator<AbstractDecorator.NoData>  implements CDBasisVisitor2 {
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this); // 4CDBasis means this decorator will be applied to all elements of the class diagram AST
  }

  @Override
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    return Iterables.concat(super.getMustRunAfter(), Collections.singletonList(
        GetterDecorator.class));
    // GetterDecorator is a Decorator that must be run before this decorator
  }
  
  //...
  
}
```

Now we can add functionality by using the generated visitor pattern. Let’s assume we want to add a `toString()` method to all classes.
We can do this by implementing the `visit(ASTCDClass node)` method of the `CDBasisVisitor2` interface which we need to add as well.

```java  
  @Override
  public void visit(ASTCDClass node) {
    if (decoratorData.shouldDecorate(this.getClass(), node)) {
      addToStringMethod(node);
    }
  }
  
  private void addToStringMethod(ASTCDClass node) {
    if (node.getCDMethodList().stream().noneMatch(m -> m.getName().equals("toString"))) {
      // add the toString method signature to the AST
      ASTCDMethod toStringMethod = CD4CodeMill.cDMethodBuilder()
          .setName("toString")
          .setModifier(CDModifier.PUBLIC.build())
          .setMCReturnType((CD4CodeMill.mCReturnTypeBuilder()
              .setMCType(MCTypeFacade.getInstance().createStringType())
              .build()))
          .build();
      node.addCDMember(toStringMethod);
  
      // create the toString method body
      StringBuilder body = new StringBuilder();
      body.append("return \"").append(node.getName()).append("{\" +\n");
      for (ASTCDAttribute attribute : node.getCDAttributeList()) {
        body.append("  \"").append(attribute.getMCType().printType()).append("=\" + ").append(attribute.getName()).append(" +\n");
      }
      body.append("  '}';");
  
      // as the class diagram language does not support method bodies, we need to add the body as a hook to the method signature
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, toStringMethod, new StringHookPoint(body.toString())));
    }
  }
```

### Adding the new Decorator to the DecoratorConfig
To add the new decorator to the CD4Code generator, we need to add it to the `DecoratorConfig` class. 
This class is responsible for configuring the decorators that are applied to the class diagram. 
To do this, we simply need to add the method `withToString` to the `DecoratorConfig` class.

```java
public class DecoratorConfig {

  //...

  public ChainableGenSetup withToString() {
    return this.withDecorator(new ToStringDecorator());
  }

  //...
}
``` 

Then we can modify the template used for configuration our decorators. 
As we want to generate the `toString()` method for all classes, we add the following line to the `CD2Pojo.ftl` template:
```injectedfreemarker
<#-- ... -->

${decConfig.withToString().applyOnName("toString").ignoreOnName("noToString").defaultApply()}

<#-- ... -->
```
to apply to all classes by default.

### Add tests
To test the new decorator, we can add a test class to the `cdlang/src/test/java/de/monticore/cd/cdgen/` folder. 
This test class should extend the `AbstractDecoratorTest` class which provides some basic functionality for testing decorators.
For more information about test infrastructure, please look into the already existing test classes.

Finally, we just run the CD4Code generator and the `toString()` method will be generated for all classes in our class diagram.

## The TOP Mechanism: Integrating Handwritten Code

A fundamental principle of the CD4Code Generator—and model-driven software engineering in general—is that generated code 
should never be modified manually. If we edit a generated file in the `build/` directory, your changes will be permanently 
overwritten the next time the generator runs.

Class diagrams are structural models. They do not define behavior logic or method bodies. To solve the conflict between 
preserving generated code and implementing custom behavior, the CD4Code Generator uses the TOP Mechanism.

We need to create the handwritten class files *before* running the generator for the first time. 
This prevents the generator from creating a conflicting file and ensures a smooth integration.

To use the TOP mechanism, we need to tell the generator where to look for handwritten code. This is done by specifying 
the `-hcp` option when running the generator. If we want to change the behavior of a class `X` in the generated code, 
we can create a file in the same directory as the generated file, named `X.java`. The generator checks for each class 
if it already has a corresponding file in the handwritten code directory. If it finds one, it will rename the generated 
class to `XTOP` and use the handwritten code instead. This way, the generated code can still be used by extending it, 
while the handwritten code can modify specific behavior without complete class rewrites.

### Use Cases for the TOP Mechanism
The TOP mechanism allows developers to seamlessly inject handwritten code into the generated architecture. It is primarily used for:

- **Implementing Method Bodies**: We can write the logic for methods defined in the class diagram without modifying generated files.
- **Adding complex Business Logic**: Business logic not directly related to the class diagram can be added to the generated code.
- **Connecting to non-generated code**: We can integrate with external libraries or frameworks that are not part of the generated architecture.
- **Overriding Generated Code**: Intercepting and modifying the generated code.
- **Adding Non-Modeled State**: Adding attributes or methods that are not part of the class diagram.

### Fixing the Issues 
If we look back at our class diagram, we see that the abstract class `Asset` has the method `process()`. 
This method is inherited by both `Project` and `Task`. However, the implementation of this method is not 
defined in the class diagram. This poses a problem for the CD4Code generator, as method bodies cannot be 
generated from the class diagram. But if we do not add the method body before generating the code, the 
generated code will not compile. Which is against the design principle of the CD4Code generator.

To safely implement the `process()` methods, we use the TOP mechanism by telling the generator where our handwritten 
code lives using the Handwritten Code Path (`-hcp`).
To implement the behavior for `Project` and `Task`, we create the following files in the `src/` directory:

```src/
├── main/
│   └── java/
│       └── MyOrganization/
│           ├── Project.java
│           └── Task.java
```

In `Project.java`, we implement the `process()` method for the `Project` class:

```java
package MyOrganization;

class Project extends ProjectTOP {
  
  @Override
  public void process() {
    // Custom logic for Project processing
  }
}
```

and in `Task.java`, we implement the `process()` method for the `Task` class:

```java
package MyOrganization;

class Task extends TaskTOP {
  
  @Override
  public void process() {
    // Custom logic for Task processing
  }
}
```
By following this approach, we can safely implement the behavior for both `Project` and `Task` without modifying 
any generated files. The CD4Code generator will generate the necessary structure and method signatures based on 
the class diagram, while our handwritten code will provide the specific logic for the `process()` methods. This allows 
us to maintain a clear separation between generated code and custom behavior, adhering to the principles of model-driven 
software engineering.

The CD4Code generator will also now generate the compiling code, where the classes `Project` and `Task` are not
abstract anymore. Therefore, the generated Builder and Observer classes will produce compilable code.

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

The generated Java files should contain the expected getters, setters, and other methods as defined by the applied decorators. 
You can now integrate this generated code into your Java project, further customize it, or use it as a base for additional development using the TOP-Mechanism.