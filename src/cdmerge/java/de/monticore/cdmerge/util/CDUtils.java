/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._ast.ASTCD4CodeNode;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationNode;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceAndEnumNode;
import de.monticore.cdmerge.validation.CDMergeCD4ACoCos;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import org.apache.commons.io.FileUtils;

/**
 * Utility class offering static methods to access or modify CD elements or produce loggable output
 * etc.
 */
public class CDUtils {

  private static CD4CodeParser parser;

  /** Returns the common Attributes from two classes (Names checked only) */
  public static List<ASTCDAttribute> commonAttributeNames(ASTCDClass class1, ASTCDClass class2) {
    List<ASTCDAttribute> common = new ArrayList<>();
    for (ASTCDAttribute attr1 : class1.getCDAttributeList()) {
      for (ASTCDAttribute attr2 : class2.getCDAttributeList()) {
        if (attr1.getName().equalsIgnoreCase(attr2.getName())) {
          common.add(attr2);
        }
      }
    }
    return common;
  }

  /** Returns the Diff of Attributes from two classes (Names checked only) */
  public static List<ASTCDAttribute> diffAttributes(ASTCDClass class1, ASTCDClass class2) {
    List<ASTCDAttribute> common = commonAttributeNames(class1, class2);
    List<ASTCDAttribute> diff = new ArrayList<>();

    diff.addAll(class1.getCDAttributeList());
    diff.addAll(class2.getCDAttributeList());
    if (common.isEmpty()) {
      return diff;
    }

    diff.removeIf(attr -> common.stream().anyMatch(a -> a.getName().equals(attr.getName())));
    return diff;
  }

  /**
   * resolves an attribute in a AST Class
   *
   * @param name the name of the attribute to be resolved
   * @param clazz class to be searched in
   * @return the found attribute's ASTNode or empty if the class does not contain this attribute or
   *     the class doesn't exist
   */
  public static Optional<ASTCDAttribute> getAttributeFromClass(
      final String name, final ASTCDClass clazz) {
    for (ASTCDAttribute attribute : clazz.getCDAttributeList()) {
      if (attribute.getName().equalsIgnoreCase(name)) {
        return Optional.of(attribute);
      }
    }
    return Optional.empty();
  }

  /**
   * resolves a class in a class diagram
   *
   * @param className the name of the class to be resolved
   * @param cd the class diagram
   * @return the found type's ASTNode empty if the cd does not contain a class with this name
   */
  public static Optional<ASTCDClass> getClass(final String className, final ASTCDDefinition cd) {
    for (ASTCDClass clazz : cd.getCDClassesList()) {
      if (clazz.getName().equalsIgnoreCase(className)) {
        return Optional.of(clazz);
      }
    }
    return Optional.empty();
  }

  /**
   * resolves a constant in an enum
   *
   * @param constName the name of the constant to be resolved
   * @param astEnum enum to be searched in
   * @return the found constant
   */
  public static Optional<ASTCDEnumConstant> getConstFromEnum(String constName, ASTCDEnum astEnum) {
    for (ASTCDEnumConstant en : astEnum.getCDEnumConstantList()) {
      if (en.getName().equalsIgnoreCase(constName)) {
        return Optional.of(en);
      }
    }
    return Optional.empty();
  }

  /**
   * resolves an enumeration in a class diagram
   *
   * @param enumName the name of the enumeration to be resolved
   * @param cd the class diagram
   * @return the found type's ASTNode empty if the cd does not contain an enumeration with this name
   */
  public static Optional<ASTCDEnum> getEnum(final String enumName, final ASTCDDefinition cd) {
    for (ASTCDEnum en : cd.getCDEnumsList()) {
      if (en.getName().equalsIgnoreCase(enumName)) {
        return Optional.of(en);
      }
    }
    return Optional.empty();
  }

  public static String getFullQualifiedName(List<String> parts) {
    if (parts == null || parts.isEmpty()) {
      return "";
    }
    StringJoiner sj = new StringJoiner(".");
    parts.forEach(sj::add);
    return sj.toString();
  }

  /**
   * resolves an interface in a class diagram
   *
   * @param interfaceName the name of the interface to be resolved
   * @param cd the class diagram
   * @return the found type's ASTNode empty if the cd does not contain an interface with this name
   */
  public static Optional<ASTCDInterface> getInterface(
      final String interfaceName, final ASTCDDefinition cd) {
    for (ASTCDInterface iface : cd.getCDInterfacesList()) {
      if (iface.getName().equalsIgnoreCase(interfaceName)) {
        return Optional.of(iface);
      }
    }
    return Optional.empty();
  }

  public static String getName(ASTMCObjectType referencedType) {
    return CD4CodeMill.prettyPrint(referencedType, false);
  }

  public static String getName(ASTNode astCDNode) {
    if (astCDNode instanceof ASTCDDefinition) {
      return ((ASTCDDefinition) astCDNode).getName();
    }
    if (astCDNode instanceof ASTCDCompilationUnit) {
      return ((ASTCDCompilationUnit) astCDNode).getCDDefinition().getName();
    }
    if (astCDNode instanceof ASTCDType) {
      return ((ASTCDType) astCDNode).getName();
    }
    if (astCDNode instanceof ASTCDAttribute) {
      return ((ASTCDAttribute) astCDNode).getName();
    }
    if (astCDNode instanceof ASTCDAssociation) {
      ASTCDAssociation assoc = (ASTCDAssociation) astCDNode;
      if (assoc.isPresentName()) {
        return assoc.getName();
      } else {
        return "A_"
            + assoc.getLeftQualifiedName().getBaseName()
            + "_"
            + assoc.getRightQualifiedName().getBaseName();
      }
    }
    return "";
  }

  /**
   * resolves a type in a class diagram
   *
   * @param typeName the name of the class, interface or enum to be resolved
   * @param cd the class diagram
   * @return the found type's ASTNode empty if the cd does not contain a class with this name
   */
  public static Optional<ASTCDType> getType(final String typeName, final ASTCDDefinition cd) {
    Optional<ASTCDClass> clazz = getClass(typeName, cd);
    if (clazz.isPresent()) {
      return Optional.of(clazz.get());
    }
    Optional<ASTCDInterface> iface = getInterface(typeName, cd);
    if (iface.isPresent()) {
      return Optional.of(iface.get());
    }
    Optional<ASTCDEnum> en = getEnum(typeName, cd);
    if (en.isPresent()) {
      return Optional.of(en.get());
    }
    return Optional.empty();
  }

  public static String getTypeName(ASTCDAttribute attr) {
    if (attr == null) {
      return "";
    }
    return getTypeName(attr.getMCType());
  }

  public static String getTypeName(ASTMCType type) {
    if (type == null) {
      return "";
    }
    return CD4CodeMill.prettyPrint(type, false);
  }

  /** Used for log outputs */
  public static String prettyPrint(ASTCD4CodeNode node) {
    return CD4CodeMill.prettyPrint(node, true);
  }

  public static String prettyPrint(ASTCDBasisNode node) {
    return CD4CodeMill.prettyPrint(node, true);
  }

  /** Used for log outputs, produces inline model code */
  public static String prettyPrintInline(ASTMCBasicTypesNode node) {
    return CD4CodeMill.prettyPrint(node, false);
  }

  /** Used for log outputs, produces inline model code */
  public static String prettyPrintInline(ASTCD4CodeNode node) {

    return CD4CodeMill.prettyPrint(node, false);
  }

  public static String prettyPrintInline(ASTCDAssociationNode node) {
    return CD4CodeMill.prettyPrint(node, false);
  }

  public static String prettyPrintInline(ASTCDBasisNode node) {
    return CD4CodeMill.prettyPrint(node, false);
  }

  public static String prettyPrintInline(ASTNode node) {
    if (node instanceof ASTCDCompilationUnit) {
      return "classdiagram " + ((ASTCDCompilationUnit) node).getCDDefinition().getName();
    }
    if (node instanceof ASTCDAssociationNode) {
      return prettyPrintInline((ASTCDAssociationNode) node);
    }
    if (node instanceof ASTCDInterfaceAndEnumNode) {
      return prettyPrintInline(node);
    }
    if (node instanceof ASTCD4CodeNode) {
      return prettyPrintInline((ASTCD4CodeNode) node);
    }
    if (node instanceof ASTCDBasisNode) {
      return prettyPrintInline((ASTCDBasisNode) node);
    }
    return node.toString();
  }

  public static void removeSuperInterface(ASTCDClass baseType, ASTMCType interfToRemove) {
    if (baseType.getInterfaceList().contains(interfToRemove)) {
      baseType.getInterfaceList().remove(interfToRemove);
    }
    if (baseType.getInterfaceList().size() == 0) {
      baseType.setCDInterfaceUsageAbsent();
    }
  }

  public static void removeSuperInterface(ASTCDClass baseType, String interfToRemove) {
    if (baseType.getInterfaceList().size() > 0) {
      for (ASTMCType iface : baseType.getInterfaceList()) {
        if (getTypeName(iface).equals(interfToRemove)) {
          removeSuperInterface(baseType, iface);
          return;
        }
      }
    }
  }

  public static void removeSuperInterface(ASTCDInterface baseType, ASTMCType interfToRemove) {
    if (baseType.getInterfaceList().contains(interfToRemove)) {
      baseType.getInterfaceList().remove(interfToRemove);
    }
    if (baseType.getInterfaceList().size() == 0) {
      baseType.setCDExtendUsageAbsent();
    }
  }

  public static void removeSuperInterface(ASTCDInterface baseType, String interfToRemove) {
    if (baseType.getInterfaceList().size() > 0) {
      for (ASTMCType iface : baseType.getInterfaceList()) {
        if (getTypeName(iface).equals(interfToRemove)) {
          removeSuperInterface(baseType, iface);
          return;
        }
      }
    }
  }

  public static void removeSuperInterface(ASTCDType baseType, ASTMCType interfToRemove) {
    if (baseType instanceof ASTCDClass) {
      removeSuperInterface((ASTCDClass) baseType, interfToRemove);
    } else if (baseType instanceof ASTCDInterface) {
      removeSuperInterface((ASTCDInterface) baseType, interfToRemove);
    }
  }

  public static void removeSuperInterface(ASTCDType baseType, String interfToRemove) {
    if (baseType instanceof ASTCDClass) {
      removeSuperInterface((ASTCDClass) baseType, interfToRemove);
    } else if (baseType instanceof ASTCDInterface) {
      removeSuperInterface((ASTCDInterface) baseType, interfToRemove);
    }
  }

  private static CD4CodeParser getParser() {
    if (parser == null) {
      parser = CD4CodeMill.parser();
    }
    return parser;
  }

  /**
   * Parses a CD4Code model from file, without performing any CoCo checks.
   *
   * @param modelfile The model
   * @return the AST if parsing was successful
   * @throws IOException If file was not accessible
   */
  public static Optional<ASTCDCompilationUnit> parseCDFile(String modelfile) throws IOException {
    return parseCDFile(modelfile, false);
  }

  /**
   * Parses a CD4Code model from file, will resolve Local symbol
   *
   * @param checkCoCos Activate coco checks
   * @return the AST if parsing was successful
   * @throws IOException
   */
  public static Optional<ASTCDCompilationUnit> parseCDFile(String modelfile, boolean checkCoCos)
      throws IOException {
    Path p = Paths.get(modelfile).toAbsolutePath();

    // Make sure we have a filename extension
    String[] nameParts = p.getFileName().toString().split("\\.(?=[^\\.]+$)");
    if (nameParts.length != 2) {
      return Optional.empty();
    }

    Optional<ASTCDCompilationUnit> ast =
        parseCDCompilationUnit(
            FileUtils.readFileToString(new File(p.toUri()), Charset.defaultCharset()), checkCoCos);
    if (ast.isPresent()) {
      String simpleFileName = p.getFileName().toString().replace(".cd", "");
      String modelName = ast.get().getCDDefinition().getName();
      if (!modelName.equals(simpleFileName)) {
        Log.error(
            "The name of the diagram "
                + modelName
                + " is not identical to the name of the file "
                + simpleFileName
                + " (without its file-extension).");
      }
    }
    return ast;
  }

  public static Optional<ASTCDCompilationUnit> parseCDCompilationUnit(
      String model, boolean checkCoCos) {

    Optional<ASTCDCompilationUnit> ast = Optional.empty();
    try {
      ast = getParser().parse_StringCDCompilationUnit(model);
    } catch (IOException e) {
      // Should never happen
      Log.error("Unable to parse input model due to IO Exception!");
    }
    if (ast.isPresent()) {
      final ASTCDCompilationUnit cd = ast.get();
      // Always ensure clean Symboltable for each model
      RefreshSymbolTable(cd);
      // Ensure every CDElement is in a package and perform default AST Trafos
      final CDMergeAfterParseTrafo afterParseTrafo = new CDMergeAfterParseTrafo();
      afterParseTrafo.transform(cd);

      if (checkCoCos) {
        if (checkCoCos) {
          CD4CodeCoCoChecker checker = new CDMergeCD4ACoCos().getCheckerForMergedCDs();
          checker.checkAll(cd);
        }
      }

    } else {
      if (parser.hasErrors()) {
        Log.error("Unable to parse input model due to parsing Errors!");
      } else {
        Log.error("Unable to parse input model  !");
      }
    }
    return ast;
  }

  public static void RefreshSymbolTable(ASTCDCompilationUnit cd) {
    if (cd.getEnclosingScope() != null) {
      CD4CodeMill.globalScope().removeSubScope(cd.getEnclosingScope());
    }

    // Resolve the symboltable
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    scope.addImports(new ImportStatement("java.lang", true));

    final CD4CodeTraverser completer = new CD4CodeSymbolTableCompleter(cd).getTraverser();

    cd.accept(completer);
  }

  public static Optional<ASTCDCompilationUnit> parseCDCompilationUnit(String model) {
    return parseCDCompilationUnit(model, false);
  }

  /**
   * Filters the list of attributes from class1 and keeps only the attributes which are not declared
   * in class2
   */
  public static List<ASTCDAttribute> retainUniqueAttributesFromClass1(
      ASTCDClass class1, ASTCDClass class2) {
    List<ASTCDAttribute> common = commonAttributeNames(class1, class2);
    List<ASTCDAttribute> diff = new ArrayList<>(class1.getCDAttributeList());
    diff.removeIf(attr -> common.stream().anyMatch(a -> a.getName().equals(attr.getName())));
    return diff;
  }

  public static void setSuperClass(ASTCDClass clazz, ASTMCObjectType superClazz) {
    ASTCDExtendUsage extend = new ASTCDExtendUsageBuilder().build();
    extend.addSuperclass(superClazz);
    clazz.setCDExtendUsage(extend);
  }

  /**
   * Returns a temporary ASTCDAssociation clone of association2 where lefthand-side and
   * righthand-side corresponds to the types in association1. This makes further comparison more
   * easy. Returns empty if the associations cannot be aligned (i.e. refer to different type names)
   */
  public static Optional<ASTCDAssociation> tryAlignAssociation(
      ASTCDAssociation associationReference, ASTCDAssociation associationToAlign) {

    boolean doFlip = false;

    AssociationDirection directionReference =
        AssociationDirection.getDirection(associationReference);
    AssociationDirection directionToAlign = AssociationDirection.getDirection(associationToAlign);

    // Special case for two reflexive associations A -- A
    if (associationReference
            .getLeftReferenceName()
            .toString()
            .equalsIgnoreCase(associationReference.getRightReferenceName().toString())
        && associationToAlign
            .getLeftReferenceName()
            .toString()
            .equalsIgnoreCase(associationToAlign.getRightReferenceName().toString())
        && associationReference
            .getLeftReferenceName()
            .toString()
            .equals(associationToAlign.getLeftReferenceName().toString())) {
      doFlip = checkFlipReflexiveAssociation(associationReference, associationToAlign);
    }

    // Case A -- B | A -- B : Don't Flip
    else if (associationReference
            .getLeftReferenceName()
            .toString()
            .equalsIgnoreCase(associationToAlign.getLeftReferenceName().toString())
        && associationReference
            .getRightReferenceName()
            .toString()
            .equalsIgnoreCase(associationToAlign.getRightReferenceName().toString())) {
      // Already aligned, nothing to do..
      return Optional.of(associationToAlign);
    }

    // Case A -- B | B -- A : Always flip
    else if (associationReference
            .getLeftReferenceName()
            .toString()
            .equalsIgnoreCase(associationToAlign.getRightReferenceName().toString())
        && associationReference
            .getRightReferenceName()
            .toString()
            .equalsIgnoreCase(associationToAlign.getLeftReferenceName().toString())) {
      doFlip = true;

    } else {
      // Associations don't seem to match
      return Optional.empty();
    }

    if (doFlip) {
      // FLIP
      ASTCDAssociation flippedAssociation = associationToAlign.deepClone();
      flippedAssociation.setEnclosingScope(associationToAlign.getEnclosingScope());

      // Flip sides
      flippedAssociation.setLeft(
          CDAssociationPartFromTemplateBuilder.buildLeftAssociation(associationToAlign.getRight()));
      flippedAssociation.setRight(
          CDAssociationPartFromTemplateBuilder.buildRightAssociation(associationToAlign.getLeft()));

      // flip direction
      switch (directionToAlign) {
        case LeftToRight:
          flippedAssociation.setCDAssocDir(
              CDAssociationPartFromTemplateBuilder.buildRightToLeftDir(
                  associationToAlign.getCDAssocDir()));
          break;
        case RightToLeft:
          flippedAssociation.setCDAssocDir(
              CDAssociationPartFromTemplateBuilder.buildLeftToRightDir(
                  associationToAlign.getCDAssocDir()));
          break;
        default:
          break;
      }
      return Optional.of(flippedAssociation);
    }

    // Don't Flip
    return Optional.of(associationToAlign);
  }

  /**
   * Checks if the role-name on the left-hand side of the association is explicitly defined or was
   * derived from association name or referred type
   *
   * @param association the association to check the left role side
   * @return true if this role name is explicitly defined, false if it is derived
   */
  private static boolean isExplicitRoleDefinitionLeftSide(ASTCDAssociation association) {
    if (!association.getLeft().isPresentCDRole()) {
      return false;
    }
    if (association.isPresentName()) {
      if (association.getName().equalsIgnoreCase(association.getLeft().getCDRole().getName())) {
        return false;
      }
    }
    if (association
        .getLeftQualifiedName()
        .getBaseName()
        .equalsIgnoreCase(association.getLeft().getCDRole().getName())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the role-name on the right-hand side of the association is explicitly defined or was
   * derived from association name or referred type
   *
   * @param association the association to check the right role side
   * @return true if this role name is explicitly defined, false if it is derived
   */
  private static boolean isExplicitRoleDefinitionRightSide(ASTCDAssociation association) {
    if (!association.getRight().isPresentCDRole()) {
      return false;
    }
    if (association.isPresentName()) {
      if (association.getName().equalsIgnoreCase(association.getRight().getCDRole().getName())) {
        return false;
      }
    }
    if (association
        .getRightQualifiedName()
        .getBaseName()
        .equalsIgnoreCase(association.getRight().getCDRole().getName())) {
      return false;
    }
    return true;
  }

  /**
   * Special case for reflexive associations A -- A Try to determine if the associations would
   * correspond better if we flip the second association Unchecked precondition is that both
   * associations are reflexive to the same type
   *
   * @param associationReference the Association as reference
   * @param associationToAlign the Association that should be aligned to the reference
   * @return true, if the associationToAlign should be flipped
   */
  private static boolean checkFlipReflexiveAssociation(
      ASTCDAssociation associationReference, ASTCDAssociation associationToAlign) {

    boolean alignByRoles = false;
    AssociationDirection directionReference =
        AssociationDirection.getDirection(associationReference);
    AssociationDirection directionToAlign = AssociationDirection.getDirection(associationToAlign);

    // try to do determine orientation by using the roles, but only if it's not an automatically
    // induced role (i.e. role is not the same as the association name or referred Type name)
    if (isExplicitRoleDefinitionLeftSide(associationReference)
        && isExplicitRoleDefinitionRightSide(associationToAlign)) {
      if (!associationReference
          .getLeft()
          .getCDRole()
          .getName()
          .equals(associationToAlign.getRight().getCDRole().getName())) {
        return true;
      }
    }
    if (isExplicitRoleDefinitionRightSide(associationReference)
        && isExplicitRoleDefinitionLeftSide(associationToAlign)) {
      if (!associationReference
          .getRight()
          .getCDRole()
          .getName()
          .equals(associationToAlign.getLeft().getCDRole().getName())) {
        return true;
      }
    }

    boolean conflict = false;
    // Try to align according to Cardinalities, but we must ensure first, that roles match
    if (associationReference.getLeft().isPresentCDRole()
        && associationToAlign.getLeft().isPresentCDRole()) {
      if (!associationReference
          .getLeft()
          .getCDRole()
          .getName()
          .equals(associationToAlign.getLeft().getCDRole().getName())) {
        conflict = true;
      }
    }
    if (!conflict
        && associationReference.getRight().isPresentCDRole()
        && associationToAlign.getRight().isPresentCDRole()) {
      if (!associationReference
          .getRight()
          .getCDRole()
          .getName()
          .equals(associationToAlign.getRight().getCDRole().getName())) {
        conflict = true;
      }
    }
    if (!conflict) {
      if (associationReference.getLeft().isPresentCDCardinality()
          && associationToAlign.getRight().isPresentCDCardinality()) {
        if (associationReference
            .getLeft()
            .getCDCardinality()
            .deepEquals(associationToAlign.getRight().getCDCardinality())) {
          return true;
        }
      }
      if (associationReference.getRight().isPresentCDCardinality()
          && associationToAlign.getLeft().isPresentCDCardinality()) {
        if (associationReference
            .getRight()
            .getCDCardinality()
            .deepEquals(associationToAlign.getLeft().getCDCardinality())) {
          return true;
        }
      }
    }

    // We could not determine an alignment for the two reflexive
    // associations. If none was configured bidirectional we ensure, that
    // they have the same orientation A -> A / A <- A
    // A -> A[1] vs [1] A <- A
    switch (directionReference) {
      case LeftToRight:
        if (directionToAlign == AssociationDirection.RightToLeft) return true;
        break;
      case RightToLeft:
        if (directionToAlign == AssociationDirection.LeftToRight) return true;
        break;
      default:
        break;
    }

    return false;
  }
}
