/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._ast;

public interface ASTCDMember extends ASTCDMemberTOP {
  default boolean isField() {
    return isAttribute() || isRole();
  }

  default boolean isAttribute() {
    return false;
  }

  default boolean isRole() {
    return false;
  }

  default boolean isMethodSignature() {
    return false;
  }

  default boolean isConstructor() {
    return false;
  }

  default boolean isMethod() {
    return false;
  }
}
