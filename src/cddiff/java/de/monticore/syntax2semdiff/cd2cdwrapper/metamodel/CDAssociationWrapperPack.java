package de.monticore.syntax2semdiff.cd2cdwrapper.metamodel;

public class CDAssociationWrapperPack {
  private CDAssociationWrapper cDAssociationWrapper;

  private boolean isReverse;

  public CDAssociationWrapperPack(CDAssociationWrapper cDAssociationWrapper, boolean isReverse) {
    this.cDAssociationWrapper = cDAssociationWrapper;
    this.isReverse = isReverse;
  }

  public CDAssociationWrapper getCDAssociationWrapper() {
    return cDAssociationWrapper;
  }

  public boolean isReverse() {
    return isReverse;
  }

}
