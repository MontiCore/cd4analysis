/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition.types3;

import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeIdAsConstructorCTTIVisitor;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisCTTIVisitor;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeIdAsConstructorCTTIVisitor;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.symtabdefinition.SymTabDefinitionMill;
import de.monticore.symtabdefinition._visitor.SymTabDefinitionTraverser;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcfunctiontypes.types3.MCFunctionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types.mcstructuraltypes.types3.MCStructuralTypesTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.types3.util.OOWithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;

public class SymTabDefinitionTypeCheck3 extends MapBasedTypeCheck3 {
  
  public static void init() {
    Log.trace("init " + SymTabDefinitionTypeCheck3.class.getSimpleName(), "TypeCheck setup");
    
    SymTypeRelations.init();
    MCCollectionSymTypeRelations.init();
    OOWithinTypeBasicSymbolsResolver.init();
    OOWithinScopeBasicSymbolsResolver.init();
    TypeContextCalculator.init();
    TypeVisitorOperatorCalculator.init();
    CommonExpressionsLValueRelations.init();
    TypeParameterRelations.init();
    
    SymTabDefinitionTraverser traverser = SymTabDefinitionMill.inheritanceTraverser();
    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();
    
    // Literals
    
    MCCommonLiteralsTypeVisitor visMCCommonLiterals = new MCCommonLiteralsTypeVisitor();
    visMCCommonLiterals.setType4Ast(type4Ast);
    traverser.add4MCCommonLiterals(visMCCommonLiterals);
    
    // Expressions
    
    BitExpressionsTypeVisitor visBitExpressions = new BitExpressionsTypeVisitor();
    visBitExpressions.setType4Ast(type4Ast);
    traverser.add4BitExpressions(visBitExpressions);
    
    CommonExpressionsCTTIVisitor visCommonExpressions =
        new CommonExpressionsTypeIdAsConstructorCTTIVisitor();
    visCommonExpressions.setType4Ast(type4Ast);
    visCommonExpressions.setContext4Ast(ctx4Ast);
    traverser.add4CommonExpressions(visCommonExpressions);
    traverser.setCommonExpressionsHandler(visCommonExpressions);
    
    ExpressionBasisCTTIVisitor visExpressionBasis =
        new ExpressionBasisTypeIdAsConstructorCTTIVisitor();
    visExpressionBasis.setType4Ast(type4Ast);
    visExpressionBasis.setContext4Ast(ctx4Ast);
    traverser.add4ExpressionsBasis(visExpressionBasis);
    traverser.setExpressionsBasisHandler(visExpressionBasis);
    
    // MCTypes
    
    MCBasicTypesTypeVisitor visMCBasicTypes = new MCBasicTypesTypeVisitor();
    visMCBasicTypes.setType4Ast(type4Ast);
    traverser.add4MCBasicTypes(visMCBasicTypes);
    
    MCCollectionTypesTypeVisitor visMCCollectionTypes = new MCCollectionTypesTypeVisitor();
    visMCCollectionTypes.setType4Ast(type4Ast);
    traverser.add4MCCollectionTypes(visMCCollectionTypes);
    
    MCFunctionTypesTypeVisitor visMCFunctionTypes = new MCFunctionTypesTypeVisitor();
    visMCFunctionTypes.setType4Ast(type4Ast);
    traverser.add4MCFunctionTypes(visMCFunctionTypes);
    
    MCSimpleGenericTypesTypeVisitor visMCSimpleGenericTypes = new MCSimpleGenericTypesTypeVisitor();
    visMCSimpleGenericTypes.setType4Ast(type4Ast);
    traverser.add4MCSimpleGenericTypes(visMCSimpleGenericTypes);
    
    MCStructuralTypesTypeVisitor visMCStructuralTypes = new MCStructuralTypesTypeVisitor();
    visMCStructuralTypes.setType4Ast(type4Ast);
    traverser.add4MCStructuralTypes(visMCStructuralTypes);
    
    // create delegate
    SymTabDefinitionTypeCheck3 tc3 = new SymTabDefinitionTypeCheck3(traverser, type4Ast, ctx4Ast);
    tc3.setThisAsDelegate();
  }
  
  public static void reset() {
    TypeCheck3.resetDelegate();
    SymTypeRelations.reset();
    MCCollectionSymTypeRelations.reset();
    OOWithinTypeBasicSymbolsResolver.reset();
    OOWithinScopeBasicSymbolsResolver.reset();
    TypeContextCalculator.reset();
    TypeVisitorOperatorCalculator.reset();
    CommonExpressionsLValueRelations.reset();
    TypeParameterRelations.reset();
  }
  
  protected SymTabDefinitionTypeCheck3(ITraverser typeTraverser, Type4Ast type4Ast,
      InferenceContext4Ast ctx4Ast) {
    super(typeTraverser, type4Ast, ctx4Ast);
  }
  
}
