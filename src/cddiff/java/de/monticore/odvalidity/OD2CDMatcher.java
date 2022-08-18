package de.monticore.odvalidity;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

public class OD2CDMatcher {

  protected final MCCollectionTypesFullPrettyPrinter typesPrinter =
      new MCCollectionTypesFullPrettyPrinter(new IndentPrinter());

  protected final ClassMatcher classMatcher = new ClassMatcher(typesPrinter);

  protected final AssociationsMatcher associationsMatcher =
      new AssociationsMatcher(typesPrinter);

  /**
   *  Returns true if the od is within the baseCD semantics but not within the compareCD semantics.
   *  Returns false otherwise.
   */
  public boolean checkIfDiffWitness(CDSemantics semantics, File baseCDFile,
      File compareCDFile, File odFile ) throws FileNotFoundException {
    ModelLoader loader = new ModelLoader();

    Optional<ASTCDCompilationUnit> base = loader.loadCDModel(baseCDFile);
    Optional<ASTCDCompilationUnit> compare = loader.loadCDModel(compareCDFile);
    Optional<ASTODArtifact> odOpt = loader.loadODModel(odFile);

    if(base.isPresent() && compare.isPresent() && odOpt.isPresent()){
      ASTCDCompilationUnit baseCD = base.get();
      ASTCDCompilationUnit compareCD = compare.get();
      ASTODArtifact od = odOpt.get();

      if(Semantic.isMultiInstance(semantics)){
        return new MultiInstanceMatcher(this).isDiffWitness( semantics, baseCD, compareCD, od);
      } else {
        return checkODValidity(semantics, od, baseCD) && !checkODValidity(semantics, od, compareCD);
      }
    } else {
      Log.error("A model file could not be loaded.");
      return false;
    }
  }

  /**
   * Single Instance Check - Files given
   */
  public boolean checkODValidity(CDSemantics semantic, File cdFile, File odFile) {
    ModelLoader loader = new ModelLoader();
    try {
      return checkODValidity(semantic, loader.loadODModel(odFile).get(),
          loader.loadCDModel(cdFile).get());
    }
    catch (Exception e) {
      //Doesn't matter which exception ... check fails
      return false;
    }
  }

  /**
   * Single Instance Check - AST given
   */
  public boolean checkODValidity(CDSemantics semantic, ASTODArtifact od,
      ASTCDCompilationUnit cd) {

    Log.println(String.format("[CHECK] Check if %s permits %s.",cd.getCDDefinition().getName(),
            od.getObjectDiagram().getName()));

    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);

    //Check all objects from OD if they can exist in the CD
    if (classMatcher.checkAllObjectsInClassDiagram(od, cd, semantic) && associationsMatcher.checkAssociations(od.deepClone().getObjectDiagram(), cd, semantic)) {
      Log.println(String.format("[RESULT] %s permits %s.",cd.getCDDefinition().getName(),
          od.getObjectDiagram().getName()));
      return true;
    }

    Log.println(String.format("[RESULT] %s does not permit %s.",cd.getCDDefinition().getName(),
        od.getObjectDiagram().getName()));
    return false;
  }

}
