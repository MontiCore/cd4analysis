/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.permutations;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.ASTCDField;
import de.monticore.utils.ASTNodes;

import java.util.Collection;
import java.util.function.UnaryOperator;

public class SetAttributeNamesEqualAssociationNames implements
    UnaryOperator<Permutation<ASTCDAssociation>> {
  
  @Override
  public Permutation<ASTCDAssociation> apply(Permutation<ASTCDAssociation> assocPermutation) {
    setAssocName(assocPermutation.getAstNodes(), "assoc");
    setAttributeName(assocPermutation.getAstNodes(), "assoc");
    
    assocPermutation.addIdSetter(IdSetters::setAssocId);
    assocPermutation.addIdSetter(IdSetters::setAttributeId);
    
    return assocPermutation;
  }
  
  private void setAssocName(Collection<ASTNode> astNodes, String name) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAssociation.class))
        .flatMap(Collection::stream)
        .forEach(cdAssociation -> cdAssociation.setName(name));
  }
  
  private void setAttributeName(Collection<ASTNode> astNodes, String name) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDField.class))
        .flatMap(Collection::stream)
        .forEach(cdAttribute -> cdAttribute.setName(name));
  }
}
