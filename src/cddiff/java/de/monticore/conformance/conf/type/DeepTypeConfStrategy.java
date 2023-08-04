package de.monticore.conformance.conf.type;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.conformance.conf.AttributeChecker;
import de.monticore.matcher.MatchingStrategy;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DeepTypeConfStrategy extends BasicTypeConfStrategy {
  public DeepTypeConfStrategy(
      ASTCDCompilationUnit conCD,
      ASTCDCompilationUnit refCD,
      AttributeChecker attributeChecker,
      MatchingStrategy<ASTCDType> typeInc,
      MatchingStrategy<ASTCDAssociation> assocInc) {
    super(conCD, refCD, attributeChecker, typeInc, assocInc);
  }

  @Override
  protected boolean checkAttributeIncarnation(ASTCDType concrete, ASTCDType ref) {
    return checkIncarnationAt(
        new HashSet<>(CDSymbolTables.getAttributesInHierarchy(concrete)),
        new HashSet<>(ref.getCDAttributeList()));
  }

  @Override
  protected boolean checkAttributeConformance(ASTCDType concrete) {
    return checkConformanceAt(new HashSet<>(CDSymbolTables.getAttributesInHierarchy(concrete)));
  }

  @Override
  protected boolean checkAssocIncarnation(ASTCDType concrete, ASTCDType ref) {

    Set<ASTCDAssociation> conAssocSet =
        CDDiffUtil.getAllSuperTypes(concrete, conCD.getCDDefinition()).stream()
            .flatMap(
                supertype ->
                    CDDiffUtil.getReferencingAssociations(supertype, conCD).stream()
                        .filter(
                            assoc ->
                                assoc.getLeftQualifiedName().equals(assoc.getRightQualifiedName())
                                    || (assoc.getCDAssocDir().isDefinitiveNavigableRight()
                                        && supertype
                                            .getSymbol()
                                            .getInternalQualifiedName()
                                            .contains(assoc.getLeftQualifiedName().getQName()))
                                    || (assoc.getCDAssocDir().isDefinitiveNavigableLeft()
                                        && supertype
                                            .getSymbol()
                                            .getInternalQualifiedName()
                                            .contains(assoc.getRightQualifiedName().getQName()))))
            .collect(Collectors.toSet());

    conAssocSet.addAll(CDDiffUtil.getReferencingAssociations(concrete, conCD));

    return checkIncarnationAs(conAssocSet, CDDiffUtil.getReferencingAssociations(ref, refCD));
  }
}
