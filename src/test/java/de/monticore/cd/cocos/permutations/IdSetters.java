/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.permutations;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDClassTOP;
import de.monticore.cd.cd4analysis._ast.ASTCDField;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.utils.ASTNodes;

import java.util.Collection;
import java.util.Optional;

class IdSetters {
  
  static void setRoleId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAssociation.class))
        .flatMap(Collection::stream)
        .forEach(cdAssociation -> {
          cdAssociation.setLeftRole(cdAssociation.getLeftRole() + id);
          cdAssociation.setRightRole(cdAssociation.getRightRole() + id);
        });
  }
  
  static void setAssocId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAssociation.class))
        .flatMap(Collection::stream)
        .forEach(cdAssociation -> {
          cdAssociation.setName(cdAssociation.getName() + id);
        });
  }
  
  static void setAttributeId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDField.class))
        .flatMap(Collection::stream)
        .forEach(cdAttribute -> cdAttribute.setName(cdAttribute.getName() + id));
  }
  
  static void setSuperClassNameId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDClass.class))
        .flatMap(Collection::stream)
        .filter(ASTCDClassTOP::isPresentSuperclass)
        .map(ASTCDClass::getSuperclass)
        .filter(ASTMCQualifiedType.class::isInstance)
        .map(ASTMCQualifiedType.class::cast)
        .forEach(simpleReferenceType -> {
          String oldName = simpleReferenceType.getNameList().get(0);
          simpleReferenceType.getNameList().set(0, oldName + id);
        });
  }
  
  static void setClassNameId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDClass.class))
        .flatMap(Collection::stream)
        .forEach(cdClass -> cdClass.setName(cdClass.getName() + id));
  }
}
