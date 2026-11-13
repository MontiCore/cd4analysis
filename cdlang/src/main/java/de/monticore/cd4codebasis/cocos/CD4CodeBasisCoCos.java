/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDClassCoCo;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDMethodCoCo;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisCoCoChecker;
import de.monticore.cd4codebasis.cocos.ebnf.*;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.cdbasis._cocos.CDBasisASTCDTargetImportStatementCoCo;
import de.monticore.cdbasis.cocos.CDOnlyResolvableImportStatements;
import de.monticore.cdbasis.cocos.ConstructorNameEqualsClassNameCoCo;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.monticore.types.mcbasictypes._cocos.MCBasicTypesASTMCImportStatementCoCo;

public class CD4CodeBasisCoCos extends CoCoParent<CD4CodeBasisCoCoChecker> {
  
  public CD4CodeBasisCoCos() {
  }
  
  @Override
  public CD4CodeBasisCoCoChecker createNewChecker() {
    return new CD4CodeBasisCoCoChecker();
  }
  
  @Override
  protected void addCheckerForAllCoCos(CD4CodeBasisCoCoChecker checker) {
    addCheckerForEbnfCoCos(checker);
    addCheckerForMcgCoCos(checker);
    addCheckerForMcg2EbnfCoCos(checker);
  }
  
  @Override
  protected void addMcgCoCos(CD4CodeBasisCoCoChecker checker) {
    super.addMcgCoCos(checker);
    
    // CDImport Statements
    CDOnlyResolvableImportStatements resolvableImports = new CDOnlyResolvableImportStatements();
    checker.addCoCo((CDBasisASTCDTargetImportStatementCoCo) resolvableImports);
    checker.addCoCo((MCBasicTypesASTMCImportStatementCoCo) resolvableImports);
  }
  
  @Override
  protected void addEbnfCoCos(CD4CodeBasisCoCoChecker checker) {
    super.addEbnfCoCos(checker);
    
    // CDClass
    checker.addCoCo(new ConstructorNameEqualsClassNameCoCo());
    
    // CDClass and CDInterface: sealed and permits
    // (registered once each, the CoCos visit both classes and interfaces)
    checker.addCoCo((CD4CodeBasisASTCDClassCoCo) new CDPermitsTargetsResolvable());
    checker.addCoCo((CD4CodeBasisASTCDClassCoCo) new CDPermittedTypesAreSubtypes());
    checker.addCoCo((CD4CodeBasisASTCDClassCoCo) new CDSealedModifierCombinations());
    // (once per grammar: CDBasis, CD4CodeBasis, CDInterfaceAndEnum; CDAssociation in CD4CodeCoCos)
    CDSealedOnlyOnClassesAndInterfaces sealedOnlyOnTypes = new CDSealedOnlyOnClassesAndInterfaces();
    checker.addCoCo((CDBasisASTCDAttributeCoCo) sealedOnlyOnTypes);
    checker.addCoCo((CD4CodeBasisASTCDMethodCoCo) sealedOnlyOnTypes);
    checker.addCoCo((CDInterfaceAndEnumASTCDEnumCoCo) sealedOnlyOnTypes);
    
    // CDMethodSignature
    checker.addCoCo(new CDMethodSignatureParameterNamesUnique());
    
    // CD4CodeEnumConstant
    checker.addCoCo(new CD4CodeEnumConstantParameterMatchConstructorArguments());
  }
  
}
