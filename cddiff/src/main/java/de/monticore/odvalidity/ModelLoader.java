/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.od4report._parser.OD4ReportParser;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

public class ModelLoader {
  
  protected Optional<ASTCDCompilationUnit> loadCDModel(File cdModel) throws FileNotFoundException {
    
    if (cdModel.exists() && cdModel.isFile()) {
      CD4AnalysisParser parser = new CD4AnalysisParser();
      Optional<ASTCDCompilationUnit> cdAST = parser.parse(cdModel.getPath());
      if (parser.hasErrors()) {
        Log.error("Model parsed with errors. Model path: " + cdModel.getPath());
      }
      
      cdAST.ifPresent(CDDiffUtil::refreshSymbolTable);
      return cdAST;
      
    }
    else {
      Log.error("No File found using path: " + cdModel.getPath());
      throw new FileNotFoundException();
    }
  }
  
  protected Optional<ASTODArtifact> loadODModel(File odModel) throws FileNotFoundException {
    
    if (odModel.exists() && odModel.isFile()) {
      OD4ReportParser parser = new OD4ReportParser();
      Optional<ASTODArtifact> odAST = parser.parse(odModel.getPath());
      if (parser.hasErrors()) {
        Log.error("Model parsed with errors. Model path: " + odModel.getPath());
      }
      
      return odAST;
      
    }
    else {
      Log.error("No File found using path: " + odModel.getPath());
      throw new FileNotFoundException();
    }
  }
  
}
