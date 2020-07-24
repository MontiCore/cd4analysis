/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._visitor;

import de.monticore.cd4code._visitor.CD4CodeVisitor;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDMember;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Visitor to collect all CDMembers of specific types
 */
public class CDMemberVisitor
    implements CD4CodeVisitor {
  protected final Set<Options> options;
  protected final List<ASTCDMember> elements;
  protected CD4CodeVisitor realThis;

  public CDMemberVisitor(Options... options) {
    this.options = new HashSet<>(Arrays.asList(options));
    this.elements = new ArrayList<>();
    setRealThis(this);
  }

  @Override
  public CD4CodeVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4CodeVisitor realThis) {
    this.realThis = realThis;
  }

  public List<ASTCDMember> getCDMemberList() {
    return elements;
  }

  @Override
  public void visit(ASTCDAttribute node) {
    if (options.contains(Options.ALL) || options.contains(Options.FIELDS) || options.contains(Options.ATTRIBUTES)) {
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
    if (options.contains(Options.ALL) || options.contains(Options.METHOD_SIGNATURES) || options.contains(Options.METHODS)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDConstructor node) {
    if (options.contains(Options.ALL) || options.contains(Options.METHOD_SIGNATURES) || options.contains(Options.CONSTRUCTORS)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDRole node) {
    if (options.contains(Options.ALL) || options.contains(Options.FIELDS) || options.contains(Options.ROLES)) {
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
}
