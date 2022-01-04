<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Beta-version: This is intended to become a MontiCore stable explanation. -->

# Class Diagrams (also: UML/P CD)

We provide two versions of UML class diagrams:
- [**CD4Analysis**][CD4AGrammar] is a CD variant for the modelling of data
  structures with classes, attributes, associations, and enumerations. 
  It is well suited for data modelling as e.g. needed in requirements 
  engineering activities.
- [**CD4Code**][CD4CGrammar] is an extension of CD4Analysis including methods
  and constructors.
  It thus allows to attach behavioral aspects and to modell the APIs of classes.

Both languages are composed of several component languages 
that describe parts of the CD languages:
- [**CDBasis**][CDBasisGrammar] is the base grammar for all CD languages. It
  contains the root compilation unit, and modelling constructs for 
  classes, and attributes.
- [**CDInterfaceAndEnum**][CDIAEGrammar] extends CDBasis with 
  modelling constructs for interfaces and enums.
- [**CDAssociation**][CDAssocGrammar] allows to model associations and roles.
- [**CD4CodeBasis**][CD4CBasisGrammar] adds the behavioral modelling constructs
  for methods and their parameters.

# CD4Analysis

The main purpose of this language is the modeling of data structure, which 
typically emerges as result of requirements elicitation activities.

The main grammar file is [`CD4Analysis`][CD4AGrammar].

## Example for CD4Analysis
```
package de.monticore.life;

classdiagram MyLife { 
  abstract class Person {
    int age;
    Date birthday;
    List<String> nickNames;
  }
  package uni {
    class Student extends Person {
      StudentStatus status;
      -> Address [1..*] {ordered};
    }
    enum StudentStatus { ENROLLED, FINISHED; }
    composition Student -> Grades [*];
    association phonebook uni.Student [String] -> PhoneNumber;
  }
  association [0..1] Person (parent) <-> (child) de.monticore.life.Person [*];
}
```

The example shows a section of the [CD4ALanguageTeaser][LanguageTeaser]:
- It defines two classes `Person` and `Student`.
- `Person` is an abstract class.
- `Student` extends `Person` (like in Java); interfaces would also be 
  possible.
- Classes contain attributes, which have a type and a name.
- Available types are basic types (like in Java), imported types (like `Date`),
  and predefined forms of generic types (like `List<.>`).
- Associations and compositions are defined between two classes,
  can have a name, a navigation information (e.g. `<->`), role names on both
  sides, multiplicities (like `[0..1]`) and certain predefined 
  tags/stereotypes (like `{ordered}`).
- Both, association and compositions can be qualified, for example by 
  `[String]`.
- Packages (like in Java) can be used to organize the model.

Further examples can be found [here][ExampleModels].

## Available handwritten Extensions

### AST 
- [`ASTCDDefinition`][ASTCDDefinition]
  adds methods for easy access to and modification of various 
  elements within a CD. It e.g. allows to access available associations, 
  classes or enums and also add such model elements.
- [`ASTCDAssociation`][ASTCDAssociation]
  among retrieval methods for role names, method `getPrintableName` to retreive 
  the name of the association, etc.

## Symboltable for CD4Analysis
- [`CD4AnalysisSymbolTableCompleter`][CD4ASTCompleter] links the symbols in 
  the symbol table.
- The type (e.g. the type of an attribute) is stored in a 
  [`SymTypeExpression`][SymTypeExpression].
- De-/Serialization functionality for the symbol table uses the
  [`CD4AnalysisDeSer`][CD4ASD] and for specific logic for serialization 
  in [`CD4AnalysisSymbol2Json`][CD4ASTP]
- CD4A contains TypesCalculator ([`DeriveSymTypeOfCD4Analysis`][CD4ATC]) for
  all its subgrammars.

- [`SymAssociation`][SymAssociation] is a class which is included in the
  symbol table and contains all information of a `CDAssociation`. It links to
  a `CDAssociationSymbol` (which only exists, when the association has a 
  name), and to the two sides of the association which are stored in
  [`CDRoleSymbol`][CDRoleSymbol].

## Symbol kinds used by the CD4A language (importable or subclassed):
- CD4A uses the symbol kinds from grammar [`OOSymbols`][OOSymbols] as the 
  basis for the definition of its type-defining symbols.
  - `OOTypeSymbol`s are used for all type-defining Symbols. These are 
    sub-nonterminals of `CDType`, namely `CDClass`, `CDInterface`, and 
    `CDEnum`.
  - `FieldSymbol`s are used for `CDAttribute`, `CDEnumConstant`, and
    `CDRole`, additionally, grammar `CD4Code` uses `FieldSymbol`
    for`CDParameter`
  - `MethodSymbol`s are not used in CD4A, because it doesn't include methods.
     But they are defined in CD4Code: `CDMethodSignature`
- All these symbols are not used directly, but extended in symbol-subclasses,
  which also means that symbol-import only works for symbol kinds introduced
  below.   


## Symbol kinds defined by the CD4A language (exported):

- CD4A defines three kinds of symbols: `CDTypeSymbol`, `CDAssociationSymbol`,
  and `CDRoleSymbol`
- All types either implement `CDTypeSymbol` or one of the `TypeSymbol`s and 
  have no additional functionality or attributes.
  The exception is `CDRoleSymbol`, which is similar to `FieldSymbol` but has
  a different behavior.

### `CDTypeSymbol`

- `CDTypeSymbol` exactly reflects the symbols that are provided by
  `OOSymbols` and does not need additional attributes. 

### Attributes 
- are stored as `FieldSymbol`s. 

### `CDAssociationSymbol`

- `CDAssociationSymbol` reflects the externally accessible part of an 
  association.
- An association may introduce several symbols, namely the association symbol
  itself and up to two role symbols (`CDRoleSymbol`). Furthermore, the
  association symbol may be missing if the association has no name, but role
  symbols can be given.
- Class `SymAssociation` stores the information about an association:
```java
  public class SymAssociation {
    protected Optional<CDAssociationSymbol> association;
    protected CDRoleSymbol left, right;
    protected boolean isAssociation, isComposition;
  }
```

### `CDRoleSymbol`

- `CDRoleSymbol` is defined in an association and connected with the class it 
  belongs to. In the concrete model, roles can be omitted, but are then
  calculated by suitable defaults.
- `CDRoleSymbol` has the following attributes
```
  symbolrule CDRole =
    isDefinitiveNavigable: boolean
    cardinality: Optional<de.monticore.cdassociation._ast.ASTCDCardinality>
    field: Optional<de.monticore.symbols.oosymbols._symboltable.FieldSymbol>
    attributeQualifier: Optional<de.monticore.symbols.basicsymbols._symboltable.VariableSymbol>
    typeQualifier: Optional<de.monticore.types.check.SymTypeExpression>
    assoc: Optional<de.monticore.cdassociation._symboltable.SymAssociation>
    isOrdered: boolean
    isLeft: boolean
    type: de.monticore.types.check.SymTypeExpression
    isReadOnly: boolean
    isPrivate: boolean
    isProtected: boolean
    isPublic: boolean
    isStatic:boolean
    isFinal: boolean
```
- `attributeQualifier` is defined exactly, if a qualifier is given using 
  an attribute of the opposite class (i.e. the opposite class knows
  its qualifier, like in a public phone book)
- `typeQualifier` is defined, if the qualifier is independent of the
  qualified object (i.e. like in the private phone book of a smart phone)  
- the `CDRoleSymbol` is separated from the `FieldSymbol` because the type in a
  role is always the concrete type of the other side of the association, the
  type of a field can be a container, e.g. `List<Person>`


## Symbols imported by CD4A models:
- currently CD4A imports only class, interface and enum symbols from other 
  class diagrams.
- Extensions to include e.g. implemented Java-classes or other type-definining
  languages are planned.
- Other kinds and forms of symbols need to be mapped to CD-like symbols to be
  usable. 


## Symbols exported by CD4A models:
- From the symbol used by CD4A, the following symbols are
  also exported:
  - `CDType`, the interface for all type definitions in the CD languages
  - `Field`, for attributes
  - `CDAssociation`, containing information about association and composition
  - `CDRole`, containing one end of an association
  - `CDMethodSignature`, containing the MethodSymbol attributes and a list of
    exceptions
- CD4A has additional informations (objects) in the SymbolTable:
  - `SymAssociation`, containing all general information of an association,
    because, when the association has no name, then there is no 
    `CDAssociationSymbol`

### `CDTypeSymbol`

- An example for a stored `CDTypeSymbol` (json format):
```json
  {
    "kind": "de.monticore.cdbasis._symboltable.CDTypeSymbol",
    "name": "de.monticore.life.Person",
    "isClass": true,
    "symbols": [ 
       // ... contained attributes and roles 
    ]
  }
```

### Attributes 
- are stored as `FieldSymbol`s. An example:
```json
  {
    "kind": "de.monticore.types.typesymbols._symboltable.FieldSymbol",
    "name": "de.monticore.life.Person.name",
    "type": {
        "kind": "de.monticore.types.check.SymTypeOfObject",
        "objName": "String"
    }
  }
```

### `CDAssociationSymbol`

- Additional class `SymAssociation` stores the information about an 
  association.
  It is stored as follows, with a name ID that allows to be referred to by 
  the associated symbols (json format):
```json
  {
    "kind": "de.monticore.cdassociation._symboltable.SymAssociation",
    "name": 556488341,
    "isAssociation": true
  }
```

- An example for a stored `CDAssociationSymbol` (json format):
```json
{
  "kind": "de.monticore.cdassociation._symboltable.CDAssociationSymbol",
  "name": "uni.phonebook",
  "association": 1237825806
} 
```

### `CDRoleSymbol`

- An example for a stored `CDRoleSymbol` (json format):
```json
  {
    "kind": "de.monticore.cdassociation._symboltable.CDRoleSymbol",
    "name": "de.monticore.life.Person.child",
    "isDefinitiveNavigable": true,
    "cardinality": "[*]",
    "association": 556488341,
    "type": {
      "kind": "de.monticore.types.check.SymTypeOfObject",
      "objName": "de.monticore.life.Person"
    }
  }
```

## Packages
- Packages are used to structure the model file
- They are either 
  1. defined explicitly (`package foo {...}`)
  2. implicitly via the package name of the model
    ```cd
    package de.example;
  
    classdiagram Example {
      class A;
    }
    ```
  3. via a default (`de.monticore`) if none of the above options are the case

## Functionality: CoCos
- [`CD4ACoCosDelegator`][CD4ACoCos] combines all CoCos for all its sublanguages
- the individual CoCos can be found in the [`cocos`][cocos]-directory of each
  language
- the CoCos are separated in these categories:
  - `ebnf` handles cases which should not be handled by the grammar itself,
    e.g., the name of an attribute is lowercase
  - `mcg` contains CoCos that check semantics of the models, e.g., the list
    of valid `UMLModifier`s on an object
- CoCos ensure semantic correctness, here is a list of some important ones:
  - Uniqueness of names of e.g. classes in each package, attributes in each
    class
  - Classes hierarchy is free of cycles
  - Correct counter part on `extends` and `implements` keywords
  - Correct association qualifiers
    
### Shadowing of attributes
CD4Analysis is meant to behave like Java. Java allows attribute
shadowing in any shape of form, like the same or a completely different
type. Supporting this is useful when analysing the class diagrams, as a class
diagram should not automatically be invalid if an inherited class changes.
In code generation (using CD4Code) on the other hand, access methods are
generated for attributes of classes and therefore are problematic with the
method parameters and return types (see 
[covariance and contravariance](https://tinyurl.com/2n4rw87j))
.

## Transformations
- different transformations are provided to simplify the parsing and ST 
  creation
1. the `AfterParseTrafo`s can be executed directly after the parsing of 
   models, currently there are 2 important transformations:
  - [`CDBasisDefaultPackageTrafo`][CDBasisDefaultPackageTrafo]
    - moves elements in a default package, if they are not already in a 
      package
    - the default package is either the package of the model file, or if that
      doesn't exist, `de.monticore` is used
  - ['CD4AnalysisDirectCompositionTrafo`][CD4AnalysisDirectCompositionTrafo]
    - `ASTCDDirectComposition` (short form of composition), are transformed 
      to (normal) `composition`s
2. `Trafo4Defaults` provide additional transformations, that are optional
   defaults, which can be used after a SymbolTable is created:
  - [`CDAssociationTrafo4Default`][CDAssociationTrafo4Default] adds Roles and
    `CDRoleSymbol`s to `AssociationSide`, if there is not already a `CDRole`. 
    That's the case when the side has no name
  - the name of the new role is either the name of the association, or the 
    (lowercase) name of the type
3. Create `FieldSymbol`s from `CDRoleSymbols`s
  - [`CDAssociationCreateFieldsFromAllRoles`][CDAssociationCreateFieldsFromAllRoles]
    creates a `FieldSymbol` for each of the existing `CDRoleSymbol`s
  - [`CDAssociationCreateFieldsFromNavigableRoles`][CDAssociationCreateFieldsFromNavigableRoles]
    creates `FieldSymbol`s only for navigable `CDRoleSymbol`s

### Types
- Currently: The BuiltinTypes can be found here [`BuiltInTypes`][BuiltInTypes].
- CD4A imports their types from foreign artifacts, respectively their
  provided symbol tables.
- CD4A provides a set of predefined types to be given 
  through grammar inclusion of [`MCCollectionTypes`][MCCollectionTypes]:
  - Primitives:  `char`, `int`, `double`, `float`, `long`, `boolean`, `short`, 
    `byte`, `void`
  - ObjectTypes: `Character`, `Integer`, `Double`, `Float`, `Long`, `Boolean`,
    `Short`, `Byte`, `Void`, `Number`, `String`
  - UtilTypes: `List<T>`, `Optional<T>`, `Set<T>`, `Map<T1,T2>`, `Date`
  - Special types, such as `Queue` must then be provided through the imports of 
    other artifacts.
- The BuiltInTypes are not added automatically. They have to be added to the
  `GlobalScope`, by calling `addBuiltInTypes`. This enables more detailed
  control concerning the types that should be available.

### PrettyPrinter
- [`CD4AnalysisFullPrettyPrinter`][PrettyPrinter] contains a basic pretty printer
  for CD4A
- Externally available [PlantUML](https://plantuml.com/en/class-diagram)
  printer can be used to upload the model to plant uml and receive a graphical
  representation of the class diagram.

### Helper
There exist helper classes and methods which provide easier usage of often 
used functionality, e.g.:
- Different helpers for the symbol tables in [`cd/_symboltable`][STHelper]

### Reporting
- [`reporting`][reporting] provides an infrastructure for a complete reporting 
  functionality. This is used by MontiCore to provide additional information 
  concerning changes that have been applied to an AST. 
  
### Tool stand alone application:
- [`CDTool`][CDTool] contains a standalone, but extensible cli application which:
  1. Parses the given model
  2. Creates a symbol table
  3. Checks the CoCos
- the tool is designed with [`the best practices`][ToolBestPractices] in mind
- example usage of the tool
```shell
cd4analysis-tool -i SocNet.cd -r ./reports
cd4analysis-tool -pp Complete.puml -puml -svg --showAttr
cd4analysis-tool -script adapted.groovy
```
- option '-h' or '--help' can be used to get information of allowed parameters

### Gradle Plugin
- the gradle plugin provides the functionality of the tool for gradle builds
- the plugin can be used to check given class diagrams or generate symbol tables
- example usage of the gradle plugin
```gradle
task checkCD(type: CDTask) {
  inputFile = file "src/main/resources/de/monticore/cd/Test.cd"
  outputDir = file buildDir
  modelPaths = files(buildDir, "src/main/resources")
  reportDir = file "$buildDir/reports"
  scriptFile = file "src/main/resources/adapted.groovy"
}
```

# CD4Code
CD4Code is a conservative extension of CD4Analysis and adds methods,
 constructors. Its main purpose is the usage in code generation.
* Conservative means: 
  * all models of CD4A also parse as CD4Code models
  * all functionalities developed for CD4A (and obeying the guidelines for
    extensibility) also apply for CD4Code models (if needed in extended form)

The grammar file is [`de.monticore.CD4Code`][CD4CGrammar].
 
## Example (in addition to CD4A)
```
classdiagram MyLife2 { 
  abstract class Person {
    protected String name;
    public Person(String name, int id);
    Set<Person> getParents();
  }
  class Student extends Person {
    abstract public change(StudentStatus status);
    void addFriends(Person... friends) throws Exception;
  }
  enum StudentStatus {
    ENROLLED(1),
    FINISHED(2);
  }
}
```

The example shows a section of the 
  [CD4CodeLanguageTeaser.cd][CD4CLanguageTeaser]:
- the basic structure is the same as `CD4Analysis`
- class `Person` also contains methods and a constructor 
- methods and constructor can contain any number of arguments, separated by
  `,`
- there is no method body provided.
- enum constants can contain attributes (which call the constructor of the
  enum)

Further examples can be found [here][CD4CExampleModels].

## Handwritten Extensions
- CD4Code contains some handwritten extension (similar to CD4Analysis) like [`CD4CodeFullPrettyPrinter`][CD4CodePrinter].

## Usage
- CD4Code  can play its role as intermediate language (especially it's AST)
  capturing the structural part of the classes to be generated.
  It captures classes, and method signatures and allows to add
  templates as hook points that contain method bodies. Examples are:  
  * [MontiCoreTool](https://github.com/MontiCore/monticore/blob/dev/monticore-generator/src/main/java/de/monticore/codegen/cd2java/_symboltable/SymbolTableCDDecorator.java): 
    Grammar -> 
    [Grammar AST encoded in CD4Code](https://github.com/MontiCore/monticore/blob/dev/monticore-generator/src/main/java/de/monticore/MontiCoreScript.java#L411) ->
    [Decoration for custom behavior](https://github.com/MontiCore/monticore/blob/dev/monticore-generator/src/main/java/de/monticore/codegen/cd2java/_symboltable/SymbolTableCDDecorator.java) -> 
    [Java code](https://github.com/MontiCore/monticore/blob/dev/monticore-generator/src/main/java/de/monticore/codegen/cd2java/_symboltable/SymbolTableCDDecorator.java)
  * Statechart -> State pattern encoded in CD4Code 
  -> Decoration by monitoring methods -> Java code.
- This is on contrast to CD4A which allows us to capture data structures for 
  example from the requirements elicitation activities.
  
## Generator Extensions - CD4C Infrastructure to define method signatures in templates
The class `CD4C` extends the generation possibilities provided in 
monticore-runtime by GLEX.
It is possible to describe both the method signature and the method body in a 
template.
For this `CD4C` must be initialized once.
The method `addMethod` in `CD4C` needs the class and the name of the template as
parameters.
The given template describes the signature in the first line with the call to 
`cd4c`.
Subsequently, follows the code for the generated method body.
The class `CD4C` is located [here][CD4C].

### Example 
```
  // Configure glex
  glex = new GlobalExtensionManagement();
  config = new GeneratorSetup();
  config.setGlex(glex);

  // Configure CD4C
  CD4C.init(config);
  
  // add the method that is described in template "PrintMethod"
  CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");
  
  // generate Java-Code
  GeneratorEngine generatorEngine = new GeneratorEngine(config);
  final Path output = Paths.get("HelloWorld.java");
  generatorEngine.generate("de.monticore.cd.methodtemplates.core.Class", output, clazz, printer);
```
The corresponding template looks like this:
```
${tc.signature()}
${cd4c.method("public java.lang.String print()")}
{
  System.out.println("Hello world");
}

```
The corresponding test can be found [here][CD4CTest].

### Usage

Starting from a class, the CD4C infrastructure provides methods to call a
template, that includes a signature and the method body (as usual).
For this, the Global Extension Manager (GLEX) is extended with the `cd4c`
tooling components.

#### Call the CD4C
```java
    ASTCDClass clazz = ...
    Optional<ASTCDMethodSignature> methodSignature = CD4C.getInstance().createMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");
```

The cd4c infrastructure will create the method (signature), but not
automatically add it to the provided class. This allows the use of information
in the class e.g., attributes or associations to create the methods' signature
and/or the method body.

A template can use the cd4c infrastructure to define a method signature as
following:
```freemarker
${tc.signature()}
${cd4c.method("public java.lang.String print()")} <#-- use cd4c to create the signature -->
{
  System.out.println("Hello world");
}
```

Example of a signature with a variable parameter list, extracted from the class.
Here all public attributes are used to create the parameter list of the
constructor.
```freemarker
${tc.signature()}
<#assign parameter = ast.getCDAttributeList()?filter(a -> a.getModifier().isPublic())>
${cd4c.constructor("public HelloWorldWithConstructor(" + parameter?map(a -> a.printType() + " " + a.getName())?join(", ") + ")")}
{
  <#list parameter as param>
    this.${param.getName()} = ${param.getName()};
  </#list>
}
```

The CD4C infrastructure contains multiple ways to create methods.
The main entry point is the CD4C class which has to be initialized before being
used for the first time:
```java
GeneratorSetup setup = ...
CD4C.init(config);
```
This sets the environment that should be used for generating the code.
After initialization, the methods can be created.

Create a method/constructor defined in a template resulting in a
`ASTCDMethodSignature`:
```java
ASTCDClass clazz = ...
// method
Optional<ASTCDMethodSignature> methodSignature = 
    CD4C.getInstance().createMethod(clazz, "de.monticore.ToString");
// constructor
Optional<ASTCDMethodSignature> constructorSignature = 
    CD4C.getInstance().createConstructor(clazz, "de.monticore.ConstructorWithAllAttributes");
```

Those can then be used and added to the class or used elsewhere.
Another option is to add the method directly to the provided class.
This can be done using the `addX`-methods:
```java
ASTCDClass clazz = ...
// method
CD4C.getInstance().addMethod(clazz, "de.monticore.ToString");
// constructor
CD4C.getInstance().addConstructor(clazz, "de.monticore.ConstructorWithAllAttributes");
```

#### Add predicate/checks that handle the methods

After handling the template, the resulting method is then checked with
1. method predicate (`CD4C.getInstance()..addPredicate` or
   `CD4C.getInstance()..addCoCo`): check if the method itself is valid (e.g.
   unique parameter names)
2. class predicate (`CD4C.getInstance()..addClassPredicate`): if a class would
   be valid if the method was added (e.g. the new method does not have the exact
   same signature as an existing method)

CD4C comes with predefined predicates that can be added to the current instance
with `CD4C.getInstance().addDefaultPredicates` and
`CD4C.getInstance().addDefaultClassPredicate`.

### Functionality

The following describes the functionality of CD4C and how the different elements
interact.

#### Call to a CD4C template

A CD4C template is a template that uses the CD4C infrastructure to create
methods.
The call to the template is only working when called from CD4C with
`CD4C.getInstance()...` otherwise the method signature cannot be created.
Additionally, the use of the template fails (throws an exception during run-time)
, if there is no method signature defined in the template.

When the template is valid (see above for examples) then a method signature is
created and added to `CD4C.methodQueue`.
This allows to call a CD4C template in another (CD4C) template.

#### The `CD4CTemplateMethodHelper` class

For each method that is created, a `CD4CTemplateMethodHelper` object is created
that stores the `ASTCDMethodSignature` created from the method signature.

#### Method body

The method body is calculated when the template is executed/called as it is
already processes by the freemarker engine and returned as a string.
This leads to the fact, that the freemarker logic in the template is executed
exactly once during the transformation of the AST and not when the generator
is triggered to create the finished result (usually `generate.generate()`).
This can be a problem, when the method (body) is added before necessary
attributes or other settings are set.

#### Connect method signature and body

The connection of the method body and the method AST node (needed for the
generator) is done by `CD4C.createMethodSignatureAndBody` and
`CD4C.addMethodBody` using GLEX.

#### different from "normal" template call

The difference to other templates is that the CD4C templates (using the CD4C
infrastructure) have to be executed from CD4C, otherwise the signature
definitions can not be handled.
The method body definition is exactly the same.

## Additional info
### Error codes
`X` stands for any valid character

#### Languages
- `0xCDX0X`-`0xCDX2X`: CDBasis
- `0xCDX3X`-`0xCDX5X`: CDInterfaceAndEnum
- `0xCDX6X`-`0xCDX8X`: CDAssociation
- `0xCDX9X`-`0xCDXBX`: CD4CodeBasis
- `0xCDXCX`-`0xCDXDX`: CD4A
- `0xCDXEX`-`0xCDXFX`: CD4Code

#### CoCos (`0xCDCXX`)
- `0xCDC0X`: CDBasisEbnf (`0xCDC3X` for CDInterfaceAndEnumEbnf...)
- `0xCDC1X`: CDBasisMCG
- `0xCDC2X`: CDBasisMCG2Ebnf
  
#### Symboltable (`0xCDAXX`)
#### Other Errors (`0xCD0XX`)

[CD4AGrammar]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/grammars/de/monticore/CD4Analysis.mc4
[CD4CGrammar]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/grammars/de/monticore/CD4Code.mc4
[CDBasisGrammar]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/grammars/de/monticore/CDBasis.mc4
[CDIAEGrammar]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/grammars/de/monticore/CDInterfaceAndEnum.mc4
[CDAssocGrammar]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/grammars/de/monticore/CDAssociation.mc4
[CD4CBasisGrammar]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/grammars/de/monticore/CD4CodeBasis.mc4

[ASTCDDefinition]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cdbasis/_ast/ASTCDDefinition.java
[CD4AAfterParseTrafo]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd4analysis/trafo/CD4AnalysisAfterParseTrafo.java
[CDBasisDefaultPackageTrafo]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cdbasis/trafo/CDBasisDefaultPackageTrafo.java
[CDAssociationDirectCompositionTrafo]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cdassociation/trafo/CDAssociationDirectCompositionTrafo.java
[CDAssociationCreateFieldsFromAllRoles]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cdassociation/trafo/CDAssociationCreateFieldsFromAllRoles.java
[CDAssociationCreateFieldsFromNavigableRoles]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cdassociation/trafo/CDAssociationCreateFieldsFromNavigableRoles.java
[CD4ASTCompleter]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd4analysis/_symboltable/CD4AnalysisSymbolTableCompleter.java
[CD4ASD]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd4analysis/_symboltable/CD4AnalysisDeSer.java
[CD4ASTP]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd4analysis/_symboltable/CD4AnalysisSymbols2Json.java
[CD4ATC]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd4analysis/typescalculator/DeriveSymTypeOfCD4Analysis.java
[SymTypeExpression]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/java/de/monticore/types/check/SymTypeExpression.java
[SymAssociation]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cdassociation/_symboltable/SymAssociation.java
[CDRoleSymbol]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cdassociation/_symboltable/CDRoleSymbol.java
[STHelper]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd/_symboltable/CDSymbolTableHelper.java
[reporting]: https://github.com/MontiCore/cd4analysis/tree/master/src/main/java/de/monticore/cd4analysis/reporting
[CDTool]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd4code/CD4CodeTool.java
[LanguageTeaser]: https://github.com/MontiCore/cd4analysis/blob/master/src/test/resources/de/monticore/cd4analysis/parser/MyLife.cd
[ExampleModels]: https://github.com/MontiCore/cd4analysis/tree/master/src/test/resources/de/monticore/cd4analysis/
[ASTCDAssociation]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cdassociation/_ast/ASTCDAssociation.java
[PrettyPrinter]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd4analysis/prettyprint/CD4AnalysisFullPrettyPrinter.java
[ASTCDType]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cdbasis/_ast/ASTCDType.java
[BuiltInTypes]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd/_symboltable/BuiltInTypes.java

[CD4ACoCos]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd4analysis/cocos/CD4AnalysisCoCosDelegator.java
[CD4AParser]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd4analysis/_parser/CD4AnalysisParser.java
[CD4CLanguageTeaser]: https://github.com/MontiCore/cd4analysis/blob/master/src/test/resources/de/monticore/cd4code/parser/MyLife2.cd
[CD4CExampleModels]: https://github.com/MontiCore/cd4analysis/blob/master/src/test/resources/de/monticore/cd4code/
[CD4CodePrinter]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd4code/prettyprint/CD4CodeFullPrettyPrinter.java
[CD4C]: https://github.com/MontiCore/cd4analysis/blob/master/src/main/java/de/monticore/cd/methodtemplates/CD4C.java
[CD4CTest]: https://github.com/MontiCore/cd4analysis/blob/master/src/test/java/de/monticore/cd/methodtemplates/CD4CTest.java

[OOSymbols]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/symbols/OOSymbols.mc4
[MCCollectionTypes]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCCollectionTypes.mc4

[ToolBestPractices]: https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

