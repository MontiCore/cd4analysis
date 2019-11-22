/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4analysis._ast;

public class ASTCDQualifier extends ASTCDQualifierTOP {

  public ASTCDQualifier() {
    super();
  }

  @Override
  public String getName() {
    return isPresentName() ? this.name.get() : "";
  }
}
