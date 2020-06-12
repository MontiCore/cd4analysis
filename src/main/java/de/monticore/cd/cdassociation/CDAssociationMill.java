/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdassociation;

import de.monticore.cd.cdassociation._symboltable.SymAssociationBuilder;
import de.monticore.cd.cdassociation._visitor.CDAssociationNavigableVisitor;

public class CDAssociationMill extends CDAssociationMillTOP {
  protected static CDAssociationMill millSymAssocation;
  protected static CDAssociationMill millCDAssociationNavigableVisitor;

  public static SymAssociationBuilder symAssocationBuilder() {
    if (millSymAssocation == null) {
      millSymAssocation = getMill();
    }
    return millSymAssocation._symAssociationBuilder();
  }

  protected SymAssociationBuilder _symAssociationBuilder() {
    return new SymAssociationBuilder();
  }

  public static CDAssociationNavigableVisitor associationNavigableVisitor() {
    if (millCDAssociationNavigableVisitor == null) {
      millCDAssociationNavigableVisitor = getMill();
    }
    return millCDAssociationNavigableVisitor._associationNavigableVisitor();
  }

  protected CDAssociationNavigableVisitor _associationNavigableVisitor() {
    return new CDAssociationNavigableVisitor();
  }
}
