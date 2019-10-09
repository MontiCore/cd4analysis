/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;

import java.util.Optional;

public interface ASTCDField extends ASTCDFieldTOP, ASTCD4AnalysisNode {

  void setSymbol(CDFieldSymbol symbol);

  CDFieldSymbol getSymbol ();

  Optional<CDFieldSymbol> getSymbolOpt ();

}
