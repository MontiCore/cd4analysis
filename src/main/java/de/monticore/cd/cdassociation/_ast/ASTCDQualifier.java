/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdassociation._ast;

public class ASTCDQualifier extends ASTCDQualifierTOP {
  public ASTCDQualifier() {
  }

  @Override
  public String getName() {
    if (isPresentByAttributeName()) {
      return getByAttributeName();
    } else if (isPresentByType()) {
      return getByType().printType(MCBasicTypesMillForCDAssociation.mcBasicTypesPrettyPrinter());
    }

    return null;
  }
}
