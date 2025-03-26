package de.monticore.cdconcretization.typedetails;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.attribute.AbstractAttributeCompleter;
import de.monticore.cdconcretization.attribute.BaseAttributeCompleter;
import de.monticore.cdconcretization.attribute.IAttributeCompleter;
import de.monticore.cdconcretization.typedetails.AbstractTypeDetailsCompleter;
import de.monticore.cdconcretization.util.ChainBuilder;
import de.monticore.cdconformance.conf.attribute.CompAttributeChecker;
import de.monticore.cdconformance.conf.attribute.EqNameAttributeChecker;
import de.monticore.cdconformance.conf.attribute.STNamedAttributeChecker;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

// TODO Do we really want to decompose the logic THAT much or directly call attribute/method
// completion from the BaseCDCompleter??
public class TypeAttributesCompleter extends AbstractTypeDetailsCompleter {

  private final String mapping;

  public TypeAttributesCompleter(String mapping) {
    this.mapping = mapping;
  }

  @Override
  protected void completeClassDetails(ASTCDClass concreteType, ASTCDClass referenceType)
      throws CompletionException {
    completeAttributes(concreteType, referenceType);
    next(concreteType, referenceType);
  }

  @Override
  protected void completeInterfaceDetails(ASTCDInterface concreteType, ASTCDInterface referenceType)
      throws CompletionException {
    completeAttributes(concreteType, referenceType);
    next(concreteType, referenceType);
  }

  protected void completeAttributes(ASTCDType concreteType, ASTCDType referenceType) {
    IAttributeCompleter attributeCompleter =
        createAttributeCompleterChain(concreteType, referenceType);

    for (ASTCDAttribute rAttribute : referenceType.getCDAttributeList()) {
      attributeCompleter.completeAttribute(concreteType, rAttribute);
    }
  }

  private IAttributeCompleter createAttributeCompleterChain(
      ASTCDType typeInCCCD, ASTCDType referenceType) {
    CompAttributeChecker attributeIncStrategy = new CompAttributeChecker(mapping);
    EqNameAttributeChecker eqNameAttributeChecker = new EqNameAttributeChecker(mapping);
    STNamedAttributeChecker stNamedAttributeChecker = new STNamedAttributeChecker(mapping);
    attributeIncStrategy.addIncStrategy(stNamedAttributeChecker);
    attributeIncStrategy.addIncStrategy(eqNameAttributeChecker);
    attributeIncStrategy.setConcreteType(typeInCCCD);
    attributeIncStrategy.setReferenceType(referenceType);

    return new ChainBuilder<AbstractAttributeCompleter>()
        .add(new BaseAttributeCompleter(attributeIncStrategy))
        // TODO add name stereotype support here
        // TODO add forEach stereotype support here
        .build();
  }
}
