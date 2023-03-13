package de.monticore.conformance.basic;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.conformance.ConformanceStrategy;
import de.monticore.conformance.inc.IncarnationStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;

public class BasicAssocConfStrategy implements ConformanceStrategy<ASTCDAssociation> {

  protected ASTCDCompilationUnit refCD;
  protected ASTCDCompilationUnit conCD;
  protected IncarnationStrategy<ASTCDType> typeInc;
  protected IncarnationStrategy<ASTCDAssociation> assocInc;

  public BasicAssocConfStrategy(
      ASTCDCompilationUnit refCD,
      ASTCDCompilationUnit conCD,
      IncarnationStrategy<ASTCDType> typeInc,
      IncarnationStrategy<ASTCDAssociation> assocInc) {
    this.refCD = refCD;
    this.conCD = conCD;
    this.typeInc = typeInc;
    this.assocInc = assocInc;
  }

  @Override
  public boolean checkConformance(ASTCDAssociation concrete) {
    return assocInc.getRefElements(concrete).stream()
        .allMatch(ref -> checkConformance(concrete, ref));
  }

  public boolean checkConformance(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if (check(concrete, ref) || checkReverse(concrete, ref)) {
      return true;
    } else {
      System.out.println(
          CD4CodeMill.prettyPrint(concrete, false)
              + " does not conform to "
              + CD4CodeMill.prettyPrint(ref, false));
      return false;
    }
  }

  protected boolean check(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if ((!ref.getCDAssocDir().isDefinitiveNavigableRight()
            || concrete.getCDAssocDir().isDefinitiveNavigableRight())
        && (!ref.getCDAssocDir().isDefinitiveNavigableLeft()
            || concrete.getCDAssocDir().isDefinitiveNavigableLeft())) {
      return checkReference(
              concrete.getLeftQualifiedName().getQName(), ref.getLeftQualifiedName().getQName())
          && checkCardinality(concrete.getLeft(), ref.getLeft())
          && checkReference(
              concrete.getRightQualifiedName().getQName(), ref.getRightQualifiedName().getQName())
          && checkCardinality(concrete.getRight(), ref.getRight());
    }
    return false;
  }

  public boolean checkReverse(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if ((!ref.getCDAssocDir().isDefinitiveNavigableRight()
            || concrete.getCDAssocDir().isDefinitiveNavigableLeft())
        && (!ref.getCDAssocDir().isDefinitiveNavigableLeft()
            || concrete.getCDAssocDir().isDefinitiveNavigableRight())) {
      return checkReference(
              concrete.getLeftQualifiedName().getQName(), ref.getRightQualifiedName().getQName())
          && checkCardinality(concrete.getLeft(), ref.getRight())
          && checkReference(
              concrete.getRightQualifiedName().getQName(), ref.getLeftQualifiedName().getQName())
          && checkCardinality(concrete.getRight(), ref.getLeft());
    }

    return false;
  }

  protected boolean checkCardinality(ASTCDAssocSide concrete, ASTCDAssocSide ref) {
    if (!ref.isPresentCDCardinality()) {
      return true;
    }
    if (!concrete.isPresentCDCardinality()) {
      return false;
    }
    if (ref.getCDCardinality().toCardinality().isNoUpperLimit()) {
      return ref.getCDCardinality().toCardinality().getLowerBound()
          <= concrete.getCDCardinality().getLowerBound();
    }
    if (concrete.getCDCardinality().toCardinality().isNoUpperLimit()) {
      return false;
    }
    return (ref.getCDCardinality().toCardinality().getLowerBound()
            <= concrete.getCDCardinality().getLowerBound())
        && (ref.getCDCardinality().toCardinality().getUpperBound()
            >= concrete.getCDCardinality().toCardinality().getUpperBound());
  }

  protected boolean checkReference(String concrete, String ref) {
    Optional<CDTypeSymbol> conTypeSymbol = conCD.getEnclosingScope().resolveCDTypeDown(concrete);
    Optional<CDTypeSymbol> refTypeSymbol = refCD.getEnclosingScope().resolveCDTypeDown(ref);

    if (conTypeSymbol.isPresent() && refTypeSymbol.isPresent()) {
      ASTCDType conType = conTypeSymbol.get().getAstNode();
      ASTCDType refType = refTypeSymbol.get().getAstNode();
      return typeInc.isIncarnation(conType, refType);
    }
    Log.error("0xCDD17: Could not resolve association reference!");
    return false;
  }
}
