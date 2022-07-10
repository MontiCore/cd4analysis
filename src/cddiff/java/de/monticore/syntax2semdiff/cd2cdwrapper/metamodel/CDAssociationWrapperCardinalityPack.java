package de.monticore.syntax2semdiff.cd2cdwrapper.metamodel;

public class CDAssociationWrapperCardinalityPack {
  private CDWrapper.CDAssociationWrapperCardinality leftCardinality;

  private CDWrapper.CDAssociationWrapperCardinality rightCardinality;

  public CDAssociationWrapperCardinalityPack(
      CDWrapper.CDAssociationWrapperCardinality leftCardinality,
      CDWrapper.CDAssociationWrapperCardinality rightCardinality) {
    this.leftCardinality = leftCardinality;
    this.rightCardinality = rightCardinality;
  }

  public CDWrapper.CDAssociationWrapperCardinality getLeftCardinality() {
    return leftCardinality;
  }

  public CDWrapper.CDAssociationWrapperCardinality getRightCardinality() {
    return rightCardinality;
  }

}
