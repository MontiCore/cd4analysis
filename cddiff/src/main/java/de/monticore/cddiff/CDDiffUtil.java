/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.od4report._parser.OD4ReportParser;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

/** Collection of helper-methods for CDDiff. */
public class CDDiffUtil {

  private static boolean useJavaTypes;

  public static void setUseJavaTypes(boolean useJavaTypes) {
    CDDiffUtil.useJavaTypes = useJavaTypes;
  }

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

  /**
   * The default role-name for a referenced type is the (simple) type-name with the first letter in
   * lower case.
   */
  public static String inferRole(ASTCDAssocSide assocSide) {
    if (assocSide.isPresentCDRole()) {
      return assocSide.getCDRole().getName();
    }
    char[] roleName =
        assocSide.getMCQualifiedType().getMCQualifiedName().getBaseName().toCharArray();
    roleName[0] = Character.toLowerCase(roleName[0]);
    return new String(roleName);
  }

  public static void saveDiffCDs2File(
      ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2, String outputPath) throws IOException {
    String cd1 = CD4CodeMill.prettyPrint(ast1, true);
    String cd2 = CD4CodeMill.prettyPrint(ast2, true);

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

  /**
   * Parse the model, add default role-names and replace all qualified names with (internal) full
   * names.
   */
  public static ASTCDCompilationUnit loadCD(String modelPath) throws IOException {
    Optional<ASTCDCompilationUnit> cd = CD4CodeMill.parser().parseCDCompilationUnit(modelPath);
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
      Log.error("Could not parse OD model.");
      e.printStackTrace();
    }
    return null;
  }

  public static ASTCDCompilationUnit reparseCD(ASTCDCompilationUnit cd) {
    String content = CD4CodeMill.prettyPrint(cd, true);
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
          superName =
              ((CDTypeSymbol) objectType.getDefiningSymbol().get()).getInternalQualifiedName();

          for (ASTCDClass astClass : classes) {
            if (superName.equals(astClass.getSymbol().getInternalQualifiedName())) {
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
      interfaceName =
          ((CDTypeSymbol) objectType.getDefiningSymbol().get()).getInternalQualifiedName();

      for (ASTCDInterface allowedInterface : allowedInterfaces) {
        if (interfaceName.equals(allowedInterface.getSymbol().getInternalQualifiedName())) {
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
        interfaceName =
            ((CDTypeSymbol) objectType.getDefiningSymbol().get()).getInternalQualifiedName();

        for (ASTCDInterface allowedInterface : allowedInterfaces) {
          if (interfaceName.equals(allowedInterface.getSymbol().getInternalQualifiedName())) {
            toProcess.add(allowedInterface);
            break;
          }
        }
      }
    }

    return interfaces;
  }

  /**
   * A helper function to compute the reflexive transitive hull of all super-interfaces of an
   * interface in allowedInterfaces.
   */
  public static Set<ASTCDInterface> getAllInterfaces(
      ASTCDInterface astcdInterface, Collection<ASTCDInterface> allowedInterfaces) {
    Set<ASTCDInterface> interfaces = new HashSet<>();
    interfaces.add(astcdInterface);

    Set<ASTCDInterface> remaining = new HashSet<>(allowedInterfaces);
    remaining.remove(astcdInterface);

    for (SymTypeExpression typeExp : astcdInterface.getSymbol().getInterfaceList()) {
      for (ASTCDInterface superInterface : allowedInterfaces) {
        if (((CDTypeSymbol) typeExp.getTypeInfo())
            .getInternalQualifiedName()
            .equals(superInterface.getSymbol().getInternalQualifiedName())) {
          interfaces.add(superInterface);
          remaining.remove(superInterface);
          interfaces.addAll(getAllInterfaces(superInterface, remaining));
        }
      }
    }

    return interfaces;
  }

  /**
   * A helper function to compute the reflexive transitive hull of all super-types of type in cd.
   */
  public static Set<ASTCDType> getAllSuperTypes(ASTCDClass type, ASTCDDefinition cd) {
    Set<ASTCDType> superTypes = new HashSet<>();
    superTypes.addAll(getAllSuperclasses(type, cd.getCDClassesList()));
    superTypes.addAll(getAllInterfaces(type, cd.getCDInterfacesList()));
    return superTypes;
  }

  /**
   * A helper function to compute the reflexive transitive hull of all super-types of type in cd.
   */
  public static Set<ASTCDType> getAllSuperTypes(ASTCDType type, ASTCDDefinition cd) {
    if (type instanceof ASTCDClass) {
      return getAllSuperTypes((ASTCDClass) type, cd);
    }
    if (type instanceof ASTCDInterface) {
      return new HashSet<>(getAllInterfaces((ASTCDInterface) type, cd.getCDInterfacesList()));
    }
    return new HashSet<>();
  }

  /** A helper function to compute all associations in cd that reference astcdType. */
  public static Set<ASTCDAssociation> getReferencingAssociations(
      ASTCDType astcdType, ASTCDCompilationUnit cd) {
    return cd.getCDDefinition().getCDAssociationsList().stream()
        .filter(
            rAssoc ->
                astcdType
                        .getSymbol()
                        .getInternalQualifiedName()
                        .contains(rAssoc.getLeftQualifiedName().getQName())
                    || astcdType
                        .getSymbol()
                        .getInternalQualifiedName()
                        .contains(rAssoc.getRightQualifiedName().getQName()))
        .collect(Collectors.toSet());
  }

  public static List<ASTCDType> getAllCDTypes(ASTCDCompilationUnit cd) {
    List<ASTCDType> types = new ArrayList<>();
    types.addAll(cd.getCDDefinition().getCDClassesList());
    types.addAll(cd.getCDDefinition().getCDInterfacesList());
    types.addAll(cd.getCDDefinition().getCDEnumsList());
    return types;
  }

  /** A helper function that collects all strict subtypes of a type in cd. */
  public static Set<ASTCDType> getAllStrictSubTypes(ASTCDType type, ASTCDDefinition cd) {
    Set<ASTCDType> result = new HashSet<>();
    Set<ASTCDType> allTypes = new HashSet<>();
    allTypes.addAll(cd.getCDInterfacesList());
    allTypes.addAll(cd.getCDClassesList());

    for (ASTCDType astcdType : allTypes) {
      if (getAllSuperTypes(astcdType, cd).contains(type)) {
        result.add(astcdType);
      }
    }
    result.remove(type);
    return result;
  }

  public static void refreshSymbolTable(ASTCDCompilationUnit cd) {
    if (cd.getEnclosingScope() != null) {
      CD4CodeMill.globalScope().removeSubScope(cd.getEnclosingScope());
    }
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    if (useJavaTypes) {
      scope.addImports(new ImportStatement("java.lang", true));
    }
    final CD4CodeTraverser completer = new CD4CodeSymbolTableCompleter(cd).getTraverser();
    cd.accept(completer);
  }

  /** using pretty printer to print OD */
  public static String printOD(ASTODArtifact astodArtifact) {
    // pretty print the AST
    return OD4ReportMill.prettyPrint(astodArtifact, true);
  }
}
