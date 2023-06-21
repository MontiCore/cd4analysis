<!-- (c) https://github.com/MontiCore/monticore -->
## CDGeneratorTool

The CDGeneratorTool provides a lightweight way to use the 
functionality provided for the CD Language 
from a command line (i.e. a shell) and processing classdiagram 
models including:

- loading classdiagram, coco-checking and symboltable creation
- storing symbols in symbol files
- generating a core set of Java classes from of the class diagram

The possible options are:

| Option                     | Explanation                                                                                              |
|----------------------------|----------------------------------------------------------------------------------------------------------|
| `-h` `--help`              | Prints out all possible Options as well as short explanations                                            |
| `-i` `--input`             | Inputs the location of a classdiagram-file to be parsed, and checked                                     |
| `-c2mc` `--class2mc`       | Enables to resolve java classes used in the class diagram in the model diagram                           |
| `-path`                    | Sets the path of for loading additionally stored symbols                                                 |
| `-c` `--checkcocos`        | Checks all CD4C-CoCos on the current classdiagram-model                                                  |
| `-o` `--output`            | Sets the path for all output files                                                                       |
| `-s` `--symboltable`       | Sets the additional path and file for a serialized version of the symbol-table                           |
| `-tp` `--template`         | Sets the path of additional templates that can be used in the generation process                         |
| `-hwc` `--handwrittencode` | Sets the path for additional handwritten code to be integrated in the generation process                 |
|                            | (see explanation for details)                                                                            |
| `-ct` `--configtemplate`   | Sets a template for generator configurations to replace the default template                   |
| `-v` `--version`           | Prints out the current version of the tool to the console                                                |
