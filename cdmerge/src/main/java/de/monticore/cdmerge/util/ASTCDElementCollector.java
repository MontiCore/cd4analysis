/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/** Collects the named elements of an CD AST and makes them accessible more easily */
public class ASTCDElementCollector
    implements CDBasisVisitor2, CDAssociationVisitor2, CDInterfaceAndEnumVisitor2 {

  private List<String> ownPackage;

  private List<String> imports;

  private ASTCDHelper helper;

  protected CD4CodeTraverser traverser;

  public ASTCDElementCollector(ASTCDHelper helper) {
    this.helper = helper;
    this.ownPackage = new LinkedList<String>();
    this.imports = new LinkedList<String>();

    this.traverser = CD4CodeMill.inheritanceTraverser();

    this.traverser.add4CDBasis(this);
    this.traverser.add4CDAssociation(this);
    this.traverser.add4CDInterfaceAndEnum(this);
  }

  public void collect(ASTCDCompilationUnit cd) {
    cd.accept(traverser);
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    if (node.sizePackage() > 0) {
      this.ownPackage = new ArrayList<>(node.getCDPackageList());
      // FIXME Own Package as Default Package???
      this.ownPackage.add(node.getCDDefinition().getName());
    }
    if (!node.getMCImportStatementList().isEmpty()) {
      for (ASTMCImportStatement imp : node.getMCImportStatementList()) {
        // TODO Handle .* Imports is possibly not supported/needed?
        this.imports.add(imp.getQName().toString());
      }
    }
  }

  @Override
  public void visit(ASTCDPackage node) {
    this.helper.addPackageScope(node);
  }

  @Override
  public void visit(ASTCDClass node) {
    helper.addClass(node.getName(), node);
    helper.addAttributesForClass(node.getName(), node.getCDAttributeList());

    // Add Superclass if defined in this CD (i.e. we have an ASTNode for
    // it)
    if (node.getSuperclassList().size() > 0) {
      helper.addSuperclass(node.getName(), node.printSuperclasses());
    }
    // Add Interfaces if defined in this CD (i.e. we have an ASTNode for
    // it)
    if (node.getInterfaceList().size() > 0) {
      helper.addSuperInterfaces(
          node.getName(), new ArrayList<>(Arrays.asList(node.printInterfaces().split(","))));
    }
  }

  @Override
  public void visit(ASTCDInterface node) {
    helper.addInterface(node.getName(), node);
    // Add Interfaces if defined in this CD (i.e. we have an ASTNode for
    // it)
    if (node.getInterfaceList().size() > 0) {
      helper.addSuperInterfaces(
          node.getName(), new ArrayList<>(Arrays.asList(node.printInterfaces().split(","))));
    }
  }

  @Override
  public void visit(ASTCDEnum node) {
    helper.addEnum(node.getName(), node);
  }

  @Override
  public void visit(ASTCDAssociation node) {
    if (node.isPresentName()) {
      helper.addNamedAssociation(node.getName(), node);
    }

    if ((node.getLeftReferenceName().size() == 1 || isSamePackage(node.getLeftReferenceName()))
            && node.getRightReferenceName().size() == 1
        || isSamePackage(node.getRightReferenceName())) {
      // It could still be a imported type, these will be filtered out
      // later. Types with same or imported package will be normalized to
      // just type name
      helper.addAssociationForTypeReference(
          node.getLeftReferenceName().get(node.getLeftReferenceName().size() - 1), node);
      helper.addAssociationForTypeReference(
          node.getRightReferenceName().get(node.getRightReferenceName().size() - 1), node);

      helper.addInternalAssociation(node);
    } else {
      helper.addAssociationWithExternalReferences(node);
    }
  }

  private boolean isSamePackage(List<String> referenceParts) {
    if (referenceParts.size() == 1) {
      // simple, non-qualified type in same CD
      return false;
    }
    if (ownPackage.size() > 0 && ownPackage.size() == referenceParts.size() - 1) {
      for (int i = 0; i < referenceParts.size() - 1; i++) {
        if (!referenceParts.get(i).equalsIgnoreCase(ownPackage.get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
