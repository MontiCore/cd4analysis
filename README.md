This documentation is intended for  **modelers** who use the class diagram (CD) languages.
A detailed documentation for **language engineers** using or extending the CD languages is 
located **[here](src/main/grammars/de/monticore/cd4analysis.md)**.
We recommend that **language engineers** read this documentation before reading the detailed
documentation.

# An Example Model

The following example CD `MyLife` illustrates the textual syntax for CDs:
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
The CD is contained in the package `de.monticore.life` and is called `MyLife`.
The example CD shows
- the definition of the two classes `Person` and `Student`.
- the abstract class `Person`.
- the class `Student` extending the class `Person` (like in Java); interfaces are also be possible.
- classes containing attributes, which have a type and a name.
- available default types, which are basic types (from Java), imported types (like `Date`),
  and predefined forms of generic types (like `List`).
- associations and compositions that are defined between two classes and
  can have a name, a navigation information (e.g. `<->`), role names on both
  sides, multiplicities (like `[0..1]`) and certain predefined tags/stereotypes 
  (like `{ordered}`).
- that both, association and compositions, can be qualified for example by `[String]`.
- that packages can be used to structure the classes contained in the model.

Further examples can be found [here][ExampleModels].

# Command Line Interface

This section describes the CLI tool of the CD language. 
The CLI tool provides typical functionality used when
processing models. To this effect, it provides funcionality
for 
* parsing, 
* coco-checking, 
* pretty-printing, 
* creating symbol tables, 
* storing symbols in symbol files, 
* loading symbols from symbol files, and 
* transforming CDs into the svg format and textual PlantUML models. 

The requirements for building and using the SD CLI tool are that Java 8, Git, and Gradle are 
installed and available for use in Bash. 

The following subsection describes how to download the CLI tool.
Then, this document describes how to build the CLI tool from the source files.
Afterwards, this document contains a tutorial for using the CLI tool.  

## Downloading the Latest Version of the CLI Tool
A ready to use version of the CLI tool can be downloaded in the form of an executable JAR file.
You can use [**this download link**](https://nexus.se.rwth-aachen.de/service/rest/v1/search/assets/download?sort=version&repository=monticore-snapshots&maven.groupId=de.monticore.lang&maven.artifactId=cd4analysis&maven.extension=jar&maven.classifier=cli) 
for downloading the CLI tool. 

Alternatively, you can download the CLI tool using `wget`.
The following command downloads the latest version of the CLI tool and saves it under the name `CD4analysisCLI.jar` 
in your working directory:
```
wget "https://nexus.se.rwth-aachen.de/service/rest/v1/search/assets/download?sort=version&repository=monticore-snapshots&maven.groupId=de.monticore.lang&maven.artifactId=cd4analysis&maven.extension=jar&maven.classifier=cli" -O CD4analysisCLI.jar
``` 

## Building the CLI Tool from the Sources
 
It is possible to build an executable JAR of the CLI tool from the source files located in GitHub.
The following describes the process for building the CLI tool from the source files using Bash.
For building an executable Jar of the CLI with Bash from the source files available
in GitHub, execute the following commands.

First, clone the repository:
```
git clone https://github.com/MontiCore/cd4analysis.git
```
Change the directory to the root directory of the cloned sources:
```
cd cd4analysis
```
Afterwards, build the source files with gradle (if `./gradlew.bat` is not recognized as a command in your shell, then use `./gradlew`):
```
./gradlew.bat build
```
Congratulations! You can now find the executable JAR file `SD4DevelopmentCLI.jar` in
 the directory `target/libs` (accessible via `cd target/libs`).





[ExampleModels]: src/test/resources/de/monticore/cd4analysis
