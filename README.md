<!-- (c) https://github.com/MontiCore/monticore -->

This introduction is 
intended for  *modelers* who use the class diagram (CD)
languages. We also provide a 
[detailed documentation of CD languages](src/main/grammars/de/monticore/cd4analysis.md). 
for *language engineers* using or
extending one of the CD languages.

The CD languages are mainly intended for  
1. Analysis modelling (i.e. structures of the system context 
   as well as data structures of the system)
1. Code modelling (implementation) oriented
1. Generating of code, data tables, transport functions and more
1. It is also possible, to use CDs only as intermediate structure
   to map from one or more other DSLs into an object-oriented 
   target language, such as Java or C++. 
   (E.g. the MontiCore generator maps grammars to CDs before generating code
   from there).

# An Example Model

The following example CD `MyLife` illustrates the textual syntax of CDs:
```
package monticore;

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
  (like `Date`), and predefined forms of generic types (like `List`).
- associations and compositions that are defined between two classes and
  can have a name, a navigation information (e.g. `<->`), role names on both
  sides, multiplicities (like `[0..1]`) and certain predefined tags/stereotypes 
  (like `{ordered}`).
- that both, association and compositions, can be qualified for example by 
  `[String]`.
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

The requirements for building and using the SD CLI tool are that Java 8, Git, 
and Gradle are installed and available for use in Bash. 

The following subsection describes how to download the CLI tool.
Then, this document describes how to build the CLI tool from the source files.
Afterwards, this document contains a tutorial for using the CLI tool.  

## Downloading the Latest Version of the CLI Tool
A ready to use version of the CLI tool can be downloaded in the form of an
executable JAR file.
You can use [**this download link**][CLIDownload] for downloading the CLI tool. 

Alternatively, you can download the CLI tool using `wget`.
The following command downloads the latest version of the CLI tool and saves it
under the name `CDCLI.jar` in your working directory:
```shell
wget "https://nexus.se.rwth-aachen.de/service/rest/v1/search/assets/download?sort=version&repository=monticore-snapshots&maven.groupId=de.monticore.lang&maven.artifactId=cd4analysis&maven.extension=jar&maven.classifier=cli" -O CDCLI.jar
``` 

## Building the CLI Tool from the Sources
 
It is possible to build an executable JAR of the CLI tool from the source files
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
Afterwards, build the source files with gradle (if `./gradlew.bat` is not 
recognized as a command in your shell, then use `./gradlew`).
To this effect, execute the following two commands:
```shell
./gradlew.bat clean build
./gradlew.bat shadowJar
```
Congratulations! You can now find the executable JAR file `CDCLI.jar` in
 the directory `target/libs` (accessible via `cd target/libs`).

## Tutorial: Getting Started Using the CD CLI Tool
The previous sections describe how to obtain an executable JAR file
(CD CLI tool). This section provides a tutorial for
using the CD CLI tool. The following examples assume
that you locally named the CLI tool `CDCLI.jar`.
If you built the CLI tool from the sources or used the `wget`
command above, then you are fine. If you manually downloaded 
the CLI tool, then you should consider renaming the downloaded JAR. 

### First Steps
Open a command line shell and change your working directory to the directory 
containing the CLI tool. For executing the CLI tool, execute the following 
command:
```shell
java -jar CDCLI.jar
```

Executing the Jar file without any options prints usage information of the CLI 
tool to the console:
```shell
$ java -jar CDCLI.jar
usage: Examples in case the CLI file is called CDCLI.jar:
java -jar CDCLI.jar -i Person.cd -p target:src/models -o target/out -t true -s
java -jar CDCLI.jar -i Person.cd -pp Person.out.cd -puml --showAtt --showRoles
 -f,--failquick <value>                   Configures if the application should quickfail on errors
                                          [true/false]. The default value is "false".
 -h,--help                                Print this help dialogue.
 -i,--input <file>                        Reads the input CD artifact given as argument.
 -o,--output <dir>                        Path of generated files (optional). The default value is ".".
 -p,--path <dirlist>                      Sets the artifact path for imported symbols separated by ';'. The
                                          default value is ".".
 -pp,--prettyprint <prettyprint>          Prints the input SDs to stdout or to the specified file (optional).
 -puml,--plantUML                         Transform the input model to a PlantUML model.
 -r,--report <dir>                        Prints reports of the parsed artifact to the specified directory
                                          (optional). Available reports are language-specific. The default
                                          value is "_output_path_".
 -s,--symboltable <file>                  Serializes and prints the symbol table to stdout or the specified
                                          output file (optional). The default value is
                                          "{inputArtifactName}.cdsym".
 -stdin,--stdin                           Reads the path to the input CD artifact from stdin.
 -t,--usebuiltintypes <useBuiltinTypes>   Configures if built-in-types should be considered [true/false]. The
                                          default value is "true".
```

To work properly, the CLI tool needs the mandatory argument `-i,--input <file>`,
which takes the file path of exactly one file containing CD models as input.
If no further options are specified, the CLI tool parses the model, builds its 
symbol table, and then checks whether the model satisfies all context 
conditions.

For trying this out, copy the `SD4DevelopmentCLI.jar` into a directory of your 
choice. Afterwards, create a text file containing the following simple CD:
```
classdiagram Example {
}
```

Save the text file as `Example.cd` in the directory where `CD4CLI.jar` is 
located. 

Now execute the following command:
```
java -jar CDCLI.jar -i Example.cd
```

You may notice that the CLI tool prints the following text to the console:
```
Successfully parsed Example
Successfully checked the CoCos for Example
```
Thus, the tool successfully parsed the artifact and successfully checked the 
conditions for the CDs contained in the artifact. The tool further
constructed the symbol tables of the CDs contained in the parsed artifact.

The contents of the input CD artifact can also be piped to the CLI tool.
For trying this out, execute the following command:

```shell
cat Example.cd | java -jar CDCLI.jar --stdin
``` 
The output is the same as for the previous command.

### Step 2: Pretty-Printing
The CLI tool provides a pretty-printer for the CD language.
A pretty-printer can be used, e.g., to fix the formatting of files containing 
CDs.
To execute the pretty-printer, the `-pp,--prettyprint` option can be used.
Using the option without any arguments pretty-prints the models contained in the
input files to the console.

Execute the following command for trying this out:
```shell
java -jar CDCLI.jar -i Example.cd -pp
```
The command prints the pretty-printed model contained in the input file to the 
console:
```
Successfully parsed Example
Successfully checked the CoCos for Example

classdiagram Example {
  package de.monticore {
  }
}
```
The pretty-printed CD contains the package `de.monticore` as the artifact
contains no package and `de.monticore` is the default package.

It is possible to pretty-print the models contained in the input file to an 
output file. For this task, it is possible to provide the name of the output 
file as an argument to the `-pp,--prettyprint <prettyPrintOutput>` option.

Execute the following command for trying this out:
```shell
java -jar CDCLI.jar -i Example.cd -pp PPExample.cd
```
The command prints the pretty-printed model contained in the input file into the
file `PPExample.cd`.

### Step 3: Importing Symbol Files Using a Path
In this section, we import a symbol file defining type symbols that are used by 
a CD. 

Let us now consider an example that is more complex than `Example.cd`.
Recall the CD `MyLife` from the `An Example Model` section above.
For continuing, copy the textual representation of the CD `MyLife` and save it 
in a file `MyLife.cd` contained in the directory `monticore`, which is again 
contained in the directory where the file `CDCLI.jar` is located. Thus, relative
to your working directory, the file `MyLife.cd` is contained in the directory 
`monticore` and your working directory should contain the CLI Jar `CDCLI.jar`.   

Execute the following command for processing the file `MyLife.cd`:
```shell
java -jar CDCLI.jar -i monticore/MyLife.cd
```

After executing the command, you may notice that the CLI tool produces some 
output. The output states the reason why a context condition is not satisfied by
the model. The output contains the following error message: 
```
[ERROR] 0xA1038 TypeSymbolSurrogate Could not load full information of 'Address' 
```
The error message indicates that there seems to be a problem with the used type 
`Address`. Indeed, the tool tries to load some type information about the 
`Address` type. However, we never defined this type at any place, and therefore 
the tool is not able to find any information of the `Address` type.

There must be another model defining the type `Address`. 
The model must provide the information about the definition of this type to its 
environment via storing this information in its symbol file (its symbol table 
stored in the file system).

The symbol file of this model has to be imported by the CD model for accessing 
the type. The type can be defined in an arbitrary model of an arbitrary language
, as long as the information about the definition of the type is stored in the 
symbol file of the model and the CD imports this symbol file. 
This may sound complicated at this point, but conceptually it is actually quite 
simple. This has even a huge advantage because it allows us to use the CD 
language with any other language that defines types. You could even use 
languages that are not defined with MontiCore, as long as suitable symbol files 
are generated from the models of these languages.

The following describes how to fix the error in the example model `MyLife.sd` 
by importing a symbol file defining the (yet undefined) type. 
We make use of the model path and provide the CLI tool with
a symbol file (stored symbol table) of another model, which contains the 
necessary type information.

Create a new directory `mytypes` in the directory where the CLI tool `CDCLI.jar`
is located. The symbol file `AddressType.cdsym` of a model, which provides all 
necessary type information, can be found [here](doc/AddressType.cdsym).
Download the file, name it `AddressType.cdsym`, and move it into the directory 
`mytypes`.

The contents of the symbol file are of minor importance for you as a language 
user. In case you are curious and had a look into the symbol file: 
The symbol file contains a JSON representation of the symbols defined in a model
. In this case, the symbol file contains information about defined types. 
Usually, the CLI tools of MontiCore languages automatically generate the 
contents of these files and you, as a language user, must not be concerned with 
their contents. 
  
The path containing the directory structure that contains the symbol file is 
called the "Model Path". If we provide the model path to the tool, it will 
search for symbols in symbol files, which are stored in directories contained in
the model path. So, if we want the tool to find our symbol file, we have to 
provide the model path to the tool via the `-p,--path <dirlist> ` option:
```shell
java -jar CDCLI.jar -i monticore/MyLife.cd -p <MODELPATH>
```
where `<MODELPATH>` is the path where you stored the downloaded symbol file.
In our example, in case you stored the model in the directory `mytypes`,
execute the following command:
```shell
java -jar CDCLI.jar -i monticore/MyLife.cd -p mytypes
```

Well, executing the above command still produces the same error message.
This is because the symbol file needs to be imported first, just like in Java.
Therefore, we add the following import statement to the beginning of the 
contents contained in the file `MyLife.cd` containing the CD `MyLife`:
```
package monticore;

import AddressType;

classdiagram MyLife {
  ...
}
```
The added import statement means that the file containing the CD imports all
symbols that are stored in the symbol file `Address`. 
Note that you may have to change the name here, depending on how you named the
symbol file from above.
The concrete file ending `.cdsym` is not important 
in this case. However, the file ending of the symbol file must end with `sym`, 
i.e., the name of the symbol file must be compatible to the pattern `*.*sym`.
If you strictly followed the instructions of this tutorial, then you are fine.

If we now execute the command again, the CLI tool will print the following 
output: 

```
Successfully parsed MyLife
Successfully checked the CoCos for MyLife
```

This means that it processed 
the model successfully without any context condition violations.
Great! 

### Step 4: Storing Symbols
The previous section describes how to load symbols from an existing symbol file.
Now, we will use the CLI tool to store a symbol file for our `MyLife.cd` model.
The stored symbol file will contain information about the types and associations
defined in the CD.
It can be imported by other models for using the introduced symbols,
similar to how we changed the file `MyLife.cd` for importing the symbols 
contained in the symbol file `AddressType.cdsym`.

Using the `-s,--symboltable <file>` option builds the symbol table of the input 
model and stores it in the file path given as argument.
Providing the file path is optional.
If you do not provide a file path, the CLI tool stores the symbol table of the 
input model in the file `{fileName}.cdsym` where `fileName` is the name of the 
file containing the input model in the directory where the input file is located
. 

For storing the symbol file of `MyLife.cd`, execute the following command 
(the context condition checks require using the path option):
```shell
java -jar CDCLI.jar -i monticore/MyLife.cd -p mytypes -s
```
The CLI tool produces the file `monticore/MyLife.cdsym`, which can now be 
imported by other models, e.g., by models that need to
use some of the types defined in the CD `MyLife`. The tool additionally 
indicates the correct generation by its outputs:
```
Successfully parsed MyLife
Successfully checked the CoCos for MyLife
modelName:MyLife
Creation of symbol table .\monticore\MyLife.cdsym successful
```

For storing the symbol file of `MyLife.cd` in the file `syms/MyLifeSyms.cdsym`, for example, execute the following command
(again, the implicit context condition checks require using the model path option):
```shell
java -jar CDCLI.jar -i monticore/MyLife.cd -p mytypes -s syms/MyLifeSyms.cdsym
```

Congratulations, you have just finished the tutorial about saving CD symbol files!

### Using PlantUML to create graphical representations of cd files
The CDCLI provides the option to create plantUML and svg files.
PlantUML can be configured further to add additional details.
Using the previously used model files, a plantUML model can be created with:
```shell
java -jar CDCLI.jar -i monticore/MyLife.cd -p mytypes -puml
```

If there is no given output name, then the name of the model is used.

Additionally to the used cli parameter, CDCLI can be configured for PlantUML
using futher options specifically for PlantUML output:
```shell
$ java -jar CDCLI.jar -h -puml
...
usage: PLANTUML
 -assoc,--showAssociations            show associations [true] when used. The default value is "false".
 -attr,--showAttributes               show attributes [true] when used. The default value is "false".
 -card,--showCardinality              show cardinalities [true] when used. The default value is "false".
 -comment,--showComments              show comments [true] when used. The default value is "false".
 -mod,--showModifier                  show modifier [true] when used. The default value is "false".
    --nodeSeparator <nodesep>   set the node separator [number]. The default value is "-1".
    --orthogonal                      show lines only orthogonal [true] when used. The default value is
                                      "false".
    --rankSeparator <ranksep>   set the rank separator [number]. The default value is "-1".
    --showRoles                   show roles [true] when used. The default value is "false".
    --shortenWords                shorten displayed words [true] when used. The default value is "false".
    --svg                             print as plantUML svg
```

A SVG can be created by passing the parameter `--svg`.
The svg ![MyLife.svg](doc/MyLife.svg "MyLife")

is created by the command:
```shell
java -jar CDCLI.jar -i monticore/MyLife.cd -p mytypes -puml MyLife --orthogonal -attr -assoc --showRoles --svg
```

[ExampleModels]: src/test/resources/de/monticore/cd4analysis
[CLIDownload]: https://nexus.se.rwth-aachen.de/service/rest/v1/search/assets/download?sort=version&repository=monticore-snapshots&maven.groupId=de.monticore.lang&maven.artifactId=cd4analysis&maven.extension=jar&maven.classifier=cli

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

