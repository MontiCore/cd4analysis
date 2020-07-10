/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cli;

import com.google.common.collect.Multimap;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.configuration.DelegatingConfigurationContributor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CDCLIConfiguration implements Configuration {

  protected Configuration configuration;

  private CDCLIConfiguration(CLIArguments arguments) {
    init(ConfigurationPropertiesMapContributor.fromSplitMap(arguments.asMap()));
  }

  private CDCLIConfiguration(Multimap<String, String> arguments) {
    init(ConfigurationPropertiesMapContributor.fromSplitMap(arguments));
  }

  private CDCLIConfiguration(Map<String, Iterable<String>> arguments) {
    init(ConfigurationPropertiesMapContributor.fromSplitMap(arguments));
  }

  public static CDCLIConfiguration fromArguments(CLIArguments arguments) {
    return new CDCLIConfiguration(arguments);
  }

  public static CDCLIConfiguration fromMap(Multimap<String, String> arguments) {
    return new CDCLIConfiguration(arguments);
  }

  public static CDCLIConfiguration fromMap(Map<String, Iterable<String>> arguments) {
    return new CDCLIConfiguration(arguments);
  }

  public static <T> Optional<T> optionalAlternative(Optional<T> orig, Optional<T> alt) {
    if (orig.isPresent()) {
      return orig;
    }
    else {
      return alt;
    }
  }

  protected void init(Configuration internal) {
    this.configuration = ConfigurationContributorChainBuilder
        .newChain()
        .add(DelegatingConfigurationContributor.
            with(internal))
        .build();
  }

  /**
   * Getter for the value of the help option.
   *
   * @return whether help should be shown
   */
  public boolean isSetHelp() {
    return hasProperty(Options.HELP.toString()) || hasProperty(Options.HELP_SHORT.toString());
  }

  /**
   * Getter for the value of the no-builtin-types option.
   *
   * @return whether the builtin-types should be used
   */
  public boolean isSetNoBuiltInTypes() {
    return hasProperty(Options.NO_BUILTIN_TYPES.toString()) || hasProperty(Options.NO_BUILTIN_TYPES_SHORT.toString());
  }

  public boolean useBuiltInTypes() {
    return !isSetNoBuiltInTypes();
  }

  public boolean isPresentModelFile() {
    return optionalAlternative(getAsString(Options.MODEL_FILE.toString()), getAsString(Options.MODEL_FILE_SHORT.toString())).isPresent();
  }

  public Optional<String> getModelFile() {
    return optionalAlternative(getAsString(Options.MODEL_FILE.toString()), getAsString(Options.MODEL_FILE_SHORT.toString()));
  }

  public boolean isSetFailQuick() {
    return hasProperty(Options.NO_FAIL_QUICK.toString()) || hasProperty(Options.NO_FAIL_QUICK_SHORT.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValues()
   */
  @Override
  public Map<String, Object> getAllValues() {
    return this.configuration.getAllValues();
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValuesAsStrings()
   */
  @Override
  public Map<String, String> getAllValuesAsStrings() {
    return this.configuration.getAllValuesAsStrings();
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBoolean(String)
   */
  @Override
  public Optional<Boolean> getAsBoolean(String s) {
    return this.configuration.getAsBoolean(s);
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBooleans(String)
   */
  @Override
  public Optional<List<Boolean>> getAsBooleans(String s) {
    return this.configuration.getAsBooleans(s);
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDouble(String)
   */
  @Override
  public Optional<Double> getAsDouble(String s) {
    return this.configuration.getAsDouble(s);
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDoubles(String)
   */
  @Override
  public Optional<List<Double>> getAsDoubles(String s) {
    return this.configuration.getAsDoubles(s);
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsInteger(String)
   */
  @Override
  public Optional<Integer> getAsInteger(String s) {
    return this.configuration.getAsInteger(s);
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsIntegers(String)
   */
  @Override
  public Optional<List<Integer>> getAsIntegers(String s) {
    return this.configuration.getAsIntegers(s);
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsString(String)
   */
  @Override
  public Optional<String> getAsString(String s) {
    return this.configuration.getAsString(s);
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsStrings(String)
   */
  @Override
  public Optional<List<String>> getAsStrings(String s) {
    return this.configuration.getAsStrings(s);
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValue(String)
   */
  @Override
  public Optional<Object> getValue(String s) {
    return this.configuration.getValue(s);
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValues(String)
   */
  @Override
  public Optional<List<Object>> getValues(String s) {
    return this.configuration.getValues(s);
  }

  /**
   * @return whether the given key is contained in this configuration
   */
  public boolean hasProperty(String key) {
    return this.configuration.hasProperty(key);
  }

  /**
   * @return whether the given key is contained in this configuration
   */
  public boolean hasProperty(Enum<?> key) {
    return hasProperty(key.toString());
  }

  /**
   * Provides access to the internally used Configuration.
   *
   * @return
   */
  Configuration getInternal() {
    return this.configuration;
  }

  public enum Options {

    HELP("help"), HELP_SHORT("h"),
    NO_BUILTIN_TYPES("no-builtin-types"), NO_BUILTIN_TYPES_SHORT("t"),
    MODEL_FILE("model"), MODEL_FILE_SHORT("m"),
    NO_FAIL_QUICK("no-fail-quick"), NO_FAIL_QUICK_SHORT("q");

    String name;

    Options(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

}
