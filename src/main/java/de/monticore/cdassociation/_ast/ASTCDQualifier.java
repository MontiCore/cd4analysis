/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;

public class ASTCDQualifier extends ASTCDQualifierTOP {
  public ASTCDQualifier() {}

  public String getName() {
    if (isPresentByAttributeName()) {
      return getByAttributeName();
    } else if (isPresentByType()) {
      return getByType().printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter());
    }

    return null;
  }
}
