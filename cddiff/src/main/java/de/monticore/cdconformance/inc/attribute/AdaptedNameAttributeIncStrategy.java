/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.attribute;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
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
  private ASTCDType referenceType;

  public AdaptedNameAttributeIncStrategy(BooleanMatchingStrategy<ASTCDType> typeMatcher) {
    this.typeMatcher = typeMatcher;
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    return referenceType.getCDAttributeList().stream().filter(attr -> isMatched(concrete, attr))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    Optional<ASTCDType> refAttrType = resolveCDType(ref);
    Optional<ASTCDType> conAttrType = resolveCDType(concrete);
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

  private Optional<ASTCDType> resolveCDType(ASTCDAttribute attribute) {
    // Only qualified types can be CD types; primitives and collection types cannot
    if (!(attribute.getMCType() instanceof ASTMCQualifiedType)) {
      return Optional.empty();
    }
    // Use global scope lookup to avoid NPE on types without enclosing scope (e.g., deep-cloned
    // elements added during concretization that have not been through symbol table construction)
    String typeName = ((ASTMCQualifiedType) attribute.getMCType()).getMCQualifiedName().getQName();
    return CD4CodeMill.globalScope().resolveCDTypeDown(typeName)
        .filter(sym -> sym.isPresentAstNode())
        .map(CDTypeSymbol::getAstNode);
  }

}
