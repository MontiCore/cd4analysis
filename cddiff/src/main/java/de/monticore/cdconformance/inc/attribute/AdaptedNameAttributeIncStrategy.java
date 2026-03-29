/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Matches a concrete attribute to a reference attribute when the concrete attribute name is the
 * implicit-name-adapted form of the reference attribute name.<br>
 * <br>
 * For example, {@code assignedTickets: Ticket} is matched to {@code assignedTasks: Task} when
 * {@code Ticket} incarnates {@code Task}, because
 * {@link NameUtil#adaptTemplatedName(String, String, String)
 * adaptTemplatedName("assignedTasks", "Task", "Ticket")} produces {@code "assignedTickets"}.
 */
public class AdaptedNameAttributeIncStrategy implements CDAttributeMatchingStrategy {

  private final BooleanMatchingStrategy<ASTCDType> typeMatcher;
  private final ICDBasisScope conScope;
  private final ICDBasisScope refScope;
  private ASTCDType referenceType;

  public AdaptedNameAttributeIncStrategy(BooleanMatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD) {
    this.typeMatcher = typeMatcher;
    this.conScope = concreteCD.getEnclosingScope();
    this.refScope = referenceCD.getEnclosingScope();
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    return referenceType.getCDAttributeList().stream().filter(attr -> isMatched(concrete, attr))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    Optional<ASTCDType> refAttrType = resolveCDType(ref.getMCType() instanceof ASTMCQualifiedType
        ? ((ASTMCQualifiedType) ref.getMCType()).getMCQualifiedName().getQName() : null, refScope);
    Optional<ASTCDType> conAttrType = resolveCDType(concrete.getMCType() instanceof ASTMCQualifiedType
        ? ((ASTMCQualifiedType) concrete.getMCType()).getMCQualifiedName().getQName() : null, conScope);
    if (refAttrType.isPresent() && conAttrType.isPresent()
        && typeMatcher.isMatched(conAttrType.get(), refAttrType.get())) {
      return NameUtil.adaptTemplatedName(ref.getName(), refAttrType.get().getName(),
          conAttrType.get().getName()).map(adapted -> adapted.equals(concrete.getName()))
          .orElse(false);
    }
    return false;
  }

  @Override
  public void setReferenceType(ASTCDType referenceType) {
    this.referenceType = referenceType;
  }

  private Optional<ASTCDType> resolveCDType(String typeName, ICDBasisScope scope) {
    if (typeName == null) {
      return Optional.empty();
    }
    return scope.resolveCDTypeDown(typeName)
        .filter(CDTypeSymbol::isPresentAstNode)
        .map(CDTypeSymbol::getAstNode);
  }

}
