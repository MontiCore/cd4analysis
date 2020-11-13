/* (c) https://github.com/MontiCore/monticore */
package de.monticore

import de.monticore.cd.cli.CDCLI
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/**
 * A Gradle task that executes the MontiCore generator.
 * <pre>
 * * Required configuration:
 *    - inputFile       - the file to handle
 * Optional configuration:
 *    - help            - prints the help
 *    - logLevel        - the logLevel to execute the tooling with
 *                        # defaults to LogLevel.INFO
 *    - addModelConfig  - boolean that specifies whether the configuration
 *                        called 'model' should be added to the model path
 *                        # defaults to true
 *    - modelPath       - a list of paths for imported artifacts
 *                        # defaults to '[$projectDir/src/main/resources]'
 *    - symboltableFile - the name of the created symboltable export
 *                        # defaults to '_inputFile_.cdsym'
 *    - outputDir       - output path for the generated files
 *                        # defaults to '$buildDir'
 *    - reportDir       - the path for the reports
 *                        defaults to '_output_/report'
 *    - usebuiltintypes - use the predefined built-in-types
 *                        defaults to 'true'
 *    - scriptFile      - the script to be used for the execution
 *                        defaults to 'cdconfiguration.groovy'
 *    - includeConfigs  - list of names of configurations that should be added to the model path
 *                        defaults to empty list
 * </pre>
 */
public class CDTask extends DefaultTask {

  public enum LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR;
  }

  CDTask() {
    // set the task group name, in which all instances of CDTask will appear
    group = 'CD'
  }

  boolean help = false

  LogLevel logLevel = LogLevel.INFO

  boolean addModelConfig = true

  final RegularFileProperty inputFile = project.objects.fileProperty()

  final ConfigurableFileCollection modelPaths =
      project.objects.fileCollection().from(
          project.layout.projectDirectory.dir("src/main/resources")
      )

  String symboltableFile;

  final DirectoryProperty outputDir = project.layout.buildDirectory

  final DirectoryProperty reportDir = project.objects.directoryProperty()

  boolean useBuiltinTypes = true

  final RegularFileProperty scriptFile = project.objects.fileProperty()

  List<String> includeConfigs = []

  @Incremental
  @InputFile
  RegularFileProperty getInputFile() {
    return inputFile
  }

  @Input
  boolean getHelp() {
    return help
  }

  @Input
  boolean getAddModelConfig() {
    return addModelConfig
  }

  @Input
  @Optional
  LogLevel getLogLevel() {
    return logLevel
  }

  @InputFiles
  @Optional
  List<File> getModelPaths() {
    return modelPaths.files.toList()
  }

  @Input
  @Optional
  String getSymboltableFile() {
    return symboltableFile
  }

  @OutputDirectory
  @Optional
  DirectoryProperty getOutputDir() {
    return outputDir
  }

  @OutputDirectory
  @Optional
  DirectoryProperty getReportDir() {
    return reportDir
  }

  @Input
  boolean getUseBuiltinTypes() {
    return useBuiltinTypes
  }

  @InputFile
  @Optional
  RegularFileProperty getScriptFile() {
    return scriptFile
  }

  @Input
  @Optional
  List<String> getIncludeConfigs() {
    return includeConfigs
  }

  public void modelPath(File... paths) {
    getModelPath().addAll(paths)
  }

  @TaskAction
  void execute(InputChanges inputs) {
    logger.info(inputs.isIncremental() ? "CHANGED inputs considered out of date"
        : "ALL inputs considered out of date")

    // if no model path is specified use $projectDir/src/main/resources as default
    if (modelPaths.isEmpty()) {
      File mp = project.layout.projectDirectory.file("src/main/resources").getAsFile()
      if (mp.exists()) {
        modelPaths.from(mp)
      }
    }

    // if no outputDir path is specified use $buildDir as default
    if (!outputDir.isPresent()) {
      outputDir.set(project.layout.buildDirectory.dir("generated-models"))
    }

    // if no symboltable file is specified use _inputFile_.cdsym as default
    if (symboltableFile == null) {
      String inf = inputFile.get().getAsFile().getName() - ~/\.\w+$/
      symboltableFile = inf + ".cdsym"
    }

    // if no reportDir path is specified use $buildDir/resource as default
    if (!reportDir.isPresent()) {
      reportDir.value(project.layout.buildDirectory.dir("resource"))
    }

    // if no scriptFile is specified use cdconfiguration.groovy as default
    if (!scriptFile.isPresent()) {
      scriptFile.set(new File('cdconfiguration.groovy'))
    }

    if (!inputs.getFileChanges(inputFile).isEmpty()) {
      // execute CDCLI if task is out of date
      logger.info("Rebuild because " + inputFile.get().getAsFile().getName() + " itself has changed.")
      rebuild()
    }
  }

  void rebuild() {
    logger.info("out of date: " + inputFile.get().getAsFile().getName())

    List<String> mp = new ArrayList()
    // if not disabled put 'model' configuration on model path
    if (addModelConfig) {
      project.configurations.getByName("model").each { mp.add it }
    }
    // if specified by the user put further configurations on model path
    for (c in includeConfigs) {
      project.configurations.getByName(c).each { mp.add it }
    }

    mp.addAll(modelPaths)

    // construct string array from configuration to pass it to CDCLI
    List<String> params = ["-i", inputFile.get().asFile.toString(),
                           "-s", symboltableFile,
                           "-p", mp.stream().map { it.toString() }.collect().join(':'),
                           "-o", outputDir.get().toString(),
                           "-r", reportDir.get().toString(),
                           "-t", useBuiltinTypes]
    if (scriptFile != null) {
      params.add("-script")
      params.add(scriptFile)
    }
    if (logLevel != null) {
      params.add("-log")
      params.add(logLevel.toString().toLowerCase())
    }
    if (help) {
      params.add("-h")
    }
    def p = params.toArray() as String[]

    // execute Monticore with the given parameters
    CDCLI.main(p)
  }

}
