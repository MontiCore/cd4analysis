/* (c) https://github.com/MontiCore/monticore */
package de.monticore.alloy2od;

import de.monticore.od4data._parser.OD4DataParser;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.A4Tuple;
import edu.mit.csail.sdg.translator.A4TupleSet;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * A collection of functions to transform alloy solutions to object diagrams
 */
public class Alloy2ODGenerator {

  /**
   * Creates the String representation of an Objectdiagram (OD) based on A4 Tuples extracted from an
   * alloy solution
   */
  private static String generateOutputString(CompModule module, A4Solution solution, String name) {
    StringBuilder od = new StringBuilder();

    A4TupleSet fNames = null;
    A4TupleSet type = null;
    A4TupleSet superTypes = null;
    A4TupleSet obj = null;
    A4TupleSet val = null;
    A4TupleSet enumVal = null;
    A4TupleSet getObj = null;

    // ---------------------------------------------------------------------------------------
    // Derive important structures
    // ---------------------------------------------------------------------------------------

    // Derive all important sets from reachable signals
    for (Sig sig : solution.getAllReachableSigs()) {

      // Get Obj representing all objects in the OM
      if (sig.label.equals("this/Obj")) {
        obj = solution.eval(sig);
      }

      // Get Val representing values of primitive and unknown types of
      // attributes in the OM
      if (sig.label.equals("this/Val")) {
        val = solution.eval(sig);
      }

      // EnumV al representing enumeration values assigned to attributes in the
      // OM
      if (sig.label.equals("this/EnumVal")) {
        enumVal = solution.eval(sig);
      }

      // Get FNames representing attribute and role names
      if (sig.label.equals("this/FName")) {
        fNames = solution.eval(sig);
      }
    }

    // Get the get part:
    Expr e;
    try {
      e = CompUtil.parseOneExpression_fromString(module, "get");
      getObj = (A4TupleSet) solution.eval(e);
      e = CompUtil.parseOneExpression_fromString(module, "type");
      type = (A4TupleSet) solution.eval(e);
      e = CompUtil.parseOneExpression_fromString(module, "super");
      superTypes = (A4TupleSet) solution.eval(e);
    }
    catch (Err e1) {
      e1.printStackTrace();
    }

    // Handle null.
    if (getObj == null || val == null || enumVal == null || obj == null) {
      Log.error("0xCDD03: Unable to derive getObj, val, enumVal or obj.");
      return "";
    }

    // Derive all necessary information
    // The set of all value names
    Set<String> valNames = new HashSet<>();
    for (A4Tuple value : val) {
      valNames.add(value.toString());
    }

    // The set of all enum names
    Set<String> enumValNames = new HashSet<>();
    for (A4Tuple value : enumVal) {
      enumValNames.add(value.toString());
    }

    // The set of all object names
    Set<String> objNames = new HashSet<>();
    for (A4Tuple value : obj) {
      objNames.add(value.toString());
    }

    // ---------------------------------------------------------------------------------------
    // Generate output
    // ---------------------------------------------------------------------------------------

    // Set indent variable
    String indent = "  ";

    // Write static part into the OD
    od.append("objectdiagram ");
    od.append(name);
    od.append(" {").append(System.lineSeparator()).append(System.lineSeparator());

    // Write dynamic part to OD
    for (A4Tuple o : obj) {

      if (!(type == null || superTypes == null || isDummyType(type))) {
        od.append(indent).append(executeRuleOType(o, type, superTypes));
      }
      od.append(indent).append(executeRuleODecl(o));
      od.append(" {").append(System.lineSeparator());

      indent = indent.concat(indent);

      // Write attribute assignments and enum positions into OD
      for (A4Tuple a4Tuple : getObj) {
        String oName = o.atom(0);
        String oPName = a4Tuple.atom(0);
        String valName = a4Tuple.atom(2);
        String a = a4Tuple.atom(1);

        // Write attribute assignments into OD
        if (oPName.equals(oName) && valNames.contains(valName)) {
          od.append(indent).append(executeRulePrim(valName, a));
          od.append(";").append(System.lineSeparator());
        }

        // Write enums into OD
        if (oPName.equals(oName) && enumValNames.contains(valName)) {
          od.append(indent).append(executeRuleEnum(valName, a));
          od.append(";").append(System.lineSeparator());
        }
      }
      indent = indent.substring(0, 2);
      od.append(indent).append("};").append(System.lineSeparator()).append(System.lineSeparator());
    }

    // Handle links
    for (A4Tuple a4Tuple : getObj) {
      String sourceName = a4Tuple.atom(0);
      String targetName = a4Tuple.atom(2);
      String a = a4Tuple.atom(1);

      if (objNames.contains(sourceName) && objNames.contains(targetName)) {
        od.append(indent).append(executeRuleLink(sourceName, targetName, a));
        od.append(";").append(System.lineSeparator());
      }
    }
    od.append("}").append(System.lineSeparator());

    return od.toString();
  }

  private static boolean isDummyType(A4TupleSet type) {
    for (A4Tuple t : type) {
      if (!t.atom(1).contains("Dummy")) {
        return false;
      }
    }
    return true;
  }

  /**
   * Generates the diff witness for a given alloy module and alloy solution and returns it as
   * string
   */
  public static String generateString(CompModule module, A4Solution currentSolution, int number) {

    // Create output string
    return generateOutputString(module, currentSolution, "od" + number);
  }

  /**
   * Generates the diff witness for a given alloy module and alloy solution and returns it as
   * string
   *
   * @param module   The compiled alloy module for which witnesses should be generated
   * @param solution The Alloy solution for the alloy diff predicate
   */
  public static String generateString(CompModule module, A4Solution solution) {
    // Set variable for possibly multiple solutions to zero
    int number = 0;

    // Call numbered variant
    return generateString(module, solution, number);
  }

  /**
   * Generates all diff witnesses for a given alloy module and alloy solution and saves them in the
   * outputDirectory
   *
   * @param module   The compiled alloy module for which witnesses should be generated
   * @param solution The Alloy solution for the alloy diff predicate
   */
  public static void generateAll(CompModule module, A4Solution solution, File outputDirectory) {
    // Variable for possibly multiple solutions
    int number = 0;

    // Set solution as initial value
    A4Solution currentSolution = solution;

    // Do this for all solutions
    while (currentSolution.satisfiable()) {
      // Derive module name
      String name = "witness" + number;

      // Generate module
      String currentOD = generateString(module, currentSolution, number);

      // Save module
      saveOD(currentOD, name, outputDirectory);

      // Increase loop variables
      try {
        currentSolution = currentSolution.next();
      }
      catch (Err e) {
        e.printStackTrace();
        return;
      }
      number++;
    }
  }

  /**
   * Generates at most limit diff witnesses for a given alloy module and alloy solution and saves
   * them in the outputDirectory
   *
   * @param module   The compiled alloy module for which witnesses should be generated
   * @param solution The Alloy solution for the alloy diff predicate
   */
  public static void generateLimited(CompModule module, A4Solution solution, int limit,
      File outputDirectory) {

    // Variable for possibly multiple solutions
    int number = 0;

    // Set solution as initial value
    A4Solution currentSolution = solution;

    // Do this for all solutions
    while (currentSolution.satisfiable() && number < limit) {
      // Derive module name
      String name = solution.getOriginalFilename() + number;

      // Generate module
      String currentOD = generateString(module, currentSolution, number);

      // Save module
      saveOD(currentOD, name, outputDirectory);

      // Increase loop variables
      try {
        currentSolution = currentSolution.next();
      }
      catch (Err e) {
        e.printStackTrace();
        return;
      }
      number++;
    }
  }

  /**
   * Generates the diff witness for a given alloy module and alloy solution and saves it as od
   *
   * @param module   The compiled alloy module for which witnesses should be generated
   * @param solution The Alloy solution for the alloy diff predicate
   */
  public static Optional<ASTODArtifact> generateOD(CompModule module, A4Solution solution) {
    Optional<ASTODArtifact> od = Optional.empty();

    // Variable for possibly multiple solutions
    int number = 0;

    // Create output string
    String odString = generateOutputString(module, solution, "od" + number);

    // Parse output
    OD4DataParser odParser = new OD4DataParser();

    try {
      od = odParser.parse_String(odString);
    }
    catch (IOException e1) {
      System.out.println("Unable to parse:");
      System.out.println(odString);
    }

    //    System.out.println(odString);
    return od;
  }

  /**
   * Generates all ODs and returns them as parsed ASTOArtifacts
   */
  public static List<ASTODArtifact> generateAllODs(CompModule module, A4Solution solution) {
    List<ASTODArtifact> ods = new ArrayList<>();

    // Set solution as initial value
    A4Solution currentSolution = solution;

    // Do this for all solutions
    while (currentSolution.satisfiable()) {
      // Generate Module
      Optional<ASTODArtifact> optOd = generateOD(module, currentSolution);

      // Add to result if present
      optOd.ifPresent(ods::add);

      // Increase loop variables
      try {
        currentSolution = currentSolution.next();
      }
      catch (Err e) {
        e.printStackTrace();
        return ods;
      }
    }

    return ods;
  }

  /**
   * Generates all unique ODs and returns them as parsed ASTOArtifacts
   */
  public static List<ASTODArtifact> generateUniqueODs(CompModule module, A4Solution solution) {
    List<ASTODArtifact> ods = new ArrayList<>();

    // Set solution as initial value
    A4Solution currentSolution = solution;

    // Do this for all solutions
    while (currentSolution.satisfiable()) {
      // Generate Module
      Optional<ASTODArtifact> optOd = generateOD(module, currentSolution);

      // Add to result if present
      if (optOd.isPresent() && !ods.contains(optOd.get())) {
        ods.add(optOd.get());
      }

      // Increase loop variables
      try {
        currentSolution = currentSolution.next();
      }
      catch (Err e) {
        e.printStackTrace();
        return ods;
      }
    }

    return ods;
  }

  /**
   * Generates at most limit ODs and returns them as parsed ASTOArtifacts
   */
  public static List<ASTODArtifact> generateLimitODs(CompModule module, A4Solution solution,
      int limit) {
    List<ASTODArtifact> ods = new ArrayList<>();

    // Variable for possibly multiple solutions
    int number = 0;

    // Set solution as initial value
    A4Solution currentSolution = solution;

    // Do this for all solutions
    while (currentSolution.satisfiable() && number < limit) {
      // Generate Module
      Optional<ASTODArtifact> optOd = generateOD(module, currentSolution);

      // Add to result if present
      optOd.ifPresent(ods::add);

      // Increase loop variables
      try {
        currentSolution = currentSolution.next();
      }
      catch (Err e) {
        e.printStackTrace();
        return ods;
      }
      number++;
    }

    return ods;
  }

  /**
   * Writes a string containing an object diagram into a file
   *
   * @param od              the string the Alloy module should contain
   * @param outputDirectory the directory to generate the Alloy Module in.
   */
  public static void saveOD(String od, String odName, File outputDirectory) {
    // Set Output Path
    String outputPath = outputDirectory.toString() + "/";
    Path outputFile = Paths.get(outputPath, odName + ".od");

    // Write results into a file
    try {
      FileUtils.writeStringToFile(outputFile.toFile(), od, Charset.defaultCharset());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Translation rule Link translated two Obj atoms and one FName atom to a link declaration. The
   * link connects the objects encoded by the Obj atoms and has a name that is encoded by the FName
   * atom.
   */
  private static String executeRuleLink(String sourceName, String targetName, String a) {
    StringBuilder od = new StringBuilder();

    // Get left name
    String lObj = sourceName.replaceAll("[$]", "");

    // Get link name
    String link = a.replaceAll("[$.*]\\d*", "");

    // Get right name
    String rObj = targetName.replaceAll("[$]", "");

    // Generate output
    od.append("link ");
    od.append(lObj);
    od.append(" -> (");
    od.append(link);
    od.append(") ");
    od.append(rObj);

    return od.toString();
  }

  /**
   * Translation rule Enum translates an EnumVal atom and a FName atom to a declaration of an
   * attribute having the name of the field encoded by the FName atom and having the type encoded by
   * the EnumVal atom.
   */
  private static String executeRuleEnum(String val, String fName) {
    StringBuilder od = new StringBuilder();

    // Remove enum_ and $number from val
    String type = val.replaceAll("enum_", "");
    type = type.replaceAll("_.*", "");

    // Get name from fName by removing $number from fName
    String name = fName.replaceAll("[$]\\d*", "");

    // Get value from val by removing "*_*_" part and $number from fName
    String value = val.replaceAll("[$]\\d*", "");
    value = value.replaceAll(".*_", "");

    // Generate output
    od.append(type);
    od.append(" ");
    od.append(name);
    od.append(" = ");
    od.append(value);

    return od.toString();
  }

  /**
   * Translation rule Prim translates a Val atom and a FName atom to an attribute declaration. The
   * name attribute’s name is encoded by the FName atom, whereas the value and the type of the
   * attribute are encoded by the Val atom.
   */
  private static String executeRulePrim(String val, String fName) {
    StringBuilder od = new StringBuilder();

    // Remove type_ and $number from val
    String type = val.replaceAll("_of__", "<").replaceAll("__", ">").replaceAll(".*_", "");
    type = type.replaceAll("[$]\\d*", "");

    // Get name from fName by removing $number from fName
    String name = fName.replaceAll("[$]\\d*", "");

    // Get value from val by $number from fName
    String value = val.replaceAll("[$]\\d*", "");

    // Generate output
    od.append(type);
    od.append(" ");
    od.append(name);
    od.append(" = some_");
    od.append(value);

    // Remove all "_" and return
    return od.toString();
  }

  /**
   * Rule ODecl translates an Obj atom to an object declaration.
   *
   * @param o Object we want to
   * @return object declaration
   */
  private static String executeRuleODecl(A4Tuple o) {
    StringBuilder od = new StringBuilder();

    // Remove $ from o to get name
    String name = o.toString().replaceAll("[$]", "");

    // Remove $ and numbers after it to get type
    String type = o.toString().replaceAll("[$]\\d*", "");
    type = type.replaceAll("[_]", ".");

    // Generate output
    od.append(name);
    od.append(":");
    od.append(type);

    return od.toString();
  }

  private static String executeRuleOType(A4Tuple o, A4TupleSet type, A4TupleSet superTypes) {
    StringBuilder typeDecl = new StringBuilder();
    typeDecl.append("<<instanceOf = \"");

    for (A4Tuple t : type) {
      if (o.atom(0).equals(t.atom(0))) {
        for (A4Tuple superType : superTypes) {
          if (t.atom(1).equals(superType.atom(0))) {
            typeDecl.append(superType.atom(1)
                .replaceAll("Type_", ", ")
                .replaceAll("[$]\\d*", ""));
          }
        }
      }
    }

    typeDecl.append("\">>").append(System.lineSeparator());
    return typeDecl.toString().replaceFirst("= \", ", "= \"");
  }

}
