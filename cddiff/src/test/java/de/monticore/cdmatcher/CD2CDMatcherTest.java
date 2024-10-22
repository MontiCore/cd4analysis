package de.monticore.cdmatcher;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CD2CDMatcherTest {

  public static final String dir = "src/test/resources/de/monticore/cdmatcher/";

  protected ASTCDCompilationUnit tgt;

  protected ASTCDCompilationUnit src;

  @BeforeEach
  public void setup() {
    LogStub.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  @Test
  public void testMatchAssocName() {
    parseModels("Source.cd", "Refinement.cd");
    assertTrue(
        CD2CDMatcher.matchAssocsByName(
            src.getCDDefinition().getCDAssociationsList().get(0),
            tgt.getCDDefinition().getCDAssociationsList().get(0),
            tgt));
  }

  @Test
  public void testMatchNameType() {
    parseModels("Source.cd", "Refinement.cd");
    assertTrue(
        CD2CDMatcher.matchTypesByName(
            src.getCDDefinition().getCDClassesList().get(0),
            tgt.getCDDefinition().getCDClassesList().get(0),
            tgt));
  }

  @Test
  public void testMatchStructureType() {
    parseModels("Source6.cd", "Refinement6.cd");
    assertTrue(
        CD2CDMatcher.matchTypesByStructure(
            src.getCDDefinition().getCDClassesList().get(0),
            tgt.getCDDefinition().getCDClassesList().get(0),
            tgt));
  }

  @Test
  public void testMatchSubToSuperClass() {
    parseModels("Source2.cd", "Refinement2.cd");
    assertTrue(
        CD2CDMatcher.matchSubToSuperType(
            src.getCDDefinition().getCDClassesList().get(0),
            tgt.getCDDefinition().getCDClassesList().get(0),
            src,
            tgt));
  }

  @Test
  public void testMatchSrcClassTgtRoleName() {
    parseModels("Source3.cd", "Refinement3.cd");
    assertTrue(
        CD2CDMatcher.matchAssocBySrcTypeAndTgtRole(
            tgt.getCDDefinition().getCDAssociationsList().get(0),
            src.getCDDefinition().getCDAssociationsList().get(0),
            src,
            tgt));
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
