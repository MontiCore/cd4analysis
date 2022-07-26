package de.monticore.syntaxdiff;

import de.monticore.alloycddiff.CDSemantics;
import de.monticore.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.alloycddiff.classDifference.AlloyCDDiff;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.ow2cw.ReductionTrafo;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Paths;
import java.util.Optional;

public class SemDiff {
  //Todo: Error with minimal associations, assoc name is concatenated with left side
  // MCQualifiedType resulting wrong typs
  public SemDiff(ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2) {
    //FileUtils.forceDelete();
    String outputPath = "target/git-diff";
    //CD4CodeMill.globalScope().clear();
    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(cd1, cd2);
    //CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    //cd1.accept(pp.getTraverser());
    //ICD4CodeArtifactScope test = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);
    //System.out.println(test.resolveCDTypeDown("Object4Analysis").get().getFullName());

    //System.out.println(pp.prettyprint(cd1));
    int cd1size = cd1.getCDDefinition().getCDClassesList().size() + cd1.getCDDefinition()
        .getCDInterfacesList()
        .size();

    int cd2size = cd2.getCDDefinition().getCDClassesList().size() + cd2.getCDDefinition()
        .getCDInterfacesList()
        .size();
    int diffsizeSem = Math.max(20, 2 * Math.max(cd1size, cd2size));
    //saveDiffCDs2File(cd1, cd2, outputPath);
    Optional<AlloyDiffSolution> optS = AlloyCDDiff.cddiff(cd1, cd2, diffsizeSem,
        CDSemantics.MULTI_INSTANCE_CLOSED_WORLD, outputPath);

    // test if solution is present
    if (!optS.isPresent()) {
      Log.error("0xCDD01: Could not compute semdiff.");
      return;
    }
    AlloyDiffSolution sol = optS.get();

    // limit number of generated diff-witnesses
    sol.setSolutionLimit(1);
    sol.setLimited(true);

    // generate diff-witnesses in outputPath
    sol.generateSolutionsToPath(Paths.get(outputPath));
    /*
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2);

    ICD4CodeArtifactScope scopeCD1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);
    ICD4CodeArtifactScope scopeCD2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2);
   */
  }

}
