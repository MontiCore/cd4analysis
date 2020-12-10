/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._ast;

import java.util.Collections;
import java.util.List;

public class ASTCDCompilationUnit extends ASTCDCompilationUnitTOP {
  public int sizePackage() {
    return this.getCDPackageList().size();
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public List<String> getCDPackageList() {
    if (isPresentCDPackageStatement()) {
      return this.cDPackageStatement.get().getPackageList();
    }
    else {
      return Collections.emptyList();
    }
  }
}
