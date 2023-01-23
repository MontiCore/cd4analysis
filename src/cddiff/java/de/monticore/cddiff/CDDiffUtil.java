/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.od4report._parser.OD4ReportParser;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.apache.commons.io.FileUtils;

public class CDDiffUtil {

  public static String escape2Alloy(String type) {
    return type.replaceAll("_", "__")
        .replaceAll("\\.", "_q_dot_")
        .replaceAll("<", "_l_br_")
        .replaceAll(">", "_r_br_");
  }

  public static String unescape2Name(String name) {
    return name.replaceAll("_q_dot_", "_")
        .replaceAll("_l_br_", "_of_")
        .replaceAll("_r_br_", "")
        .replaceAll("__", "_");
  }

  public static String unescape2Type(String type) {
    return type.replaceAll("_l_br_", "<")
        .replaceAll("_r_br_", ">")
        .replaceAll("_q_dot_", "\\.")
        .replaceAll("__", "_");
  }

  /**
   * The default role-name for a referenced type is the (simple) type-name with the first letter in
   * lower case.
   *
   * @param qname is the qualified name of the referenced type
   * @return default role-name
   */
  public static String processQName2RoleName(String qname) {
    List<String> nameList = new ArrayList<>();
    Collections.addAll(nameList, qname.split("\\."));
    char[] roleName = nameList.get(nameList.size() - 1).toCharArray();
    roleName[0] = Character.toLowerCase(roleName[0]);
    return new String(roleName);
  }

  public static String inferRole(ASTCDAssocSide assocSide) {
    if (assocSide.isPresentCDRole()) {
      return assocSide.getCDRole().getName();
    }
    char[] roleName =
        assocSide.getMCQualifiedType().getMCQualifiedName().getBaseName().toCharArray();
    roleName[0] = Character.toLowerCase(roleName[0]);
    return new String(roleName);
  }

  protected static void saveDiffCDs2File(
      ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2, String outputPath) throws IOException {
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

  public static ASTCDCompilationUnit loadCD(String modelPath) throws IOException {
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
      } else if (optOD.isPresent()) {
        return optOD.get();
      }
    } catch (IOException e) {
      Log.error("Could not parse CD model.");
      e.printStackTrace();
    }
    return null;
  }

  public static ASTCDCompilationUnit reparseCD(ASTCDCompilationUnit cd) {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter();
    cd.accept(pp.getTraverser());
    String content = pp.prettyprint(cd);
    try {
      Optional<ASTCDCompilationUnit> opt = CD4CodeMill.parser().parse_String(content);
      if (opt.isPresent()) {
        cd = opt.get();
      }
    } catch (IOException e) {
      Log.warn("Could not reparse CD: " + cd.getCDDefinition().getName());
    }
    return cd;
  }

  /**
   * A helper function to compute the transitive hull of all superclasses of a class astcdClass in
   * classes.
   *
   * @return All superclasses of a class
   */
  public static Set<ASTCDClass> getAllSuperclasses(
      ASTCDClass astcdClass, Collection<ASTCDClass> classes) {
    // Initialize variables
    Set<ASTCDClass> superclasses = new HashSet<>();
    LinkedList<ASTCDClass> toProcess = new LinkedList<>();
    toProcess.add(astcdClass);
    superclasses.add(astcdClass);

    // Add all superclasses of the superclasses
    while (!toProcess.isEmpty()) {
      ASTCDClass currentClass = toProcess.pop();
      superclasses.add(currentClass);

      String superName;
      if (currentClass.isPresentCDExtendUsage()) {
        for (ASTMCObjectType objectType : currentClass.getCDExtendUsage().getSuperclassList()) {
          assert objectType.getDefiningSymbol().isPresent();
          superName = objectType.getDefiningSymbol().get().getFullName();

          for (ASTCDClass astClass : classes) {
            if (superName.equals(astClass.getSymbol().getFullName())) {
              toProcess.add(astClass);
            }
          }
        }
      }
    }

    return superclasses;
  }

  /**
   * A helper function to compute the transitive hull of all interfaces implemented by a class
   * superClass in environment classes.
   */
  public static Set<ASTCDInterface> getAllInterfaces(
      ASTCDClass superClass, Collection<ASTCDInterface> allowedInterfaces) {
    // Initialize variables
    Set<ASTCDInterface> interfaces = new HashSet<>();
    LinkedList<ASTCDInterface> toProcess = new LinkedList<>();

    // Add all interfaces of the superclass to the processing List

    String interfaceName;
    for (ASTMCObjectType objectType : superClass.getInterfaceList()) {
      assert objectType.getDefiningSymbol().isPresent();
      interfaceName = objectType.getDefiningSymbol().get().getFullName();

      for (ASTCDInterface allowedInterface : allowedInterfaces) {
        if (interfaceName.equals(allowedInterface.getSymbol().getFullName())) {
          toProcess.add(allowedInterface);
          break;
        }
      }
    }

    // Add all interfaces implemented by superclass or its superclasses and
    // implemented interfaces
    while (!toProcess.isEmpty()) {
      // Pop element from processing list and add it to the result
      ASTCDInterface currentInterface = toProcess.pop();
      interfaces.add(currentInterface);

      // Add all interfaces implemented by the current interface to the
      // processing list
      for (ASTMCObjectType objectType : currentInterface.getInterfaceList()) {
        assert objectType.getDefiningSymbol().isPresent();
        interfaceName = objectType.getDefiningSymbol().get().getFullName();

        for (ASTCDInterface allowedInterface : allowedInterfaces) {
          if (interfaceName.equals(allowedInterface.getSymbol().getFullName())) {
            toProcess.add(allowedInterface);
            break;
          }
        }
      }
    }

    return interfaces;
  }

  public static Set<ASTCDInterface> getAllInterfaces(
      ASTCDInterface astcdInterface, Collection<ASTCDInterface> allowedInterfaces) {
    Set<ASTCDInterface> interfaces = new HashSet<>();
    interfaces.add(astcdInterface);

    Set<ASTCDInterface> remaining = new HashSet<>(allowedInterfaces);
    remaining.remove(astcdInterface);

    for (SymTypeExpression typeExp : astcdInterface.getSymbol().getInterfaceList()) {
      for (ASTCDInterface superInterface : allowedInterfaces) {
        if (typeExp.getTypeInfo().getFullName().equals(superInterface.getSymbol().getFullName())) {
          interfaces.add(superInterface);
          remaining.remove(superInterface);
          interfaces.addAll(getAllInterfaces(superInterface, remaining));
        }
      }
    }

    return interfaces;
  }
}
