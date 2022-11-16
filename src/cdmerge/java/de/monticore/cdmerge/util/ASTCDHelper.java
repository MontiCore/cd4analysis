/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmerge.exceptions.ConfigurationException;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility methods for browsing through the CD-AST, finding nodes, types etc. uses own inventories
 * as well as resolving mechanisms from symboltable. The intention is to serve as a facade to wrap
 * concrete implementation of the CD4Code Symboltable
 */
public class ASTCDHelper {

  private Map<String, List<ASTCDAssociation>> associationPerReference;

  private List<ASTCDAssociation> associationsCDInternalTypes;

  private List<ASTCDAssociation> associationsWithExternalReference;

  private Map<String, List<ASTCDAttribute>> attributesForClass;

  private ASTCDCompilationUnit cdFile;

  private final Map<String, List<ASTCDElement>> cdPackages;

  private Map<ASTCDElement, ASTCDPackage> cdPackagesLookup;

  private final Map<String, ASTCDClass> classes;

  private final Map<String, ASTCDEnum> enums;

  private final Map<String, ASTCDInterface> interfaces;

  private final Map<String, ASTCDAssociation> namedAssociations;

  private final Map<String, String> superClasses;

  private final Map<String, List<String>> superInterfaces;

  public ASTCDHelper(ASTCDCompilationUnit cd) {
    this();
    this.cdFile = cd;

    // Start resolving all the stuff
    new ASTCDElementCollector(this).collect(cd);

    // We need to check consistent type hierarchy here, otherwise resolving of
    // super-classes will not terminate later
    if (this.superClasses.size() > 1) {
      Iterator<String> iter = this.superClasses.keySet().iterator();
      while (iter.hasNext()) {
        String clazz = iter.next();
        List<String> typeHiearchy = new ArrayList<String>();
        if (checkIsInheritanceCycle(this.superClasses.get(clazz), typeHiearchy)) {
          throw new ConfigurationException(
              "Class Diagram "
                  + cd.getCDDefinition().getName()
                  + " introduces a cyclic dependence in super class hierachy of "
                  + clazz
                  + " > "
                  + String.join(" > ", typeHiearchy)
                  + " !");
        }
      }
    }

    if (this.superInterfaces.size() > 1) {
      Iterator<String> iter = this.superInterfaces.keySet().iterator();
      while (iter.hasNext()) {
        String type = iter.next();
        List<String> typeHiearchy = new ArrayList<String>();
        if (checkIsTypeHierarchyCycle(this.superInterfaces.get(type), typeHiearchy)) {
          throw new ConfigurationException(
              "Class Diagram "
                  + cd.getCDDefinition().getName()
                  + " introduces a cyclic dependence in type hierachy of "
                  + type
                  + " > "
                  + String.join(" > ", typeHiearchy)
                  + "!");
        }
      }
    }

    // CleanUp Interfaces and Superclasses - we only want to have local
    // stuff
    cleanSuperclasses();
    cleanInterfaces();
    cleanAssociations();
  }

  private ASTCDHelper() {

    this.classes = new HashMap<String, ASTCDClass>();
    this.interfaces = new HashMap<String, ASTCDInterface>();
    this.enums = new HashMap<String, ASTCDEnum>();
    this.namedAssociations = new HashMap<String, ASTCDAssociation>();
    this.attributesForClass = new HashMap<String, List<ASTCDAttribute>>();
    this.associationPerReference = new HashMap<String, List<ASTCDAssociation>>();
    this.associationsWithExternalReference = new ArrayList<ASTCDAssociation>();
    this.associationsCDInternalTypes = new ArrayList<ASTCDAssociation>();
    this.superClasses = new HashMap<String, String>();
    this.superInterfaces = new HashMap<String, List<String>>();
    this.cdPackages = new HashMap<String, List<ASTCDElement>>();
    this.cdPackagesLookup = new HashMap<ASTCDElement, ASTCDPackage>();
  }

  public boolean cdContainsClass(final String className) {
    if (isFullyQualifiedName(className)) {
      Optional<ASTCDType> type = getLocalTypeReference(className);
      if (type.isPresent()) {
        return this.classes.containsKey(type.get().getName());
      }
    }
    return this.classes.containsKey(className);
  }

  public boolean cdContainsEnum(final String className) {
    if (isFullyQualifiedName(className)) {
      Optional<ASTCDType> type = getLocalTypeReference(className);
      if (type.isPresent()) {
        return this.enums.containsKey(type.get().getName());
      }
    }
    return this.enums.containsKey(className);
  }

  public boolean cdContainsInterface(final String className) {
    if (isFullyQualifiedName(className)) {
      Optional<ASTCDType> type = getLocalTypeReference(className);
      if (type.isPresent()) {
        return this.interfaces.containsKey(type.get().getName());
      }
    }
    return this.interfaces.containsKey(className);
  }

  public boolean cdContainsType(final String typeName) {
    return cdContainsClass(typeName) || cdContainsInterface(typeName) || cdContainsEnum(typeName);
  }

  /** Checks if theInterface extends the superInterfacs */
  public boolean checkExtendsInterface(final String theInterface, final String superInteface) {
    List<ASTCDInterface> superInterfaces = getLocalSuperInterfaces(theInterface);
    for (ASTCDInterface superInterface : superInterfaces) {
      if (superInterface.getName().equalsIgnoreCase(superInteface)) {
        return true;
      }
    }
    return false;
  }

  public List<ASTCDAssociation> getAllAssociations() {
    return this.cdFile.getCDDefinition().getCDAssociationsList();
  }

  public List<ASTCDClass> getAllClasses() {
    return this.cdFile.getCDDefinition().getCDClassesList();
  }

  public List<ASTCDEnum> getAllEnums() {
    return this.cdFile.getCDDefinition().getCDEnumsList();
  }

  public List<ASTCDInterface> getAllInterfaces() {
    return this.cdFile.getCDDefinition().getCDInterfacesList();
  }

  /**
   * Resolves Association for a type
   *
   * @param typeName - the type which is referred to either of the association end
   * @return all associations which are associated with type name (ignoring navigation!) or empty if
   *     no association refers to this type
   */
  public Optional<List<ASTCDAssociation>> getAssociationsForType(final String typeName) {
    if (this.associationPerReference.containsKey(typeName)) {
      return Optional.of(this.associationPerReference.get(typeName));
    }
    return Optional.empty();
  }

  /**
   * Associations with external references
   *
   * @return all associations which are associated with at least one external type, i.e. an imported
   *     or fully qualified type which is not defined within this class diagram
   */
  public Optional<List<ASTCDAssociation>> getAssociationsWithExternalReferences() {

    return Optional.of(this.associationsWithExternalReference);
  }

  /**
   * resolves an attribute in a class
   *
   * @param attrName the name of the attribute to be resolved
   * @param clazz class to be searched in
   * @return the found attribute's ASTNode or empty if the class does not contain this attribute or
   *     the class doesn't exist
   */
  public Optional<ASTCDAttribute> getAttributeFromClass(
      final String attrName, final String className) {
    if (this.attributesForClass.containsKey(className)) {
      for (ASTCDAttribute attribute : this.attributesForClass.get(className)) {
        if (attribute.getName().equalsIgnoreCase(attrName)) {
          return Optional.of(attribute);
        }
      }
    }
    return Optional.empty();
  }

  public ASTCDDefinition getCDDefinition() {
    return this.cdFile.getCDDefinition();
  }

  public ASTCDCompilationUnit getCDFile() {
    return this.cdFile;
  }

  public Optional<ASTCDPackage> getCDPackage(ASTCDElement element) {
    if (this.cdPackagesLookup.containsKey(element)) {
      return Optional.of(this.cdPackagesLookup.get(element));
    } else {
      Optional<ASTCDElement> keyElement =
          this.cdPackagesLookup.keySet().stream().filter(el -> el.deepEquals(element)).findAny();
      if (keyElement.isPresent()) {
        return Optional.of(this.cdPackagesLookup.get(keyElement.get()));
      }
    }
    return Optional.empty();
  }

  public Optional<String> getCDPackageName(ASTCDElement element) {
    Optional<ASTCDPackage> cdPackage = getCDPackage(element);
    if (cdPackage.isPresent()) {
      return Optional.of(cdPackage.get().getMCQualifiedName().getQName());
    }
    // if the type exists in this CD, we return the default package;
    if (this.cdFile.getCDDefinition().getCDElementList().stream()
        .filter(el -> el.deepEquals(element))
        .findAny()
        .isPresent()) {
      return getDefaultCDPackageName();
    }
    return Optional.empty();
  }

  /**
   * resolves a type in a class diagram
   *
   * @param className the name of the class to be resolved
   * @return the found type's ASTNode empty if the cd does not contain a class with this name
   */
  public Optional<ASTCDClass> getClass(final String className) {
    if (this.classes.containsKey(className)) {
      return Optional.of(this.classes.get(className));
    }
    return Optional.empty();
  }

  /** Returns the names of all classes declared in this class diagram */
  public Set<String> getClassNames() {
    return this.classes.keySet();
  }

  /**
   * resolves an enum in a class diagram
   *
   * @param en the name of the enum to be resolved
   * @return the found type's ASTNode empty if the cd does not contain an enum with this name
   */
  public Optional<ASTCDEnum> getEnum(final String en) {
    if (this.enums.containsKey(en)) {
      return Optional.of(this.enums.get(en));
    }
    return Optional.empty();
  }

  /** Returns the names of all enums declared in this class diagram */
  public Set<String> getEnumNames() {
    return this.enums.keySet();
  }

  /**
   * Returns the first common Super Class of these classes ore empty if the classes don't share a
   * common superclass
   */
  public Optional<ASTCDClass> getFirstCommonSuperClass(
      final ASTCDClass class1, final ASTCDClass class2) {
    String clazz;
    List<String> allSuperClasses1 = new ArrayList<String>();
    if (superClasses.containsKey(class1.getName()) && superClasses.containsKey(class2.getName())) {
      clazz = class1.getName();
      while (superClasses.containsKey(clazz)) {
        allSuperClasses1.add(superClasses.get(clazz));
        clazz = superClasses.get(clazz);
      }
      clazz = class2.getName();
      // Loop through superclasses of class1 and find the first match
      while (superClasses.containsKey(clazz)) {
        if (allSuperClasses1.contains(clazz)) {
          return getClass(clazz);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Returns the ASTReference to the first common Super Class of these classes ore empty if the
   * classes don't share a common superclass in this class diagram
   */
  public Optional<ASTMCObjectType> getFirstCommonSuperClassReference(
      final ASTCDClass class1, final ASTCDClass class2) {
    Optional<ASTCDClass> superclass = getFirstCommonSuperClass(class1, class2);
    if (superclass.isPresent()) {
      // We have to find one subclass to reconstruct the ASTReference....
      String clazz = class1.getName();
      String directSubclass = "";
      while (superClasses.containsKey(clazz)
          && !clazz.equalsIgnoreCase(superclass.get().getName())) {
        directSubclass = clazz;
        clazz = superClasses.get(clazz);
      }
      if (!directSubclass.isEmpty()) {
        return Optional.of(getClass(directSubclass).get().getSuperclassList().get(0));
      }
    }
    return Optional.empty();
  }

  /**
   * resolves a type in a class diagram
   *
   * @param iface the name of the interface to be resolved
   * @return the found type's ASTNode empty if the cd does not contain an interface with this name
   */
  public Optional<ASTCDInterface> getInterface(final String iface) {
    if (this.interfaces.containsKey(iface)) {
      return Optional.of(this.interfaces.get(iface));
    }
    return Optional.empty();
  }

  /** Returns the names of all interfaces declared in this class diagram */
  public Set<String> getInterfaceNames() {
    return this.interfaces.keySet();
  }

  /**
   * Associations with only cd internal references
   *
   * @return all associations which are associated with internal types, i.e. both referenced types
   *     are declared in this cd
   */
  public List<ASTCDAssociation> getLocalAssociations() {

    return this.associationsCDInternalTypes;
  }

  public List<ASTCDInterface> getLocalImplementedInterfaces(final String className) {
    List<ASTCDInterface> interfaces = new ArrayList<>();
    Optional<ASTCDClass> clazz = getClass(className);
    if (clazz.isPresent()) {
      for (ASTMCObjectType iface : clazz.get().getInterfaceList()) {
        if (getInterface(CDUtils.getName(iface)).isPresent()) {
          interfaces.add(getInterface(CDUtils.getName(iface)).get());
        }
        interfaces.addAll(getLocalSuperInterfaces(CDUtils.getName(iface)));

        if (clazz.get().getSuperclassList().size() == 1) {
          // Consider the implemented interfaces of the superclasses
          interfaces.addAll(
              getLocalImplementedInterfaces(
                  CDUtils.getName(clazz.get().getSuperclassList().get(0))));
        }
      }
    }
    return interfaces;
  }

  public List<ASTCDClass> getLocalSuperClasses(final String className) {
    List<ASTCDClass> sc = new ArrayList<ASTCDClass>();
    if (this.superClasses.containsKey(className)
        && getClass(this.superClasses.get(className)).isPresent()) {
      ASTCDClass clazz = getClass(this.superClasses.get(className)).get();
      sc.add(clazz);
      // Add transitive Superclasses
      if (clazz.getSuperclassList().size() == 1) {
        sc.addAll(getLocalSuperClasses(clazz.getName()));
      }
    }
    return sc;
  }

  public List<ASTCDInterface> getLocalSuperInterfaces(final String typeName) {
    List<ASTCDInterface> interfaces = new ArrayList<ASTCDInterface>();
    if (this.superInterfaces.containsKey(typeName)) {
      for (String iface : this.superInterfaces.get(typeName)) {
        if (getInterface(iface).isPresent()) {
          interfaces.add(getInterface(iface).get());
          // Add transitive SuperInterfaces
          interfaces.addAll(getLocalSuperInterfaces(iface));
        }
      }
    }
    return interfaces;
  }

  /**
   * Returns the local Name if the provided fullyQualifiedName refers to type declared in this class
   * diagram. Compares fully qualified path with cd.package and checks if the type is known
   *
   * @param fullyQualifiedNameParts - a "." separated String or a simple Name
   * @return
   */
  public Optional<ASTCDType> getLocalTypeReference(List<String> fullyQualifiedNameParts) {
    if (fullyQualifiedNameParts.size() == 0) {
      return Optional.empty();
    }

    if (this.cdFile.getCDPackageList() == null || this.cdFile.getCDPackageList().isEmpty()) {
      if (fullyQualifiedNameParts.size() > 1) {
        return Optional.empty();
      }
    } else {
      // Compare package: skip the typename on index size-1
      if (!fullyQualifiedNameParts
          .subList(0, fullyQualifiedNameParts.size() - 2)
          .equals(this.cdFile.getCDPackageList())) {
        return Optional.empty();
      }
    }
    return getType(fullyQualifiedNameParts.get(fullyQualifiedNameParts.size() - 1));
  }

  public Optional<ASTCDType> getLocalTypeReference(String fullyQualifiedName) {
    if (!fullyQualifiedName.contains(".")) {
      return getType(fullyQualifiedName);
    }
    return getLocalTypeReference(
        Arrays.stream(fullyQualifiedName.split(".")).collect(Collectors.toList()));
  }

  /**
   * Resolves an Association by name
   *
   * @param associatioName - the name of the association
   * @return the association with the specified name or empty if no associaition was found with this
   *     name
   */
  public Optional<ASTCDAssociation> getNamedAssociations(final String associatioName) {
    if (this.namedAssociations.containsKey(associatioName)) {
      return Optional.of(this.namedAssociations.get(associatioName));
    }
    return Optional.empty();
  }

  /**
   * resolves a type in a class diagram
   *
   * @param typeName the name of the type to be resolved
   * @return the found type's ASTNode empty if the cd does not contain a type with this name
   */
  public Optional<ASTCDType> getType(final String typeName) {
    if (cdContainsClass(typeName)) {
      return Optional.of(this.classes.get(typeName));
    }
    if (cdContainsInterface(typeName)) {
      return Optional.of(this.interfaces.get(typeName));
    }
    if (cdContainsEnum(typeName)) {
      return Optional.of(this.enums.get(typeName));
    }
    return Optional.empty();
  }

  /**
   * Returns the names of all types inn (Classes, Interfaces, Enums) declared in this class diagram
   */
  public Set<String> getTypeNames() {
    Set<String> types = new HashSet<String>();
    types.addAll(getClassNames());
    types.addAll(getInterfaceNames());
    types.addAll(getEnumNames());
    return types;
  }

  public boolean isInDefaultPackage(ASTCDElement element) {
    return !getCDPackage(element).isPresent();
  }

  /**
   * Returns true if the provided fullyQualifiedName refers to type declared in this class diagram.
   * Compares fully qualified path with cd.package and checks if the type is known
   *
   * @param fullyQualifiedNameParts - a "." separated String or a simple Name
   * @return
   */
  public boolean isLocalTypeReference(List<String> fullyQualifiedNameParts) {
    return getLocalTypeReference(fullyQualifiedNameParts).isPresent();
  }

  public boolean isLocalTypeReference(String fullyQualifiedName) {
    if (!fullyQualifiedName.contains(".")) {
      return true;
    }
    return (isLocalTypeReference(
        Arrays.stream(fullyQualifiedName.split(".")).collect(Collectors.toList())));
  }

  /**
   * returns the list of attributes from class which are not included in any of it's superclasses
   */
  public List<ASTCDAttribute> retainUniqueAttributesFromSuperClasses(ASTCDClass clazz) {
    List<ASTCDClass> superclasses = getLocalSuperClasses(clazz.getName());
    List<ASTCDAttribute> unique = new ArrayList<>(clazz.getCDAttributeList());
    List<ASTCDAttribute> common = new ArrayList<>();
    for (ASTCDClass superClass : superclasses) {
      common.addAll(CDUtils.commonAttributeNames(clazz, superClass));
    }
    unique.removeIf(
        attr ->
            common.stream().filter(a -> a.getName().equals(attr.getName())).findAny().isPresent());
    return unique;
  }

  protected void addAssociationForTypeReference(
      final String typeName, final ASTCDAssociation association) {

    if (!this.associationPerReference.containsKey(typeName)) {
      this.associationPerReference.put(typeName, new LinkedList<ASTCDAssociation>());
    }
    this.associationPerReference.get(typeName).add(association);
  }

  protected void addAssociationWithExternalReferences(final ASTCDAssociation association) {
    this.associationsWithExternalReference.add(association);
  }

  protected void addAttributesForClass(
      final String clazzName, final List<ASTCDAttribute> attributes) {
    this.attributesForClass.put(clazzName, attributes);
  }

  protected void addClass(final String name, final ASTCDClass clazz) {
    this.classes.put(name, clazz);
  }

  protected void addEnum(final String name, final ASTCDEnum en) {
    this.enums.put(name, en);
  }

  protected void addInterface(final String name, final ASTCDInterface iface) {
    this.interfaces.put(name, iface);
  }

  protected void addInternalAssociation(final ASTCDAssociation association) {
    this.associationsCDInternalTypes.add(association);
  }

  protected void addNamedAssociation(final String name, final ASTCDAssociation association) {
    this.namedAssociations.put(name, association);
  }

  protected void addPackageScope(ASTCDPackage node) {
    if (node != null && !node.getMCQualifiedName().toString().isEmpty()) {
      this.cdPackages.put(node.getMCQualifiedName().getBaseName(), node.getCDElementList());
      for (ASTCDElement element : node.getCDElementList()) {
        this.cdPackagesLookup.put(element, node);
      }
    } else {
      // NO PACKAGE? OR DEFAULT PACKAGE?
    }
  }

  protected void addSuperclass(final String className, final String superClass) {
    this.superClasses.put(className, superClass);
  }

  protected void addSuperInterfaces(final String typeName, final List<String> ifaces) {
    this.superInterfaces.put(typeName, ifaces);
  }

  private boolean checkIsInheritanceCycle(String className, List<String> visited) {
    if (visited.stream().filter(i -> i.equals(className)).findAny().isPresent()) {
      return true;
    }
    if (this.superClasses.containsKey(className)) {
      visited.add(className);
      return checkIsInheritanceCycle(this.superClasses.get(className), visited);
    } else {
      return false;
    }
  }

  private boolean checkIsTypeHierarchyCycle(List<String> interfaces, List<String> visited) {
    for (String iface : interfaces) {
      if (visited.stream().filter(i -> i.equals(iface)).findAny().isPresent()) {
        return true;
      }
      if (this.superInterfaces.containsKey(iface)) {
        visited.add(iface);
        return checkIsTypeHierarchyCycle(this.superInterfaces.get(iface), visited);
      }
    }
    return false;
  }

  private void cleanAssociations() {
    // Move all the associations which do not refer to a type declared
    // in this class diagramm (i.e. imported Types)
    List<String> externalRefs = new ArrayList<String>();
    for (String referenceName : associationPerReference.keySet()) {
      if (!getType(referenceName).isPresent()) {
        this.associationsWithExternalReference.addAll(
            this.associationPerReference.get(referenceName));
        externalRefs.add(referenceName);
      }
    }
    // Cleanup: remove the old associations
    ASTCDAssociation assoc;
    for (String referenceName : externalRefs) {
      // Collect reverse direction of association first
      Iterator<ASTCDAssociation> it = this.associationPerReference.get(referenceName).iterator();
      while (it.hasNext()) {
        assoc = it.next();
        this.associationsCDInternalTypes.remove(assoc);
        String refName = assoc.getLeftReferenceName().get(assoc.getLeftReferenceName().size() - 1);

        if (getType(refName).isPresent()) {
          // Remove all associations referring to the foreign type
          this.associationPerReference.get(refName).remove(assoc);
        } else {
          refName = assoc.getRightReferenceName().get(assoc.getLeftReferenceName().size() - 1);
          if (getType(refName).isPresent()) {
            // Remove all associations referring to the foreign type
            this.associationPerReference.get(refName).remove(assoc);
          }
        }
      }
      // Remove the entries for the foreign type
      this.associationPerReference.remove(referenceName);
    }
  }

  private void cleanInterfaces() {
    List<String> removeKeys = new ArrayList<String>(this.superInterfaces.keySet().size());
    for (String type : superInterfaces.keySet()) {
      for (String iface : superInterfaces.get(type)) {
        if (!getInterface(iface).isPresent()) {
          removeKeys.add(iface);
        }
      }
      for (String key : removeKeys) {
        this.superInterfaces.get(type).remove(key);
      }
    }
  }

  private void cleanSuperclasses() {
    List<String> removeKeys = new ArrayList<String>(this.superClasses.keySet().size());
    for (String clazz : superClasses.keySet()) {
      if (!getClass(superClasses.get(clazz)).isPresent()) {
        removeKeys.add(clazz);
      }
    }
    for (String key : removeKeys) {
      this.superClasses.remove(key);
    }
  }

  private Optional<String> getDefaultCDPackageName() {
    if (this.cdFile.isPresentMCPackageDeclaration()) {
      return Optional.of(
          String.join(
              ".", this.cdFile.getMCPackageDeclaration().getMCQualifiedName().getPartsList()));
    }
    return Optional.empty();
  }

  private boolean isFullyQualifiedName(String name) {
    if (name == null) {
      name = "";
    }
    return name.contains(".");
  }
}
