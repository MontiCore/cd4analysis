/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.visitor;

import de.monticore.cd4code._visitor.CD4CodeVisitor;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Visitor to collect all CDElements of specific types
 */
public class CDElementVisitor
    implements CD4CodeVisitor {
  protected final Set<Options> options;
  protected final List<ASTCDElement> elements;
  protected CD4CodeVisitor realThis;

  public CDElementVisitor(Options... options) {
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

  public List<ASTCDElement> getCDElementList() {
    return elements;
  }

  @Override
  public void visit(ASTCDPackage node) {
    if (options.contains(Options.PACKAGES)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDClass node) {
    if (options.contains(Options.CLASSES)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDInterface node) {
    if (options.contains(Options.INTERFACES)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDEnum node) {
    if (options.contains(Options.ENUMS)) {
      elements.add(node);
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends ASTCDElement> List<T> getElements() {
    return elements.stream().map(e -> (T) e).collect(Collectors.toList());
  }

  public enum Options {
    PACKAGES,
    CLASSES,
    INTERFACES,
    ENUMS
  }
}
