/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd._visitor;

import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code._visitor.CD4CodeVisitor2;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisTraverser;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumTraverser;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import java.util.*;
import java.util.stream.Collectors;

/** Visitor to collect all CDMembers of specific types */
public class CDMemberVisitor
    implements CD4CodeVisitor2,
        CDBasisVisitor2,
        CDInterfaceAndEnumVisitor2,
        CD4CodeBasisVisitor2,
        CDAssociationVisitor2 {
  protected final Set<Options> options;
  protected final List<ASTCDMember> elements;

  public CDMemberVisitor(Options... options) {
    this.options = new HashSet<>(Arrays.asList(options));
    if (this.options.isEmpty()) {
      this.options.add(Options.ALL);
    }

    this.elements = new ArrayList<>();
  }

  public List<ASTCDMember> getCDMemberList() {
    return elements;
  }

  @Override
  public void visit(ASTCDAttribute node) {
    if (options.contains(Options.ALL)
        || options.contains(Options.FIELDS)
        || options.contains(Options.ATTRIBUTES)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDMethodSignature node) {
    if (options.contains(Options.ALL) || options.contains(Options.METHOD_SIGNATURES)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDMethod node) {
    if (options.contains(Options.ALL)
        || options.contains(Options.METHOD_SIGNATURES)
        || options.contains(Options.METHODS)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDConstructor node) {
    if (options.contains(Options.ALL)
        || options.contains(Options.METHOD_SIGNATURES)
        || options.contains(Options.CONSTRUCTORS)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDRole node) {
    if (options.contains(Options.ALL)
        || options.contains(Options.FIELDS)
        || options.contains(Options.ROLES)) {
      elements.add(node);
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends ASTCDMember> List<T> getElements() {
    return elements.stream().map(e -> (T) e).collect(Collectors.toList());
  }

  public enum Options {
    ALL,
    FIELDS,
    ATTRIBUTES,
    ROLES,
    METHOD_SIGNATURES,
    CONSTRUCTORS,
    METHODS,
  }

  public void run(ASTCDBasisNode ast) { // TBD: How to achieve this without downcasts?
    CDBasisTraverser t = CDBasisMill.traverser();
    t.add4CDBasis(this);

    if (t instanceof CD4CodeTraverser) {
      ((CD4CodeTraverser) t).add4CD4Code(this);
    }
    if (t instanceof CDAssociationTraverser) {
      ((CDAssociationTraverser) t).add4CDAssociation(this);
    }
    if (t instanceof CDInterfaceAndEnumTraverser) {
      ((CDInterfaceAndEnumTraverser) t).add4CDInterfaceAndEnum(this);
    }
    if (t instanceof CD4CodeBasisTraverser) {
      ((CD4CodeBasisTraverser) t).add4CD4CodeBasis(this);
    }
    if (t instanceof CD4CodeTraverser) {
      ((CD4CodeTraverser) t).add4CD4Code(this);
    }
    ast.accept(t);
  }
}
