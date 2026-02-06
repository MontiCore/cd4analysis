/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * Checks that there are not multiple occurrences of role-names of associations in
 * super-classes/interfaces.
 */
public class CDAssociationUniqueInHierarchy extends CDAssociationUnique {
  
  @Override
  protected void checkRef(ASTCDAssociation assoc1, ASTCDAssociation assoc2, ASTCDType type1,
      ASTCDType type2) {
    super.checkRef(assoc1, assoc2, type1, type2);
    // We now also check if the types are in a sub/super-type relation
    checkSuper(assoc1, assoc2, type1, type2);
    checkSuper(assoc2, assoc1, type2, type1);
  }
  
  /** Check if type2 is a super-type of type1. */
  protected void checkSuper(ASTCDAssociation assoc1, ASTCDAssociation assoc2, ASTCDType type1,
      ASTCDType type2) {
    
    Stack<TypeSymbol> typesToVisit = new Stack<>();
    
    // getSymbol().getSuperClassesOnly() did not work for some reason
    type1.getSymbol().getSuperClassesOnly().forEach(s -> typesToVisit.push(s.getTypeInfo()));
    
    // getSymbol().getInterfaces() did not work for some reason
    type1.getSymbol().getInterfaceList().forEach(s -> typesToVisit.push(s.getTypeInfo()));
    
    while (!typesToVisit.isEmpty()) {
      final TypeSymbol nextType = typesToVisit.pop();
      if (nextType.getFullName().equals(type2.getSymbol().getFullName())) {
        Log.error(String.format("0xCDCE6: %s redefines an association of %s from %s at %s", type1
            .getName(), type2.getName(), assoc2.get_SourcePositionStart(), assoc1
                .get_SourcePositionStart()), assoc1.get_SourcePositionStart());
        return;
      }
      
      // getSymbol().getSuperClassesOnly() did not work for some reason
      nextType.getSuperClassesOnly().forEach(s -> typesToVisit.push(s.getTypeInfo()));
      
      // getSymbol().getInterfaces() did not work for some reason
      nextType.getInterfaceList().forEach(s -> typesToVisit.push(s.getTypeInfo()));
    }
  }
  
}
