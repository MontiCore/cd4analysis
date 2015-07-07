package de.monticore.umlcd4a.cocos.permutations;

import static de.monticore.types.types._ast.TypesNodeFactory.createASTQualifiedName;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.monticore.ast.ASTNode;

import org.junit.Ignore;
import org.junit.Test;

import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.ASTNodes;

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
    ASTCDAssociation cdAssociation = CD4AnalysisNodeFactory.createASTCDAssociation();
    ASTQualifiedName leftReferenceName = createASTQualifiedName(Arrays.asList("LeftType"));
    cdAssociation.setLeftReferenceName(leftReferenceName);
    ASTQualifiedName rightReferenceName = createASTQualifiedName(Arrays.asList("RightType"));
    cdAssociation.setRightReferenceName(rightReferenceName);
    cdAssociation.setUnspecified(true);
    
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
        .map(astNode -> ASTNodes.getSuccessors(astNode, ASTQualifiedName.class))
        .flatMap(Collection::stream)
        .forEach(qualifiedName -> {
          String oldName = qualifiedName.getParts().get(0);
          qualifiedName.getParts().set(0, oldName + id);
        });
  }
  
}
