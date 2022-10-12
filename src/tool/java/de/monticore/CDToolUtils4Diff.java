package de.monticore;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cddiff.CDDiff;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CDToolUtils4Diff {

  protected static void computeSyntax2SemDiff(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2,
      String outputPath, boolean openWorld, boolean toDir)
      throws NumberFormatException, IOException {
    CDSemantics semantics = CDSemantics.SIMPLE_CLOSED_WORLD;

    // determine if open-world should be applied
    if (openWorld) {

      CD4CodeMill.globalScope().clear();
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(ast1, ast2);

      if (toDir) {
        CDToolUtils4Diff.saveDiffCDs2File(ast1, ast2, outputPath);
      }
      semantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
    }

    if (toDir) {
      CDDiff.printODs2Dir(CDDiff.computeSyntax2SemDiff(ast1, ast2, semantics), outputPath);
    }
    else {
      Log.print(
          CDDiff.printWitnesses2stdout(CDDiff.computeSyntax2SemDiff(ast1, ast2, semantics)));
    }

  }

  protected static void computeAlloySemDiff(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2,
      String outputPath, int diffsize, int difflimit, boolean openWorld, boolean reductionBased,
      boolean toDir) throws NumberFormatException, IOException {

    CDSemantics semantics = CDSemantics.SIMPLE_CLOSED_WORLD;

    // determine if open-world should be applied
    if (openWorld) {

      // determine which method should be used to compute the diff-witnesses
      if (reductionBased) {
        CD4CodeMill.globalScope().clear();
        ReductionTrafo trafo = new ReductionTrafo();
        trafo.transform(ast1, ast2);

        CDToolUtils4Diff.saveDiffCDs2File(ast1, ast2, outputPath);
        semantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
      }
      else {

        semantics = CDSemantics.MULTI_INSTANCE_OPEN_WORLD;

        // handle unspecified association directions for open-world
        ReductionTrafo.handleAssocDirections(ast1, ast2);

        // add subclasses to interfaces and abstract classes
        ReductionTrafo.addSubClasses4Diff(ast1);
        ReductionTrafo.addSubClasses4Diff(ast2);

        // add dummy-class for associations
        String dummyClassName = "Dummy4Diff";
        ReductionTrafo.addDummyClass4Associations(ast1, dummyClassName);
        ReductionTrafo.addDummyClass4Associations(ast2, dummyClassName);
      }
    }
    else {
      //handle unspecified association directions for closed-world
      ReductionTrafo.handleAssocDirections(ast1, ast2);
    }

    if (toDir) {
      CDDiff.printODs2Dir(CDDiff.computeAlloySemDiff(ast1, ast2, diffsize, difflimit, semantics),
          outputPath);
    }
    else {
      Log.print(CDDiff.printWitnesses2stdout(
          CDDiff.computeAlloySemDiff(ast1, ast2, diffsize, difflimit, semantics)));
    }
  }

  protected static int getDefaultDiffsize(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2) {
    int diffsize;
    int cd1size = ast1.getCDDefinition().getCDClassesList().size() + ast1.getCDDefinition()
        .getCDInterfacesList()
        .size();

    int cd2size = ast2.getCDDefinition().getCDClassesList().size() + ast2.getCDDefinition()
        .getCDInterfacesList()
        .size();

    diffsize = Math.max(20, 2 * Math.max(cd1size, cd2size));
    return diffsize;
  }

  protected static void saveDiffCDs2File(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2,
      String outputPath) throws IOException {
    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    ast1.accept(pprinter.getTraverser());
    String cd1 = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    ast2.accept(pprinter.getTraverser());
    String cd2 = pprinter.getPrinter().getContent();

    String suffix1 = "";
    String suffix2 = "";
    if (ast1.getCDDefinition().getName().equals(ast2.getCDDefinition().getName())) {
      suffix1 = "_new";
      suffix2 = "_old";
    }

    Path outputFile1 = Paths.get(outputPath, ast1.getCDDefinition().getName() + suffix1 + ".cd");
    Path outputFile2 = Paths.get(outputPath, ast2.getCDDefinition().getName() + suffix2 + ".cd");

    // Write results into a file
    FileUtils.writeStringToFile(outputFile1.toFile(), cd1, Charset.defaultCharset());
    FileUtils.writeStringToFile(outputFile2.toFile(), cd2, Charset.defaultCharset());
  }

  public static void removeDefaultPackage(ASTCDDefinition cd) {
    cd.getDefaultPackage().ifPresent(dp -> {
      cd.getCDElementList().addAll(dp.getCDElementList());
      cd.getCDElementList().remove(dp);
    });
  }

}
