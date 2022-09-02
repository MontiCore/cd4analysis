/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MCLoggerWrapper;
import de.monticore.cdmerge.refactor.CleanAttributesInheritedFromSuperclass;
import de.monticore.cdmerge.refactor.ModelRefactoringBase;
import de.monticore.cdmerge.refactor.ModelRefactoringBase.ModelRefactoringBuilder;
import de.monticore.cdmerge.refactor.RemoveRedundantInterfaces;
import de.monticore.cdmerge.util.CDUtils;
import de.monticore.cdmerge.validation.AssociationChecker;
import de.monticore.cdmerge.validation.AttributeChecker;
import de.monticore.cdmerge.validation.ModelValidatorBase;
import de.monticore.cdmerge.validation.ModelValidatorBase.ModelValidatorBuilder;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all user-specified configurations for the composition of classdiagrams
 */
public class CDMergeConfig {

  private static Logger log = Logger.getLogger("CDMergeConfig");

  private final ImmutableMap<MergeParameter, String> parameters;

  private List<ASTCDCompilationUnit> inputCDs;

  private final PrecedenceConfig precedences;

  private final boolean CLI_MODE;

  private CD4AnalysisParser parser;

  private List<ModelValidatorBuilder> modelValidators;

  private List<ModelRefactoringBuilder> modelRefactorings;

  public static class Builder {

    private static final String INPUT_SEPARATOR = ",";

    private final Map<MergeParameter, String> _parameters = new HashMap<MergeParameter, String>();

    private List<String> _inputFiles;

    private List<ModelValidatorBuilder> _modelValidators;

    private List<ModelRefactoringBuilder> _modelRefactorings;

    private PrecedenceConfig _precedences = new PrecedenceConfig();

    private boolean CLI_Mode;

    public Builder() {
      this(false);
    }

    public Builder(final boolean isCLI) {
      this.CLI_Mode = isCLI;
      this._inputFiles = new ArrayList<String>();
      this._modelRefactorings = new ArrayList<ModelRefactoringBase.ModelRefactoringBuilder>();
      this._modelValidators = new ArrayList<ModelValidatorBase.ModelValidatorBuilder>();
    }

    /**
     * Adds and activates a boolean parameter (ON), value parameters are initialized with empty
     * value. Supports chaining by returning the builder
     *
     * @param param - the parameter to add to this config
     */
    public Builder withParam(MergeParameter param) {
      if (param.isBooleanParameter()) {
        this._parameters.put(param, MergeParameter.ON);
      }
      else {
        this._parameters.put(param, "");
      }
      return this;
    }

    /**
     * Adds a parameter with the provided value. If the parameter was already present, the
     * new value
     * will be concatenated with "," to the old Value. Supports chaining by returning the
     * builder
     *
     * @param param - the parameter to add to this config
     * @param value - the parameters value
     */
    public Builder withParam(MergeParameter param, String value) {
      if (this._parameters.containsKey(param)) {
        this._parameters.put(param, this._parameters.get(param) + INPUT_SEPARATOR + value);
      }
      else {
        this._parameters.put(param, value);
      }
      return this;
    }

    /**
     * Adds a Classdiagram File to the input
     */
    public Builder addInputFile(String fileName) {
      this._inputFiles.add(fileName);
      return this;
    }

    /**
     * Returns a copy of the configured parameters
     */
    public ImmutableMap<MergeParameter, String> getMergeParameters() {
      return ImmutableMap.copyOf(this._parameters);
    }

    /**
     * Returns true if the Parameter is specified
     *
     * @return
     */
    public boolean isDefinedParameter(MergeParameter param) {
      return this._parameters.keySet().contains(param);
    }

    /**
     * Provides default values to the configuration if not specified by user
     */
    private void completeDefaultConfig() {
      for (MergeParameter m : MergeParameter.values()) {
        if (!_parameters.containsKey(m) || _parameters.get(m).isEmpty()) {
          _parameters.put(m, m.getDefaultValue());
        }
      }

    }

    public void setPrecedences(PrecedenceConfig val) {
      if (val.conflictsPresent()) {
        throw new IllegalArgumentException("Please check your precedences for contradictions.");
      }
      else {
        _precedences = val;
      }
    }

    public Builder withCustomModelValidator(ModelValidatorBuilder modelValidator) {
      this._modelValidators.add(modelValidator);
      return this;
    }

    public Builder withCustomModelRefactoring(ModelRefactoringBuilder modelRefactoring) {
      this._modelRefactorings.add(modelRefactoring);
      return this;
    }

    /**
     * Fetches and parses the specified input models and creates the actual configuration
     */
    public CDMergeConfig build() {
      completeDefaultConfig();
      // Quick Logger settings for this class as the MergeTool Log
      // Framework is not used here
      if (_parameters.get(MergeParameter.LOG_DEBUG).equals(MergeParameter.ON)) {
        log.setLevel(Level.FINE);
      }
      else if (_parameters.get(MergeParameter.LOG_VERBOSE).equals(MergeParameter.ON)) {
        log.setLevel(Level.INFO);
      }
      else {
        log.setLevel(Level.WARNING);
      }

      checkParameterConsistency();

      if (!_parameters.get(MergeParameter.NO_INPUT_MODELS).equals(MergeParameter.ON)) {
        if (this._inputFiles.size() == 0) {

          // Collect the input models either via explicit files oder
          // via
          // resolving on model path
          String[] inputModels = _parameters.get(MergeParameter.INPUT_MODELS)
              .split(INPUT_SEPARATOR);
          if (inputModels.length >= 2) {
            _inputFiles = new ArrayList<String>();
            for (String modelFile : inputModels) {
              if (!Files.exists(Paths.get(modelFile).toAbsolutePath())) {
                log.finer("No valid class diagramm found for input file " + modelFile
                    + " will check Modelpath, too");
                modelFile = Paths.get(_parameters.get(MergeParameter.MODEL_PATH), modelFile)
                    .toAbsolutePath()
                    .toString();
                if (!Files.exists(Paths.get(modelFile))) {
                  log.severe("No valid class diagramm found for specified location " + modelFile);
                  continue;
                }
                else {
                  log.fine("Found cd in input file " + modelFile);
                  _inputFiles.add(modelFile);
                }
              }
            }
            if (_inputFiles.size() < 2) {
              throw new IllegalArgumentException(
                  "No valid or sufficient (at least 2) input models specified as explicit "
                      + "inputmodels");
            }
          }
          else {
            if (!_parameters.get(MergeParameter.MODEL_PATH).isEmpty()) {
              this._inputFiles = new ArrayList<>(
                  resolveInputCDsFilesFromPath(this._parameters.get(MergeParameter.MODEL_PATH)));
            }
            else {
              throw new IllegalArgumentException(
                  "No valid or sufficient (at least 2) input models specified either in a global "
                      + "model path or as explicit inputmodels");
            }
          }
        }
      }

      // Configure the preferences
      _precedences = new PrecedenceConfig();
      String[] precendeceValues = _parameters.get(MergeParameter.PRECEDENCE).split(INPUT_SEPARATOR);
      if (precendeceValues.length > 0) {
        for (String prec : precendeceValues) {
          _precedences.addPrecedence(prec);
          if (_precedences.conflictsPresent()) {
            throw new IllegalArgumentException("Conflicting precendece " + prec);
          }
        }
      }

      // Configure Model Refactoring
      if (!this.isDefinedParameter(MergeParameter.DISABLE_MODEL_REFACTORINGS) || !this.isOn(
          MergeParameter.DISABLE_POSTMERGE_VALIDATION)) {
        if (this._modelRefactorings.size() == 0) {
          this._modelRefactorings.add(new CleanAttributesInheritedFromSuperclass.Builder());
          this._modelRefactorings.add(new RemoveRedundantInterfaces.Builder());
        }
      }

      // Configure Model Validation
      if (!this.isDefinedParameter(MergeParameter.DISABLE_POSTMERGE_VALIDATION) || !this.isOn(
          MergeParameter.DISABLE_POSTMERGE_VALIDATION)) {
        if (this._modelValidators.size() == 0) {
          this._modelValidators.add(new AssociationChecker.Builder());
          this._modelValidators.add(new AttributeChecker.Builder());
        }
      }

      return new CDMergeConfig(this);
    }

    /**
     * Resolves input class diagram models within the provided modelPath
     *
     * @param modelPath the modelPath to inspect
     * @return list of loaded input CDs
     */
    public List<String> resolveInputCDsFilesFromPath(String modelPath) {
      List<String> resolvedCDs = new ArrayList<String>();
      Collection<File> files = de.se_rwth.commons.Directories.listFilesRecursivly(
          new File(modelPath), "*.cd");
      if (files != null && !files.isEmpty()) {
        for (File cdFile : files) {
          resolvedCDs.add(cdFile.getAbsolutePath());

        }
      }
      return resolvedCDs;
    }

    private void checkParameterConsistency() {
      if (isOn(MergeParameter.LOG_SILENT) && (isOn(MergeParameter.LOG_DEBUG) || isOn(
          MergeParameter.LOG_VERBOSE))) {
        throw new InvalidParameterException(
            "Can't activate " + MergeParameter.LOG_SILENT + " with " + MergeParameter.LOG_VERBOSE
                + " or " + MergeParameter.LOG_DEBUG);
      }

      if (!isOn(MergeParameter.CHECK_ONLY) && isOn(MergeParameter.SAVE_RESULT_TO_FILE) && !hasValue(
          MergeParameter.OUTPUT_PATH)) {
        throw new InvalidParameterException("Missing output path!");
      }

      if (isOn(MergeParameter.PRIMITIVE_TYPE_CONVERSION) && isOn(MergeParameter.STRICT)) {
        System.out.println(
            "Invalid paramater combination: " + MergeParameter.PRIMITIVE_TYPE_CONVERSION
                + " cannot be used together with " + MergeParameter.STRICT);
        return;
      }

      // TODO: More checks to Come...
    }

    private boolean isOn(MergeParameter p) {
      return _parameters.get(p).equals(MergeParameter.ON);
    }

    private boolean hasValue(MergeParameter p) {
      return !_parameters.get(p).isEmpty();
    }

  }

  /**
   * Constructor for de.monticore.umlcd4a.composer.merging.CDMergeConfig
   *
   * @param builder
   */
  private CDMergeConfig(Builder builder) {
    parameters = builder.getMergeParameters();
    this.modelRefactorings = new ArrayList<ModelRefactoringBase.ModelRefactoringBuilder>();
    this.modelRefactorings.addAll(builder._modelRefactorings);
    this.modelValidators = new ArrayList<ModelValidatorBase.ModelValidatorBuilder>();
    this.modelValidators.addAll(builder._modelValidators);
    MCLoggerWrapper.init(getMinimalLogable(), isMCLogSilent());
    precedences = builder._precedences;
    if (!isEnabled(MergeParameter.NO_INPUT_MODELS)) {
      try {
        loadCDs(builder._inputFiles);
      }
      catch (IOException ex) {
        throw new RuntimeException("Unable to load input models " + ex.getMessage());
      }
    }
    this.CLI_MODE = builder.CLI_Mode;
  }

  public void loadCDsFromPaths(List<Path> inputFiles) {
    Preconditions.checkArgument(inputFiles.size() > 1,
        "At least 2 Class Diagrams ar required to run CD Merge");
    this.inputCDs = new ArrayList<>(inputFiles.size());
    Optional<ASTCDCompilationUnit> cd;
    for (Path file : inputFiles) {
      cd = parseCDFile(file.toAbsolutePath().toString());
      if (!cd.isPresent()) {
        throw new RuntimeException("No valid classdiagram found in " + file);
      }
      this.inputCDs.add(cd.get());
    }
  }

  public void loadCDs(List<String> inputFiles) throws IOException {
    Preconditions.checkArgument(inputFiles.size() > 1,
        "At least 2 Class Diagrams ar required to run CD Merge");
    this.inputCDs = new ArrayList<>(inputFiles.size());
    Optional<ASTCDCompilationUnit> cd;
    for (String file : inputFiles) {
      try {
        cd = parseCDFile(file);
      }
      catch (RuntimeException e) {
        //Catch and Reformat Monticore Runtime Exceptions for gentle program exit
        throw new RuntimeException(
            "Issues while processing input model: " + file + " Reason: " + e.getMessage(), e);
      }
      if (!cd.isPresent()) {
        throw new RuntimeException("No valid class diagram found in " + file);
      }
      this.inputCDs.add(cd.get());
    }
  }

  public void setInputCDs(List<ASTCDCompilationUnit> inputCDs) {
    Preconditions.checkArgument(inputCDs.size() > 1,
        "At least 2 Class Diagrams ar required to run CD Merge");
    this.inputCDs = new ArrayList<>(inputCDs);

  }

  private Optional<ASTCDCompilationUnit> parseCDFile(String modelfile) {
    try {
      CD4AnalysisMill.reset();
      CD4AnalysisMill.init();
      final ICD4AnalysisGlobalScope globalScope = CD4AnalysisMill.globalScope();
      globalScope.clear();
      BuiltInTypes.addBuiltInTypes(globalScope);
      return CDUtils.parseCDFile(modelfile, true);
    }
    catch (IOException e) {
      Log.error("Unable to parse modelfile " + modelfile + ". " + e.getMessage());
    }
    return Optional.empty();
  }

  public ImmutableList<ASTCDCompilationUnit> getInputCDs() {
    return ImmutableList.copyOf(inputCDs);
  }

  public String getModelPath() {
    return this.parameters.get(MergeParameter.MODEL_PATH);
  }

  public String getOutputName() {
    return this.parameters.get(MergeParameter.OUTPUT_NAME);
  }

  public String getOutputPath() {
    return this.parameters.get(MergeParameter.OUTPUT_PATH);
  }

  public String getTargetPackage() {
    return this.parameters.get(MergeParameter.TARGET_PACKAGE);
  }

  public boolean allowHeterogeneousMerge() {
    return this.parameters.get(MergeParameter.MERGE_HETEROGENOUS_TYPES).equals(MergeParameter.ON);
  }

  public boolean isVerbose() {
    return this.parameters.get(MergeParameter.LOG_VERBOSE).equals(MergeParameter.ON) || isDebug();
  }

  public boolean isDebug() {
    return this.parameters.get(MergeParameter.LOG_DEBUG).equals(MergeParameter.ON);
  }

  public boolean isSilent() {
    return this.parameters.get(MergeParameter.LOG_SILENT).equals(MergeParameter.ON);
  }

  public boolean isLogToStdErr() {
    return this.parameters.get(MergeParameter.LOG_STDERR).equals(MergeParameter.ON);
  }

  public boolean isMCLogSilent() {
    return this.parameters.get(MergeParameter.MS_LOGGER_SILENT).equals(MergeParameter.ON);
  }

  public boolean disabledCheckCoCo() {
    return this.parameters.get(MergeParameter.DISABLE_CONTEXT_CONDITIONS).equals(MergeParameter.ON);
  }

  public boolean disabledPostMergeValidation() {
    return this.parameters.get(MergeParameter.DISABLE_POSTMERGE_VALIDATION)
        .equals(MergeParameter.ON);
  }

  public boolean isFailFast() {
    return this.parameters.get(MergeParameter.FAIL_FAST).equals(MergeParameter.ON);
  }

  public boolean mergeOnlyNamedAssociations() {
    return
        this.parameters.get(MergeParameter.MERGE_ONLY_NAMED_ASSOCIATIONS).equals(MergeParameter.ON)
            || isStrict();
  }

  public boolean allowPrimitiveTypeConversion() {
    return this.parameters.get(MergeParameter.PRIMITIVE_TYPE_CONVERSION).equals(MergeParameter.ON);
  }

  public boolean assertAssociativity() {
    return this.parameters.get(MergeParameter.ASSERT_ASSOCIATIVITY).equals(MergeParameter.ON);
  }

  public boolean checkOnly() {
    return this.parameters.get(MergeParameter.CHECK_ONLY).equals(MergeParameter.ON);
  }

  public boolean cancelOnWarnings() {
    return this.parameters.get(MergeParameter.WARNINGS_AS_ERRORS).equals(MergeParameter.ON)
        || isStrict();
  }

  public boolean isStrict() {
    return this.parameters.get(MergeParameter.STRICT).equals(MergeParameter.ON);
  }

  public boolean mergeComments() {
    return this.parameters.get(MergeParameter.MERGE_COMMENTS).equals(MergeParameter.ON);
  }

  public boolean printToFile() {
    return this.parameters.get(MergeParameter.SAVE_RESULT_TO_FILE).equals(MergeParameter.ON);
  }

  public boolean printIntermediateToFile() {
    return this.parameters.get(MergeParameter.SAVE_INTERMEDIATE_RESULT_TO_FILE)
        .equals(MergeParameter.ON);
  }

  public boolean disabledModelRefactorings() {
    return this.parameters.get(MergeParameter.DISABLE_MODEL_REFACTORINGS).equals(MergeParameter.ON);
  }

  public boolean noInputModels() {
    return this.parameters.get(MergeParameter.NO_INPUT_MODELS).equals(MergeParameter.ON);
  }

  public Optional<String> getValue(MergeParameter param) {
    return Optional.of(this.parameters.get(param));
  }

  /**
   * Returns true if the parameter param is a boolean parameter and if it is enabled, throws an
   * {@link UnsupportedOperationException} if the method is called on a non boolean value
   */
  public boolean isEnabled(MergeParameter param) {
    if (param.isBooleanParameter()) {
      return this.parameters.get(param).equals(MergeParameter.ON);
    }
    else {
      throw new UnsupportedOperationException("The specified parameter '" + param.toString()
          + "' is a non booelan parameter! Call String getValue() instead!");
    }
  }

  public PrecedenceConfig getPrecedences() {
    return precedences;
  }

  /**
   * @return cLI_MODE
   */
  public boolean isCLI_MODE() {
    return CLI_MODE;
  }

  /**
   * Returns the minimal logable level
   *
   * @return - the minimal log level
   */
  public ErrorLevel getMinimalLogable() {
    if (isDebug()) {
      return ErrorLevel.DEBUG;
    }
    else if (isVerbose()) {
      return ErrorLevel.FINE;
    }
    else if (isSilent()) {
      return ErrorLevel.WARNING;
    }
    return ErrorLevel.INFO;
  }

  public boolean isTraceEnabled() {
    return (this.parameters.get(MergeParameter.LOG_TO_CONSOLE).equals(MergeParameter.ON)
        || isCLI_MODE());
  }

  public List<ModelRefactoringBuilder> getModelRefactorings() {
    return this.modelRefactorings;
  }

  public List<ModelValidatorBuilder> getModelValidators() {
    return this.modelValidators;
  }

}
