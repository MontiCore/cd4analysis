/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._tagging.CD4CodeTagger;
import de.monticore.cd4codetagdefinition.CD4CodeTagDefinitionMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._tagging.CDBasisTagger;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CD4CodeTaggingTest {

  @BeforeAll
  public static void beforeAll() {
    LogStub.init();
    Log.enableFailQuick(false);
    TagRepository.clearTags();
  }

  @BeforeEach
  public void init() {
    Log.clearFindings();
  }

  @Test
  public void test() throws Exception {
    CD4CodeTagDefinitionMill.init();

    File tagsFile = new File("src/test/resources/de/monticore/tagging/Complete.tags");
    Optional<ASTTagUnit> tagDefOpt = TagRepository.loadTagModel(tagsFile);
    Assertions.assertTrue(tagDefOpt.isPresent());
    Assertions.assertEquals(0, Log.getErrorCount());

    Optional<ASTCDCompilationUnit> astOpt =
        CD4CodeMill.parser().parse("src/test/resources/de/monticore/cd4code/parser/Complete.cd");
    Assertions.assertTrue(astOpt.isPresent());
    Assertions.assertEquals(0, Log.getErrorCount());
    // set up symbol table of the CD
    CD4CodeMill.scopesGenitorDelegator().createFromAST(astOpt.get());
    Assertions.assertEquals(0, Log.getErrorCount());

    List<ASTTag> tags = CD4CodeTagger.getInstance().getTags(astOpt.get().getCDDefinition());
    Assertions.assertEquals(1, tags.size());

    ASTCDClass classA =
        (ASTCDClass) astOpt.get().getEnclosingScope().resolveCDType("A").get().getAstNode();

    tags = CDBasisTagger.getInstance().getTags(classA);
    Assertions.assertEquals(1, tags.size());

    // Also check for CDTypeSymbols
    tags = CDBasisTagger.getInstance().getTags(classA.getSymbol());
    Assertions.assertEquals(1, tags.size());
  }
}
