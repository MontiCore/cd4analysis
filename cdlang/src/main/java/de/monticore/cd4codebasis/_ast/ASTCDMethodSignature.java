/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis._ast;

public interface ASTCDMethodSignature extends ASTCDMethodSignatureTOP {
  @Override
  default boolean isMethodSignature(){
    return true;
  }
}
