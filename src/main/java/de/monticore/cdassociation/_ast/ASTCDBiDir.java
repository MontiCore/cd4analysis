/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdassociation._ast;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;

public  class ASTCDBiDir extends ASTCDBiDirTOP {

  public boolean isBidirectional() {
    return true;
  }

  public boolean isDefinitiveNavigableLeft() {
    return true;
  }

  public boolean isDefinitiveNavigableRight() {
    return true;
  }
}


