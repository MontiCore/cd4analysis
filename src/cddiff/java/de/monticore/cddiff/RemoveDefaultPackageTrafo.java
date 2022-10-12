package de.monticore.cddiff;

import de.monticore.cdbasis._ast.ASTCDDefinition;

/**
 * This Trafo extracts all associations from packages.
 */
public class RemoveDefaultPackageTrafo {
  public void transform(ASTCDDefinition cd) {
    cd.getDefaultPackage().ifPresent(dp -> {
      cd.getCDElementList().addAll(dp.getCDElementList());
      cd.getCDElementList().remove(dp);
    });
  }

}
