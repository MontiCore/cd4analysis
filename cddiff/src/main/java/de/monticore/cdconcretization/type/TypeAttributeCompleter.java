package de.monticore.cdconcretization.type;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.attribute.IAttributeCompleter;
import de.monticore.cdconformance.conf.attribute.CompAttributeChecker;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeAttributeCompleter implements ITypeDetailsCompleter {

  private final ITypeDetailsCompleter next;
  private final IAttributeCompleter attributeCompleter;

  public TypeAttributeCompleter(
      ITypeDetailsCompleter next, IAttributeCompleter attributeCompleter) {
    this.next = next;
    this.attributeCompleter = attributeCompleter;
  }

  @Override
  public void completeTypeDetails(ASTCDType concreteType, ASTCDType referenceType) {
    CompAttributeChecker compAttributeChecker = initAttributeChecker(concreteType, referenceType);
    List<ASTCDAttribute> allConcreteAttributesInHierarchy =
        CDSymbolTables.getAttributesInHierarchy(concreteType);

    // Set of all the reference type attributes that have no match with the attributes of the
    // concrete type or any of its superclasses
    Set<ASTCDAttribute> rAttributeSet =
        referenceType.getCDAttributeList().stream()
            .filter(
                rAttribute ->
                    allConcreteAttributesInHierarchy.stream()
                        .noneMatch(
                            cAttribute -> compAttributeChecker.isMatched(cAttribute, rAttribute)))
            .collect(Collectors.toSet());

    for (ASTCDAttribute rAttribute : rAttributeSet) {
      buildAttributeIncarnation(rAttribute, concreteType);
    }
  }

  protected CompAttributeChecker initAttributeChecker(
      ASTCDType typeInCCCD, ASTCDType referenceType) {
    /*CompAttributeChecker compAttributeChecker = new CompAttributeChecker(mapping);
    EqNameAttributeChecker eqNameAttributeChecker = new EqNameAttributeChecker(mapping);
    STNamedAttributeChecker stNamedAttributeChecker = new STNamedAttributeChecker(mapping);
    compAttributeChecker.addIncStrategy(stNamedAttributeChecker);
    compAttributeChecker.addIncStrategy(eqNameAttributeChecker);
    compAttributeChecker.setConcreteType(typeInCCCD);
    compAttributeChecker.setReferenceType(referenceType);
    return compAttributeChecker;*/
    return null;
  }

  private void buildAttributeIncarnation(ASTCDAttribute rAttribute, ASTCDType ccdType) {
    ASTCDAttribute clone = rAttribute.deepClone();
    ccdType.addCDMember(clone);
  }
}
