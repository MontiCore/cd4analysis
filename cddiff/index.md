<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Beta-version: This is intended to become a MontiCore stable explanation. -->

<!-- Relevant Publications -->
[UML/P]: https://mbse.se-rwth.de/
[MRR11b]: https://www.se-rwth.de/publications/CDDiff-Semantic-Differencing-for-Class-Diagrams.pdf
[KMRR17]: https://www.se-rwth.de/publications/CD2Alloy-A-Translation-of-Class-Diagrams-to-Alloy.pdf
[NRSS22]: https://www.se-rwth.de/publications/Open-World-Loose-Semantics-of-Class-Diagrams-as-Basis-for-Semantic-Differences.pdf
[LRSS23]: https://www.se-rwth.de/publications/CDMerge-Semantically-Sound-Merging-of-Class-Diagrams-for-Software-Component-Integration.pdf
[RRS23]: https://www.se-rwth.de/publications/On-Implementing-Open-World-Semantic-Differencing-for-Class-Diagrams.pdf
[KMR24]: https://www.se-rwth.de/publications/Towards-Reference-Models-with-Conformance-Relations-for-Structure.pdf
[KRS+24]: https://www.se-rwth.de/publications/Towards-a-Semantically-Useful-Definition-of-Conformance-with-a-Reference-Model.pdf

<!-- Other Links -->
[README]: ../README.md

# CDDiff: Syntactic and Semantic Difference Analyses for Class Diagrams

#### Forward
`CDDiff` is a submodule of the `CD4Analysis` project that focuses on syntactic 
and semantic difference analysis for [UML/P] Class Diagrams. 
The submodule itself is subdivided into the following packages:
1. `de.monticore.cdconcretization`
    * implements a tool for completing concrete Class Diagrams based on a
    reference model (work in progress).
2. `de.monticore.cdconformence` 
    * implements an automatic conformance checker for Reference Class Diagrams.
2. `de.monticore.cddiff`
    * implements syntactic and semantic differencing operators for Class 
      Diagrams.
3. `de.monticore.cdmatcher` 
    * contains strategies for matching Class Diagram elements.
4. `de.monticore.odvalidity` 
    * implements a tool that determines whether an Object Diagram models a 
    valid instance of a Class Diagram.

[[_TOC_]]

## Semantic Differencing for Class Diagrams
We consider the semantics of a Class Diagram to be the set of object structures
it permits/defines, i.e., its valid instances.
The asymmetric semantic difference of two (2) Class Diagram is then given by 
the set of object structures permitted by the first that are not valid 
instances of the second. If this set is empty, we consider the former a 
(semantic) refinement of the latter.

`CDDiff` implements a semantic differencing operator for Class Diagrams
that takes two (2) Class Diagrams as input and determines whether a semantic 
difference exists. It does this by searching for _diff-witnesses_, i.e., object
structures that are in the semantics of the first model but not in the 
semantics of the second. 
These witnesses are then output in the form of [UML/P] Object Diagrams.

There are currently two approaches for computing _diff-witnesses_: 
the first utilizes a translation of the input models to Alloy and then uses the
Alloy Analyzer to find instances of the Alloy model that correspond to 
_diff-witnesses_ [[MRR11b],[KMRR17]], the second approach analyzes the syntactic 
differences on AST-level between the two models and tries to construct 
corresponding witnesses based on that.

The Alloy-based semantic differencing operation can be performed under a 
closed-world assumption or an open-world assumption on the semantics [[RRS23]].
The syntactic-to-semantic differencing operation operates only under a 
closed-world assumption. Here, a close-world assumption means that object 
structures in the semantics may only contain instances of elements explicitly 
modelled in the Class Diagram. 
E.g., an object can only have attributes defined in the corresponding class and
its superclasses as modeled in the Class Diagram.
An open-world assumption on semantics, on the other hand, would consider Class 
Diagrams as underspecified, i.e., an object structure is also in the semantics 
if it corresponds to an expansion of the Class Diagram [[NRSS22], [LRSS23]].

Both operators can be used via `de.monticore.cddiff.CDDiff` or by executing the
CD tool with the command `--semdiff` (refer to the [README] for more information 
on the CLI tool commands):

```
  /**
  * Computes the semantic difference between cd1 and cd2 
  * via translation to Alloy.
  *
  public static List<ASTODArtifact> computeAlloySemDiff(
      ASTCDCompilationUnit cd1,
      ASTCDCompilationUnit cd2,
      int diffsize,
      int difflimit,
      CDSemantics semantics) {...}
      
   /**
   * Computes the semantic difference between cd1 and cd2 
   * via syntax analysis.
   *
   public static List<ASTODArtifact> computeSyntax2SemDiff(
      ASTCDCompilationUnit ast1, 
      ASTCDCompilationUnit ast2, 
      CDSemantics cdSemantics) {...}
```

The parameter `diffsize` determines the search-space for the operation in that 
it defines the maximum number of objects and types that the Alloy Analyzer will
consider. Next, the parameter `difflimit` defines the maximum number of 
witnesses that will be output.

A reduction of open-world semantic differencing to an equivalent instance of 
the close-world problem has been implemented, as well [[RRS23]]. 
This Reduction Transformation can be used as a pre-processing step to modify 
the ASTs of the input models before executing the differencing operator, e.g.:
```
  new ReductionTrafo().transform(cd1, cd2);
  List<ASODArtifacts> diffWitnesses = 
    CDDiff.computeSyntax2SemDiff(cd1, cd2, CDSemantics.STA_CLOSED_WORLD);
```

## Validating Object Diagrams

The `CDDiff` subproject contains tooling for checking whether an object 
structure modeled in an Object Diagram is a valid instance of a CLass Diagram.
This tool can also be used to check if an object structure is a _diff-witness_ 
between two (2) Class Diagrams.
It can operate both under a closed-world and an open-world assumption.

When checking validity of an object structure under an open-world assumption,
we simply ignore semantic constraints that are closed-world specific, i.e.,
the object structure may contain links and objects that are not instances of 
elements in the Class Diagram and objects may contain additional attributes.
This constitutes a more loose interpretation of open-world semantics than the
one used for semantic differencing, as it does not ensure consistency in the 
type-hierarchy instantiated by objects in the object structure.

Furthermore, we consider two variants of objects within the semantic domain: 
1. _simple objects_ which are agnostic to type-hierarchies in Class Diagrams
2. _super-type-aware (STA) objects_ which encode information regarding 
   instantiated super-types

_STA objects_ are annotated with the stereotype `<<instanceof>>`. 
The stereo-value is a string containing the (qualified) names of all transitive
super-types separated by comma, e.g.:

```
  <<instanceof = "Object, Employee, Manager">>
  object bob:Manager {
    //...
  }
```

Object structures with _STA objects_ are used for open-world semantic 
differencing. Moreover, they are, in-fact, necessary for the currently 
implemented  approaches.

The class `de.monticore.odvalidity.OD2CDMatcher` provides the public methods
`checkODValidity` and `checkIfDiffWitness`. 
The latter of which also considers the inheritance hierarchies defined in both 
input CDs to accurately assess whether an object diagram is actually a 
diff-witness between the two according to STA semantics.


## Checking Conformance to a Reference Class Diagram

The concept of a reference model is contingent on its relation to other more 
concrete models. By itself, a reference model is a model within a given modeling 
language that is used to describe domain concepts and domain-specific relations
in an exemplary manner. 
Whether it was originally used as a concrete model or created as a pattern,
a reference model is defined by its contextual purpose. [[KMR24]]

A concrete model which conforms to a reference model is also referred to as a 
concretization of the latter.
Its elements are said to incarnate corresponding elements of the reference model.
A mapping of incarnations to their corresponding reference elements is referred
to as an incarnation mapping.

A notion of conformance to a reference model must be semantically sound,
i.e., the essence or meaning of the reference model must be preserved in its
concretizations. Formally, we require that a concretization semantically 
refines its reference model in the context of incarnation. More specifically,
after translating the incarnations to their corresponding references, the 
semantics of the concrete model must be a subset of the reference model's 
semantics. [[KRS+24]]

In the case of Class Diagrams, we require that a concretization is an expansion
of the reference model when translating the names of types, attributes, 
associations, etc. according to the incarnation mapping.
Each type, member and association in the reference model has to be incarnated 
at least once, unless it is specified as `<<optional>>` via stereotype.
Multiple incarnation mappings can be encoded within the concrete model using
stereotypes. These mappings need not be injective, i.e. a reference element may
have multiple incarnations within the same mapping.
If for a specific element in the reference model no incarnation is defined in
the mapping, but an element of the same kind and equal name exists in the 
concrete model, it is mapped to this element by default.
The tool checks conformance for each mapping that is specified.
This can be done independently, as the mappings are in super-position.
Consider for example the case of concreting the `Adapter`-pattern to a 
`GraphAdapter`:

```
/* Reference Model */

classdiagram Adapter {
  class Client;

  interface Target {
    void operation();
  }

  class Adapter implements Target;

  class Adaptee{
    void myOperation();
  }

  association Client -> (uses) Target;
  association [1] Adapter -> (adapts) Adaptee [1];
}


/* Concrete Model */

classdiagram GraphAdapter {

  /* Client and Target */

  <<m1="Client", m2="Client">> class GraphicalEditor;

  <<m1="Target", m2="Target">> interface GraphicalObject {
    <<m1="operation", m2="operation">> void display();
  }

  association GraphicalEditor -> (uses) GraphicalObject;


  /* Adapter Pattern for Nodes */

  <<m1="Adapter">> class NodeAdapter implements GraphicalObject;

  <<m1="Adaptee">> class Node{
    <<m1="myOperation">> void getLabel();
  }

  association [1] NodeAdapter -> (adapts) Node [1];


  /* Adapter Pattern for Edges */

  <<m2="Adapter">> class EdgeAdapter implements GraphicalObject;

  <<m2="Adaptee">> class Edge{
    <<m2="myOperation">> void getLabel();
  }

  association [1] EdgeAdapter -> (adapts) Edge [1];

}

```

The class `de.monticore.cdconformance.CDConformanceChecker` provides the method 
`checkConformance` which takes as input the concrete and reference model, each 
as an `ASTCDCompilationUnit`, as well as a list of names of incarnation mappings 
encoded via stereotypes in the concrete model. 
The `CDConformanceChecker` is initialized with a set of parameters from 
`de.monticore.cdconformance.CDConfParameter`.

Conformance Checking can also be used via the CD tool using the commands 
`--reference` to specify a reference model and the command `--map` to specify 
the mapping names:

```
java -jar cdtool/target/libs/MCCD.jar -i doc/GraphAdapter.cd --reference doc/Adapter.cd --map m1 m2
```


## Matching Strategies for Syntactical Difference Analysis

In addition to the aforementioned semantic differencing utilizing a translation
to Alloy, we have also implemented an analysis that compares two (2) Class 
Diagrams regarding their syntactic differences.

It takes as input a source-CD, which is considered the current version of the 
data-model, and compares it to a target-CD considered to be a previous version.
The results of this analysis are stored in a data-structure named `CDSyntaxDiff` 
which contains information regarding deleted, added and changed associations 
and types.
The data-structures `CDTypeDiff`, `CDMemberDiff` and `CDAssocDiff` are used to 
store two (2) elements of both input-models that have been matched as well as 
information regarding the specific differences between the matched elements.

The results of this analysis can be printed via the `SyntaxDiffPrinter` 
or via the CDTool using the command `--syntaxdiff`.
They are also used as input for `Syn2SemDiff` a semantic differencing analysis 
that serves as a more performant alternative to the Alloy-based operator.

The matching of elements from one CD to another is realized using the 
`Strategy`-pattern, in which our different matching strategies implement a 
common interface:

```
public interface MatchingStrategy<T> {
  List<T> getMatchedElements(T srcElem);
  boolean isMatched(T srcElem, T tgtElem);
}
```

The matching strategies are used both in the syntactic difference analysis and 
the conformance checker to identify incarnations.
In both cases multiple matching strategies are used in combination.

## Completing Concretizations of Reference Class Diagrams (upcoming)

We are currently developing a tool that would allow for automatic completion of
incomplete Class Diagrams based on a Reference Model.
As of now, the tool is still under development and not ready to use.
