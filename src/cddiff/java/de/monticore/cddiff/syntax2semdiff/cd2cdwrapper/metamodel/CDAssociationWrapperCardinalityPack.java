package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel;

public class CDAssociationWrapperCardinalityPack {
  private final CDAssociationWrapperCardinality leftCardinality;

  private final CDAssociationWrapperCardinality rightCardinality;

  public CDAssociationWrapperCardinalityPack(
      CDAssociationWrapperCardinality leftCardinality,
      CDAssociationWrapperCardinality rightCardinality) {
    this.leftCardinality = leftCardinality;
    this.rightCardinality = rightCardinality;
  }

  public CDAssociationWrapperCardinality getLeftCardinality() {
    return leftCardinality;
  }

  public CDAssociationWrapperCardinality getRightCardinality() {
    return rightCardinality;
  }
}
