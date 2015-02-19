package de.cd4analysis.cocos.permutations;

import java.util.Collection;
import java.util.function.UnaryOperator;

import mc.ast.ASTNode;
import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCDAttribute;
import de.monticore.utils.ASTNodes;

public class SetAttributeNamesEqualRoleNames implements
    UnaryOperator<Permutation<ASTCDAssociation>> {
  
  @Override
  public Permutation<ASTCDAssociation> apply(Permutation<ASTCDAssociation> assocPermutation) {
    setRoleName(assocPermutation.getAstNodes(), "role");
    setAttributeName(assocPermutation.getAstNodes(), "role");
    
    assocPermutation.addIdSetter(IdSetters::setRoleId);
    assocPermutation.addIdSetter(IdSetters::setAttributeId);
    
    return assocPermutation;
  }
  
  private void setRoleName(Collection<ASTNode> astNodes, String name) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAssociation.class))
        .flatMap(Collection::stream)
        .forEach(cdAssociation -> {
          cdAssociation.setLeftRole(name);
          cdAssociation.setRightRole(name);
        });
  }
  
  private void setAttributeName(Collection<ASTNode> astNodes, String name) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAttribute.class))
        .flatMap(Collection::stream)
        .forEach(cdAttribute -> cdAttribute.setName(name));
  }
}
