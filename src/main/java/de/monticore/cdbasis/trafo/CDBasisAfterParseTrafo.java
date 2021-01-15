/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisHandler;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDBasisAfterParseTrafo extends CDAfterParseHelper
    implements CDBasisVisitor2, CDBasisHandler {
  protected CDBasisTraverser traverser;
  protected List<String> packageNameList = new ArrayList<>(); //default, if the model has no package

  public CDBasisAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CDBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
  }

  @Override
  public CDBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CDBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    if (node.isPresentMCPackageDeclaration()) {
      packageNameList = node.getMCPackageDeclaration().getMCQualifiedName().getPartsList();
    }
    else {
      packageNameList = Arrays.asList("de", "monticore");
    }
    node.getCDDefinition().setDefaultPackageName(Joiners.DOT.join(packageNameList));
  }

  @Override
  public void visit(ASTCDDefinition node) {
    combinePackagesWithSameName(node);

    // move all elements (except packages) which are in CDDefinition
    // to the package with the package name of the model
    moveElementsToDefaultPackage(node);
  }

  protected void combinePackagesWithSameName(ASTCDDefinition node) {
    final List<ASTCDPackage> packages = node
        .streamCDElements()
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

  protected void moveElementsToDefaultPackage(ASTCDDefinition node) {
    final List<ASTCDElement> elementsInCDDefinition = node
        .streamCDElements()
        .filter(e -> !(e instanceof ASTCDPackage))
        .collect(Collectors.toList());

    // find the package with the package of the model
    Optional<ASTCDPackage> defaultPackage = node
        .streamCDElements()
        .filter(e -> e instanceof ASTCDPackage)
        .map(e -> (ASTCDPackage) e)
        .filter(e -> e
            .getMCQualifiedName()
            .getQName()
            .equals(Joiners.DOT.join(packageNameList)))
        .findFirst();

    if (!defaultPackage.isPresent()) {
      defaultPackage = Optional.of(CDBasisMill.cDPackageBuilder()
          .setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder()
              .setPartsList(packageNameList)
              .build())
          .build());
      node.addCDPackage(0, defaultPackage.get()); // add the default package as first package
    }

    defaultPackage.get().addAllCDElements(elementsInCDDefinition);
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
