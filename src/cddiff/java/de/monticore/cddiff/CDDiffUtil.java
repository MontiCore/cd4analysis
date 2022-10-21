package de.monticore.cddiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.od4report._parser.OD4ReportParser;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CDDiffUtil {

  /**
   * This helper functions processes parts of a name such that they can be used in Alloy
   *
   * @return The processed name
   */
  public static String partHandler(List<String> parts, boolean toRoleName) {
    StringBuilder completeName = new StringBuilder();

    // Process to role name only
    if (toRoleName) {
      char[] roleName = parts.get(parts.size()-1).toCharArray();
      roleName[0] = Character.toLowerCase(roleName[0]);
      return new String (roleName);
    }

    // Combine all parts using "_" as separator instead of "."
    for (String part : parts) {
      completeName.append(part).append("_");
    }
    // Remove last "_"
    completeName = new StringBuilder(completeName.substring(0, completeName.length() - 1));


    return completeName.toString();
  }

  /**
   * This helper functions processes a qualified name such that it can be used in Alloy
   * @return The processed name
   */
  public static String processQName(String qname) {
    List<String> nameList = new ArrayList<>();
    Collections.addAll(nameList, qname.split("\\."));
    return partHandler(nameList, false);
  }

  /**
   * The default role-name for a referenced type is the (simple) type-name with the first letter
   * in lower case.
   * @param qname is the qualified name of the referenced type
   * @return default role-name
   */
  public static String processQName2RoleName(String qname) {
    List<String> nameList = new ArrayList<>();
    Collections.addAll(nameList, qname.split("\\."));
    return partHandler(nameList, true);
  }

  public static String inferRole(ASTCDAssocSide assocSide) {
    if (assocSide.isPresentCDRole()){
      return assocSide.getCDRole().getName();
    }
    return CDDiffUtil.processQName2RoleName(assocSide.getMCQualifiedType().getMCQualifiedName().getQName());
  }

  protected static void saveDiffCDs2File(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2,
      String outputPath) throws IOException {
    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    ast1.accept(pprinter.getTraverser());
    String cd1 = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    ast2.accept(pprinter.getTraverser());
    String cd2 = pprinter.getPrinter().getContent();

    String suffix1 = "";
    String suffix2 = "";
    if (ast1.getCDDefinition().getName().equals(ast2.getCDDefinition().getName())) {
      suffix1 = "_new";
      suffix2 = "_old";
    }

    Path outputFile1 = Paths.get(outputPath, ast1.getCDDefinition().getName() + suffix1 + ".cd");
    Path outputFile2 = Paths.get(outputPath, ast2.getCDDefinition().getName() + suffix2 + ".cd");

    // Write results into a file
    FileUtils.writeStringToFile(outputFile1.toFile(), cd1, Charset.defaultCharset());
    FileUtils.writeStringToFile(outputFile2.toFile(), cd2, Charset.defaultCharset());
  }


  public static ASTCDCompilationUnit loadCD(String modelPath) throws IOException{
    Optional<ASTCDCompilationUnit> cd = CD4CodeMill.parser().parse(modelPath);
    if (cd.isPresent()) {
      new CDFullNameTrafo().transform(cd.get());
      return cd.get();
    } else {
      Log.error("0xCDD13: Could not load from: " + modelPath);
    }
    return null;
  }

  public static ASTODArtifact loadODModel(String modelPath) {
    try {
      OD4ReportParser parser = new OD4ReportParser();
      Optional<ASTODArtifact> optOD = parser.parse(modelPath);
      if (parser.hasErrors()) {
        Log.error("Model parsed with errors. Model path: " + modelPath);
      }
      else if (optOD.isPresent()) {
        return optOD.get();
      }
    }
    catch (IOException e) {
      Log.error("Could not parse CD model.");
      e.printStackTrace();
    }
    return null;
  }

}
