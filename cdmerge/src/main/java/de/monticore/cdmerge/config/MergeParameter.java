/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.config;

import java.util.Optional;

/**
 * Parameters allowing to configure the merging process. There are two kinds of Parameters: Boolean
 * which can only set to Disabled/Enabled and Non-Boolean which can accept arbitrary string values
 */
public enum MergeParameter {

  /* =============== ENUM CONSTANTS ================= */

  /* ================================================ */
  /* === PARAMETERS WITH ARBRITRARY STRING VALUES === */
  /* ================================================ */
  /** The basis Path for the models Default: OPTIONAL */
  MODEL_PATH("modelPath", "mp", "", false),

  /** The name of the merged class diagram and the output file + ".cd" Default: "mergeResult" */
  OUTPUT_NAME("outputName", "on", "mergeResult", false),

  /**
   * The path to store the merged class model, if omitted, the model will be printed out to standard
   * output Default: Current Working Directory
   */
  OUTPUT_PATH("outputPath", "op", "", false),

  /** Allows to define override scenarios with the provided prefix path Default: OFF */
  PRECEDENCE("precedence", "prec", "", false),

  /**
   * Blank separated list of input paths for each source model. Each Will be combined with model
   * path if specified Default: REQUIRED
   */
  INPUT_MODELS("inputModels", "m", "", false),

  /** The model package declaration for the target model. Default: EMPTY */
  // FIXME Ignore Package
  TARGET_PACKAGE("targetPackage", "tp", "", false),

  /* ====================================== */
  /* === PARAMETERS WITH BOOLEAN VALUES === */
  /* ====================================== */

  /**
   * * The input models will be checked for associativity stability (i.e. input order). This test
   * performs several mergers and slows down the merging process but gives indication on
   * inconsistencies. Default: OFF
   */
  ASSERT_ASSOCIATIVITY("assertAssociativity", "aa", MergeParameter.OFF, true),

  /**
   * The merger is simulated for consistency checking - no output model will be produced Default:
   * OFF
   */
  CHECK_ONLY("checkOnly", "c", MergeParameter.OFF, true),

  /**
   * Disables the check of CD4Analyis Class Diagram Context Conditions for the input class diagrams
   * and the merged class model. Used e.g. for testing Default: OFF
   */
  DISABLE_CONTEXT_CONDITIONS("disableCD4AContextConditions", "dcoco", MergeParameter.OFF, true),

  /**
   * Disables the post merging validation of the resulting class diagram. Includes
   * DISABLE_CONTEXT_CONDITIONS Default: OFF
   */
  DISABLE_POSTMERGE_VALIDATION("disablePostValidation", "dvalid", MergeParameter.OFF, true),

  /** Disable clean up and post merge refactorings, Default: OFF */
  DISABLE_MODEL_REFACTORINGS("disableModelRefactorings", "drefactor", MergeParameter.OFF, true),

  /** Abort merging process on first error Default: OFF */
  FAIL_FAST("failFast", "ff", MergeParameter.OFF, true),

  /** Abort merge if association match is ambiguous, Default: OFF */
  FAIL_AMBIGUOUS("failAmbiguous", "fa", MergeParameter.OFF, true),

  /**
   * Produces (a lot of) debug output (Level DEBUG, FINE, INFO) during matching and merging - only
   * useful for testing / debugging scenarios
   */
  LOG_DEBUG("debug", "d", MergeParameter.OFF, true),

  /** Logs more information (Level FINE and INFO) and reports each merged element Default: OFF */
  LOG_VERBOSE("verbose", "v", MergeParameter.OFF, true),

  /** Only Reports/Logs Level ERROR Default: OFF */
  LOG_SILENT("silent", "ls", MergeParameter.OFF, true),

  /** Write all Log entries immediately to Standard Output */
  LOG_TO_CONSOLE("logConsole", "lc", MergeParameter.OFF, true),

  /** Propagates Log entries to Monticore Logging framework (se-commons) Default: OFF */
  // FIXME not implemented
  PROPAGATE_LOG("propagateLog", "plog", MergeParameter.OFF, true),

  /** Precvent MC to log direcltly to Standard Output/Standard Err. Default: Activated */
  MS_LOGGER_SILENT("MCLoggerSilent", "noMClog", MergeParameter.ON, true),

  /** Merge all comments of all model elements from the source diagrams Default: OFF */
  MERGE_COMMENTS("mergeComments", "mc", MergeParameter.OFF, true),

  /**
   * Only merge associations with defined association name. Guarantees associativity of the merge
   * operation Default: OFF
   */
  MERGE_ONLY_NAMED_ASSOCIATIONS("mergeOnlyNamedAssociations", "mona", MergeParameter.OFF, true),

  /** Allows the merger of classes with abstract classes and interfaces Default: OFF */
  MERGE_HETEROGENOUS_TYPES("mergeHeterogenousTypes", "mht", MergeParameter.OFF, true),

  /** Enables the merging of compatible primitive type attributes like int and long Default: OFF */
  PRIMITIVE_TYPE_CONVERSION("allowPrimitiveTypeConversion", "ptc", MergeParameter.OFF, true),

  /** Prints the result merged diagram to the file. Default: OFF */
  SAVE_INTERMEDIATE_RESULT_TO_FILE("saveIntermediateToFile", "savint", MergeParameter.OFF, true),

  /** Prints the result merged diagram to the file. Default: OFF */
  SAVE_RESULT_TO_FILE("saveResultToFile", "f", MergeParameter.OFF, true),

  /** Combination of the parameters MERGE_ONLY_NAMED_ASSOCIATIONS, WARNING_AS_ERRORS Default: OFF */
  STRICT("strict", "str", MergeParameter.OFF, true),

  /** Treat warnings as errors (i.e. cancel merge process on each warning) Default: OFF */
  WARNINGS_AS_ERRORS("warningsAsErrors", "wa", MergeParameter.OFF, true),

  /**
   * Instead of specifying input files, CDs are added as ASTCDCompilationUnit via CDMerge
   * .addInputAST
   */
  AST_BASED("astBased", "asts", MergeParameter.OFF, true),

  /**
   * Used For Testing purposes only. The Merge Tool will be setup up with no input models configured
   * up front
   */
  NO_INPUT_MODELS("noInputModels", "nim", MergeParameter.OFF, true),

  /**
   * Only for CLI mode: Stay silent on standard output and defer all logging to standard error.
   * Default: OFF
   */
  LOG_STDERR("logStdErr", "lserr", MergeParameter.OFF, true);

  /* ============================================== */
  /* ========== FIELDS, CONSTRUCTOR, METHODS ====== */
  /* ============================================== */

  /** ON-Value for boolean-Parameters */
  public static final String ON = "ON";

  /** OFF-Value for boolean-Parameters */
  public static final String OFF = "OFF";

  /**
   * The name of the parameter for the command-line, preferably used in scripts for better
   * understanding
   */
  private final String cliParameter;

  /**
   * A Shortcut for the command-line interface parameter - used for experiences users in interactive
   * mode prefix with "-"
   */
  private final String shortcut;

  /** The user defined value for non-boolean-Parameters */
  private final String defaultValue;

  /** Indicates if this Parameter just accepts the Values ON and OFF */
  private final boolean onlyBoolean;

  /**
   * Constructor for de.monticore.umlcd4a.composer.merging.MergeParameter
   *
   * @param cliParameter The long parameter identifier for command line parameters
   * @param shortcut The shortcut for command line parameters
   */
  MergeParameter(String cliParameter, String shortcut, String defaultValue, boolean onlyBoolean) {
    this.cliParameter = cliParameter;
    this.shortcut = shortcut;
    this.defaultValue = defaultValue;
    this.onlyBoolean = onlyBoolean;
  }

  public static Optional<MergeParameter> getByShortcut(String shortcut) {
    for (MergeParameter param : MergeParameter.values()) {
      if (param.getShortcut().equalsIgnoreCase(shortcut)) {
        return Optional.of(param);
      }
    }
    return Optional.empty();
  }

  /**
   * Returns the default value for this Parameter
   *
   * @return the value of the parameter. For boolean Parameters ON and OFF but its recommended to
   *     use isEnabled() for boolean Parameters instead
   */
  public String getDefaultValue() {
    return this.defaultValue;
  }

  /**
   * Returns true if this parameter is enabled by default, false if disabled. Throws a Runtime
   * Exception if this parameter is a non boolean parameter
   *
   * @return true id enabled, false if disabled
   */
  public boolean isEnabledByDefault() {
    if (this.onlyBoolean) {
      return this.defaultValue.equals(ON);
    }
    throw new UnsupportedOperationException("The Parameter " + this + " is not a boolean Value");
  }

  /**
   * Return true if this is a boolean Parameter, false if it can accept arbitrary values
   *
   * @return true if this is a boolean Parameter
   */
  public boolean isBooleanParameter() {
    return this.onlyBoolean;
  }

  /**
   * Returns the Parameter by the provided command line name
   *
   * @param cliParameter name of the parameter
   * @return - the parameter or empty if non matching parameter was found
   */
  public static Optional<MergeParameter> getByCLIParameter(String cliParameter) {
    for (MergeParameter param : MergeParameter.values()) {
      if (param.getCLIParameter().equalsIgnoreCase(cliParameter)) {
        return Optional.of(param);
      }
    }
    return Optional.empty();
  }

  /**
   * Returns the short command line shortcut for this parameter
   *
   * @return the shortcut
   */
  public String getShortcut() {
    return this.shortcut;
  }

  /**
   * Returns the command line shortcut for this parameter
   *
   * @return the command line parameter
   */
  public String getCLIParameter() {
    return this.cliParameter;
  }

  @Override
  public String toString() {
    return getCLIParameter() + "(" + getShortcut() + ") DEF: " + this.defaultValue;
  }
}
