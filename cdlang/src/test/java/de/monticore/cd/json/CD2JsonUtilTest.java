/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.json;

import com.fasterxml.jackson.databind.JsonNode;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.MCPath;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

public class CD2JsonUtilTest extends CD4AnalysisTestBasis {

  protected JsonNode toJson(Path cd4a_file) throws IOException {
    final ASTCDCompilationUnit node =
        p.parseCDCompilationUnit(cd4a_file.toAbsolutePath().toString()).get();

    // create symtab
    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();

    final ICD4AnalysisGlobalScope globalScope = CD4AnalysisMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(cd4a_file));
    BuiltInTypes.addBuiltInTypes(globalScope);

    new CD4AnalysisAfterParseTrafo().transform(node);
    CD4AnalysisMill.scopesGenitorDelegator().createFromAST(node);

    node.accept(new CD4AnalysisSymbolTableCompleter(node).getTraverser());

    // ToDo: not needed? This trafo leads to errors. Without, everything seems to be ok.
    // first add roles if they don't exist
    //    CDAssociationRoleNameTrafo associationRoleNameTrafo = new CDAssociationRoleNameTrafo();
    //    CD4AnalysisTraverser traverser = CD4AnalysisMill.inheritanceTraverser();
    //    traverser.add4CDAssociation(associationRoleNameTrafo);
    //    traverser.setCDAssociationHandler(associationRoleNameTrafo);
    //    associationRoleNameTrafo.setTraverser(traverser);
    //    associationRoleNameTrafo.transform(node);

    // then add roles as fields
    CDAssociationCreateFieldsFromNavigableRoles cdAssociationCreateFieldsFromAllRoles =
        new CDAssociationCreateFieldsFromNavigableRoles();
    CD4AnalysisTraverser traverser = CD4AnalysisMill.inheritanceTraverser();
    traverser.add4CDAssociation(cdAssociationCreateFieldsFromAllRoles);
    traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromAllRoles);
    cdAssociationCreateFieldsFromAllRoles.setTraverser(traverser);
    cdAssociationCreateFieldsFromAllRoles.transform(node);

    return CD2JsonUtil.run(node, globalScope);
  }

  @Test
  public void cd2json_Simple() throws IOException {
    this.toJson(Paths.get(PATH, "cd4analysis/parser/Simple.cd"));
  }

  @Test
  public void cd2json_MyLife() throws IOException {
    this.toJson(Paths.get(PATH, "cd4analysis/parser/MyLife.cd"));
  }

  @Test
  public void cd2json_STTest() throws IOException {
    this.toJson(Paths.get(PATH, "cd4analysis/parser/STTest.cd"));
  }
}
