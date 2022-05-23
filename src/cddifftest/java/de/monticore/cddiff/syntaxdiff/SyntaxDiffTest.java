package de.monticore.cddiff.syntaxdiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.syntaxdiff.FieldDiff;
import de.monticore.syntaxdiff.SyntaxDiff;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SyntaxDiffTest extends CDDiffTestBasis {

  @Test
  public void testSyntaxDiff() {
    CD4CodeMill.globalScope().clear();

    ASTCDCompilationUnit first = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees" + "/Employees1.cd");
    ASTCDCompilationUnit second = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees" + "/Employees2.cd");

    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    gscope.clear();
    BuiltInTypes.addBuiltInTypes(gscope);

    new CD4CodeDirectCompositionTrafo().transform(first);
    new CD4CodeDirectCompositionTrafo().transform(second);

    // construct symbol tables
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    List<FieldDiff<SyntaxDiff.Op,ASTCDClass>> diffList = new ArrayList<>();

    for (ASTCDClass class1 : first.getCDDefinition().getCDClassesList()){
      for (ASTCDClass class2 : second.getCDDefinition().getCDClassesList()){
        diffList.add(SyntaxDiff.fieldDiffOptional(Optional.of(class1),Optional.of(class2)));
      }
    }

    StringBuilder output = new StringBuilder();
    for (FieldDiff<SyntaxDiff.Op,ASTCDClass> diff: diffList){
      if (diff.isPresent()){
        diff.getOperation().ifPresent(operation -> output.append(operation).append(": "));
        diff.getCd1Value().ifPresent(cd1 -> output.append(cd1.getName()).append(" -> "));
        diff.getCd2Value().ifPresent(cd2 -> output.append(cd2.getName()));
        output.append(System.lineSeparator());
      } else {
        output.append("no diff").append(System.lineSeparator());
      }
    }

    System.out.println(output);

  }

}
