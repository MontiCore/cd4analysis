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
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$, $Date$
 * @since   TODO: add version number
 *
 */
public class CD4AnalysisParser extends CD4AnalysisParserTOP {
  
  /**
   * Besides parsing, this also checks that the filename equals the model name.
   * 
   * @see de.monticore.umlcd4a.cd4analysis._parser.CDCompilationUnitMCParser#parse(java.lang.String)
   */
  @Override
  public Optional<de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit> parseCDCompilationUnit(String filename)
      throws IOException, RecognitionException {
    Optional<de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit> ast = super
        .parseCDCompilationUnit(filename);
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
