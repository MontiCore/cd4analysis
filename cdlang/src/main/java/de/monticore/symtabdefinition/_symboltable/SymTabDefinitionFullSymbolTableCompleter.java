/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition._symboltable;

import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.symtabdefinition.SymTabDefinitionMill;
import de.monticore.symtabdefinition._visitor.SymTabDefinitionTraverser;

public class SymTabDefinitionFullSymbolTableCompleter {
  
  protected SymTabDefinitionTraverser traverser;
  
  public SymTabDefinitionFullSymbolTableCompleter() {
    this.traverser = SymTabDefinitionMill.inheritanceTraverser();
    
    //TODO remove CDBasisSymbolTableCompleter
    CDBasisSymbolTableCompleter cDBasisVisitor = new CDBasisSymbolTableCompleter();
    //TODO end
    traverser.add4CDBasis(cDBasisVisitor);
    traverser.add4OOSymbols(cDBasisVisitor);
    //TODO enumInterface
    CDInterfaceAndEnumSymbolTableCompleter cdInterfaceAndEnumVisitor =
        new CDInterfaceAndEnumSymbolTableCompleter();
    //TODO end
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnumVisitor);
    //TODO CD4CodeBasisSymbolTableCompleter
    CD4CodeBasisSymbolTableCompleter cd4CodeBasisVisitor = new CD4CodeBasisSymbolTableCompleter();
    //TODO end
    traverser.add4CD4CodeBasis(cd4CodeBasisVisitor);
    traverser.add4CDBasis(cd4CodeBasisVisitor);
    SymTabDefinitionSymbolTableCompleter stDefinitionVisitor =
        new SymTabDefinitionSymbolTableCompleter();
    traverser.add4SymTabDefinition(stDefinitionVisitor);
  }
  
  public SymTabDefinitionTraverser getTraverser() { return this.traverser; }
  
}
