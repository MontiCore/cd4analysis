/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

public interface ASTCDAssocType extends ASTCDAssocTypeTOP {
  default boolean isComposition() {
    return false;
  }

  default boolean isAssociation() {
    return false;
  }
}
