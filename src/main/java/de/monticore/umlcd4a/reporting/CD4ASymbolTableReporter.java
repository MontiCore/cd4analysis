/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.umlcd4a.reporting;

import java.util.List;

import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.SymbolTableReporter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.Symbol;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDMethodSymbol;
import de.monticore.umlcd4a.symboltable.CDQualifierSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.Cardinality;
import de.monticore.umlcd4a.symboltable.Stereotype;

public class CD4ASymbolTableReporter extends SymbolTableReporter {
  
  /**
   * Constructor for de.monticore.umlcd4a.reporting.CD4ASymbolTableReporter.
   * 
   * @param outputDir
   * @param modelName
   * @param repository
   */
  public CD4ASymbolTableReporter(
      String outputDir,
      String modelName,
      ReportingRepository repository) {
    super(outputDir, modelName, repository);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.reporter.SymbolTableReporter#reportAttributes(de.monticore.symboltable.Symbol,
   * de.monticore.prettyprint.IndentPrinter)
   */
  @Override
  protected void reportAttributes(Symbol sym, IndentPrinter printer) {
    super.reportAttributes(sym, printer);
    if (sym instanceof CDAssociationSymbol) {
      reportAttributes((CDAssociationSymbol) sym, printer);
    }
    else if (sym instanceof CDFieldSymbol) {
      reportAttributes((CDFieldSymbol) sym, printer);
    }
    else if (sym instanceof CDMethodSymbol) {
      reportAttributes((CDMethodSymbol) sym, printer);
    }
    else if (sym instanceof CDQualifierSymbol) {
      reportAttributes((CDQualifierSymbol) sym, printer);
    }
    else if (sym instanceof CDTypeSymbol) {
      reportAttributes((CDTypeSymbol) sym, printer);
    }
  }
  
  private void reportAttributes(CDAssociationSymbol sym, IndentPrinter printer) {
    if (sym.getAssocName().isPresent()) {
      printer.println("assocName = \"" + sym.getAssocName().get() + "\";");
    }
    printer.println("relationShip = \"" + sym.getRelationship().name() + "\";");
    if (sym.getRole().isPresent()) {
      printer.println("role = \"" + sym.getRole().get() + "\";");
    }
    printer.println("sourceCard = \"" + printCardinality(sym.getSourceCardinality()) + "\";");
    printer.println("targetCard = \"" + printCardinality(sym.getTargetCardinality()) + "\";");
    printer.println("isBidirectional = " + sym.isBidirectional() + ";");
    printer.println("isDerived = " + sym.isDerived() + ";");
    reportStereotypes(sym.getStereotypes());
  }
  
  private void reportAttributes(CDFieldSymbol sym, IndentPrinter printer) {
    reportCommonJFieldAttributes(sym, printer);
    printer.println("isDerived = " + sym.isDerived() + ";");
    printer.println("isEnumConstant = " + sym.isEnumConstant() + ";");
    printer.println("isInitialized = " + sym.isInitialized() + ";");
    printer.println("isReadOnly = " + sym.isReadOnly() + ";");
    reportStereotypes(sym.getStereotypes());
  }
  
  private void reportAttributes(CDMethodSymbol sym, IndentPrinter printer) {
    reportCommonJMethodAttributes(sym, printer);
    reportStereotypes(sym.getStereotypes());
  }
  
  private void reportAttributes(CDQualifierSymbol sym, IndentPrinter printer) {
    printer.println("isNameQualifier = " + sym.isNameQualifier() + ";");
    printer.println("isTypeQualifier = " + sym.isTypeQualifier() + ";");
  }
  
  private void reportAttributes(CDTypeSymbol sym, IndentPrinter printer) {
    reportCommonJTypeAttributes(sym, printer);
    if (!sym.getAssociations().isEmpty()) {
      printer.println("associations = " );
      String delim = "";
      for (CDAssociationSymbol assocSymbol: sym.getAssociations()) {
        printer.print(delim);
        reportSymbol(assocSymbol, printer);
        delim = ", ";
      }
      printer.println(";");
    }
    reportStereotypes(sym.getStereotypes());
  }
    
  private void reportStereotypes(List<Stereotype> stereotypes) {
    // TODO Auto-generated method stub
    
  }
  
  private String printCardinality(Cardinality card) {
    return "[" + card.printMin() + ".." + card.printMax() + "]";
  }
}
