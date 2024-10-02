// (c) https://github.com/MontiCore/monticore
package de.monticore.stdefinition._symboltable;

import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.stdefinition.STDefinitionMill;
import de.monticore.stdefinition._visitor.STDefinitionTraverser;

public class STDefinitionFullSymbolTableCompleter {

  protected STDefinitionTraverser traverser;

  public STDefinitionFullSymbolTableCompleter() {
    this.traverser = STDefinitionMill.inheritanceTraverser();

    CDBasisSymbolTableCompleter cDBasisVisitor =
        new CDBasisSymbolTableCompleter(new FullSynthesizeFromCD4Code());
    traverser.add4CDBasis(cDBasisVisitor);
    traverser.add4OOSymbols(cDBasisVisitor);
    CDInterfaceAndEnumSymbolTableCompleter cdInterfaceAndEnumVisitor =
        new CDInterfaceAndEnumSymbolTableCompleter(new FullSynthesizeFromCD4Code());
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnumVisitor);
    CD4CodeBasisSymbolTableCompleter cd4CodeBasisVisitor =
        new CD4CodeBasisSymbolTableCompleter(new FullSynthesizeFromCD4Code());
    traverser.add4CD4CodeBasis(cd4CodeBasisVisitor);
    traverser.add4CDBasis(cd4CodeBasisVisitor);
    STDefinitionSymbolTableCompleter stDefinitionVisitor =
        new STDefinitionSymbolTableCompleter(new FullSynthesizeFromCD4Code());
    traverser.add4STDefinition(stDefinitionVisitor);
  }

  public STDefinitionTraverser getTraverser() {
    return this.traverser;
  }
}
