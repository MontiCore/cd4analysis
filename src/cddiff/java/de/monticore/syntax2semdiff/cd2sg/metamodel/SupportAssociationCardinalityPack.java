package de.monticore.syntax2semdiff.cd2sg.metamodel;

public class SupportAssociationCardinalityPack {
  private SupportGroup.SupportAssociationCardinality leftCardinality;
  private SupportGroup.SupportAssociationCardinality rightCardinality;

  public SupportAssociationCardinalityPack(SupportGroup.SupportAssociationCardinality leftCardinality,
                                           SupportGroup.SupportAssociationCardinality rightCardinality) {
    this.leftCardinality = leftCardinality;
    this.rightCardinality = rightCardinality;
  }

  public SupportGroup.SupportAssociationCardinality getLeftCardinality() {
    return leftCardinality;
  }

  public SupportGroup.SupportAssociationCardinality getRightCardinality() {
    return rightCardinality;
  }
}
