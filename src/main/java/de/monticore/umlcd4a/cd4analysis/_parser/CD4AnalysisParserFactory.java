/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cd4analysis._parser;

/**
 * This parser factory is handwritten, because for compilation units we want a
 * handwritten parser that also checks that a filename equals the model name.
 *
 * @author Robert Heim
 */
public class CD4AnalysisParserFactory extends CD4AnalysisParserFactoryTOP {
  
  public static CDCompilationUnitMCParser createCDCompilationUnitMCParser() {
    if (!(factory instanceof CD4AnalysisParserFactory)) {
      factory = new CD4AnalysisParserFactory();
    }
    return factory.doCreateCDCompilationUnitMCParser();
  }
  
  /**
   * Creates a {@link CDCompilationUnitMCParserCheckFilename}.
   */
  protected CDCompilationUnitMCParser doCreateCDCompilationUnitMCParser() {
    return new CDCompilationUnitMCParserCheckFilename();
  }
}
