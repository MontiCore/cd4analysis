/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Iterator;

public class CDInterfaceAndEnumPrettyPrinter extends PrettyPrintUtil
    implements CDInterfaceAndEnumVisitor {
  protected CDInterfaceAndEnumVisitor realThis;

  public CDInterfaceAndEnumPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDInterfaceAndEnumPrettyPrinter(IndentPrinter printer) {
    super(printer);
    setRealThis(this);
  }

  @Override
  public CDInterfaceAndEnumVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDInterfaceAndEnumVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTCDInterface node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());

    node.getModifier().accept(getRealThis());
    print("interface " + node.getName());
    if (node.isPresentCDExtendUsage()) {
      node.getCDExtendUsage().accept(getRealThis());
    }

    if (!node.isEmptyCDMembers()) {
      print(" {");
      CommentPrettyPrinter.printPostComments(node, getPrinter());
      println();
      indent();
    }
  }

  @Override
  public void traverse(ASTCDInterface node) {
    node.getCDMemberList().forEach(m -> m.accept(getRealThis()));
  }

  @Override
  public void endVisit(ASTCDInterface node) {
    if (!node.isEmptyCDMembers()) {
      unindent();
      println("}");
    }
    else {
      print(";");
      CommentPrettyPrinter.printPostComments(node, getPrinter());
      println();
    }
  }

  @Override
  public void visit(ASTCDEnum node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());

    node.getModifier().accept(getRealThis());
    print("enum " + node.getName());
    if (node.isPresentCDInterfaceUsage()) {
      node.getCDInterfaceUsage().accept(getRealThis());
    }

    if (!node.isEmptyCDMembers() || !node.isEmptyCDEnumConstants()) {
      print(" {");
      CommentPrettyPrinter.printPostComments(node, getPrinter());
      println();
      indent();
    }
  }

  @Override
  public void traverse(ASTCDEnum node) {
    final Iterator<ASTCDEnumConstant> iterator = node.getCDEnumConstantList().iterator();

    if (!iterator.hasNext()) {
      println(";");
    }
    while (iterator.hasNext()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
      iterator.next().accept(getRealThis());
      if (iterator.hasNext()) {
        print(",");
      }
      else {
        print(";");
      }
      CommentPrettyPrinter.printPostComments(node, getPrinter());
      println();
    }

    node.getCDMemberList().forEach(m -> m.accept(getRealThis()));
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    if (!node.isEmptyCDMembers() || !node.isEmptyCDEnumConstants()) {
      unindent();
      println("}");
    }
    else {
      print(";");
      CommentPrettyPrinter.printPostComments(node, getPrinter());
      println();
    }
  }

  @Override
  public void visit(ASTCDEnumConstant node) {
    print(node.getName());
  }
}
