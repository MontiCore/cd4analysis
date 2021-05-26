package de.monticore.cdbasis._ast;

public class ASTCDPackage extends ASTCDPackageTOP {
  @Override
  public String getName() {
    return getMCQualifiedName().getQName();
  }
}
