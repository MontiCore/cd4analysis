/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd._parser;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDDirectComposition;
import de.monticore.cdbasis._ast.ASTCDType;

import java.util.*;

public class CDAfterParseHelper {
  protected Stack<ASTCDAssociation> assocStack;
  protected Stack<ASTCDType> typeStack;
  protected List<ASTCDAssociation> createdAssociations;
  protected Set<ASTCDDirectComposition> removedDirectCompositions;

  public CDAfterParseHelper() {
    this(new Stack<>(), new Stack<>(), new ArrayList<>(), new HashSet<>());
  }

  public CDAfterParseHelper(CDAfterParseHelper cdAfterParseHelper) {
    this(cdAfterParseHelper.assocStack, cdAfterParseHelper.typeStack, cdAfterParseHelper.createdAssociations, cdAfterParseHelper.removedDirectCompositions);
  }

  public CDAfterParseHelper(Stack<ASTCDAssociation> assocStack, Stack<ASTCDType> typeStack, List<ASTCDAssociation> createdAssociations, Set<ASTCDDirectComposition> removedDirectCompositions) {
    this.assocStack = assocStack;
    this.typeStack = typeStack;
    this.createdAssociations = createdAssociations;
    this.removedDirectCompositions = removedDirectCompositions;
  }

  public Stack<ASTCDAssociation> getAssocStack() {
    return assocStack;
  }

  public void setAssocStack(Stack<ASTCDAssociation> assocStack) {
    this.assocStack = assocStack;
  }

  public Stack<ASTCDType> getTypeStack() {
    return typeStack;
  }

  public void setTypeStack(Stack<ASTCDType> typeStack) {
    this.typeStack = typeStack;
  }

  public List<ASTCDAssociation> getCreatedAssociations() {
    return createdAssociations;
  }

  public void setCreatedAssociations(List<ASTCDAssociation> createdAssociations) {
    this.createdAssociations = createdAssociations;
  }

  public Set<ASTCDDirectComposition> getRemovedDirectCompositions() {
    return removedDirectCompositions;
  }

  public void setRemovedDirectCompositions(Set<ASTCDDirectComposition> removedDirectCompositions) {
    this.removedDirectCompositions = removedDirectCompositions;
  }
}
