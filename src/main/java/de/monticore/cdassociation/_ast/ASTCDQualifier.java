/*
 * (c) https://github.com/MontiCore/monticore
 */
package de.monticore.cdassociation._ast;

import de.monticore.cdassociation.MCBasicTypesMillForCDAssociation;

public class ASTCDQualifier extends ASTCDQualifierTOP {
  public ASTCDQualifier() {
  }

  public String getName() {
    if (isPresentByAttributeName()) {
      return getByAttributeName();
    }
    else if (isPresentByType()) {
      return getByType().printType(MCBasicTypesMillForCDAssociation.mcBasicTypesPrettyPrinter());
    }

    return null;
  }
}
