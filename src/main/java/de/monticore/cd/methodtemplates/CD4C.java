/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.methodtemplates;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.MutatorDecorator;
import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.cd4codebasis._ast.ASTCD4CodeBasisNode;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDMethodSignatureCoCo;
import de.monticore.cd4codebasis.cocos.ebnf.CDMethodSignatureParameterNamesUnique;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CD4C {

  public static final String GLEX_GLOBAL_VAR = "cd4c";

  /*
   * TODO: This is not really a singleton because the instance can be recreated
   *  and released. Additionally, searching for usages reveals that it is not
   *  statically accessed in many places. A future update should probably
   *  investigate whether it makes sense to fully remove the "instance caching"
   *  in favor of just passing the instance to where it's needed.
   */
  private static CD4C INSTANCE;

  /**
   * build a stack for the defined methods to match the body to the "current" defined method signature
   */
  protected final Stack<CD4CTemplateHelper> methodQueue = new Stack<>();

  protected final List<Predicate<ASTCDMethodSignature>> methodPredicates = new ArrayList<>();
  protected final List<Predicate<ASTCDAttribute>> attributePredicates = new ArrayList<>();
  protected final List<BiPredicate<ASTCDClass, ASTCDMethodSignature>> classPredicates = new ArrayList<>();
  protected final List<BiPredicate<ASTCDClass, ASTCDAttribute>> classAttrPredicates = new ArrayList<>();
  protected final HashMap<ASTCDType, Set<ASTMCImportStatement>> importMap = Maps.newHashMap();

  protected String emptyBodyTemplate = "de.monticore.cd.methodtemplates.core.EmptyMethod";
  protected CD4CodeFullPrettyPrinter prettyPrinter = new CD4CodeFullPrettyPrinter();
  protected CDTypesCalculator typesCalculator = new DeriveSymTypeOfCD4Code();

  protected GeneratorSetup config;
  protected boolean isInitialized;

  protected CD4C() {}

  /**
   * Returns the current {@code CD4C} instance. If no instance is current, a new
   * one is implicitly created but must be explicitly {@link #init(GeneratorSetup)
   * initialized}.
   *
   * @return the current {@code CD4C} instance
   *
   * @see #init(GeneratorSetup)
   * @see #reset()
   */
  public static synchronized CD4C getInstance() {
    if (INSTANCE == null) INSTANCE = new CD4C();
    return INSTANCE;
  }

  /**
   * Initializes the current {@code CD4C} instance. If no instance is current, a
   * new one is implicitly created.
   *
   * @param setup the generator setup to use for the generation
   */
  public static synchronized CD4C init(GeneratorSetup setup) {
    CD4C instance = getInstance();

    instance.config = setup;
    setup.getGlex().setGlobalValue(GLEX_GLOBAL_VAR, instance);

    instance.isInitialized = true;

    return instance;
  }

  /** Invalidates and releases the current {@code C4DC} instance. */
  public static synchronized void reset() {
    CD4C instance = INSTANCE; // Don't use getInstance() since that might implicitly create new instance.
    if (instance == null) return;

    instance.config.getGlex().changeGlobalVar(GLEX_GLOBAL_VAR, null);
    instance.isInitialized = false;

    INSTANCE = null;
  }

  /** Returns {@code true} if a {@code CD4C} instance is current and initialized, or {@code false} otherwise. */
  public static boolean isInitialized() {
    // Don't use getInstance() to avoid implicitly creating a new instance.
    return (INSTANCE != null && INSTANCE.isInitialized);
  }

  /**
   * Checks if a {@code CD4C} instance is current and initialized. If the check
   * fails, an exception is thrown.
   */
  protected static void checkInitialized() {
    if (isInitialized()) return;

    // This is used twice. Don't inline to avoid misleading the unique error-code check.
    String error = "0x11000: CD4C is not yet initialized";

    Log.error(error);
    throw new RuntimeException(error + ", please initialize with `CD4C.init(setup)`");
  }

  public CD4C setPrettyPrinter(CD4CodeFullPrettyPrinter prettyPrinter) {
    this.prettyPrinter = prettyPrinter;
    return this;
  }

  public CD4C setTypesCalculator(CDTypesCalculator typesCalculator) {
    this.typesCalculator = typesCalculator;
    return this;
  }

  public CD4C setEmptyBodyTemplate(String emptyBodyTemplate) {
    this.emptyBodyTemplate = emptyBodyTemplate;
    return this;
  }

  public String getEmptyBodyTemplate() {
    return emptyBodyTemplate;
  }

  /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\
  | Methods                                                                   |
  \*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

  /**
   * Creates a method from the given {@code template}.
   *
   * @param clazz       the class from which to read necessary information
   * @param template    the name of the template that is executed to create the method signature
   * @param arguments   the arguments to be provided to the template
   *
   * @return the created method
   */
  public Optional<ASTCDMethodSignature> createMethod(ASTCDClass clazz, String template, Object... arguments) {
    checkInitialized();

    return this.createMethodSignatureAndBody(clazz, template, arguments)
      .flatMap(CD4CTemplateHelper::getMethod)
      .map(m -> this.setEnclosingScopeTo(m, clazz.getSpannedScope()))
      .flatMap(m -> this.methodPredicates.stream().anyMatch(p -> !p.test(m)) ? Optional.empty() : Optional.of(m));
  }

  /**
   * Adds a method created from the given {@code template} to the given class.
   *
   * @param clazz       the class to which the method should be added
   * @param template    the name of the template that is executed to create the method signature
   * @param arguments   the arguments to be provided to the template
   */
  public void addMethod(ASTCDClass clazz, String template, Object... arguments) {
    checkInitialized();

    Optional<ASTCDMethodSignature> method = this.createMethod(clazz, template, arguments);
    if (!method.isPresent()) {
      Log.error("0x11010: There was no method created in the template '" + template + "'");
      return;
    }

    if (this.classPredicates.stream().anyMatch(p -> !p.test(clazz, method.get()))) {
      Log.error("0x11011: A check for the class method failed for method '" + method.get().getName() + "'");
    }

    clazz.addCDMember(method.get());
  }

  /**
   * Use this method to describe the signature (with concrete syntax) in templates
   *
   * <p><b>This method is intended to be used from templates.</b></p>
   *
   * @param signature the signature (as processed by {@link de.monticore.cd4code._parser.CD4CodeParser#parseCDMethod(String) CD4CodeParser})
   */
  public void method(String signature) {
    checkInitialized();

    CD4CTemplateHelper th = new CD4CTemplateHelper();
    th.method(signature);
    this.methodQueue.add(th);
  }

  /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\
  | Constructors                                                              |
  \*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

  /**
   * Creates a constructor from the given {@code template}.
   *
   * @param clazz       the class from which to read necessary information
   * @param template    the name of the template that is executed to create the constructor signature
   * @param arguments   the arguments to be provided to the template
   *
   * @return the created constructor
   */
  public Optional<ASTCDMethodSignature> createConstructor(ASTCDClass clazz, String template, Object... arguments) {
    checkInitialized();

    return this.createMethodSignatureAndBody(clazz, template, arguments)
      .flatMap(CD4CTemplateHelper::getMethod)
      .map(m -> this.setEnclosingScopeTo(m, clazz.getSpannedScope()))
      .flatMap(m -> this.methodPredicates.stream().anyMatch(p -> !p.test(m)) ? Optional.empty() : Optional.of(m));
  }

  private ASTCDMethodSignature setEnclosingScopeTo(ASTCDMethodSignature method, ICDBasisScope scope) {
    if (!this.methodPredicates.isEmpty() || !this.classPredicates.isEmpty()) {
      method.accept(CD4CodeMill.scopesGenitorDelegator().getTraverser());
    }
    return method;
  }

  /**
   * Adds a constructor created from the given {@code template} to the given class.
   *
   * @param clazz       the class to which the constructor should be added
   * @param template    the name of the template that is executed to create the method signature
   * @param arguments   the arguments to be provided to the template
   */
  public void addConstructor(ASTCDClass clazz, String template, Object... arguments) {
    checkInitialized();

    Optional<ASTCDMethodSignature> method = this.createConstructor(clazz, template, arguments);
    if (!method.isPresent()) {
      Log.error("0x11020: There was no constructor created in the template '" + template + "'");
      return;
    }

    if (this.classPredicates.stream().anyMatch(p -> !p.test(clazz, method.get()))) {
      Log.error("0x11021: A check for the class method failed for method '" + method.get().getName() + "'");
    }

    clazz.addCDMember(method.get());
  }

  /**
   * Use this method to describe the signature (with concrete syntax) in templates
   *
   * <p><b>This method is intended to be used from templates.</b></p>
   *
   * @param signature the signature (as processed by {@link de.monticore.cd4code._parser.CD4CodeParser#parseCDConstructor(String) CD4CodeParser})
   */
  public void constructor(String signature) {
    checkInitialized();

    CD4CTemplateHelper th = new CD4CTemplateHelper();
    th.constructor(signature);
    this.methodQueue.add(th);
  }

  /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\
  | Attributes                                                                |
  \*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

  /**
   * Creates an attribute from the given {@code signature}.
   *
   * @param clazz       the class from which to read necessary information
   * @param signature   the signature (as processed by {@link de.monticore.cd4code._parser.CD4CodeParser#parseCDAttribute(String) CD4CodeParser})
   *
   * @return the created attribute
   */
  public Optional<ASTCDAttribute> createAttribute(ASTCDClass clazz, String signature) {
    checkInitialized();
    this.attribute(signature);

    Optional<ASTCDAttribute> attr = this.methodQueue.peek().astcdAttribute;
    attr.ifPresent(a -> {
      this.setEnclosingScopeTo(a, clazz.getSpannedScope());
      this.attributePredicates.forEach(p -> p.test(a));
    });

    return attr;
  }

  private ASTCDAttribute setEnclosingScopeTo(ASTCDAttribute attribute, ICDBasisScope scope) {
    if (!this.attributePredicates.isEmpty() || !this.classAttrPredicates.isEmpty()) {
      attribute.accept(CD4CodeMill.scopesGenitorDelegator().getTraverser());
    }
    return attribute;
  }

  /**
   * Adds an attribute with the given {@code signature} to the given class.
   *
   * @param clazz       the class to which the attribute should be added
   * @param signature   the signature (as processed by {@link de.monticore.cd4code._parser.CD4CodeParser#parseCDAttribute(String) CD4CodeParser})
   */
  public void addAttribute(ASTCDClass clazz, String signature) {
    this.addAttribute(clazz, false, false, signature);
  }

  /**
   * Adds an attribute with the given {@code signature} to the given class.
   *
   * @param clazz       the class to which the attribute should be added
   * @param addGetter   whether to generate a getter for the attribute
   * @param addSetter   whether to generate a setter for the attribute
   * @param signature   the signature (as processed by {@link de.monticore.cd4code._parser.CD4CodeParser#parseCDAttribute(String) CD4CodeParser})
   *
   */
  public void addAttribute(ASTCDClass clazz, boolean addGetter, boolean addSetter, String signature) {
    checkInitialized();

    Optional<ASTCDAttribute> attribute = this.createAttribute(clazz, signature);
    if (!attribute.isPresent()) {
      Log.error("0x11022: There was no attribute created in the template '" + signature + "'");
      return;
    }

    this.setEnclosingScopeTo(attribute.get(), clazz.getSpannedScope());
    this.classAttrPredicates.forEach((p -> p.test(clazz, attribute.get())));
    clazz.addCDMember(attribute.get());

    this.addMethods(clazz, attribute.get(), addGetter, addSetter);
  }

  public void addMethods(ASTCDClass clazz, ASTCDAttribute attr, boolean addGetter, boolean addSetter) {
    if (addGetter) {
      AccessorDecorator accessor = new AccessorDecorator(config.getGlex());
      clazz.addAllCDMembers(accessor.decorate(attr));
    }
    if (addSetter) {
      MutatorDecorator mutator = new MutatorDecorator(config.getGlex());
      clazz.addAllCDMembers(mutator.decorate(attr));
    }
  }

  /**
   * Use this method to describe the signature (with concrete syntax) in templates
   *
   * <p><b>This method is intended to be used from templates.</b></p>
   *
   * @param signature the signature (as processed by {@link de.monticore.cd4code._parser.CD4CodeParser#parseCDAttribute(String) CD4CodeParser})
   */
  public void attribute(String signature) {
    checkInitialized();

    CD4CTemplateHelper th = new CD4CTemplateHelper();
    th.attribute(signature);
    this.methodQueue.add(th);
  }

  /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\
  | Imports                                                                   |
  \*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

  /**
   * Adds an import with the given {@code signature} to the given class.
   *
   * @param clazz     the class to which the import should be added
   * @param signature the signature (as processed by {@link de.monticore.cd4code._parser.CD4CodeParser#parseMCImportStatement(String) CD4CodeParser})
   */
  public void addImport(ASTCDType clazz, String signature) {
    checkInitialized();

    CD4CTemplateHelper th = new CD4CTemplateHelper();
    th.importStr(signature);

    Set<ASTMCImportStatement> s = importMap.computeIfAbsent(clazz, it -> Sets.newHashSet());
    s.add(th.astcdImport.get());
  }

  public Collection<ASTMCImportStatement> getImportList(ASTCDType clazz) {
    return importMap.getOrDefault(clazz, Sets.newHashSet());
  }

  /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\
  | Internal Methods                                                          |
  \*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

  /**
   * execute the template and add the body to the created method
   *
   * @param clazz        the class where information can be read from
   * @param templateName the name of the template that is used for the method
   * @param arguments    the arguments for the template
   * @return the created method with connected method body
   */
  protected Optional<CD4CTemplateHelper> createMethodSignatureAndBody(ASTCDClass clazz, String templateName, Object... arguments) {
    checkInitialized();
    final TemplateHookPoint templateHookPoint = new TemplateHookPoint(
      templateName,
      arguments);

    TemplateController controller = new TemplateController(this.config, templateName);
    return addMethodBody(templateHookPoint.processValue(controller, clazz));
  }

  /**
   * add the method body (given as plain string)
   * and add it to the most recent created method
   *
   * @param body the method body given as string
   * @return the created method with connected method body
   */
  protected Optional<CD4CTemplateHelper> addMethodBody(String body) {
    checkInitialized();
    if (methodQueue.isEmpty()) {
      throw new RuntimeException("0x110A0: cannot add method body: no previous method present");
    }

    final CD4CTemplateHelper methodHelper = methodQueue.pop();
    if (!methodHelper.getMethod().isPresent()) {
      // if the method is not set, then add it to the stack again
      methodQueue.add(methodHelper);
      return Optional.empty();
    }

    final ASTCDMethodSignature method = methodHelper.getMethod().get();
    config.getGlex().replaceTemplate(emptyBodyTemplate, method, new StringHookPoint(body));

    return Optional.of(methodHelper);
  }

  /**
   * add a predicate that should be checked if a method is valid
   *
   * @param predicate a method that checks a method
   * @return the current CD4C object
   */
  public CD4C addPredicate(Predicate<ASTCDMethodSignature> predicate) {
    this.methodPredicates.add(predicate);
    return this;
  }

  /**
   * add a coco that should be checked if a method is valid
   *
   * @param predicate a coco that checks a method
   * @return the current CD4C object
   */
  public CD4C addCoco(CDBasisASTCDAttributeCoCo predicate) {
    this.attributePredicates.add((a) -> {
      predicate.check(a);
      return true;
    });
    return this;
  }

  /**
   * add a predicate that should be checked if a method is valid
   *
   * @param predicate a method that checks a method
   * @return the current CD4C object
   */
  public CD4C addAttributePredicate(Predicate<ASTCDAttribute> predicate) {
    this.attributePredicates.add(predicate);
    return this;
  }

  /**
   * add a coco that should be checked if a method is valid
   *
   * @param predicate a coco that checks a method
   * @return the current CD4C object
   */
  public CD4C addCoco(CD4CodeBasisASTCDMethodSignatureCoCo predicate) {
    this.methodPredicates.add((m) -> {
      predicate.check(m);
      return true;
    });
    return this;
  }

  /**
   * add predefined predicates
   *
   * @return the current CD4C object
   */
  public CD4C addDefaultPredicates() {
    // methods
    // check parameter types
    addPredicate((m) -> {
      final List<String> unknownTypes = m.getCDParameterList().stream().filter(p ->
          // if parameter types are not valid/exist
          !typesCalculator.synthesizeType(p.getMCType()).isPresentCurrentResult()
        )
        .map(p -> prettyPrinter.prettyprint(p.getMCType()))
        .collect(Collectors.toList());
      if (unknownTypes.isEmpty()) {
        return true;
      }
      else {
        Log.error("0x110C0: The following types of the method signature (" +
          prettyPrinter.prettyprint((ASTCD4CodeBasisNode) m) + ") could not be resolved '"
          + Joiners.COMMA.join(unknownTypes) + "'.");
        return false;
      }
    });
    // check return type
    addPredicate((m) -> {
      if (m instanceof ASTCDMethod) {
        final ASTCDMethod method = (ASTCDMethod) m;
        if (!new DeriveSymTypeOfCD4Code().synthesizeType(method.getMCReturnType()).isPresentCurrentResult()) {
          Log.error("0x110C1: The return type '" + prettyPrinter.prettyprint(method.getMCReturnType()) + "' of the method signature (" +
            prettyPrinter.prettyprint((ASTCD4CodeBasisNode) m) + ") could not be resolved.");
          return false;
        }
      }
      return true;
    });
    addCoco(new CDMethodSignatureParameterNamesUnique());

    // attributes
    // check type
    addAttributePredicate((attribute) -> {
      if (!new DeriveSymTypeOfCD4Code().synthesizeType(attribute.getMCType()).isPresentCurrentResult()) {
        Log.error("0x110C2: The type '" + prettyPrinter.prettyprint(attribute.getMCType()) + "' of the attribute declaration (" +
          prettyPrinter.prettyprint(attribute) + ") could not be resolved.");
        return false;
      }

      return true;
    });
    return this;
  }

  /**
   * add a predicate that is checked when a attribute should be added to a class
   *
   * @param predicate a predicate that checks a method before adding it to the class
   * @return the current CD4C object
   */
  public CD4C addAttrClassPredicate(BiPredicate<ASTCDClass, ASTCDAttribute> predicate) {
    this.classAttrPredicates.add(predicate);
    return this;
  }

  /**
   * add a predicate that is checked when a method should be added to a class
   *
   * @param predicate a predicate that checks a method before adding it to the class
   * @return the current CD4C object
   */
  public CD4C addClassPredicate(BiPredicate<ASTCDClass, ASTCDMethodSignature> predicate) {
    this.classPredicates.add(predicate);
    return this;
  }


  /**
   * add predefined class predicates
   *
   * @return the current CD4C object
   */
  public CD4C addDefaultClassPredicates() {
    // methods
    addClassPredicate((c, m) -> {
      final List<String> parameterTypes = m.getCDParameterList().stream()
        .map(p -> typesCalculator.synthesizeType(p.getMCType()).getCurrentResult().getTypeInfo().getFullName())
        .collect(Collectors.toList());
      if (c.getCDMethodSignatureList()
        .stream()
        .anyMatch(cm -> {
          final List<String> parameter = cm.getCDParameterList().stream()
            .map(p -> typesCalculator.synthesizeType(p.getMCType()).getCurrentResult().getTypeInfo().getFullName())
            .collect(Collectors.toList());
          return m.getName().equals(cm.getName()) &&
            Iterables.elementsEqual(parameterTypes, parameter);
        })) {
        Log.error("0x110C8: The class '" + c.getName() + "' already has a method named '" + m.getName() + "'");
        return false;
      }
      return true;
    });

    // attributes
    addAttrClassPredicate((c, a) -> {
      final String attrType = typesCalculator.synthesizeType(a.getMCType()).getCurrentResult().getTypeInfo().getFullName();
      if (c.getCDAttributeList()
        .stream()
        .anyMatch(ca -> attrType.equals(typesCalculator.synthesizeType(ca.getMCType()).getCurrentResult().getTypeInfo().getFullName()))) {
        Log.error("0x110C9: The class '" + c.getName() + "' already has a attribute named '" + a.getName() + "'");
        return false;
      }
      return true;
    });

    return this;
  }

}
