/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._ast;

import de.monticore.cd.CDMill;
import de.monticore.cd._visitor.CDElementVisitor;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ASTCDDefinition extends ASTCDDefinitionTOP {
  protected String defaultPackageName = "";

  public <T extends ASTCDElement> List<T> getCDElementsList(CDElementVisitor.Options... options) {
    final CDElementVisitor cdElementVisitor = CDMill.cDElementVisitor(options);
    this.accept(cdElementVisitor);
    return cdElementVisitor.getElements();
  }

  public <T extends ASTCDElement> Iterator<T> iterateCDElements(CDElementVisitor.Options... options) {
    return this.<T>getCDElementsList(options).iterator();
  }

  public <T extends ASTCDElement> Stream<T> streamCDElements(CDElementVisitor.Options... options) {
    return this.<T>getCDElementsList(options).stream();
  }

  public int sizeCDElements(CDElementVisitor.Options... options) {
    return getCDElementsList(options).size();
  }

  public List<ASTCDPackage> getCDPackagesList() {
    final CDElementVisitor cdElementVisitor = CDMill.cDElementVisitor(CDElementVisitor.Options.PACKAGES);
    this.accept(cdElementVisitor);
    return cdElementVisitor.getElements();
  }

  public List<ASTCDClass> getCDClassesList() {
    final CDElementVisitor cdElementVisitor = CDMill.cDElementVisitor(CDElementVisitor.Options.CLASSES);
    this.accept(cdElementVisitor);
    return cdElementVisitor.getElements();
  }

  public List<ASTCDInterface> getCDInterfacesList() {
    final CDElementVisitor cdElementVisitor = CDMill.cDElementVisitor(CDElementVisitor.Options.INTERFACES);
    this.accept(cdElementVisitor);
    return cdElementVisitor.getElements();
  }

  public List<ASTCDEnum> getCDEnumsList() {
    final CDElementVisitor cdElementVisitor = CDMill.cDElementVisitor(CDElementVisitor.Options.ENUMS);
    this.accept(cdElementVisitor);
    return cdElementVisitor.getElements();
  }

  public List<ASTCDAssociation> getCDAssociationsList() {
    final CDElementVisitor cdElementVisitor = CDMill.cDElementVisitor(CDElementVisitor.Options.ASSOCIATIONS);
    this.accept(cdElementVisitor);
    return cdElementVisitor.getElements();
  }

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
    return getPackageWithName(getCDPackagesList(), packageName);
  }

  public static Optional<ASTCDPackage> getPackageWithName(List<ASTCDPackage> packages, String packageName) {
    return packages
        .stream()
        .filter(p -> p.getMCQualifiedName().getQName().equals(packageName))
        .findAny();
  }

  public Optional<ASTCDPackage> getDefaultPackage() {
    return getDefaultPackage(getCDPackagesList());
  }

  public Optional<ASTCDPackage> getDefaultPackage(List<ASTCDPackage> packages) {
    return getPackageWithName(packages, getDefaultPackageName());
  }

  public ASTCDPackage getOrCreateDefaultPackage() {
    return getOrCreatePackage(defaultPackageName);
  }

  public ASTCDPackage getOrCreatePackage(String packageName) {
    final Optional<ASTCDPackage> pkg = getPackageWithName(packageName);
    if (pkg.isPresent()) {
      return pkg.get();
    }
    final ASTCDPackage createdPkg = CDBasisMill
        .cDPackageBuilder()
        .setMCQualifiedName(MCBasicTypesMill
            .mCQualifiedNameBuilder()
            .setPartsList(MCQualifiedNameFacade.createPartList(packageName))
            .build())
        .build();
    super.addCDElements(createdPkg);
    createdPkg.setEnclosingScope(this.getEnclosingScope());
    return createdPkg;
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
      return specificPackage.addCDElements(element);
    }
    else {
      specificPackage.addCDElements(index, element);
      return true;
    }
  }

  @Override
  public boolean addCDElements(ASTCDElement element) {
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
    super.addCDElements(index, p);
  }

  @Override
  public void addCDElements(int index, ASTCDElement element) {
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
