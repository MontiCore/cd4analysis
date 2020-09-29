package de.monticore.cdbasis._ast;

import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope;

public class ASTCDExtendUsage extends ASTCDExtendUsageTOP {
  // TODO: the setEnclosingScope-methods should call the setter instead of setting the scope directly
  @Override
  public void setEnclosingScope(IMCLiteralsBasisScope enclosingScope) {
    if (enclosingScope instanceof de.monticore.cdbasis._symboltable.ICDBasisScope) {
      setEnclosingScope((de.monticore.cdbasis._symboltable.ICDBasisScope) enclosingScope);
    }
    else {
      de.se_rwth.commons.logging.Log.error("0xA7005x384777510 The EnclosingScope form type de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope could not be casted to the type de.monticore.cdbasis._symboltable.ICDBasisScope. Please call the Method setEnclosingScope with a parameter form type de.monticore.cdbasis._symboltable.ICDBasisScope");
    }
  }

  @Override
  public void setEnclosingScope(ICDBasisScope enclosingScope) {
    super.setEnclosingScope(enclosingScope);

    this.streamSuperclass().forEach(s -> s.setEnclosingScope(enclosingScope));
  }
}
