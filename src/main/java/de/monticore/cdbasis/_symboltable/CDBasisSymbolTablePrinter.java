/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.basictypesymbols._symboltable.IBasicTypeSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

import java.util.List;

public class CDBasisSymbolTablePrinter extends CDBasisSymbolTablePrinterTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public CDBasisSymbolTablePrinter() {
  }

  public CDBasisSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  @Override
  public void serializeCDTypeSuperTypes(List<SymTypeExpression> superTypes) {
    SymTypeExpressionDeSer.serializeMember(printer, "superTypes", superTypes);
  }

  public void traverse(ITypeSymbolsScope node) {
    if (!node.getLocalOOTypeSymbols().isEmpty()) {
      node.getLocalOOTypeSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    if (!node.getLocalFieldSymbols().isEmpty()) {
      node.getLocalFieldSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    if (!node.getLocalMethodSymbols().isEmpty()) {
      node.getLocalMethodSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    traverse((de.monticore.types.basictypesymbols._symboltable.IBasicTypeSymbolsScope) node);
  }

  public void traverse(IBasicTypeSymbolsScope node) {
    if (!node.getLocalTypeSymbols().isEmpty()) {
      node.getLocalTypeSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    if (!node.getLocalTypeVarSymbols().isEmpty()) {
      node.getLocalTypeVarSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    if (!node.getLocalVariableSymbols().isEmpty()) {
      node.getLocalVariableSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    if (!node.getLocalFunctionSymbols().isEmpty()) {
      node.getLocalFunctionSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    getRealThis().traverse((de.monticore.mcbasics._symboltable.IMCBasicsScope) node);
  }

  @Override
  public void traverse(ICDBasisScope node) {
    if (!node.getLocalCDTypeSymbols().isEmpty()) {
      node.getLocalCDTypeSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    getRealThis().traverse((de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope) node);
    getRealThis().traverse((de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope) node);
    getRealThis().traverse((de.monticore.types.mcbasictypes._symboltable.IMCBasicTypesScope) node);
    traverse((de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope) node);
    getRealThis().traverse((de.monticore.umlstereotype._symboltable.IUMLStereotypeScope) node);
    getRealThis().traverse((de.monticore.umlmodifier._symboltable.IUMLModifierScope) node);
  }

  @Override
  public void traverse(CDTypeSymbol node) {
    if(node.getSpannedScope().isExportingSymbols() && node.getSpannedScope().getSymbolsSize() > 0) {
      printer.beginArray("symbols");
      node.getSpannedScope().accept(getRealThis());
      printer.endArray();
    }
  }
}
