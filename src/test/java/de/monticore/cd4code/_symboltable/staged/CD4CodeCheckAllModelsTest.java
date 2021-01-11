package de.monticore.cd4code._symboltable.staged;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeTrafo4DefaultsDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@RunWith(Parameterized.class)
public class CD4CodeCheckAllModelsTest extends CD4CodeTestBasis {

  protected String modelName;

  @Parameterized.Parameters(name = "{index}: {0}")
  public static Collection<String> testModels() throws IOException {
    final Path path = Paths.get(PATH);
    final String resourcePath = path.normalize().toString();

    return Files.walk(path)
        .filter(Files::isRegularFile)
        .map(Path::normalize)
        .map(Path::toString)
        .map(f -> f.substring(resourcePath.length()))
        .filter(f -> FilenameUtils.getExtension(f).equals("cd"))
        .collect(Collectors.toList());
  }

  public CD4CodeCheckAllModelsTest(String modelName) {
    this.modelName = modelName;
  }

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath(modelName));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    CD4CodeMill.cD4CodeSymbolTableCreatorDelegator().createFromAST(node);
    node.accept(new CD4CodeTrafo4DefaultsDelegator());

    cd4CodeCoCos.createNewChecker().checkAll(node);
  }
}
