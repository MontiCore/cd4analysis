package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.attribute.CompAttributeChecker;
import de.monticore.cdconformance.conf.attribute.EqNameAttributeChecker;
import de.monticore.cdconformance.conf.attribute.STNamedAttributeChecker;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultTypeIncCompleter implements IncarnationCompleter<ASTCDType> {

  protected ASTCDCompilationUnit rcd;
  protected ASTCDCompilationUnit ccd;

  protected String mapping;
  protected CompTypeIncStrategy compTypeIncStrategy;

  protected IInheritanceCompleter inheritanceCompleter;

  // todo: add parameters for method incarnation completer
  public DefaultTypeIncCompleter(
      ASTCDCompilationUnit conCD, ASTCDCompilationUnit refCD, String mapping) {
    this.rcd = refCD;
    this.ccd = conCD;
    this.mapping = mapping;

    compTypeIncStrategy = new CompTypeIncStrategy(refCD, mapping);
    compTypeIncStrategy.addIncStrategy(new STTypeIncStrategy(refCD, mapping));
    compTypeIncStrategy.addIncStrategy(new EqTypeIncStrategy(refCD, mapping));
  }

  public void setInheritanceCompleter(IInheritanceCompleter inheritanceCompleter) {
    this.inheritanceCompleter = inheritanceCompleter;
    inheritanceCompleter.setTypeMatcher(compTypeIncStrategy);
  }

  @Override
  public void completeIncarnations() throws CompletionException {

    // First step:
    identifyAndAddMissingTypeIncarnations();

    // inheritance must be completed before adding missing member incarnations
    DefaultInheritanceCompleter defaultInheritanceCompleter = new DefaultInheritanceCompleter();
    defaultInheritanceCompleter.setTypeMatcher(compTypeIncStrategy);
    defaultInheritanceCompleter.completeInheritance(rcd, ccd);

    // complete member incarnations
    for (ASTCDClass cClass : ccd.getCDDefinition().getCDClassesList()) {
      for (ASTCDType rType : compTypeIncStrategy.getMatchedElements(cClass)) {
        if (rType instanceof ASTCDClass) {
          // maybe add other modifiers later
          if (rType.getModifier().isAbstract()) {
            cClass.getModifier().setAbstract(true);
          }
          identifyAndAddMissingAttributeIncarnations(cClass, rType);
          // todo: completeMethodIncarnations
        } else {
          throw new CompletionException("A class got matched to a different type.");
        }
      }
    }
    for (ASTCDInterface cInterface : ccd.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDType rType : compTypeIncStrategy.getMatchedElements(cInterface)) {
        if (rType instanceof ASTCDInterface) {
          identifyAndAddMissingAttributeIncarnations(cInterface, rType);
          // todo: completeMethodIncarnations
        } else {
          throw new CompletionException("An interface got matched to a different type.");
        }
      }
    }
    for (ASTCDEnum cEnum : ccd.getCDDefinition().getCDEnumsList()) {
      for (ASTCDType rType : compTypeIncStrategy.getMatchedElements(cEnum)) {
        if (rType instanceof ASTCDEnum) {
          identifyAndAddMissingEnumIncarnations(cEnum, (ASTCDEnum) rType);
        } else {
          throw new CompletionException("An Enum got matched to a different type.");
        }
      }
    }
  }

  public void identifyAndAddMissingEnumIncarnations(ASTCDEnum enumInCCD, ASTCDEnum referenceEnum) {

    // Set of all the reference type enum-members that have no match with the enum-members of the
    // concrete enum
    Set<ASTCDEnumConstant> rEnumSet =
        referenceEnum.getCDEnumConstantList().stream()
            .filter(
                rEnumMember ->
                    enumInCCD.getCDEnumConstantList().stream()
                        .noneMatch(
                            cEnumMember ->
                                cEnumMember
                                    .getSymbol()
                                    .getFullName()
                                    .equals(rEnumMember.getSymbol().getFullName())))
            .collect(Collectors.toSet());

    for (ASTCDEnumConstant rEnum : rEnumSet) {
      if (enumInCCD.getCDEnumConstantList().stream()
          .noneMatch(
              cEnum -> cEnum.getSymbol().getFullName().equals(rEnum.getSymbol().getFullName()))) {
        buildEnumIncarnation(rEnum, enumInCCD);
      }
    }
  }

  public void identifyAndAddMissingAttributeIncarnations(
      ASTCDType typeInCCD, ASTCDType referenceType) {
    CompAttributeChecker compAttributeChecker = initAttributeChecker(typeInCCD, referenceType);
    // vllt attribut typen inkarnation behandeln beim kopieren
    // Set of all the reference type attributes that have no match with the attributes of the
    // concrete type
    Set<ASTCDAttribute> rAttributeSet =
        referenceType.getCDAttributeList().stream()
            .filter(
                rAttribute ->
                    typeInCCD.getCDAttributeList().stream()
                        .noneMatch(
                            cAttribute -> compAttributeChecker.isMatched(cAttribute, rAttribute)))
            .collect(Collectors.toSet());

    for (ASTCDAttribute rAttribute : rAttributeSet) {
      if (typeInCCD.getCDAttributeList().stream()
          .noneMatch(
              cAttribute ->
                  cAttribute
                      .getSymbol()
                      .getFullName()
                      .equals(rAttribute.getSymbol().getFullName()))) {
        buildAttributeIncarnation(rAttribute, typeInCCD);
      }
    }
  }

  protected CompAttributeChecker initAttributeChecker(
      ASTCDType typeInCCCD, ASTCDType referenceType) {
    CompAttributeChecker compAttributeChecker = new CompAttributeChecker(mapping);
    EqNameAttributeChecker eqNameAttributeChecker = new EqNameAttributeChecker(mapping);
    STNamedAttributeChecker stNamedAttributeChecker = new STNamedAttributeChecker(mapping);
    compAttributeChecker.addIncStrategy(stNamedAttributeChecker);
    compAttributeChecker.addIncStrategy(eqNameAttributeChecker);
    compAttributeChecker.setConcreteType(typeInCCCD);
    compAttributeChecker.setReferenceType(referenceType);
    return compAttributeChecker;
  }

  public void identifyAndAddMissingTypeIncarnations() {
    // all reference classes, enums and interfaces that are not incarnated
    Set<ASTCDClass> rClassSet =
        rcd.getCDDefinition().getCDClassesList().stream()
            .filter(
                rClass ->
                    ccd.getCDDefinition().getCDClassesList().stream()
                        .noneMatch(cClass -> compTypeIncStrategy.isMatched(cClass, rClass)))
            .collect(Collectors.toSet());
    Set<ASTCDEnum> rEnumSet =
        rcd.getCDDefinition().getCDEnumsList().stream()
            .filter(
                rEnum ->
                    ccd.getCDDefinition().getCDEnumsList().stream()
                        .noneMatch(cEnum -> compTypeIncStrategy.isMatched(cEnum, rEnum)))
            .collect(Collectors.toSet());
    Set<ASTCDInterface> rInterfaceSet =
        rcd.getCDDefinition().getCDInterfacesList().stream()
            .filter(
                rInterface ->
                    ccd.getCDDefinition().getCDInterfacesList().stream()
                        .noneMatch(
                            cInterface -> compTypeIncStrategy.isMatched(cInterface, rInterface)))
            .collect(Collectors.toSet());

    // build type incarnation for each of the types
    for (ASTCDClass rClass : rClassSet) {
      if (ccd.getCDDefinition().getCDClassesList().stream()
          .noneMatch(
              cClass ->
                  cClass
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(rClass.getSymbol().getInternalQualifiedName()))) {
        buildTypeIncarnation(rClass, ccd);
      }
    }
    for (ASTCDEnum rEnum : rEnumSet) {
      if (ccd.getCDDefinition().getCDEnumsList().stream()
          .noneMatch(
              cEnum ->
                  cEnum
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(rEnum.getSymbol().getInternalQualifiedName()))) {
        buildTypeIncarnation(rEnum, ccd);
      }
    }
    for (ASTCDInterface rInterface : rInterfaceSet) {
      if (ccd.getCDDefinition().getCDInterfacesList().stream()
          .noneMatch(
              cInterface ->
                  cInterface
                      .getSymbol()
                      .getInternalQualifiedName()
                      .equals(rInterface.getSymbol().getInternalQualifiedName()))) {
        buildTypeIncarnation(rInterface, ccd);
      }
    }
    CDDiffUtil.refreshSymbolTable(ccd);
  }

  private void buildTypeIncarnation(ASTCDClass rClass, ASTCDCompilationUnit ccd) {
    ASTCDClass clone = rClass.deepClone();
    clone.setCDExtendUsageAbsent();
    clone.setCDInterfaceUsageAbsent();
    /* für später in einer anderen Strategie
    clone.setCDMemberList(new ArrayList<>());
    clone.setCDMethodList(new ArrayList<>());
     */
    ccd.getCDDefinition().getCDElementList().add(clone);
  }

  private void buildTypeIncarnation(ASTCDEnum rEnum, ASTCDCompilationUnit ccd) {
    ASTCDEnum clone = rEnum.deepClone();
    clone.setCDInterfaceUsageAbsent();
    ccd.getCDDefinition().getCDElementList().add(clone);
  }

  private void buildEnumIncarnation(ASTCDEnumConstant rEnumMember, ASTCDEnum ccdEnum) {
    ASTCDEnumConstant clone = rEnumMember.deepClone();
    // clone.setCDInterfaceUsageAbsent();
    ccdEnum.getCDEnumConstantList().add(clone);
  }

  private void buildTypeIncarnation(ASTCDInterface rInterface, ASTCDCompilationUnit ccd) {
    ASTCDInterface clone = rInterface.deepClone();
    clone.setCDExtendUsageAbsent();
    ccd.getCDDefinition().getCDElementList().add(clone);
  }

  private void buildAttributeIncarnation(ASTCDAttribute rAttribute, ASTCDType ccdType) {
    ASTCDAttribute clone = rAttribute.deepClone();
    ccdType.addCDMember(clone);
  }
}
