/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.permutations;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.ASTCDField;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.utils.ASTNodes;

import java.util.Collection;
import java.util.function.UnaryOperator;

public class SetAttributeNameEqualsTargetType implements
    UnaryOperator<Permutation<ASTCDAssociation>> {
  
  @Override
  public Permutation<ASTCDAssociation> apply(Permutation<ASTCDAssociation> assocPermutation) {
    setAttributeName(assocPermutation);
    
    assocPermutation.addIdSetter(IdSetters::setAttributeId);
    
    return assocPermutation;
  }
  
  private void setAttributeName(Permutation<ASTCDAssociation> assocPermutation) {
    assocPermutation
        .getAstNodes()
        .stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDField.class))
        .flatMap(Collection::stream)
        .forEach(
            cdAttribute -> cdAttribute.setName(determineTypeName(cdAttribute, assocPermutation)));
  }
  
  private String determineTypeName(ASTCDField cdAttribute,
                                   Permutation<ASTCDAssociation> assocPermutation) {
    if (attributeIsOnLeftSide(cdAttribute, assocPermutation)) {
      return assocPermutation.delegate().getRightReferenceName().getPartList().get(0);
    }
    else {
      return assocPermutation.delegate().getLeftReferenceName().getPartList().get(0);
    }
  }
  
  private boolean attributeIsOnLeftSide(ASTCDField cdAttribute,
                                        Permutation<ASTCDAssociation> assocPermutation) {
    for (ASTNode astNode : assocPermutation.getAstNodes()) {
      boolean hasContainingClassOnLeft = ASTNodes.getIntermediates(astNode, cdAttribute).stream()
          .filter(ASTCDClass.class::isInstance)
          .map(ASTCDClass.class::cast)
          .map(ASTCDClass::getName)
          .anyMatch(name -> name.contains("Left"));
      if (hasContainingClassOnLeft) {
        return true;
      }
    }
    return false;
  }
}
