/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdcommon._ast;

import de.monticore.cd.cdbasis._symboltable.ICDBasisScope;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

public class ASTCDEnum extends ASTCDEnumTOP {
  @Override
  public void setSpannedScope(ICDBasisScope spannedScope) {
    super.setSpannedScope((ITypeSymbolsScope) spannedScope);
  }
}
