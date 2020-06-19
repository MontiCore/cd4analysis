/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.expressions.bitexpressions.BitExpressionsMill;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._symboltable.MCCommonLiteralsSymbolTableCreator;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.umlmodifier.UMLModifierMill;
import de.monticore.umlstereotype.UMLStereotypeMill;

public class CD4AnalysisSymbolTableCreatorDelegator
    extends CD4AnalysisSymbolTableCreatorDelegatorTOP {

  public CD4AnalysisSymbolTableCreatorDelegator(CD4AnalysisGlobalScope globalScope) {
    super(globalScope);
    init();
  }

  public void init() {
    final MCCommonLiteralsSymbolTableCreator commonLiteralsSymbolTableCreator = MCCommonLiteralsMill.mCCommonLiteralsSymbolTableCreatorBuilder().build();
    commonLiteralsSymbolTableCreator.setRealThis(getRealThis());
    setMCLiteralsBasisVisitor(commonLiteralsSymbolTableCreator);
    setMCCommonLiteralsVisitor(commonLiteralsSymbolTableCreator);

    setExpressionsBasisVisitor(ExpressionsBasisMill.expressionsBasisSymbolTableCreatorBuilder().build());
    setBitExpressionsVisitor(BitExpressionsMill.bitExpressionsSymbolTableCreatorBuilder().build());
    setCommonExpressionsVisitor(CommonExpressionsMill.commonExpressionsSymbolTableCreatorBuilder().build());
    setMCBasicTypesVisitor(MCBasicTypesMill.mCBasicTypesSymbolTableCreatorBuilder().build());
    setMCCollectionTypesVisitor(MCCollectionTypesMill.mCCollectionTypesSymbolTableCreatorBuilder().build());
    setMCSimpleGenericTypesVisitor(MCSimpleGenericTypesMill.mCSimpleGenericTypesSymbolTableCreatorBuilder().build());
    setUMLModifierVisitor(UMLModifierMill.uMLModifierDelegatorVisitorBuilder().build());
    setUMLStereotypeVisitor(UMLStereotypeMill.uMLStereotypeDelegatorVisitorBuilder().build());

    // ------------ CD --------------
    setCDBasisVisitor(CDBasisMill.cDBasisSymbolTableCreatorBuilder().build());
    setCDInterfaceAndEnumVisitor(CDInterfaceAndEnumMill.cDInterfaceAndEnumSymbolTableCreatorBuilder().build());
    setCDAssociationVisitor(CDAssociationMill.cDAssociationSymbolTableCreatorBuilder().build());
    setCD4AnalysisVisitor(CD4AnalysisMill.cD4AnalysisSymbolTableCreatorBuilder().build());
  }

}
