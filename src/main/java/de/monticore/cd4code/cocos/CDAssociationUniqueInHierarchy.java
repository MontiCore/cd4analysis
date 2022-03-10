package de.monticore.cd4code.cocos;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * Checks that there are not multiple occurrences of role-names of associations in
 * super-classes/interfaces.
 */
public class CDAssociationUniqueInHierarchy implements CDBasisASTCDDefinitionCoCo {

  private ASTCDDefinition cd;
  private final List<ASTCDType> typeList = new ArrayList<>();

  /**
   * @param node class to check.
   */
  @Override
  public void check(ASTCDDefinition node) {
    Log.init();
    cd=node;
    typeList.addAll(cd.getCDClassesList());
    typeList.addAll(cd.getCDInterfacesList());

    // we check for each pair of associations
    for(ASTCDAssociation assoc1 : cd.getCDAssociationsList()){
      for(ASTCDAssociation assoc2 : cd.getCDAssociationsList()){

        // only check if they are not the same association
        if(assoc1!=assoc2){

          // if they share a left role-name, the referenced types on the right should not be in a
          // sub/super-type relation
          if(assoc1.getLeft().isPresentCDRole() && assoc2.getLeft().isPresentCDRole()
              && assoc1.getLeft().getName().equals(assoc2.getLeft().getName())){
            checkRight(assoc1,assoc2);
          }

          // if they share a right role-name, the referenced types on the left should not be in a
          // sub/super-type relation
          if(assoc1.getRight().isPresentCDRole() && assoc2.getRight().isPresentCDRole()
              && assoc1.getRight().getName().equals(assoc2.getRight().getName())){
            checkLeft(assoc1,assoc2);
          }

        }
      }
    }
  }

  /**
   * Checks if referenced types on the left of assoc1 and assoc2 are in a sub-/super-type relation.
   */
  private void checkLeft(ASTCDAssociation assoc1, ASTCDAssociation assoc2){

    String typeName1 = assoc1.getLeftQualifiedName().getQName();
    ASTCDType type1 = findTypeByFullName(typeName1);

    String typeName2 = assoc2.getLeftQualifiedName().getQName();
    ASTCDType type2 = findTypeByFullName(typeName2);

    if(type1!=null && type2!=null){
      checkSuper(type1,type2);
      checkSuper(type2,type1);
    }

  }

  /**
   * Checks if referenced types on the right of assoc1 and assoc2 are in a sub-/super-type relation.
   */
  private void checkRight(ASTCDAssociation assoc1, ASTCDAssociation assoc2){

    String typeName1 = assoc1.getRightQualifiedName().getQName();
    ASTCDType type1 = findTypeByFullName(typeName1);

    String typeName2 = assoc2.getRightQualifiedName().getQName();
    ASTCDType type2 = findTypeByFullName(typeName2);

    if(type1!=null && type2!=null){
      checkSuper(type1,type2);
      checkSuper(type2,type1);
    }

  }

  /**
   * helper-method to find types by full-name
   * used instead of resolve(), since ASTCDDefinition should not be used by Cocos
   */
  private ASTCDType findTypeByFullName(String fullName){
    for (ASTCDType type : typeList){
      // it has to be contains(), since getQName != getFullName
      if(type.getSymbol().getFullName().contains(fullName)){
        return type;
      }
    }
    Log.error("Could not find: " + fullName);
    return null;
  }

  /**
   * Check if type2 is a super-type of type1.
   */
  private void checkSuper(ASTCDType type1, ASTCDType type2){

    Stack<ASTCDType> typesToVisit = new Stack<>();

    // getSymbol().getSuperClassesOnly() did not work for some reason
    type1.getSuperclassList().forEach(s -> typesToVisit.push(findTypeByFullName(((ASTMCQualifiedType) s).getMCQualifiedName().getQName())));
    type1.getInterfaceList().forEach(s -> typesToVisit.push(findTypeByFullName(((ASTMCQualifiedType) s).getMCQualifiedName().getQName())));

    while (!typesToVisit.isEmpty()) {
      final ASTCDType nextType = typesToVisit.pop();
      if (nextType.getSymbol().getFullName().equals(type2.getSymbol().getFullName())) {
        Log.error(String.format("%s redefines an association of %s.", type1.getName(),
            type2.getName()));
        return;
      }
      // getSymbol().getSuperClassesOnly() did not work for some reason
      nextType.getSuperclassList().forEach(s -> typesToVisit.push(findTypeByFullName(((ASTMCQualifiedType) s).getMCQualifiedName().getQName())));
      nextType.getInterfaceList().forEach(s -> typesToVisit.push(findTypeByFullName(((ASTMCQualifiedType) s).getMCQualifiedName().getQName())));
    }
  }

}
