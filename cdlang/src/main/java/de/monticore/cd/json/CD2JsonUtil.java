/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.json;

import com.fasterxml.jackson.databind.JsonNode;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;

public class CD2JsonUtil {

  public static JsonNode run(
      ASTCDCompilationUnit astcdCompilationUnit, ICD4AnalysisGlobalScope globalScope)
      throws IOException {
    // Generate JSON-Schema for each class
    CD2JsonTransform cd2SchemeVisitor = new CD2JsonTransform(globalScope);
    astcdCompilationUnit.getCDDefinition().getCDClassesList().forEach(cd2SchemeVisitor::visit);

    return cd2SchemeVisitor.getScheme();
  }
}
