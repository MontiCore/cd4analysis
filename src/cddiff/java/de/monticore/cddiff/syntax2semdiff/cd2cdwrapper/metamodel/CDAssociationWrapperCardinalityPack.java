package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel;

public class CDAssociationWrapperCardinalityPack {
  private CDAssociationWrapperCardinality leftCardinality;

  private CDAssociationWrapperCardinality rightCardinality;

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
