package de.cd4analysis.cocos.permutations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mc.ast.ASTNode;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.types._ast.ASTSimpleReferenceType;
import de.monticore.types._ast.TypesNodeFactory;
import de.monticore.utils.ASTNodes;

public class CreateInheritanceTree implements
    Function<Permutation<ASTCDAssociation>, Set<Permutation<ASTCDAssociation>>> {
  
  @Override
  public Set<Permutation<ASTCDAssociation>> apply(Permutation<ASTCDAssociation> testPermutation) {
    
    Set<Permutation<ASTCDAssociation>> successorPermutations = new LinkedHashSet<>();
    for (int inheritanceDepth = 0; inheritanceDepth < 4; inheritanceDepth++) {
      successorPermutations.add(createInheritanceClash(testPermutation, inheritanceDepth));
    }
    return successorPermutations;
  }
  
  private Permutation<ASTCDAssociation> createInheritanceClash(
      Permutation<ASTCDAssociation> assocPermutation,
      int inheritanceDepth) {
    Permutation<ASTCDAssociation> successorPermutation = assocPermutation.copy();
    
    ASTCDClass referencedClass = createReferencedClass(successorPermutation.delegate()
        .getLeftReferenceName().getParts().get(0));
    List<ASTCDClass> superClasses = createSuperClasses(referencedClass, inheritanceDepth);
    ASTCDClass topClass = !superClasses.isEmpty()
        ? Iterables.getLast(superClasses)
        : referencedClass;
    addAttributeToTopClass(topClass);
    
    successorPermutation.addAstNode(referencedClass);
    successorPermutation.addAstNodes(superClasses);
    
    successorPermutation.addIdSetter(IdSetters::setClassNameId);
    successorPermutation.addIdSetter(IdSetters::setSuperClassNameId);
    
    return successorPermutation;
  }
  
  private ASTCDClass createReferencedClass(String referencedClassName) {
    ASTCDClass cdClass = CD4AnalysisNodeFactory.createASTCDClass();
    cdClass.setName(referencedClassName);
    return cdClass;
  }
  
  private List<ASTCDClass> createSuperClasses(ASTCDClass baseClass, int inheritanceDepth) {
    return Stream
        .iterate(baseClass, CreateInheritanceTree::newSuperClass)
        .skip(1)
        .limit(inheritanceDepth)
        .collect(Collectors.toList());
  }
  
  private static void addAttributeToTopClass(ASTCDClass topClass) {
    ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    topClass.getCDAttributes().add(cdAttribute);
  }
  
  private static ASTCDClass newSuperClass(ASTCDClass subClass) {
    ASTCDClass superClass = CD4AnalysisNodeFactory.createASTCDClass();
    superClass.setName("Super" + subClass.getName());
    subClass.setSuperclass(createSimpleReference(superClass));
    return superClass;
  }
  
  private static ASTSimpleReferenceType createSimpleReference(ASTCDClass superClass) {
    ASTSimpleReferenceType reference = TypesNodeFactory.createASTSimpleReferenceType();
    ArrayList<String> name = new ArrayList<String>();
    name.add(superClass.getName());
    reference.setName(name);
    return reference;
  }
  
}
