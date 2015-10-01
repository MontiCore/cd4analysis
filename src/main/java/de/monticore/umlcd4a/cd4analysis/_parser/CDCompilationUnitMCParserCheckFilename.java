/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

package de.monticore.umlcd4a.cd4analysis._parser;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import com.google.common.io.Files;

import de.se_rwth.commons.logging.Log;

/**
 * This parser is handwritten, because it also checks that a filename euqals the
 * model name.
 * 
 * @author Robert Heim
 */
public class CDCompilationUnitMCParserCheckFilename extends CDCompilationUnitMCParser {
  /**
   * Besides parsing, this also checks that the filename equals the model name.
   * 
   * @see de.monticore.umlcd4a.cd4analysis._parser.CDCompilationUnitMCParser#parse(java.lang.String)
   */
  @Override
  public Optional<de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit> parse(String filename)
      throws IOException, RecognitionException {
    Optional<de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit> ast = super
        .parse(filename);
    if (ast.isPresent()) {
      String simpleFileName = Files.getNameWithoutExtension(filename);
      String modelName = ast.get().getCDDefinition().getName();
      if (!modelName.equals(simpleFileName))
        Log.error("0xC4A02 The name of the diagram " + modelName
            + " is not identical to the name of the file " + filename
            + " (without its fileextension).");
    }
    return ast;
  }
}
