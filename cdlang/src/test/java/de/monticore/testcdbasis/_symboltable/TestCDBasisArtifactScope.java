/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis._symboltable;

import de.monticore.symboltable.ImportStatement;
import java.util.List;
import java.util.Optional;

public class TestCDBasisArtifactScope extends TestCDBasisArtifactScopeTOP {
  public TestCDBasisArtifactScope() {}

  public TestCDBasisArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
  }

  public TestCDBasisArtifactScope(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
          Optional<ITestCDBasisScope> enclosingScope,
      String packageName,
      List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
  }

  @SuppressWarnings("SameReturnValue")
  @Override
  public boolean checkIfContinueAsSubScope(String symbolName) {
    return true;

    /*
     always check the subscopes
     there are 2 constellations, what the symbolName could contain:
     1. an absolute name like "a.b.A":
        in this case, we traverse further in the subscopes
        (possibly with the package name removed, when this.getName() has parts of the package name)
     2. a QualifiedName without a package, like "A" or "A.name":
        search in all subscopes, for any defined type with this name
    */
  }
}
