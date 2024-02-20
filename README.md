<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Beta-version: This is intended to become a MontiCore stable explanation. -->

# Class Diagram Languages: CD4A, CD4C



This introduction is 
intended for *modelers* who use the class diagram (CD)
languages. We also provide a 
[detailed documentation of CD 
languages](cdlang/src/main/grammars/de/monticore/cd4analysis.md) 
for *language engineers* using or
extending one of the CD languages.

The CD languages are mainly intended for  
1. analysis modeling (i.e., structures of the system context 
   as well as data structures of the system),
2. (implementation-oriented) code modeling, including method signatures,
3. generating code, data tables, transport functions and more.
4. It is also possible to use CDs only as intermediate structures
   to map from one or more other DSLs into an object-oriented 
   target language, such as Java or C++. 
   (e.g., the MontiCore generator maps grammars to CDs before generating code
   from there).
5. Finally, CDs can also be used as reported results from any other 
   generation or analysis process.

## Downloads
* [Download Link for the CD Tool][ToolDownload]
* [Example Models][ExampleModels]
* [Download Page for all of our public MontiCore Tools][MCDownloadPage]

## An Example Model

The following example CD [`MyCompany`](doc/MyCompany.cd) illustrates the textual 
syntax of CDs:
```
package corp;
import java.util.Date;

classdiagram MyCompany {

  enum CorpKind { SOLE_PROPRIETOR, S_CORP, C_CORP, B_CORP, CLOSE_CORP, NON_PROFIT; }
  abstract class Entity;
  package people {
    class Person extends Entity {
      Date birthday;
      List<String> nickNames;
      -> Address [*] {ordered};
    }
    class Address {
      String city;
      String street;
      int number;
    }
  }
  class Company extends Entity {
    CorpKind kind;
  }
  class Employee extends people.Person {
    int salary;
  }
  class Share {
    int value;
  }
  association [1..*] Company (employer) <-> Employee [*];
  composition [1] Company <- Share [*];
  association shareholding [1] Entity (shareholder) -- (owns) Share [*];

}
```
This example CD contains the following information:
- The CD is contained in the package `corp` and is called `MyCompany`.
- The package `corp` also serves as the default package for all classes in the CD.
- The CD defines the six (6) classes `Entity`,`Person`, `Address`, `Company`, `Employee`, and `Share`. 
- `Entity` is an abstract class, and therefore cannot be instantiated directly.
- `Person` and `Address` are contained in the package `people`. Packages can be used to structure the classes contained in a CD.
- The class `Employee` extends the class `Person` (like in Java, implementation of interfaces
  are also possible).
- Each class may contain attributes, which have a type and name.
- The CD uses available default types (which are basic types from Java), imported types 
  (like `Date`), and predefined forms of generic types (e.g., `List<.>`),
- Enums can also be defined (eg., `CorpKind` ).
- The CD contains associations and compositions that are defined between two classes.
- An association can have a name and navigation information (e.g., `<->`),.
- Each side of an association can have a role name, a cardinality (e.g., `[0..1]`) and certain predefined 
  tags/stereotypes (e.g., `{ordered}`).
- Associations and attributes may also reference qualified types (e.g., `[java.util.Date]`).

More examples can be found [here][ExampleModels].

The CD language infrastructure can be used as a command-line tool from shell or gradle, 
as well as a framework with dirct Java API access.

## Command Line Interface Tool
 
The tool provides typical functionality used when
processing models, including:
* parsing with coco-checking and symbol table creation, 
* pretty-printing, 
* storing symbols in symbol files, 
* loading symbols from symbol files, 
* transforming a CD into a graphical svg format
* computing the semantic difference of 2 CDs, and
* merging 2 CDs (iff the result is semantically sound)

The requirements for building and using the CD tool are that Java 11, Git, 
and Gradle are installed and available for use (e.g., in bash). 

### Downloading the Latest Version of the Tool

A ready to use version of the tool can be downloaded in the form of an
executable JAR file.
You can use [**this download link**][ToolDownload] for downloading the tool. 
Alternatively, the `wget` command can be used to download the latest version into your working directory:
```shell
wget "https://monticore.de/download/MCCD.jar" -O MCCD.jar
``` 

### Actions and Parameters of the Tool

The tool provides quite a number of executable actions and configurable parameters. 
These commands are examples for calling the tool:

```shell
java -jar MCCd.jar -i src/MyCompany --path symbols -o out --gen
java -jar MCCD.jar -i src/MyAddress.cd -s symbols/MyAddress.cdsym
java -jar MCCD.jar -i src/MyLife --path symbols -pp 
```

To try them out for yourself download and put the files
[MyAddress.cd](doc/MyAddress.cd) and [MyLife.cd](doc/MyLife.cd) into your `src` directory. The second command needs to be executed before the third.

The possible options are:

| Option                            | Explanation                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|-----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `-ct,--configTemplate <file>`     | Executes this template at the beginning of a generation with `--gen`. This allows configuration of the generation process (optional, `-fp` is needed to specify the template path).                                                                                                                                                                                                                                                                    |
| `-d,--defaultpackage <boolean>`   | Configures if a default package should be created. Default: false. If `true`, all classes, that are not already in a package, are moved to the default package.                                                                                                                                                                                                                                                                                        |
| `--difflimit <int>`               | Maximum number of shown witnesses when using `--semdiff` (optional; default is: 1, i.e. only one witness is shown).                                                                                                                                                                                                                                                                                                                                    |
| `--diffsize <int>`                | Maximum number of objects in witnesses when comparing the semantic diff with `--semdiff` (optional; default is based on a heuristic, but at least 20). This constrains long searches.                                                                                                                                                                                                                                                                  |
| `--fieldfromrole <fieldfromrole>` | Configures if explicit field symbols, which are typically used for implementing associations, should be added, if derivable from role symbols (default: none). Values: `none` is typical for modeling, `all` adds always on both classes, `navigable` adds only if the association is navigable.                                                                                                                                                       |
| `-fp,--templatePath <pathlist>`   | Directories and jars for handwritten templates to integrate when using `--gen` (optional, but needed, when `-ct` is used).                                                                                                                                                                                                                                                                                                                             |
| `--gen`                           | Generate .java-files corresponding to the classes defined in the input class diagram.                                                                                                                                                                                                                                                                                                                                                                  |
| `-h,--help`                       | Prints short help; other options are ignored.                                                                                                                                                                                                                                                                                                                                                                                                          |
| `-i,--input <file>`               | Reads the source file and parses the contents as a CD. Alternatively, `--stdin` can be used to read the input CD from stdin. Using one of the two options is mandatory for all further operations.                                                                                                                                                                                                                                                     |
| `--json`                          | Writes a "Schema.json" to the output directory.                                                                                                                                                                                                                                                                                                                                                                                                        |
| `-nt,--nobuiltintypes`            | If this option is used, built-in-types will not be considered.                                                                                                                                                                                                                                                                                                                                                                                         |
| `--merge <file>`                  | Parses the file as a second CD and merges it with the input CD (iff semantically sound). The result is stored in memory.                                                                                                                                                                                                                                                                                                                               |
| `-o,--output <dir>`               | Defines the path for generated files (optional; default is: `.`).                                                                                                                                                                                                                                                                                                                                                                                      |
| `--open-world`                    | Compute the multi-instance open-world difference of 2 class diagrams when using `--semdiff` (optional). The method is either `reduction-based` or `alloy-based` (default is: `reduction-based`).                                                                                                                                                                                                                                                       |
| `--path <dirlist>`                | Artifact path for importable symbols, separated by spaces (default is: `.`).                                                                                                                                                                                                                                                                                                                                                                           |
| `-pp,--prettyprint <file>`        | Prints the input CDs to stdout or to the specified file (optional). The output directory is specified by `-o`.                                                                                                                                                                                                                                                                                                                                         |
| `-r,--report <dir>`               | Prints reports of the parsed artifact to the specified directory (optional) or the output directory specified by `-o` (default is: `.`) This includes e.g. all defined packages, classes, interfaces, enums, and associations. The file name is "report.{CDName}"                                                                                                                                                                                      |
| `--rule-based`                    | Uses a rule-based approach to `--semdiff` instead of the model-checker Alloy to compute the diff witnesses. Improved performance.                                                                                                                                                                                                                                                                                                                      |
| `-s,--symboltable <file>`         | Stores the symbol table of the CD. The default value is `{CDName}.cdsym`. This option does not use the output directory specified by `-o`.                                                                                                                                                                                                                                                                                                             |
| `--semdiff <file>`                | Parses the file as a second CD and compares it semantically with the first CD that is currently in memory. Output: object diagrams (witnesses) that are valid in the first CD, but invalid in the second CD. This is a semantics-based, asymmetric diff. Details: https://www.se-rwth.de/topics/Semantics.php                                                                                                                                          |
| `--show <print_option>`           | Specifies the print option for `--syntaxdiff`: `diff` (default) prints only the differences in a color-coded format (red for deleted, yellow for changed, and green for newly added elements). `old` will print only the old CD with color-coded diffs and `new` only the new CD. `both` prints both CDs. `added` prints only the added CD-elements; `removed` prints only the removed CD-elements, and `changed` prints only the changed CD-elements. |
| `--stdin`                         | Reads the input CD from stdin instead of the source file specified by `-i`. Using one of the two options is mandatory for all further operations.                                                                                                                                                                                                                                                                                                      |
| `--syntaxdiff <file>`             | Performs a syntactical difference analysis on the current CD in memory (new) and a second CD (old) and prints the result to stdout. Default: Outputs color-coded differences (red for deleted, yellow for changed, and green for newly added elements) to stdout.                                                                                                                                                                                      |

### Building the Tool from the Sources (if desired)
 
As alternative to a download, 
it is possible to build an executable JAR of the tool from the source files
located in GitHub. In order to build an executable Jar of the tool with
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
recognized as a command in your shell, please install 
[Gradle](https://gradle.org/releases/)).
To this effect, execute the following two commands:
```shell
gradle build
gradle shadowJar
```
Congratulations! The  executable JAR file `MCCD.jar` is now in
the directory `target/libs`.

## Tutorial: Getting Started Using the CD Tool

The following small tutorial should help to get an idea 
of how to use the CD tool given in `MCCD.jar`.

### Step 1:

Executing the tool with the following command and no 
options prints information on the available options:
```shell
java -jar MCCD.jar
```
You may also use the option `-h, --help` for the same result:
```shell
java -jar MCCD.jar -h
```

For any other action, the tool requires either the option `-i,--input <file>`,
which reads a file containing a CD model as input, or `--stdin`, 
which parses the input CD from `stdin` instead.
If no additional options are specified, the tool processes the model,
but does not produce any further output, except for error messages or 
a message indicating success. Note that processing means that the tool parses the model, builds its 
symbol table, and then checks whether the model satisfies all context 
conditions.

If you want to try this out yourself, copy the `MCCD.jar` into a directory of your 
choice. Then create a text file `src/MyExample.cd` 
([also available here](doc/MyExample.cd)) in a `src` subdirectory of the
directory where `MCCD.jar` is located containing e.g. the following simple CD 
(please note that, like in Java, filename and model name in the file have to be
the same):

```
import java.lang.String;

classdiagram MyExample {
  class Person {
    int age;
    String surname;
  }

  association Person -> (friends) Person [*];
}
```

Now execute the following command:
```
java -jar MCCD.jar -i src/MyExample.cd
```

You may notice that the tool prints the following text to the console:
```
Successfully parsed src/MyExample.cd
Successfully checked the CoCos for class diagram MyExample
```

The contents of the input CD artifact can also be piped to the tool.
For this, execute the following command:

```shell
cat src/MyExample.cd | java -jar MCCD.jar --stdin
``` 
The output is the same as for the previous command.

### Step 2: Pretty-Printing

The tool provides a pretty-printer for the CD language.
A pretty-printer can be used, e.g., to fix the formatting of files containing 
CDs, but has its main application in printing internally constructed 
or transformed CDs.

To execute the pretty-printer, the `-pp,--prettyprint` option can be used.
Using the option without any arguments pretty-prints the model contained in the
input file to the console (stdout):

```shell
java -jar MCCD.jar -i src/MyExample.cd -pp
```
After executing the command, the following output should appear on your console:
```
import java.lang.String;

classdiagram MyExample {
  class Person {
    int age;
    String surname;
  }
  
  association Person -> (friends) Person [*];
}
```

It is possible to pretty-print the models contained in the input file to an 
output file (here: `PPExample.cd`), missing directories are created 
automatically:

```shell
java -jar MCCD.jar -i src/MyExample.cd -pp target/PPExample.cd
```

### Step 3: Storing Symbols

If the symbols of the `src/MyExample.cd` model should be available elsewhere,
they can be stored.
The symbol file will contain information about the classes and associations
defined in the CD.
It can be imported by other models in order to use these symbols.

Using the option `-s,--symboltable <file>` builds the symbol table of the input
model and stores it in the file path given as argument.
Providing the file path is optional.
If no file path is provided, the tool stores the symbol table of the
input model in the file `{CDName}.cdsym`.

For storing the symbol file for `src/MyExample.cd`, we execute the following 
command (the context condition checks require using the path option):
```shell
java -jar MCCD.jar -i src/MyExample.cd -s
```
The tool produces the file `MyExample.cdsym`, which can now be
imported by other models, e.g., by models that need to
use some of the classes defined in the CD `MyExample`. The tool additionally
indicates the correct generation by its outputs:
```
Successfully parsed src/MyExample
Successfully checked the CoCos for MyExample
Creation of symbol table src/MyExample.cdsym successful
```
The symbol file contains a JSON representation of the symbols defined in the CD,
which are type, association, interface, attribute and method symbols.

E.g., for storing the symbols of `src/MyExample.cd` in the file 
`symbols/MyExample.cdsym`, execute the following command:
```shell
java -jar MCCD.jar -i src/MyExample.cd -s symbols/MyExample.cdsym
```

### Step 4: Adding `FieldSymbol`s corresponding to association roles

By default, the CD tool stores exactly the symbols that have been explicitly 
defined. This is the typical modeling approach. However, code generation 
usually maps the `CDRoleSymbol`s defined in an association to attributes and 
thus implicitly adds `FieldSymbol`s into the classes that host an association. 
These additional symbols can be made available in the symbol file in the two 
following forms: 

Form 1: For each of the `CDRoleSymbol`s add a `FieldSymbol` in the source class
   of the role. This can be used in languages, like OCL, 
   that always allow for the navigation in both directions.
These additional field symbols are stored with:
```shell
java -jar MCCD.jar -i src/MyExample.cd -s symbols/MyExample.cdsym --fieldfromrole all
```

* two additional `FieldSymbol`s were stored for both sides of the association

Form 2: `FieldSymbol`s are added only for navigable roles.
  This can be used in implementation oriented languages that have to cope
  with the actual implementation restrictions:
```shell
java -jar MCCD.jar -i src/MyExample.cd -s symbols/MyExample.cdsym --fieldfromrole navigable
```
* only one additional `FieldSymbol` is stored for the navigable Role `friends`,
  because the association is only unidirectional

### Step 5: Importing Symbol Files Using a Path

MontiCore is designed for modularity (both on the model and the language level).
The CD languages are participating in the symbol exchange infrastructure.
We import a symbol file defining type symbols that are used by a CD.

Let us now consider the example `MyLife` from above.
Please, copy the file [`MyLife.cd`](doc/MyLife.cd) and save it 
in a file `src/monticore/MyLife.cd`.

Execute the following command for processing the file `MyLife.cd`:
```shell
java -jar MCCD.jar -i src/monticore/MyLife.cd
```

After executing the command, 
the output states that a context condition is not satisfied by
the model: 
```
[ERROR] MyLife.cd:<18,9>: 0xA0324 Cannot find symbol Address
...
```

The missing class `Address` is currently not imported.
`MyLife` already has an `import` statement to another class diagram 
included ([available here](doc/MyAddress.cd)):

(content of src/MyAddress.cd)
```
import java.lang.String;

classdiagram MyAddress {
  class Address {
    String city;
    String street;
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

However, the tool has to be applied to the new additional model first:

```shell
java -jar MCCD.jar -i src/MyAddress.cd -s symbols/MyAddress.cdsym
```

We then add the symbol file to the model path using `--path`:

```shell
java -jar MCCD.jar -i src/monticore/MyLife.cd --defaultpackage --path symbols
```
 
The model path is used to identify the directory structure that contains the 
needed symbol files. 
As we provide the model path to the tool, it will successfully 
search for symbols in symbol files stored in the model path. 
This means that it processes the model successfully without any context 
condition violations.
Great! 

### Step 6: Create a Default Package in the Class Diagram

The class diagram languages support structuring the CD into packages (similar to
Java).
For classes with no explicit defined package the tool can assume those classes 
to be in a default package. This default is calculated as follows:
1. If the class diagram itself is defined in a package, this package is 
   propagated to the classes contained in the cd.
2. If such a package is not explicitly given, the default 'de.monticore' is used.

### Step 7: Generating .java-Files

By using the option `--gen`, we can generate .java-files corresponding to the
input class diagram:
```shell
java -jar MCCD.jar -i src/MyExample.cd --gen
```
With option `-o` we can specify the output directory; the default is `.`:
```shell
java -jar MCCD.jar -i src/MyExample.cd --gen -o out
```
Note that the option `--fieldfromrole` must be used with the appropriate
argument in order to generate attributes for associations contained in the input CD. 
Use the following commands in order to generate .java-files for the CD `MyCompany`([available here](doc/MyCompany.cd)):

```shell
java -jar MCCD.jar -i src/MyCompany.cd -o out --gen --fieldfromrole navigable
```

### Step 8: The Semantic Difference of Two Class Diagrams

We define the semantic difference semdiff(CD1,CD2) of two class diagrams CD1 and
CD2 as the set of all object diagrams that are valid in CD1 but invalid in CD2. 
These object diagrams are also referred to as diff-witnesses. We observe
that this difference is asymmetric. For more details on semantic 
differencing:

https://www.se-rwth.de/topics/Semantics.php

The option `--semdiff <file>` computes the semantic difference of the current CD in memory
and the CD specified by the argument.

For the following examples, download the files 
[MyEmployees1.cd](doc/MyEmployees1.cd) and [MyEmployees2.cd](doc/MyEmployees2.cd) 
and save them in
`src`:

```shell
java -jar MCCD.jar -i src/MyEmployees1.cd --semdiff scr/MyEmployees2.cd
```

We can use the option `difflimit` to specify the maximum number of witnesses 
that are generated in the output directory; the default is to generate one diff-witness. 
Once again, the option `-o` can be used to specify the output directory; the default is `.`:

```shell
java -jar MCCD.jar -i src/MyEmployees1.cd  --semdiff src/MyEmployees2.cd --difflimit 20 -o out
```

Note that `--semdiff` does not use symbols from symbol files.

### Step 9: Merging Two Class Diagram

The option `--merge <file>` merges the input-CD with the CD specified by the argument 
iff the two are semantically compatible.
The result is stored in memory as the current CD.

For the following examples, download the files 
[Teaching.cd](doc/Teaching.cd) and [Management.cd](doc/Management.cd) and 
save them in `src`:

```shell
java -jar MCCD.jar -i src/Teaching.cd --merge src/Management.cd -pp
```

If `-pp` is used in conjunction with `--merge`, the name of the merged CD always corresponds to the 
name of the file (without the suffix `.cd`):

```shell
java -jar MCCD.jar -i src/Teaching.cd --merge src/Management.cd -o out -pp UniversitySystem.cd
```

Note that `--merge` does not use symbols from symbol files. Instead of using the `--merge` option 
from the command line, you may also call the static method 
[`CDMerge.merge()`](src/cdmerge/java/de/monticore/cdmerge/CDMerge.java) 
with a set of appropriate 
[`MergeParameter`](src/cdmerge/java/de/monticore/cdmerge/config/MergeParameter.java)


[ExampleModels]: src/test/resources/de/monticore/cd4analysis/examples
[ToolDownload]: https://monticore.de/download/MCCD.jar
[MCDownloadPage]: https://monticore.github.io/monticore/docs/Download/

## Further Information
* [Other MontiCore Tools][MCDownloadPage]
* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of Languages**](https://github.com/MontiCore/monticore/blob/HEAD/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/HEAD/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/HEAD/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Research Topics](https://www.se-rwth.de/topics)
* [Licence definition](https://github.com/MontiCore/monticore/blob/HEAD/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
