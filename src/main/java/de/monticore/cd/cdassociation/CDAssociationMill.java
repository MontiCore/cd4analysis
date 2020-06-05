/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdassociation;

import de.monticore.cd.cdassociation._symboltable.SymAssociationBuilder;

public class CDAssociationMill extends CDAssociationMillTOP {
  protected static CDAssociationMill millSymAssocation;

  public static SymAssociationBuilder symAssocationBuilder() {
    if (millSymAssocation == null) {
      millSymAssocation = getMill();
    }
    return millSymAssocation._symAssociationBuilder();
  }

  protected SymAssociationBuilder _symAssociationBuilder() {
    return new SymAssociationBuilder();
  }
}
