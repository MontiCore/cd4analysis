<!-- (c) https://github.com/MontiCore/monticore -->

# Class Diagram Languages: CD4A, CD4C

[[_TOC_]]

This introduction is 
intended for  *modelers* who use the class diagram (CD)
languages. We also provide a 
[detailed documentation of CD languages](src/main/grammars/de/monticore/cd4analysis.md). 
for *language engineers* using or
extending one of the CD languages.

The CD languages are mainly intended for  
1. Analysis modelling (i.e. structures of the system context 
   as well as data structures of the system),
1. Code modelling (implementation) oriented, including method signatures,
1. Generating code, data tables, transport functions and more.
1. It is also possible to use CDs only as intermediate structure
   to map from one or more other DSLs into an object-oriented 
   target language, such as Java or C++. 
   (E.g. the MontiCore generator maps grammars to CDs before generating code
   from there).
1. Finally CDs can also be used as reported results from any other 
   generation or analysis process.

## An Example Model

The following example CD `MyLife` illustrates the textual syntax of CDs:
```
package monticore;
import AddressType; 

classdiagram MyLife { 
  abstract class Person {
    int age;
    java.util.Date birthday;
    java.util.List<java.lang.String> nickNames;
  }
  class PhoneNumber;
  package uni {
    class Student extends Person {
      StudentStatus status;
      -> Address [1..*] {ordered};
    }
    class Grade;
    enum StudentStatus { ENROLLED, FINISHED; }
    composition Student -> Grade [*];
    association phonebook uni.Student [java.lang.String] -> PhoneNumber;
  }
  association [0..1] Person (parent) <-> (child) monticore.Person [*];
}
```
The CD is contained in the package `monticore` and is called `MyLife`.
The example CD shows
- the definition of the two classes `Person` and `Student`.
- the abstract class `Person`.
- the class `Student` extending the class `Person` (like in Java); interfaces
  are also be possible.
- classes containing attributes, which have a type and a name.
- available default types, which are basic types (from Java), imported types 
  (like `java.util.Date`), and predefined forms of generic types (like `java.util.List`).
- associations and compositions that are defined between two classes and
  can have a name, a navigation information (e.g. `<->`), role names on both
  sides, multiplicities (like `[0..1]`) and certain predefined tags/stereotypes 
  (like `{ordered}`).
- that both, association and compositions, can be qualified for example by 
  `[java.lang.String]`.
- that packages can be used to structure the classes contained in the model.

Further examples can be found [here][ExampleModels].

The CD language infrastructure can be used as CLI tool from shell as well 
as within gradle or just as framework with dirct Java API access.

## Command Line Interface
 
The CLI tool provides typical functionality used when
processing models. It provides funcionality
for 
* parsing including coco-checking and creating symbol tables, 
* pretty-printing, 
* storing symbols in symbol files, 
* loading symbols from symbol files, and 
* transforming CDs into a graphical svg format. 

The requirements for building and using the CD CLI tool are that Java 8, Git, 
and Gradle are installed and available for use e.g. in Bash. 

### Downloading the Latest Version of the CLI Tool

A ready to use version of the CLI tool can be downloaded in the form of an
executable JAR file.
You can use [**this download link**][CLIDownload] for downloading the CLI tool. 
Or you can use `wget` to download the latest version in your working directory:
```shell
wget "http://monticore.de/download/CDCLI.jar" -O CDCLI.jar
``` 

### Parameters of the CLI

The CLI provides quite a number of configurable parameters. 
These two are examples for calling  the the CLI:

```shell
java -jar CDCLI.jar -i Person.cd -p target:src/models -o target/out -t true -s
java -jar CDCLI.jar -i Person.cd -pp Person.out.cd
```

The possible options are:
| Option                     | Explanation |
| ------                     | ------ |
| `--fieldfromrole <value>` | Configures if explicit field symbols, which are typically used for implementing associations, should be added, if derivable from from role symbols  (default: no). Values: `none` is typical for modelling, `all` adds always on both classes, `navigable` adds only if the association is navigable.
| `-f,--failquick <bool>`    | Configures if the application should quickfail on errors. `-f` equals to `--failquick true`. Default is `false`. If `true` the check stops at the first error, otherwise tries to find all errors before it stops. |
| `-h,--help`                | Prints short help information   |
| `-i,--input <file>`        | Reads the source file (mandatory) and parses the contents as CD |
| `-o,--output <dir>`        | Path for generated files (optional). Default is `.`. |
| `--path <dirlist>`         | Artifact path for importable symbols. It is separated by ';', default is `.`. |
| `-pp,--prettyprint <file>` | Prints the CD to stdout or the specified file (optional). |
| `-r,--report <dir>`        | Prints reports of the CD to the specified directory (optional).  |
| `-s,--symboltable <file>`  | Stores the symbol table of the CD. The default value is `{CDName}.cdsym`. |
| `-stdin,--stdin`             | Reads the the input CD from stdin instead of argument `-i`. |
| `-t, --usebuiltintypes <bool>`  |  Configures if built-in-types should be considered. Default: `true`. `-f` toggles it to `--usebuiltintypes false` |

### Building the CLI Tool from the Sources (if desired)
 
As alternative to a download, 
it is possible to build an executable JAR of the CLI tool from the source files
located in GitHub. The following describes the process for building the CLI tool
from the source files using Bash. For building an executable Jar of the CLI with
Bash from the source files available in GitHub, execute the following commands.

First, clone the repository:
```shell
git clone https://github.com/MontiCore/cd4analysis.git
```
Change the directory to the root directory of the cloned sources:
```shell
cd cd4analysis
```
Then build the source files with gradle (if `gradle` is not 
recognized as a command in your shell, please install [Gradle](https://gradle.org/releases/)).
To this effect, execute the following two commands:
```shell
gradle build
gradle shadowJar
```
Congratulations! The  executable JAR file `CDCLI.jar` is now in
the directory `target/libs`.

## Tutorial: Getting Started Using the CD CLI Tool

The following small tutorial should help to get an idea 
of how to use the CD CLI tool given in `CDCLI.jar`.

### First Steps

This prints usage information of the CLI, if executing the CLI tool with the following 
command and no parameters:
```shell
java -jar CDCLI.jar
```

To work properly, the CLI tool needs the mandatory argument `-i,--input <file>`,
which takes file containing CD models as input.
If no further options are specified, the CLI tool processes the model,
but does not produce any further output.
That means it parses the model, builds its 
symbol table, and then checks whether the model satisfies all context 
conditions. Only errors or success are printed.

For trying this out, copy the `CDCLI.jar` into a directory of your 
choice. Then create a text file `Example.cd` in the directory where `CDCLI.jar` is 
located containing e.g. the following simple CD 
(please note that, like in Java, filename and modelname in the file are
 equal):

```
classdiagram Example {
  class Person {
    int age;
    java.lang.String surname;
  }

  association Person -> (friends) Person [*];
}
```

Now execute the following command:
```
java -jar CDCLI.jar -i Example.cd
```

You may notice that the CLI tool prints the following text to the console:
```
Successfully parsed Example
Successfully checked the CoCos for Example
```

The contents of the input CD artifact can also be piped to the CLI tool.
For trying this out, execute the following command:

```shell
cat Example.cd | java -jar CDCLI.jar --stdin
``` 
The output is the same as for the previous command.

### Step 2: Pretty-Printing

The CLI tool provides a pretty-printer for the CD language.
A pretty-printer can be used, e.g., to fix the formatting of files containing 
CDs, but has its main application to print internally constructed 
or transformed CDs.

To execute the pretty-printer, the `-pp,--prettyprint` option can be used.
Using the option without any arguments pretty-prints the models contained in the
input files to the console:

```shell
java -jar CDCLI.jar -i Example.cd -pp
```
The command prints the pretty-printed model contained in the input file to the 
console:
```
classdiagram Example {
  class Person {
    int age;
    java.lang.String surname;
  }
  
  association Person -> (friends) Person [*];
}
```

It is possible to pretty-print the models contained in the input file to an 
output file (here: `PPExample.cd`):

```shell
java -jar CDCLI.jar -i Example.cd -pp PPExample.cd
```

### Step 3: Storing Symbols

When the symbols of the `Example.cd` model shall be available elsewhere,
they can bes stored.
The symbol file will contain information about the types and associations
defined in the CD.
It can be imported by other models for using the introduced symbols.

Using the `-s,--symboltable <file>` option builds the symbol table of the input
model and stores it in the file path given as argument.
Providing the file path is optional.
If no file path is provided, the CLI tool stores the symbol table of the
input model in the file `{CDName}.cdsym`.

For storing the symbol file for `Example.cd`, we execute the following command
(the context condition checks require using the path option):
```shell
java -jar CDCLI.jar -i Example.cd -s
```
The CLI tool produces the file `Example.cdsym`, which can now be
imported by other models, e.g., by models that need to
use some of the types defined in the CD `Example`. The tool additionally
indicates the correct generation by its outputs:
```
Successfully parsed Example
Successfully checked the CoCos for Example
Creation of symbol table Example.cdsym successful
```
The symbol file contains a JSON representation of the symbols defined in the CD, which are type, association, interface, attribute and method symbols.

For storing the symbol file of `Example.cd` in the file `symbols/Example.cdsym`,
for example, execute the following command (again, the implicit context 
condition checks require using the model path option):
```shell
java -jar CDCLI.jar -i Example.cd -s symbols/Example.cdsym
```

### Step 4: Adding `FieldSymbol`s corresponding to association roles

By default, the CLI stores exactly the symbols that have been explicitely defined. This is the typical modelling approach. However, code generation typically maps the `CDRoleSymbol`s defined in an association to attributes and thus implcitly adds `FieldSymbol`s into the classes that host an association. These additional symbols can be made available in the symbol file in the two following forms: 

Form 1: For each of the `CDRoleSymbol`s add a `FieldSymbol` in the source class
   of the role. This can be used in languages, like OCL, 
   that always allow for the navigation in both directions.
These additional field symbols are stored with:
```shell
java -jar CDCLI.jar -i Example.cd -s syms/Example.sym --fieldfromrole all
```

* two additional `FieldSymbol`s were stored for both sides of the association

Form 2:  `FieldSymbol`s are added only for navigable roles.
  This can be used in implementation oriented languages that have to cope
  with the actual implementation restrictions:
```shell
java -jar CDCLI.jar -i Example.cd -s syms/Example.sym --fieldfromrole navigable
```
* only one additional `FieldSymbol` is stored for the navigable Role `friends`

### Step 5: Importing Symbol Files Using a Path

MontiCore is designed for modularity (both on the model and the language level).
The CD languages are participating in the symbol exchange infrastructure.
We import a symbol file defining type symbols that are used by 
a CD.

Let us now consider the example `MyLife` from above.
Please, copy the content of `MyLife` and save it 
in a file `monticore/MyLife.cd`. The directory `monticore` is needed 
because of the package definition in line 1.

Execute the following command for processing the file `MyLife.cd`:
```shell
java -jar CDCLI.jar -i monticore/MyLife.cd
```

After executing the command, 
the output states that a context condition is not satisfied by
the model: 
```
[ERROR] MyLife.cd:<13,9>: 0xCDA80: Type 'Address' is used but not defined.
[ERROR] 0xA1038 TypeSymbolSurrogate Could not load full information of 'Address' (Kind de.monticore.symbols.basicsymbols._symboltable.TypeSymbol).
...
```

The missing type `Address` is currently not imported.
`MyLife` already has an `import` statement to another class diagram included, but this class diagram doesn't yet exist. E.f. the following minimal diagram can be stored as well
([also available here](doc/AddressType.cd)):

```
// content of mytypes/AddressType.cdsym
classdiagram AddressType {
  class Address {
    java.lang.String city;
    java.lang.String street;
    int number;
  }
}
```

The CD tool, however, does not directly load dependent models, but only 
their symbol files. This has several interesting advantages:

* it allows us to use the CD language with any other language that defines
  types, because it decouples the languages down to shared symbols,
* the tools themselves also remain decoupled and independent, 
* the build process can be organized in an incremental effective way
    (when using e.g. `gradle` or `make`, but not mvn). 
* even symbols from languages, such as Java, 
  that not are defined with MontiCore can be
  integrated (e.g. we integrate handwritten code via their symbols).

However, the CLI tool has to be applied to the new additional model first:

```shell
java -jar CDCLI.jar -i mytypes/AddressType.cd -s mytypes/AddressType.cdsym
```

We then add the symbol file to the model path using `--path`:

```shell
java -jar CDCLI.jar -i monticore/MyLife.cd --path mytypes
```
 
The model path is used to identify the directory structure that contains the 
needed symbol files. 
As we provide the model path to the tool, it will successfully 
search for symbols in symbol files stored in the model path. 
This means that it processes 
the model successfully without any context condition violations.
Great! 

### Step 6: Creating a graphical representation of CD files

The CDCLI provides the option to create svg files.
A SVG can be created by passing the parameter `--svg` e.g. by command:
```shell
java -jar CDCLI.jar -i monticore/MyLife.cd -p mytypes -puml MyLife --orthogonal -attr -assoc --showRoles --svg
```
that creates the following svg: 

![MyLife.svg](doc/MyLife.svg "MyLife")

It is also possible to export the class diagram only to plantUML:
```shell
java -jar CDCLI.jar -i monticore/MyLife.cd -p mytypes -puml
```

[ExampleModels]: src/test/resources/de/monticore/cd4analysis
[CLIDownload]: http://monticore.de/download/CDCLI.jar

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

