/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.permutations;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.ASTCDField;
import de.monticore.cd.cd4analysis._ast.ASTRole;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.utils.ASTNodes;

import java.util.Collection;
import java.util.function.UnaryOperator;

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
    ASTRole role = CD4AnalysisMill.roleBuilder().setName(name).build();
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAssociation.class))
        .flatMap(Collection::stream)
        .forEach(cdAssociation -> {
          cdAssociation.setLeftRole(role);
          cdAssociation.setRightRole(role);
        });
  }
  
  private void setAttributeName(Collection<ASTNode> astNodes, String name) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDField.class))
        .flatMap(Collection::stream)
        .forEach(cdAttribute -> cdAttribute.setName(name));
  }
}
