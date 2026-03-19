/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.attribute;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;

import java.util.List;
import java.util.stream.Collectors;

public class EqNameFieldSymIncStrategy implements FieldSymMatchingStrategy {
  
  private ASTCDType referenceType;
  
  @Override
  public List<FieldSymbol> getMatchedElements(FieldSymbol concrete) {
    return referenceType.getSymbol().getFieldList().stream().filter(field -> isMatched(concrete,
        field)).collect(Collectors.toList());
  }
  
  @Override
  public boolean isMatched(FieldSymbol concrete, FieldSymbol ref) {
    return ref.getName().equals(concrete.getName());
  }
  
  @Override
  public void setReferenceType(ASTCDType referenceType) { this.referenceType = referenceType; }
  
}
