package de.monticore.cddiff.syndiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDMemberDiff;
import de.monticore.cddiff.syndiff.imp.CDTypeDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class MemberDiffTest {

  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  /*--------------------------------------------------------------------*/
  //Syntax Diff Tests

  public static final String dir = "src/cddifftest/resources/de/monticore/cddiff/syndiff/MemberDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;
  @Test
  public void testMember1() {
    parseModels("Source1.cd", "Refinement1.cd");

    ASTCDClass cNew = CDTestHelper.getClass("Customer", src.getCDDefinition());
    ASTCDClass cOld = CDTestHelper.getClass("Customer", tgt.getCDDefinition());

    assert cNew != null;
    ASTNode attributeNew = CDTestHelper.getAttribute(cNew, "a");
    assert cOld != null;
    ASTNode attributeOld = CDTestHelper.getAttribute(cOld, "a");

    CDMemberDiff memberDiff = new CDMemberDiff(attributeNew,attributeOld);
    System.out.println(memberDiff.getBaseDiff());
    Assert.assertTrue(memberDiff.isCheck());
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
