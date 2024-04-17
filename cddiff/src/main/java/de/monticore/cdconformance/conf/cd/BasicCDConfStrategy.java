package de.monticore.cdconformance.conf.cd;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdmatcher.MatchingStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BasicCDConfStrategy implements ConformanceStrategy<ASTCDCompilationUnit> {
  protected ASTCDCompilationUnit refCD;
  protected MatchingStrategy<ASTCDType> typeInc;
  protected MatchingStrategy<ASTCDAssociation> assocInc;
  protected ConformanceStrategy<ASTCDType> typeChecker;
  protected ConformanceStrategy<ASTCDAssociation> assocChecker;

  protected String optTag = "optional";

  public BasicCDConfStrategy(
      ASTCDCompilationUnit refCD,
      MatchingStrategy<ASTCDType> typeInc,
      MatchingStrategy<ASTCDAssociation> assocInc,
      ConformanceStrategy<ASTCDType> typeChecker,
      ConformanceStrategy<ASTCDAssociation> assocChecker) {
    this.refCD = refCD;
    this.typeInc = typeInc;
    this.assocInc = assocInc;
    this.typeChecker = typeChecker;
    this.assocChecker = assocChecker;
  }

  @Override
  public boolean checkConformance(ASTCDCompilationUnit concrete) {
    return checkTypeIncarnation(concrete)
        & checkAssocIncarnation(concrete)
        & checkTypeConformance(concrete)
        & checkAssocConformance(concrete);
  }

  protected boolean checkAssocConformance(ASTCDCompilationUnit concrete) {
    Set<ASTCDAssociation> nonConforming =
        concrete.getCDDefinition().getCDAssociationsList().stream()
            .filter(conAssoc -> !assocChecker.checkConformance(conAssoc))
            .collect(Collectors.toSet());
    return nonConforming.isEmpty();
  }

  protected boolean checkTypeConformance(ASTCDCompilationUnit concrete) {
    boolean classConformance =
        concrete.getCDDefinition().getCDClassesList().stream()
            .allMatch(conClass -> typeChecker.checkConformance(conClass));
    boolean interfaceConformance =
        concrete.getCDDefinition().getCDInterfacesList().stream()
            .allMatch(conInterface -> typeChecker.checkConformance(conInterface));
    boolean enumConformance =
        concrete.getCDDefinition().getCDEnumsList().stream()
            .allMatch(conEnum -> typeChecker.checkConformance(conEnum));
    return classConformance && interfaceConformance && enumConformance;
  }

  protected boolean checkTypeIncarnation(ASTCDCompilationUnit concrete) {
    Set<ASTCDType> refTypes = new HashSet<>(refCD.getCDDefinition().getCDClassesList());
    refTypes.addAll(refCD.getCDDefinition().getCDInterfacesList());
    refTypes.addAll(refCD.getCDDefinition().getCDEnumsList());

    Set<ASTCDType> conTypes = new HashSet<>(concrete.getCDDefinition().getCDClassesList());
    conTypes.addAll(concrete.getCDDefinition().getCDInterfacesList());
    conTypes.addAll(concrete.getCDDefinition().getCDEnumsList());

    boolean conform = true;

    for (ASTCDType refType : refTypes) {
      if (!(refType.getModifier().isPresentStereotype()
              && refType.getModifier().getStereotype().contains(optTag))
          && conTypes.stream().noneMatch(conType -> typeInc.isMatched(conType, refType))) {
        Log.println(refType.getSymbol().getInternalQualifiedName() + " has no incarnation!");
        conform = false;
      }
    }
    return conform;
  }

  protected boolean checkAssocIncarnation(ASTCDCompilationUnit concrete) {

    boolean conform = true;

    for (ASTCDAssociation refAssoc : refCD.getCDDefinition().getCDAssociationsList()) {
      if (!(refAssoc.getModifier().isPresentStereotype()
              && refAssoc.getModifier().getStereotype().contains(optTag))
          && concrete.getCDDefinition().getCDAssociationsList().stream()
              .noneMatch(conAssoc -> assocInc.isMatched(conAssoc, refAssoc))) {
        System.out.println(CD4CodeMill.prettyPrint(refAssoc, false) + " has no incarnation!");
        conform = false;
      }
    }
    return conform;
  }
}
