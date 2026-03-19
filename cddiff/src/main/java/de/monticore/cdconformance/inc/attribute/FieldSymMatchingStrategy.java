/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.attribute;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;

public interface FieldSymMatchingStrategy extends ExternalCandidatesMatchingStrategy<FieldSymbol> {
  
  void setReferenceType(ASTCDType refType);
  
}
