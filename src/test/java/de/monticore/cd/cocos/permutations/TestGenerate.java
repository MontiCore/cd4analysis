/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.permutations;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.utils.ASTNodes;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Sebastian Oberhoff
 */
public class TestGenerate {
  
  @Test
  @Ignore
  public void runTest() {
    Permutation<ASTCDAssociation> assocPermutation = createDefaultAssociation();
    
    Set<Permutation<ASTCDAssociation>> permutations = Stream.of(assocPermutation)
        .flatMap(new CreateInheritanceTree().andThen(Set::stream))
        // .map(new SetAttributeNamesEqualAssociationNames())
        // .map(new SetAttributeNamesEqualRoleNames())
        // .map(new SetAttributeNameEqualsTargetType())
        .collect(Collectors.toSet());
    
    print(permutations);
  }
  
  private Permutation<ASTCDAssociation> createDefaultAssociation() {
    ASTMCQualifiedName leftReferenceName = CD4AnalysisMill.mCQualifiedNameBuilder().setPartList(Arrays.asList("LeftType")).build();
    ASTMCQualifiedName rightReferenceName = CD4AnalysisMill.mCQualifiedNameBuilder().setPartList(Arrays.asList("RightType")).build();
    ASTCDAssociation cdAssociation = CD4AnalysisMill.cDAssociationBuilder().
            setLeftReferenceName(leftReferenceName).
            setRightReferenceName(rightReferenceName).
            setUnspecified(true).build();
    Permutation<ASTCDAssociation> assocPermutation = new Permutation<>(cdAssociation);
    assocPermutation.addIdSetter(this::setReferenceNameId);
    return assocPermutation;
  }
  
  private void print(Set<Permutation<ASTCDAssociation>> permutations) {
    int index = 1;
    for (Permutation<ASTCDAssociation> permutation : permutations) {
      permutation.applyIdSetters("" + index);
      System.out.println(permutation);
      System.out.println("------");
      index++;
    }
  }
  
  private void setReferenceNameId(Collection<ASTNode> astNodes, String id) {
    astNodes.stream()
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTMCQualifiedName.class))
        .flatMap(Collection::stream)
        .forEach(qualifiedName -> {
          String oldName = qualifiedName.getPartList().get(0);
          qualifiedName.getPartList().set(0, oldName + id);
        });
  }
  
}
