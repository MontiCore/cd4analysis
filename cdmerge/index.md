<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

<!-- Relevant Publications -->
[UML/P]: https://mbse.se-rwth.de/
[LRSS23]: https://www.se-rwth.de/publications/CDMerge-Semantically-Sound-Merging-of-Class-Diagrams-for-Software-Component-Integration.pdf

<!-- Other Links -->
[README]: ../README.md/#step-9-merging-two-class-diagrams

# CDMerge: Semantically Sound Merging of Class Diagrams

#### Forward
`CDMerge` is a submodule of the `CD4Analysis` project that focuses on the 
syntactic and semantic composition of [UML/P] Class Diagrams.
It implements a syntactic merge operator of the same name that composes 
semantically compatible Class Diagram such that the resulting merged model is
semantically refining each component model.
In the following, we will give an overview on the concepts behind as well as the
capabilities of `CDMerge`.
A more detailed discussion on the formal aspects of the operator can be found in 
[[LRSS23]].

## The Semantics of Merging

When composing multiple Class Diagrams, it is important to preserve the
semantically-relevant information of each component model.
For this purpose we require that a semantically sound merging operator produces
a composed model that is a semantic refinement of each component model.
We consider the semantics of a Class Diagram to be the set of object structures
it permits/defines, i.e., its valid instances.
Precisely if a Class Diagram's semantics is included in the semantics of another
Class Diagram, the former is said to be a semantic refinement of the latter.
For model composition we consider an open-world assumption of semantics instead
of a closed-world assumption:
Under a closed-world assumption the semantics of a Class Diagram permits only
instances of explicitly modelled elements. On the other hand, an open-world
assumption considers the model to be underspecified and thus also allows
instances of additional elements not specified in the model.

## Implementation

`CDMerge` merges Class Diagrams in an iterative manner, i.e., two models are 
merged and the resulting model is then merged with the next one.
In each of these merge steps, the merging operator first identifies matching
elements in both component models. The matching elements then have to be merged in a manner that preserves their 
semantics. If this is not possible, because their semantics is 
incompatible, the merging process is aborted and the user is informed of the 
corresponding merge conflict. Otherwise, a new Class Diagram is produced from 
the merged elements and the remaining unmatched elements.

### Merge Conflicts

Incompatibilities of model elements that prevent a merge are referred to as
merge conflict. A merge conflict occurs, for example, if both models define the 
same association but with mismatching role-names. E.g., in the following the 
`Students` attending a `Lecture` are referred to as `attendees` by the first
model but are called `participants` by the second:

```
// first component model
classdiagram MyStudents {
  class Student;
  class Lecture;
  
  association attendance [1..*] Student (attendees) <-> (attends) Lecture [*];
  //...
}

// second component model
classdiagram YourStudents {
  class Student;
  class Lecture;
  
  association attendance [1..*] Student (participants) <-> (attends) Lecture [*];
  //...
}
```

Some merge conflicts are identified during the matching process, while others
are found when merging the matched elements or during post-merge validation.
Merge conflicts can also be use-case-dependant. For examples, if code generation
is considered, multiple-inheritance might be permissible or not depending on the
target language.
For this purpose, different configuration options should be considered.

## Using CDMerge

The merge operator can be used by calling the static method 
[`CDMerge.merge()`](src/main/java/de/monticore/cdmerge/CDMerge.java)
with a set of appropriate
[`MergeParameter`](src/main/java/de/monticore/cdmerge/config/MergeParameter.java):

```
    public static ASTCDCompilationUnit merge(
      List<ASTCDCompilationUnit> inputCDs,
      String compositeCDName,
      Set<MergeParameter> mergeParameters) {...}
```

As can be seen in the [README], the operator is also available via the command 
line option `--merge <file>` of the CD Tool.
Additionally, the option `--mrg-config <file>` can be used to specify a 
config-file containing a `JSON`-object with a list of `"Merge Parameters"`:

```
java -jar cdtool/target/libs/MCCD.jar -i doc/Management.cd --merge doc/Teaching.cd --mrg-config doc/mrg-param.json -pp
```

Unknown and unsupported parameters are ignored. If the option `--mrg-config` is omitted, the parameters 
`LOG_TO_CONSOLE` and `FAIL_AMBIGUOUS` are used by default.


### List of Merge Parameters Supported by the CD Tool

* `ASSERT_ASSOCIATIVITY`:
  * The input models will be checked for associativity stability (i.e. input 
    order). This test performs several mergers and slows down the merging 
    process but gives indication on inconsistencies.
* `DISABLE_CONTEXT_CONDITIONS`
  * Disables the check of `cd4analysis` context conditions for the input Class 
    Diagrams and the merged model.
* `DISABLE_POSTMERGE_VALIDATION`
  * Disables the post merging validation of the resulting Class Diagram.
* `DISABLE_MODEL_REFACTORINGS`
  * Disable clean-up and post-merge refactorings of the final model.
* `FAIL_FAST`
  * Abort merging process immediately when the first error is detected.
* `FAIL_AMBIGUOUS`
  * Abort the merging process if matching of associations is ambiguous.
* `LOG_DEBUG`
  * Logs debug information (Level DEBUG, FINE, INFO) during matching and 
    merging.
* `LOG_STDERR`
  * Stay silent on standard output and defer all logging to standard error.
* `LOG_VERBOSE`
  * Logs more information (Level FINE and INFO) and reports each merged element.
* `LOG_TO_CONSOLE`
  * Write all Log entries immediately to standard output.
* `MERGE_COMMENTS`
  * Merge all comments of all model elements from the source diagrams.
* `MERGE_ONLY_NAMED_ASSOCIATIONS`
  * Only merge associations with defined association name. 
    Guarantees associativity of the merge.
* `MERGE_HETEROGENEOUS_TYPES`
  * Allows the merger of classes with interfaces.
* `PRIMITIVE_TYPE_CONVERSION`
  * Enables the merging of compatible primitive type attributes like int and 
    long.
* `STRICT`
  * Combination of the parameters `MERGE_ONLY_NAMED_ASSOCIATIONS` 
    and `WARNING_AS_ERRORS`.
* `WARNINGS_AS_ERRORS`
  * Treat warnings as errors, i.e., cancel merge process on each warning.
