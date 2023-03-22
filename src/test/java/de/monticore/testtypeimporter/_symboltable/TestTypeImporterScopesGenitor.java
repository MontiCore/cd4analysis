/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testtypeimporter._symboltable;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;
import de.monticore.testcdinterfaceandenum._symboltable.ITestCDInterfaceAndEnumArtifactScope;
import de.monticore.testtypeimporter._ast.ASTCompilationUnit;
import de.monticore.testtypeimporter._ast.ASTElement;
import de.monticore.types.check.FullSynthesizeFromMCBasicTypes;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.logging.Log;

public class TestTypeImporterScopesGenitor extends TestTypeImporterScopesGenitorTOP {

  public TestTypeImporterScopesGenitor() {}

  @Override
  public ITestTypeImporterArtifactScope createFromAST(ASTCompilationUnit rootNode) {
    ITestTypeImporterArtifactScope as = super.createFromAST(rootNode);

    // add imports
    rootNode.getMCImportStatementList()
      .forEach(i -> as.addImports(new ImportStatement(i.getQName(), i.isStar())));

    return as;
  }

  @Override
  public void endVisit(ASTElement node) {
    super.endVisit(node);

    final TypeCheckResult typeResult =
        new FullSynthesizeFromMCBasicTypes().synthesizeType(node.getMCType());
    if (!typeResult.isPresentResult()) {
      Log.error(
          String.format(
              "0xCDE00: The type (%s) of the element (%s) could not be calculated",
              node.getMCType().printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()),
              node.getName()),
          node.getMCType().get_SourcePositionStart());
    } else {
      node.getSymbol().setType(typeResult.getResult());
    }
  }
}
