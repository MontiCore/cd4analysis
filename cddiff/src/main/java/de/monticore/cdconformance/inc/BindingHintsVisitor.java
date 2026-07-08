/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.monticore.types3.TypeCheck3;

import java.util.Optional;
import java.util.Set;

/**
 * Visitor that collects binding hints for the subtree starting from a certain element in the AST.
 * After that, the collected hints can be used to derive incarnation bindings holding in this
 * subtree.<br>
 * In general, if a reference element is always incarnated with the same concrete element in a
 * certain subtree, this is a hint that there may be a binding of the reference element to this
 * concrete element.
 */
public class BindingHintsVisitor implements CDBasisVisitor2, CD4CodeBasisVisitor2,
    MCBasicTypesVisitor2 {
  
  protected final CDIncarnationMapping incMapping;
  protected final SetMultimap<ASTCDType, ASTCDType> typeIncs = HashMultimap.create();
  protected final SetMultimap<ASTCDAttribute, ASTCDAttribute> attributeIncs = HashMultimap.create();
  protected final SetMultimap<ASTCDMethod, ASTCDMethod> methodIncs = HashMultimap.create();
  
  public BindingHintsVisitor(CDIncarnationMapping incMapping) {
    this.incMapping = incMapping;
  }
  
  /**
   * Collects binding hints for the given concrete element and its subtree.
   *
   * @param startElement the starting element of the AST to collect hints from
   */
  public void collectHints(ASTCDBasisNode startElement) {
    CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
    addToTraverser(traverser);
    startElement.accept(traverser);
  }
  
  protected void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
    traverser.add4CD4CodeBasis(this);
    traverser.add4MCBasicTypes(this);
  }
  
  @Override
  public void visit(ASTCDType conType) {
    incMapping.getReferenceElements(conType).forEach(refType -> typeIncs.put(refType, conType));
  }
  
  @Override
  public void visit(ASTCDAttribute attribute) {
    incMapping.getReferenceElements(attribute).forEach(refAttribute -> attributeIncs.put(
        refAttribute, attribute));
  }
  
  @Override
  public void visit(ASTCDMethod conMethod) {
    incMapping.getReferenceElements(conMethod).forEach(refMethod -> methodIncs.put(refMethod,
        conMethod));
  }
  
  @Override
  public void visit(ASTMCType conType) {
    SymTypeExpression symType = TypeCheck3.symTypeFromAST(conType);
    if (!symType.isObscureType() && symType.getSourceInfo().getSourceSymbol().isPresent()) {
      ISymbol conTypeSymbol = symType.getSourceInfo().getSourceSymbol().get();
      if (conTypeSymbol instanceof CDTypeSymbol && conTypeSymbol.isPresentAstNode()) {
        ASTCDType concreteType = (ASTCDType) conTypeSymbol.getAstNode();
        incMapping.getReferenceElements(concreteType).forEach(refType -> typeIncs.put(refType,
            concreteType));
      }
    }
  }
  
  /**
   * Returns the unique incarnation of the given reference type, if it exists.
   * If there are none or multiple incarnations, an empty Optional is returned.
   *
   * @param referenceType the reference type for which to find the unique incarnation
   * @return an Optional containing the unique incarnation if it exists, otherwise empty
   */
  public Optional<ASTCDType> getUniqueTypeIncarnation(ASTCDType referenceType) {
    Set<ASTCDType> incarnations = typeIncs.get(referenceType);
    if (incarnations.size() == 1) {
      return Optional.of(incarnations.iterator().next());
    }
    else {
      return Optional.empty();
    }
  }
  
  /**
   * Returns the unique incarnation of the given reference attribute, if it exists.
   * If there are none or multiple incarnations, an empty Optional is returned.
   *
   * @param referenceAttribute the reference attribute for which to find the unique incarnation
   * @return an Optional containing the unique incarnation if it exists, otherwise empty
   */
  public Optional<ASTCDAttribute> getUniqueAttributeIncarnation(ASTCDAttribute referenceAttribute) {
    Set<ASTCDAttribute> incarnations = attributeIncs.get(referenceAttribute);
    if (incarnations.size() == 1) {
      return Optional.of(incarnations.iterator().next());
    }
    else {
      return Optional.empty();
    }
  }
  
  /**
   * Returns the unique incarnation of the given reference method, if it exists.
   * If there are none or multiple incarnations, an empty Optional is returned.
   *
   * @param referenceMethod the reference method for which to find the unique incarnation
   * @return an Optional containing the unique incarnation if it exists, otherwise empty
   */
  public Optional<ASTCDMethod> getUniqueMethodIncarnation(ASTCDMethod referenceMethod) {
    Set<ASTCDMethod> incarnations = methodIncs.get(referenceMethod);
    if (incarnations.size() == 1) {
      return Optional.of(incarnations.iterator().next());
    }
    else {
      return Optional.empty();
    }
  }
  
}
