package de.cd4analysis.cocos.permutations;

import java.util.Collection;
import java.util.function.UnaryOperator;

import mc.ast.ASTNode;
import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.monticore.utils.ASTNodes;

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
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTCDAttribute.class))
        .flatMap(Collection::stream)
        .forEach(
            cdAttribute -> cdAttribute.setName(determineTypeName(cdAttribute, assocPermutation)));
  }
  
  private String determineTypeName(ASTCDAttribute cdAttribute,
      Permutation<ASTCDAssociation> assocPermutation) {
    if (attributeIsOnLeftSide(cdAttribute, assocPermutation)) {
      return assocPermutation.delegate().getRightReferenceName().getParts().get(0);
    }
    else {
      return assocPermutation.delegate().getLeftReferenceName().getParts().get(0);
    }
  }
  
  private boolean attributeIsOnLeftSide(ASTCDAttribute cdAttribute,
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
