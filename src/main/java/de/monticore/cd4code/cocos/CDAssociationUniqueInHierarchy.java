package de.monticore.cd4code.cocos;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * Checks that there are not multiple occurrences of role-names of associations in
 * super-classes/interfaces.
 */
public class CDAssociationUniqueInHierarchy implements CDBasisASTCDDefinitionCoCo {

  /**
   * @param node class to check.
   */
  @Override
  public void check(ASTCDDefinition node) {

    // we check for each pair of associations
    for(ASTCDAssociation assoc1 : node.getCDAssociationsList()){
      for(ASTCDAssociation assoc2 : node.getCDAssociationsList()){

        // only check if they are not the same association
        if(assoc1!=assoc2){

          // if they share a left role-name, the referenced types on the right should not be in a
          // sub/super-type relation
          if(assoc1.getLeft().isPresentCDRole() && assoc2.getLeft().isPresentCDRole()
              && assoc1.getLeft().getName().equals(assoc2.getLeft().getName())){
            checkRef(node, assoc1, assoc2, true);
          }

          // if they share a right role-name, the referenced types on the left should not be in a
          // sub/super-type relation
          if(assoc1.getRight().isPresentCDRole() && assoc2.getRight().isPresentCDRole()
              && assoc1.getRight().getName().equals(assoc2.getRight().getName())){
            checkRef(node, assoc1, assoc2, false);
          }

        }
      }
    }
  }

  /**
   * Checks if referenced types on the right/left of assoc1 and assoc2 are in a sub-/super-type
   * relation.
   */
  protected void checkRef(ASTCDDefinition node, ASTCDAssociation assoc1,
      ASTCDAssociation assoc2, boolean checkRight){

    String typeName1;
    String typeName2;

    if(checkRight){
      typeName1 = assoc1.getRightQualifiedName().getQName();
      typeName2 = assoc2.getRightQualifiedName().getQName();
    } else {
      typeName1 = assoc1.getLeftQualifiedName().getQName();
      typeName2 = assoc2.getLeftQualifiedName().getQName();
    }

    ASTCDType type1 = findTypeByFullName(node,typeName1);
    ASTCDType type2 = findTypeByFullName(node,typeName2);

    if(type1!=null && type2!=null){
      checkSuper(node,type1,type2);
      checkSuper(node,type2,type1);
    }

  }


  /**
   * helper-method to find types by full-name
   */
  protected ASTCDType findTypeByFullName(ASTCDDefinition node, String fullName){

    Optional<CDTypeSymbol> optSymbol = node.getEnclosingScope().resolveCDTypeDown(fullName);
    if (optSymbol.isPresent()){
      return optSymbol.get().getAstNode();
    }

    Log.error("0xCDCE2: Could not find: " + fullName + ".");
    return null;
  }

  /**
   * Check if type2 is a super-type of type1.
   */
  protected void checkSuper(ASTCDDefinition node, ASTCDType type1,
      ASTCDType type2){

    Stack<ASTCDType> typesToVisit = new Stack<>();

    // getSymbol().getSuperClassesOnly() did not work for some reason
    type1.getSuperclassList().forEach(s -> typesToVisit.push(findTypeByFullName(node,
        ((ASTMCQualifiedType) s).getMCQualifiedName().getQName())));

    // getSymbol().getInterfaces() did not work for some reason
    type1.getInterfaceList().forEach(s -> typesToVisit.push(findTypeByFullName(node,
        ((ASTMCQualifiedType) s).getMCQualifiedName().getQName())));

    while (!typesToVisit.isEmpty()) {
      final ASTCDType nextType = typesToVisit.pop();
      if (nextType.getSymbol().getFullName().equals(type2.getSymbol().getFullName())) {
        Log.error(String.format("0xCDCE1: %s redefines an association of %s.", type1.getName(),
            type2.getName()));
        return;
      }

      // getSymbol().getSuperClassesOnly() did not work for some reason
      nextType.getSuperclassList().forEach(s -> typesToVisit.push(findTypeByFullName(node,
          ((ASTMCQualifiedType) s).getMCQualifiedName().getQName())));

      // getSymbol().getInterfaces() did not work for some reason
      nextType.getInterfaceList().forEach(s -> typesToVisit.push(findTypeByFullName(node,
          ((ASTMCQualifiedType) s).getMCQualifiedName().getQName())));
    }
  }

}
