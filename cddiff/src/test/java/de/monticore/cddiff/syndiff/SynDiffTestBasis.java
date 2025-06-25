package de.monticore.cddiff.syndiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class SynDiffTestBasis {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  public static String dir;
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;

  public void parseModels(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> src = CD4CodeMill.parser().parseCDCompilationUnit(dir
        + concrete);
      Optional<ASTCDCompilationUnit> tgt = CD4CodeMill.parser().parseCDCompilationUnit(dir + ref);
      if (src.isPresent() && tgt.isPresent()) {
        (new CD4AnalysisAfterParseTrafo()).transform(src.get());
        (new CD4AnalysisAfterParseTrafo()).transform(tgt.get());
        CD4CodeMill.scopesGenitorDelegator().createFromAST(src.get());
        CD4CodeMill.scopesGenitorDelegator().createFromAST(tgt.get());
        src.get().accept(new CD4CodeSymbolTableCompleter(src.get()).getTraverser());
        tgt.get().accept(new CD4CodeSymbolTableCompleter(tgt.get()).getTraverser());
        this.tgt = tgt.get();
        this.src = src.get();
      }
      else {
        fail(String.format("Parsing src: '%s', tgt: '%s'.", src.isPresent() ? "success" : "failure", tgt.isPresent() ? "success" : "failure"));
      }

    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }

  public Map<DiffTypes, Long> getDiffTypesCount(CDSyntaxDiff synDiff) {
    return synDiff.getBaseDiff().stream().collect(
      Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
      )
    );
  }

}
