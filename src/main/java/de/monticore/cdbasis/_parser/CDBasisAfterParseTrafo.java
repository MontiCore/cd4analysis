/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._parser;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDBasisAfterParseTrafo extends CDAfterParseHelper
    implements CDBasisVisitor {
  protected CDBasisVisitor realThis;

  public CDBasisAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CDBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
    setRealThis(this);
  }

  @Override
  public CDBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTCDDefinition node) {
    combinePackagesWithSameName(node);

    // move all elements (except packages) which are in CDDefinition to the anonymous package
    // moveElementsToAnonymousPackage(node);
  }

  protected void combinePackagesWithSameName(ASTCDDefinition node) {
    final List<ASTCDPackage> packages = node
        .getCDElementList().stream()
        .filter(e -> e instanceof ASTCDPackage)
        .map(e -> (ASTCDPackage) e)
        .collect(Collectors.toList());

    final List<ASTCDPackage> duplicates = CoCoHelper.findDuplicatesBy(packages, (e) -> e.getMCQualifiedName().getQName());
    node.getCDElementList().removeAll(duplicates);
    duplicates.forEach(e ->
        packages.stream()
            .filter(p -> p.getMCQualifiedName().getQName().equals(e.getMCQualifiedName().getQName()))
            .findFirst()
            .ifPresent(pa -> pa.addAllCDElements(e.getCDElementList())));
  }

  protected void moveElementsToAnonymousPackage(ASTCDDefinition node) {
    final List<ASTCDElement> elementsInCDDefinition = node
        .getCDElementList().stream()
        .filter(e -> !(e instanceof ASTCDPackage))
        .collect(Collectors.toList());

    // find the anonymous (name = "") package
    Optional<ASTCDPackage> anonymousPackage = node
        .getCDElementList().stream()
        .filter(e -> e instanceof ASTCDPackage)
        .map(e -> (ASTCDPackage) e)
        .filter(e -> e.getMCQualifiedName().getQName().equals(""))
        .findFirst();

    if (!anonymousPackage.isPresent()) {
      anonymousPackage = Optional.of(CDBasisMill.cDPackageBuilder()
          .setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder()
              .setPartList(Collections.singletonList(""))
              .build())
          .build());
    }

    anonymousPackage.get().addAllCDElements(elementsInCDDefinition);
    node.removeAllCDElements(elementsInCDDefinition);
  }

  @Override
  public void visit(ASTCDClass node) {
    typeStack.push(node);
    removedDirectCompositions.clear();
  }

  @Override
  public void endVisit(ASTCDClass node) {
    node.removeAllCDMembers(removedDirectCompositions);
    typeStack.pop();
  }

  @Override
  public void visit(ASTCDPackage node) {
    createdAssociations.clear();
  }

  @Override
  public void endVisit(ASTCDPackage node) {
    node.addAllCDElements(createdAssociations);
  }
}
