package de.monticore.cd4codebasis.cocos.ebnf;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDMethodSignatureCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class CDMethodSignatureParameterNamesUnique
    implements CD4CodeBasisASTCDMethodSignatureCoCo {
  @Override
  public void check(ASTCDMethodSignature node) {
    CoCoHelper.findDuplicates(node.getCDParameterList().stream().map(ASTCDParameter::getName).collect(Collectors.toList()))
        .forEach(e -> Log.error(String.format("0xCDC90: Parameter with name %s is defined multiple times in method %s.",
            e, node.getName()), node.get_SourcePositionStart()));
  }
}
