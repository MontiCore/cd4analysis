package de.monticore.sydiff2semdiff.cd2sg.metamodel;

public class SupportAssociationPack {
  private SupportAssociation supportAssociation;
  private boolean isReverse;

  public SupportAssociationPack(SupportAssociation supportAssociation, boolean isReverse) {
    this.supportAssociation = supportAssociation;
    this.isReverse = isReverse;
  }

  public SupportAssociation getSupportAssociation() {
    return supportAssociation;
  }

  public boolean isReverse() {
    return isReverse;
  }

}
