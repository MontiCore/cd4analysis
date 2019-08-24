/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.cd.cd4analysis._ast.*;

import java.util.Optional;

import static de.monticore.cd.prettyprint.AstPrinter.EMPTY_STRING;

public interface ASTCDField extends ASTCDFieldTOP, ASTCD4AnalysisNode {

  void setCDFieldSymbol(CDFieldSymbol symbol);

  CDFieldSymbol getCDFieldSymbol ();

  Optional<CDFieldSymbol> getCDFieldSymbolOpt ();

}
