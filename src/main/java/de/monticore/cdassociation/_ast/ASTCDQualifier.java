/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

public class ASTCDQualifier extends ASTCDQualifierTOP {
  public ASTCDQualifier() {}

  public String getName() {
    if (isPresentByAttributeName()) {
      return getByAttributeName();
    } else if (isPresentByType()) {
      return getByType().printType();
    }

    return null;
  }
}
