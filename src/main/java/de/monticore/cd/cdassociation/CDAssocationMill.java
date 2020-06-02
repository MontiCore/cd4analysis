/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdassociation;

public class CDAssocationMill extends CDAssocationMillTOP {
  protected static CDAssocationMill millSymAssocation;

  public static SymAssocationBuilder symAssocationBuilder() {
    if (millSymAssocation == null) {
      millSymAssocation = getMill();
    }
    return millSymAssocation._symAssociationBuilder();
  }

  protected SymAssocationBuilder _symAssociationBuilder() {
    return new SymAssocationBuilder();
  }
}
