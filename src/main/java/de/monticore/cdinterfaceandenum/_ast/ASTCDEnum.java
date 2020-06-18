/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum._ast;

import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

public class ASTCDEnum extends ASTCDEnumTOP {
  @Override
  public void setSpannedScope(ICDBasisScope spannedScope) {
    super.setSpannedScope((ITypeSymbolsScope) spannedScope);
  }
}
