/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.visitor;

import de.monticore.cd4code._visitor.CD4CodeVisitor;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDMember;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Visitor to collect all CDMembers of specific types
 */
public class CDMemberVisitor
    implements CD4CodeVisitor {
  public enum Options {
    ATTRIBUTE,
    METHOD_SIGNATURE,
    CONSTRUCTOR,
    METHOD;
  }

  protected CD4CodeVisitor realThis;

  protected Set<Options> options;
  protected List<ASTCDMember> elements;

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
    if (options.contains(Options.ATTRIBUTE)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDMethodSignature node) {
    if (options.contains(Options.METHOD_SIGNATURE)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDMethod node) {
    if (options.contains(Options.METHOD_SIGNATURE) || options.contains(Options.METHOD)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDConstructor node) {
    if (options.contains(Options.METHOD_SIGNATURE) || options.contains(Options.CONSTRUCTOR)) {
      elements.add(node);
    }
  }

  public <T extends ASTCDMember> List<T> getElements() {
    return elements.stream().map(e -> (T) e).collect(Collectors.toList());
  }
}
