/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._ast;

import de.monticore.cd._ast.MCQualifiedNameFacade;
import de.monticore.cd.visitor.CDElementVisitor;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ASTCDDefinition extends ASTCDDefinitionTOP {
  protected String defaultPackageName = "";

  public List<String> getDefaultPackageNameList() {
    return MCQualifiedNameFacade.createPartList(this.defaultPackageName);
  }

  public String getDefaultPackageName() {
    return this.defaultPackageName;
  }

  public void setDefaultPackageName(String defaultPackageName) {
    this.defaultPackageName = defaultPackageName;
  }

  public Optional<ASTCDPackage> getPackageWithName(String packageName) {
    return getPackageWithName(getCDPackageList(), packageName);
  }

  public Optional<ASTCDPackage> getPackageWithName(List<ASTCDPackage> packages, String packageName) {
    return packages
        .stream()
        .filter(p -> p.getMCQualifiedName().getQName().equals(packageName))
        .findAny();
  }

  public Optional<ASTCDPackage> getDefaultPackage() {
    return getDefaultPackage(getCDPackageList());
  }

  public Optional<ASTCDPackage> getDefaultPackage(List<ASTCDPackage> packages) {
    return getPackageWithName(packages, getDefaultPackageName());
  }

  public ASTCDPackage getOrCreateDefaultPackage() {
    return getOrCreatePackage(defaultPackageName);
  }

  public ASTCDPackage getOrCreatePackage(String packageName) {
    final Optional<ASTCDPackage> defaultPackage = getPackageWithName(getDefaultPackageName());
    if (defaultPackage.isPresent()) {
      return defaultPackage.get();
    }
    final ASTCDPackage createdDefaultPackage = CDBasisMill
        .cDPackageBuilder()
        .setMCQualifiedName(MCBasicTypesMill
            .mCQualifiedNameBuilder()
            .setPartList(MCQualifiedNameFacade.createPartList(packageName))
            .build())
        .build();
    super.addCDElement(createdDefaultPackage);
    createdDefaultPackage.setEnclosingScope(this.getEnclosingScope());
    return createdDefaultPackage;
  }

  public List<ASTCDPackage> getCDPackageList() {
    final CDElementVisitor cdElementVisitor = new CDElementVisitor(CDElementVisitor.Options.PACKAGES);
    this.accept(cdElementVisitor);
    return cdElementVisitor.getElements();
  }

  public boolean addCDElementToPackageWithName(ASTCDElement element) {
    return addCDElementToPackageWithName(element, this.defaultPackageName);
  }

  public boolean addCDElementToPackageWithName(int index, ASTCDElement element) {
    return addCDElementToPackageWithName(index, element, defaultPackageName);
  }

  public boolean addCDElementToPackageWithName(ASTCDElement element, String packageName) {
    return addCDElementToPackageWithName(-1, element, packageName);
  }

  public boolean addCDElementToPackageWithName(int index, ASTCDElement element, String packageName) {
    // should be added to the package
    final ASTCDPackage specificPackage = getOrCreatePackage(packageName);
    if (index < 0) {
      return specificPackage.addCDElement(element);
    }
    else {
      specificPackage.addCDElement(index, element);
      return true;
    }
  }

  @Override
  public boolean addCDElement(ASTCDElement element) {
    return addCDElementToPackageWithName(element);
  }

  public boolean addCDElementToPackage(ASTCDElement element, String packageName) {
    return addCDElementToPackageWithName(element, packageName);
  }

  @Override
  public boolean addAllCDElements(Collection<? extends ASTCDElement> collection) {
    collection.forEach(this::addCDElementToPackageWithName);
    return true;
  }

  public boolean addCDElementToPackage(Collection<? extends ASTCDElement> collection, String packageName) {
    collection.forEach(e -> addCDElementToPackageWithName(e, packageName));
    return true;
  }

  public void addCDPackage(int index, ASTCDPackage p) {
    super.addCDElement(index, p);
  }

  @Override
  public void addCDElement(int index, ASTCDElement element) {
    addCDElementToPackageWithName(index, element);
  }

  public boolean addCDElementToPackage(int index, ASTCDElement element, String packageName) {
    return addCDElementToPackageWithName(index, element, packageName);
  }

  @Override
  public boolean addAllCDElements(int index, Collection<? extends ASTCDElement> collection) {
    return addCDElementToPackage(index, collection, defaultPackageName);
  }

  public boolean addCDElementToPackage(int index, Collection<? extends ASTCDElement> collection, String packageName) {
    // count index up, because every element will be inserted separately and
    // therefore the latest element would be at place index
    int i = index;
    for (ASTCDElement element : collection) {
      addCDElementToPackageWithName(i, element, packageName);
      ++i;
    }
    return true;
  }
}
