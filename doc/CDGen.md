<!-- (c) https://github.com/MontiCore/monticore -->

> This readme is still under construction!

The decorating CD generator is available as a simple gradle plugin,
as a standalone CLI-jar, and as a library for inclusion.

An example gradle configuration can be found below:

```groovy
// build.gradle
plugins {
  id 'java-library'
  id 'de.rwth.se.cdgen' version '7.8.0-SNAPSHOT'
}

tasks.named("generateClassDiagrams") {
  // Tag-like configuration of the "MyCD.ConfiguredFromCLI" element
  options.add("MyCD.ConfiguredFromCLI:noGetter")
  options.add("MyCD.ConfiguredFromCLI:noSetter")
  // Optionally, enable class2mc
  // getClass2MC().set(true)
  // Optionally, disable CoCo checks (not encouraged!)
  // getCoCos().set(false)
  // Change the output directory of the original ST, default: cdgensymbols/main/original
  // getOriginalSymbolOutput().set(...)
  // Change the output directory of the decorated ST, default: cdgensymbols/main/decorated
  // getDecoratedSymbolOutput().set(...)
  // Change the config template used to configure the decorators, default see below
  // getConfigTemplate().set(...)
  // Change the output directory
  // getOutputDir().set(...)
  // ... and further MCAllFilesTask properties
}

repositories {
  maven { url 'https://nexus.se.rwth-aachen.de/content/groups/public' }
  mavenCentral()
}
dependencies {
  implementation "de.monticore:monticore-runtime:7.8.0-SNAPSHOT"
}
```

For the main source set, all `.cd` files within the `src/main/cds` directory will be processed.

### Default Configuration: CD2Pojo

By default, the [CD2Pojo.ftl](../cdlang/src/main/resources/cd2java/init/CD2Pojo.ftl) template
is used by the generator.
It includes the following transformations:

* CD4CodeAfterParseTrafo:
* DefaultVisibilityPublicTrafo: absent visibility means *public*
  It includes the following decorators:
* CopyCreator: Include all elements of the original CD in the output
* GetterDecorator: By default, getters are added to all elements
* SetterDecorator: By default, setters are added to all elements
* CardinalityDefaultDecorator: By default, optional and list attributes are initialized with an empty default
* NavigableSetterDecorator: By default, the setters of bidirectional associations are also bidirectional
* BuilderDecorator: If elements are marked with `<<builder>>`, a builder is added
* ObserverDecorator: If elements are marked with `<<observer>>`, an observer is added

### Element Configuration

The decoration process can be configured via multiple ways:

* directly in the class diagram using Stereotypes: `<<noGetter>> String x;`
* externally via symbol based tagging
* via CLI options: `-cliconfig` / the `options` Gradle property
* via the defaults provided in the config template / API

A decorator will work on an element if the element is marked for this decorator via the configuration steps outlined above.
If no positive or negative configuration is found, the elements parents are tested until a parent is marked or the root is reached.
Finally, the default, as configured via the config template, is applied.

Modellers should select a suitable config template and use stereotypes for class diagram specific configuration.
Tool-developers should provide their own config template.

### Writing Your Own Decorator

All decorators extend the [AbstractDecorator](../cdlang/src/main/java/de/monticore/cd/codegen/decorators/data/AbstractDecorator.java) class
and implement the `addToTraverser` class.
By additionally implementing various CD visitors and adding itself to the traverser,
the decorator can register itself to be called during the traversal of the original class diagram.
During this decoration, a decorator must not change the original class diagram.
A decorator should check if it should decorate an element via `decoratorData.shouldDecorate(this.getClass(), originalCDElement)`.
This enables the explicit enabling and disabling of specific decorators.

To find the parent of an original CD element, `decoratorData.getParent(originalCDElement)` can be used.
Only the decorated CD shall be modified.
To retrieve the decorated equivalent of an original CD element,
`decoratorData.getAsDecorated(originalCDElement)` should be used.
This method returns the object created by the `ICreator`, such as the default CopyCreator.
The `addElementToParent` method helps in adding a CD element to a decorated package or diagram parent.

The following example of a decorator adds a class `XFancy` for every class `X`.
Via a template replacement, a start method is added.

```java
public class MyFancyDecorator
  extends AbstractDecorator<AbstractDecorator.NoData>
  implements CDBasisVisitor2 {
  @Override
  public void visit(ASTCDClass node) {
    // Only act if we should decorate the class
    if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
      // Get the parent (package or CDDef)
      var origParent = this.decoratorData.getParent(node).get();
      // and the parent, but now the element of the target CD
      var decParent = this.decoratorData.getAsDecorated(origParent);

      // Create a new class with the "Fancy" suffix
      var additionalClassBuilder = CD4CodeMill.cDClassBuilder();
      additionalClassBuilder.setName(node.getName() + "Fancy");
      additionalClassBuilder.setModifier(node.getModifier().deepClone());
      var additionalClass = additionalClassBuilder.build();
      // Add the fancy class to the decorated CD
      addElementToParent(decParent, additionalClass);

      // Add a public start() method to the builder class
      ASTCDMethod myMethod =
        CDMethodFacade.getInstance()
          .createMethod(
            CD4CodeMill.modifierBuilder().PUBLIC().build(), node.getName(), "start");
      glexOpt.ifPresent(
        glex ->
          glex.replaceTemplate(
            EMPTY_BODY,
            myMethod,
            new TemplateHookPoint("methods.fancy.Start", node.getName())));
      addToClass(additionalClass, myMethod);
    }
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    // Our decorator is only interested in CDBasis elements and thus a CDBasisVisitor2
    traverser.add4CDBasis(this);
  }
}
```

Unless an explicit ordering is given, decorators may run in parallel, i.e. during the same AST traversal.
By providing an explicit ordering via the `getMustRunAfter`, the original class diagram may be traversed multiple times:

```java
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
  // We require data of the Setter Decorator, thus it has to run before this decorator
  return Iterables.concat(
    super.getMustRunAfter(), Collections.singletonList(SetterDecorator.class));
}
```

In case the ordering can not be satisfied, the tool fails with an error.
A softer ordering, i.e. in case another decorator is present, it must run before, is planned in the future.
It is possible to specify an interface instead of the concrete decorator class,
such as `super.getMustRunAfter` specifies that an `ICreator` must have run before.

Decorators can exchange data between each other.
In the future, this data should be exchanged via the symbol table,
but in the meanwhile data classes are used.

It is possible to access the data of another decorator using:

```java
decoratorData.getDecoratorData(OtherDecorator .class);
```

For the data to be available, the other decorator should have run beforehand.

As of now, it is not possible to use other decorators in your own decorator,
such as the getter or setter decorator.

By default, the tool creates the symbol table of the original class diagram and the resulting, decorated class diagram.
This requires, that all used types are present in the global scope. It can either be achieved using class2mc or symbol surrogates.

#### Config Template

To configure the decorating generator tool to include your decorator, the config template has to be modified:

```injectedfreemarker
<#--CD2OwnDecorator.ftl-->
${tc.signature("glex", "deConf")}

<#--Apply the default creator: Copy the original CD and use it as the base-->
${deConf.withCopyCreator().defaultApply()}

<#--It is possible to add your own decorators via this config template too: -->
<#-- We just have to add the class to the classpath of the CDTool -->
${deConf.withDecorator("mc.MyOwnDecorator").defaultApply()}
```

```groovy
// build.gradle (excerpt)
tasks.named('generateClassDiagrams') {
  configTemplate = 'CD2OwnDecorator'        // use the CD2OwnDecorator.ftl config template
  tmplDir = file('src/main/configTemplate') // and load it from the src/main/configTemplate directory
  getExtraClasspathElements().from(sourceSets.decorators.output)  // add the compiled decorator to the classpath of the tool 
  // an alternative is to use Gradle configurations and dependencies
}
```

When using the CLI tool, the following arguments can be used: `-ct CD2OwnDecorator -fp src/main/configTemplate`.
When using the API, the *DecoratorConfig* can be modified directly, as seen in the following tests.

#### Testing

All decorators should be tested by at least the following:

* Testing the decoration:
  * A Test class extending [AbstractDecoratorTest](../cdlang/src/test/java/de/monticore/cd/cdgen/AbstractDecoratorTest.java)
  * Each call of `doTest()` with a CD
  * Ensure unique class diagram names to not clash with other tests.
  * For example: [GetterDecoratorTest.java](../cdlang/src/test/java/de/monticore/cd/cdgen/GetterDecoratorTest.java)
* Testing the generated result:
  * The generated code is available and can be tested against.
  * This ensures the absence of compilation errors and enables tests of the functional correctness
  * For example: [GetterDecoratorResultTest.java](../cdlang/src/cdGenIntTest/java/getter/GetterDecoratorResultTest.java)
