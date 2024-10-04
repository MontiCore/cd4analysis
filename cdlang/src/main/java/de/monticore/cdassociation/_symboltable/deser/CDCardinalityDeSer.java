/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable.deser;

import de.monticore.cardinality.CardinalityMill;
import de.monticore.cardinality._ast.ASTCardinalityBuilder;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDCardinality;

public class CDCardinalityDeSer {
  public static ASTCDCardinality fromString(String cardinality) {
    return new CDCardinalityDeSer().createFromString(cardinality);
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
        return createNonStandardCardinality(cardinality);
    }
  }

  protected ASTCDCardinality createNonStandardCardinality(String cardinality) {
    String card = cardinality.replaceAll("\\s", "").replace("[", "").replace("]", "");
    ASTCardinalityBuilder builder = CardinalityMill.cardinalityBuilder();
    String[] bounds = card.split("\\.\\.");
    if (bounds.length < 1 || bounds.length > 2) {
      return null;
    }
    try {
      builder
          .setMany(false)
          .setLowerBoundLit(CardinalityMill.natLiteralBuilder().setDigits(bounds[0]).build());
      if (bounds.length == 2) {
        if (bounds[1].equals("*")) {
          builder.setNoUpperLimit(true).setUpperBoundLitAbsent();
        } else {
          builder
              .setNoUpperLimit(false)
              .setUpperBoundLit(CardinalityMill.natLiteralBuilder().setDigits(bounds[1]).build());
        }
      } else {
        builder.setNoUpperLimit(false).setUpperBoundLitAbsent();
      }
      return CDAssociationMill.cDCardOtherBuilder().setCardinality(builder.build()).build();
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return null;
  }
}
