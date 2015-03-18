package de.monticore.umlcd4a.cocos.permutations;

import java.util.Collection;

import mc.ast.ASTNode;

import com.google.common.base.Optional;

import de.monticore.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._ast.ASTCDAttribute;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.utils.ASTNodes;

class IdSetters {
  
  static void setRoleId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAssociation.class))
        .flatMap(Collection::stream)
        .forEach(cdAssociation -> {
          cdAssociation.setLeftRole(cdAssociation.getLeftRole().get() + id);
          cdAssociation.setRightRole(cdAssociation.getRightRole().get() + id);
        });
  }
  
  static void setAssocId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAssociation.class))
        .flatMap(Collection::stream)
        .forEach(cdAssociation -> {
          cdAssociation.setName(cdAssociation.getName().get() + id);
        });
  }
  
  static void setAttributeId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAttribute.class))
        .flatMap(Collection::stream)
        .forEach(cdAttribute -> cdAttribute.setName(cdAttribute.getName() + id));
  }
  
  static void setSuperClassNameId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDClass.class))
        .flatMap(Collection::stream)
        .map(ASTCDClass::getSuperclass)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(ASTSimpleReferenceType.class::isInstance)
        .map(ASTSimpleReferenceType.class::cast)
        .forEach(simpleReferenceType -> {
          String oldName = simpleReferenceType.getName().get(0);
          simpleReferenceType.getName().set(0, oldName + id);
        });
  }
  
  static void setClassNameId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDClass.class))
        .flatMap(Collection::stream)
        .forEach(cdClass -> cdClass.setName(cdClass.getName() + id));
  }
}
