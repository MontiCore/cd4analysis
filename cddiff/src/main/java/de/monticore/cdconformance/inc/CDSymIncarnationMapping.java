/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.inc.attribute.CDAttributeMatchingStrategy;
import de.monticore.cdconformance.inc.mctype.MCTypeMatchingStrategy;
import de.monticore.cdconformance.inc.method.CDMethodMatchingStrategy;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.symbols.oosymbols.refmodel.OOSymbolsIncMapping;

// TODO: Implement me
public class CDSymIncarnationMapping extends DefaultCDIncarnationMapping {
  
  public CDSymIncarnationMapping(ASTCDCompilationUnit concreteCD,
      ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategy,
      MCTypeMatchingStrategy mcTypeIncStrategy, CDAttributeMatchingStrategy attributeIncStrategy,
      CDMethodMatchingStrategy methodIncStrategy,
      ExternalCandidatesMatchingStrategy<ASTCDAssociation> associationIncStrategy,
      OOSymbolsIncMapping ooSymbolsIncMapping) {
    super(concreteCD, typeIncStrategy, mcTypeIncStrategy, attributeIncStrategy, methodIncStrategy,
        associationIncStrategy, ooSymbolsIncMapping);
  }
  
}
