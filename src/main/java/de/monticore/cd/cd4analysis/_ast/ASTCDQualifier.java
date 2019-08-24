/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4analysis._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTCDQualifier extends ASTCDQualifierTOP {
  public ASTCDQualifier(Optional<String> name, Optional<ASTMCType> mCType) {
    super(name, mCType);
  }

  public ASTCDQualifier() {
    super();
  }

  @Override
  public String getName() {  return getNameOpt().orElse("");
  }
}
