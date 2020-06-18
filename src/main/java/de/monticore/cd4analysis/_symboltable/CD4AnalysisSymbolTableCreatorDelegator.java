/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCreator;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCreator;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCreator;
import de.monticore.expressions.bitexpressions.BitExpressionsMill;
import de.monticore.expressions.bitexpressions._symboltable.BitExpressionsSymbolTableCreator;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._symboltable.CommonExpressionsSymbolTableCreator;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymbolTableCreator;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._symboltable.MCCommonLiteralsSymbolTableCreator;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._symboltable.MCBasicTypesSymbolTableCreator;
import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._symboltable.MCCollectionTypesSymbolTableCreator;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._symboltable.MCSimpleGenericTypesSymbolTableCreator;

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

    final ExpressionsBasisSymbolTableCreator expressionsBasisSymbolTableCreator = ExpressionsBasisMill.expressionsBasisSymbolTableCreatorBuilder().build();
    expressionsBasisSymbolTableCreator.setRealThis(getRealThis());
    setExpressionsBasisVisitor(expressionsBasisSymbolTableCreator);

    final BitExpressionsSymbolTableCreator bitExpressionsSymbolTableCreator = BitExpressionsMill.bitExpressionsSymbolTableCreatorBuilder().build();
    bitExpressionsSymbolTableCreator.setRealThis(getRealThis());
    setBitExpressionsVisitor(bitExpressionsSymbolTableCreator);

    final CommonExpressionsSymbolTableCreator commonExpressionsSymbolTableCreator = CommonExpressionsMill.commonExpressionsSymbolTableCreatorBuilder().build();
    commonExpressionsSymbolTableCreator.setRealThis(getRealThis());
    setCommonExpressionsVisitor(commonExpressionsSymbolTableCreator);

    final MCBasicTypesSymbolTableCreator basicTypesSymbolTableCreator = MCBasicTypesMill.mCBasicTypesSymbolTableCreatorBuilder().build();
    basicTypesSymbolTableCreator.setRealThis(getRealThis());
    setMCBasicTypesVisitor(basicTypesSymbolTableCreator);

    final MCCollectionTypesSymbolTableCreator collectionTypesSymbolTableCreator = MCCollectionTypesMill.mCCollectionTypesSymbolTableCreatorBuilder().build();
    collectionTypesSymbolTableCreator.setRealThis(getRealThis());
    setMCCollectionTypesVisitor(collectionTypesSymbolTableCreator);

    final MCSimpleGenericTypesSymbolTableCreator genericTypesSymbolTableCreator = MCSimpleGenericTypesMill.mCSimpleGenericTypesSymbolTableCreatorBuilder().build();
    genericTypesSymbolTableCreator.setRealThis(getRealThis());
    setMCSimpleGenericTypesVisitor(genericTypesSymbolTableCreator);

    // ------------ CD --------------
    final CDBasisSymbolTableCreator basisSymbolTableCreator = CDBasisMill.cDBasisSymbolTableCreatorBuilder().build();
    basisSymbolTableCreator.setRealThis(getRealThis());
    setCDBasisVisitor(basisSymbolTableCreator);

    final CDInterfaceAndEnumSymbolTableCreator interfaceAndEnumSymbolTableCreator = CDInterfaceAndEnumMill.cDInterfaceAndEnumSymbolTableCreatorBuilder().build();
    interfaceAndEnumSymbolTableCreator.setRealThis(getRealThis());
    setCDInterfaceAndEnumVisitor(interfaceAndEnumSymbolTableCreator);

    final CDAssociationSymbolTableCreator associationSymbolTableCreator = CDAssociationMill.cDAssociationSymbolTableCreatorBuilder().build();
    associationSymbolTableCreator.setRealThis(getRealThis());
    setCDAssociationVisitor(associationSymbolTableCreator);

    final CD4AnalysisSymbolTableCreator cd4AnalysisSymbolTableCreator = CD4AnalysisMill.cD4AnalysisSymbolTableCreatorBuilder().build();
    cd4AnalysisSymbolTableCreator.setRealThis(getRealThis());
    setCD4AnalysisVisitor(cd4AnalysisSymbolTableCreator);
  }

}
