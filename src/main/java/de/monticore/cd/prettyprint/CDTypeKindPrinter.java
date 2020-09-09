/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.prettyprint;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;

public class CDTypeKindPrinter extends PrettyPrintUtil
    implements CDInterfaceAndEnumVisitor {
  protected final IndentPrinter printer;
  protected CDInterfaceAndEnumVisitor realThis;

  public CDTypeKindPrinter() {
    this(new IndentPrinter());
  }

  public CDTypeKindPrinter(IndentPrinter printer) {
    this.printer = printer;
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
  public void visit(ASTCDClass node) {
    print("class");
  }

  @Override
  public void visit(ASTCDInterface node) {
    print("interface");
  }

  @Override
  public void visit(ASTCDEnum node) {
    print("enum");
  }

  public String print(ASTCDType type) {
    type.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String print(OOTypeSymbol type) {
    if (type.isIsClass()) {
      print("class");
    }
    if (type.isIsInterface()) {
      print("interface");
    }
    if (type.isIsEnum()) {
      print("enum");
    }

    return getPrinter().getContent();
  }

  public String print(TypeSymbol type) {
    if (type instanceof OOTypeSymbol) {
      return print((OOTypeSymbol) type);
    }

    return EMPTY_STRING;
  }

  public String print(SymTypeExpression expression) {
    return print(expression.getTypeInfo());
  }
}
