/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._ast;

import java.util.Collections;
import java.util.List;

public class ASTCDCompilationUnit extends ASTCDCompilationUnitTOP {
  public int sizePackage() {
    return this.getCDPackageList().size();
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public List<String> getCDPackageList() {
    if (isPresentMCPackageDeclaration()) {
      return this.mCPackageDeclaration.get().getMCQualifiedName().getPartsList();
    } else {
      // Return an empty, immutable (!!) list to not skip some updates without knowledge
      return Collections.emptyList();
    }
  }
}
