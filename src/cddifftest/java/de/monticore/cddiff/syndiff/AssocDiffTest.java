package de.monticore.cddiff.syndiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.SyntaxDiffBuilder;
import de.monticore.cddiff.syndiff.imp.TestHelper;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

public class AssocDiffTest extends CDDiffTestBasis {
  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }
  @Test
  public void testCD10() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD101.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD102.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
    testHelper.changedAssocs();
  }

  @Test
  public void testCD5() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD51.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD52.cd");
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
    testHelper.changedAssocs();
  }

  @Test
  public void testCD7() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD71.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD72.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
    testHelper.changedAssocs();
  }

  @Test
  public void testCD8() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD81.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD82.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
    testHelper.changedAssocs();
  }



  /*--------------------------------------------------------------------*/
  //Syntax Diff Tests

  public static final String dir = "src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;
  @Test
  public void testAssoc1() {
    parseModels("Source1.cd", "Target1.cd");


    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt);
    //System.out.println(synDiff.printDiff());
  }

  @Test
  public void testAssoc2() {
    parseModels("Source2.cd", "Target2.cd");

    CDSyntaxDiff associationDiff = new CDSyntaxDiff(src, tgt);
    //System.out.println(associationDiff.printDiff());
    //System.out.println(associationDiff.getBaseDiff());
    //System.out.println(associationDiff.getMatchedAssocs());
  }

  @Test
  public void testAssoc3() {
    parseModels("Source3.cd", "Target3.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    //System.out.println(syntaxDiff.printSrcCD());
    //System.out.println(syntaxDiff.printTgtCD());
    //System.out.println(syntaxDiff.getBaseDiff());
  }

  @Test
  public void testAssoc4() {
    parseModels("Source4.cd", "Target4.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    System.out.println(syntaxDiff.getBaseDiff());
  }

  @Test
  public void testAssoc5() {
    parseModels("Source5.cd", "Target5.cd");
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src,tgt);
    System.out.println(syntaxDiff.getMatchedClasses());
    SyntaxDiffBuilder sb = new SyntaxDiffBuilder(src,tgt);
    System.out.println(sb.printDiff());
  }

  public void parseModels(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> src =
        CD4CodeMill.parser().parseCDCompilationUnit(dir + concrete);
      Optional<ASTCDCompilationUnit> tgt = CD4CodeMill.parser().parseCDCompilationUnit(dir + ref);
      if (src.isPresent() && tgt.isPresent()) {
        CD4CodeMill.scopesGenitorDelegator().createFromAST(src.get());
        CD4CodeMill.scopesGenitorDelegator().createFromAST(tgt.get());
        src.get().accept(new CD4CodeSymbolTableCompleter(src.get()).getTraverser());
        tgt.get().accept(new CD4CodeSymbolTableCompleter(tgt.get()).getTraverser());
        this.tgt = tgt.get();
        this.src = src.get();
      } else {
        fail("Could not parse CDs.");
      }

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
