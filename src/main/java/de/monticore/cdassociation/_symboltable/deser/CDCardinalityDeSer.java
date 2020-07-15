/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable.deser;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDCardinality;

public class CDCardinalityDeSer {
  public static ASTCDCardinality fromString(String cardinality) {
    return CDAssociationMill.cDCardinalityDeSer().createFromString(cardinality);
  }

  public ASTCDCardinality createFromString(String cardinality) {
    switch (cardinality) {
      case "[*]":
        return CDAssociationMill.cDCardMultBuilder().build();
      case "[1]":
        return CDAssociationMill.cDCardOneBuilder().build();
      case "[1..*]":
        return CDAssociationMill.cDCardAtLeastOneBuilder().build();
      case "[0..1]":
        return CDAssociationMill.cDCardOptBuilder().build();
      default:
        return null;
    }
  }
}
