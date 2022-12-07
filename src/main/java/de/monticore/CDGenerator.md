## CDGeneratorTool

The CDGeneratorTool provides a lightweight way to interact with the CD Language using the command line and processing classdiagram models including:

- inputting and parsing a classdiagram-model with coco-checking and symbol table creation
- storing symbols in symbol files
- generating java code out of the class diagram

The possible options are:

| Option                     | Explanation                                                                                              |
|----------------------------|----------------------------------------------------------------------------------------------------------|
| `-h` `--help`              | Prints out all possible Options as well as short explanations                                            |
| `-i` `--input`             | Inputs the location of a classdiagram-file to be parsed, and checked                                     |
| `-c2mc` `--class2mc`       | Enables to resolve java classes used in the class diagram in the model diagram                           |
| `-sym` `--symbolpath`      | Sets the path of for loading additionally stored symbols                                                 |
| `-c` `--checkcocos`        | Checks all CD4C-CoCos on the current classdiagram-model                                                  |
| `-o` `--output`            | Sets the path for all output files                                                                       |
| `-s` `--symboltable`       | Sets the additional path and file for a serialized version of the symbol-table                           |
| `-gen` `--generate`        | Generates Java Classes and Code in the given output path corresponding to the current classdiagram-model |
| `-tp` `--template`         | Sets the path of additional templates that can be used in the generation process                         |
| `-hwc` `--handwrittencode` | Sets the path for additional handwritten code to be considered in the generation process                 |
| `-ct` `--configtemplate`   | Sets a different template for generator configurations to replace the default template                   |
| `-v` `--version`           | Prints out the current version of the tool to the console                                                |
