/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.mctype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.generics.bounds.UnsatisfiableBound;
import de.monticore.types3.util.SymTypeCompatibilityCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.List;

public class IncMappingAwareSymTypeCompatibilityCalculator extends SymTypeCompatibilityCalculator {
  
  private BooleanMatchingStrategy<ASTCDType> cdTypeMatcher;
  
  @Override
  protected List<Bound> objectConstrainSameType(SymTypeExpression typeA, SymTypeExpression typeB) {
    // return empty array list for "compatible"
    if (typeA.hasTypeInfo() && typeB.hasTypeInfo()) {
      TypeSymbol typeSymbolA = typeA.getTypeInfo();
      TypeSymbol typeSymbolB = typeB.getTypeInfo();
      
      if (typeSymbolA instanceof CDTypeSymbol && typeSymbolB instanceof CDTypeSymbol) {
        CDTypeSymbol cdTypeA = (CDTypeSymbol) typeSymbolA;
        CDTypeSymbol cdTypeB = (CDTypeSymbol) typeSymbolB;
        if (cdTypeA.isPresentAstNode() && cdTypeB.isPresentAstNode()) {
          // we assume typeA is the concrete type and typeB is the reference type
          // this has to be considered when calling constrainSameType(typeA, typeB)
          if (cdTypeMatcher.isMatched(cdTypeA.getAstNode(), cdTypeB.getAstNode())) {
            return Collections.emptyList();
          }
          else {
            return Collections.singletonList(new UnsatisfiableBound(typeA.printFullName()
                + " is not an incarnation of " + typeB.printFullName()));
          }
        }
        else {
          Log.warn("The concrete type or the reference type does not have an AST node. "
              + "Incarnation mapping is only defined for CDTypeSymbol with AST nodes.");
        }
      }
    }
    return super.objectConstrainSameType(typeA, typeB);
  }
  
  public void setCDTypeMatcher(BooleanMatchingStrategy<ASTCDType> typeMatcher) {
    this.cdTypeMatcher = typeMatcher;
  }
  
}
