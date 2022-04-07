/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._ast;

import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope;

public class ASTCDExtendUsage extends ASTCDExtendUsageTOP {
  @Override
  public void setEnclosingScope(IMCLiteralsBasisScope enclosingScope) {
    if (enclosingScope instanceof de.monticore.cdbasis._symboltable.ICDBasisScope) {
      setEnclosingScope((de.monticore.cdbasis._symboltable.ICDBasisScope) enclosingScope);
    }
    else {
      de.se_rwth.commons.logging.Log.error("0xAE886 The EnclosingScope form type de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope could not be casted to the type de.monticore.cdbasis._symboltable.ICDBasisScope. Please call the Method setEnclosingScope with a parameter form type de.monticore.cdbasis._symboltable.ICDBasisScope");
    }
  }

  @Override
  public void setEnclosingScope(ICDBasisScope enclosingScope) {
    super.setEnclosingScope(enclosingScope);

    this.streamSuperclass().forEach(s -> s.setEnclosingScope(enclosingScope));
  }
}
