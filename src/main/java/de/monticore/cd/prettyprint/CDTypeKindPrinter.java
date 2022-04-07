/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.prettyprint;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumTraverser;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;

public class CDTypeKindPrinter extends PrettyPrintUtil
    implements CDInterfaceAndEnumVisitor2, CDBasisVisitor2 {
  protected final IndentPrinter printer;
  protected boolean followingSpace;

  public CDTypeKindPrinter() {
    this(false);
  }

  public CDTypeKindPrinter(boolean followingSpace) {
    this(new IndentPrinter());
  }

  public CDTypeKindPrinter(IndentPrinter printer) {
    this(printer, false);
  }

  public CDTypeKindPrinter(IndentPrinter printer, boolean followingSpace) {
    this.printer = printer;
  }

  @Override
  public void visit(ASTCDClass node) {
    print("class");
    if (followingSpace) {
      print(" ");
    }
  }

  @Override
  public void visit(ASTCDInterface node) {
    print("interface");
    if (followingSpace) {
      print(" ");
    }
  }

  @Override
  public void visit(ASTCDEnum node) {
    print("enum");
    if (followingSpace) {
      print(" ");
    }
  }

  public String print(ASTCDType type) {
    CDInterfaceAndEnumTraverser t = CDInterfaceAndEnumMill
        .traverser();
    t.add4CDInterfaceAndEnum(this);
    type.accept(t);
    return getPrinter().getContent();
  }

  public String print(OOTypeSymbol type) {
    if (type.isIsClass()) {
      print("class");
      if (followingSpace) {
        print(" ");
      }
    }
    if (type.isIsInterface()) {
      print("interface");
      if (followingSpace) {
        print(" ");
      }
    }
    if (type.isIsEnum()) {
      print("enum");
      if (followingSpace) {
        print(" ");
      }
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
