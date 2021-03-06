// (c) https://github.com/MontiCore/monticore

package de.monticore.cd.methodtemplates;

import com.google.common.collect.Iterables;
import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.cd4codebasis._ast.ASTCD4CodeBasisNode;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDMethodSignatureCoCo;
import de.monticore.cd4codebasis.cocos.ebnf.CDMethodSignatureParameterNamesUnique;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CD4C {
  public static final String GLEX_GLOBAL_VAR = "cd4c";
  /**
   * build a stack for the defined methods to match the body to the "current" defined method signature
   */
  protected final Stack<CD4CTemplateMethodHelper> methodQueue = new Stack<>();
  protected GeneratorSetup config;
  protected String emptyBodyTemplate = "de.monticore.cd.methodtemplates.core.EmptyMethod";
  protected static CD4C INSTANCE;
  protected boolean isInitialized = false;
  protected List<Predicate<ASTCDMethodSignature>> methodPredicates = new ArrayList<>();
  protected List<Predicate<ASTCDAttribute>> attributePredicates = new ArrayList<>();
  protected List<BiPredicate<ASTCDClass, ASTCDMethodSignature>> classPredicates = new ArrayList<>();
  protected List<BiPredicate<ASTCDClass, ASTCDAttribute>> classAttrPredicates = new ArrayList<>();
  protected CD4CodeFullPrettyPrinter prettyPrinter = new CD4CodeFullPrettyPrinter();
  protected CDTypesCalculator typesCalculator = new DeriveSymTypeOfCD4Code();

  protected CD4C() {
  }

  public static CD4C getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CD4C();
    }
    return INSTANCE;
  }

  /**
   * initialize the CD4C infrastructure
   * has to be called to use CD4C
   *
   * @param setup the generator setup to use for the generation
   */
  public static CD4C init(GeneratorSetup setup) {
    getInstance().config = setup;
    setup.getGlex().defineGlobalVar(GLEX_GLOBAL_VAR, INSTANCE);
    INSTANCE.isInitialized = true;
    return INSTANCE;
  }

  /**
   * reset the CD4C infrastructure
   */
  public static void reset() {
    if (INSTANCE != null) {
      INSTANCE.config.getGlex().changeGlobalVar(GLEX_GLOBAL_VAR, null);
      INSTANCE.isInitialized = false;
      INSTANCE = null;
    }
  }

// TODO: Warum ist dies static, checkInitialized aber nicht?
//
  public static boolean isInitialized() {
    return getInstance().isInitialized;
  }

  /**
   * check if the CD4C infrastructure is initialized,
   * if not, then an exception is thrown
   */
  protected void checkInitialized() {
    if (!isInitialized()) {
      final String error = "0x11000: CD4C is not yet initialized";
      Log.error(error);
      throw new RuntimeException(error + ", please initialize with `CD4C.init(setup)`");
    }
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

  /***************************************************************************/
  /* Methods                                                                 */
  /***************************************************************************/

  /**
   * create the new method
   *
   * @param clazz        the class where information can be read from
   * @param templateName the name of the template that is executed to create the method signature
   * @param arguments    the arguments to the template
   * @return the created method
   */
  public Optional<ASTCDMethodSignature> createMethod(ASTCDClass clazz, String templateName, Object... arguments) {
    checkInitialized();
    return createMethodSignatureAndBody(clazz, templateName, arguments)
        .flatMap(CD4CTemplateMethodHelper::getMethod)
        .map(m -> setEnclosingScopeTo(m, clazz.getSpannedScope()))
        .flatMap(m ->
            this.methodPredicates.stream().anyMatch(p -> !p.test(m)) ? Optional.empty() : Optional.of(m)
        );
  }

  /**
   * add the new method to the provided class
   *
   * @param clazz        the class where the method should be added
   * @param templateName the name of the template that is used for the method
   * @param arguments    the arguments for the template
   */
  public void addMethod(ASTCDClass clazz, String templateName, Object... arguments) {
    checkInitialized();
    final Optional<ASTCDMethodSignature> method = createMethod(clazz, templateName, arguments);
    if (!method.isPresent()) {
      Log.error("0x11010: There was no method created in the template '" + templateName + "'");
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
   * @param methodSignature the method signature as {@link de.monticore.cd4codebasis._ast.ASTCDMethod}
   */
  public void method(String methodSignature) {
    checkInitialized();
    final CD4CTemplateMethodHelper m = new CD4CTemplateMethodHelper();
    m.method(methodSignature);
    methodQueue.add(m);
  }

  /***************************************************************************/
  /* Constructors                                                                */
  /***************************************************************************/

  /**
   * create the new constructor
   *
   * @param clazz        the class where information can be read from
   * @param templateName the name of the template that is executed to create the method signature
   * @param arguments    the arguments to the template
   * @return the created constructor
   */
  public Optional<ASTCDMethodSignature> createConstructor(ASTCDClass clazz, String templateName, Object... arguments) {
    checkInitialized();
    return createMethodSignatureAndBody(clazz, templateName, arguments)
        .flatMap(CD4CTemplateMethodHelper::getMethod)
        .map(m -> setEnclosingScopeTo(m, clazz.getSpannedScope()))
        .flatMap(m ->
            this.methodPredicates.stream().anyMatch(p -> !p.test(m)) ? Optional.empty() : Optional.of(m)
        );
  }

  private ASTCDMethodSignature setEnclosingScopeTo(ASTCDMethodSignature method, ICDBasisScope scope) {
    // TODO: maybe just create a symbol table
    if (!this.methodPredicates.isEmpty() || !this.classPredicates.isEmpty()) {
      method.setEnclosingScope(scope);
      method.getCDParameterList().forEach(p -> {
        p.getMCType().setEnclosingScope(scope);
      });
      if (method instanceof ASTCDMethod) {
        ((ASTCDMethod) method).getMCReturnType().setEnclosingScope(scope);
      }
    }
    return method;
  }

  /**
   * add the new constructor to the provided class
   *
   * @param clazz        the class where the method should be added
   * @param templateName the name of the template that is used for the method
   * @param arguments    the arguments for the template
   */
  public void addConstructor(ASTCDClass clazz, String templateName, Object... arguments) {
    checkInitialized();
    final Optional<ASTCDMethodSignature> method = createConstructor(clazz, templateName, arguments);
    if (!method.isPresent()) {
      Log.error("0x11020: There was no constructor created in the template '" + templateName + "'");
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
   * @param constructorSignature the method signature as {@link de.monticore.cd4codebasis._ast.ASTCDConstructor}
   */
  public void constructor(String constructorSignature) {
    checkInitialized();
    final CD4CTemplateMethodHelper m = new CD4CTemplateMethodHelper();
    m.constructor(constructorSignature);
    methodQueue.add(m);
  }

  /***************************************************************************/
  /* Attributes                                                               */
  /***************************************************************************/

  /**
   * create the new attribute
   *
   * @param clazz        the class where information can be read from
   * @return the created attribute
   */
  public Optional<ASTCDAttribute> createAttribute(ASTCDClass clazz, String attributeSignature) {
    checkInitialized();
    attribute(attributeSignature);

    Optional<ASTCDAttribute> attr = methodQueue.peek().astcdAttribute;
    attr.ifPresent(a -> {setEnclosingScopeTo(a, clazz.getSpannedScope()); this.attributePredicates.forEach(p -> p.test(a));});
    return attr;
   }

  private ASTCDAttribute setEnclosingScopeTo(ASTCDAttribute attribute, ICDBasisScope scope) {
    // TODO: maybe just create a symbol table
    if (!this.attributePredicates.isEmpty() || !this.classAttrPredicates.isEmpty()) {
      attribute.setEnclosingScope(scope);
      attribute.getMCType().setEnclosingScope(scope);
    }
    return attribute;
  }

  /**
   * add the new constructor to the provided class
   *
   * @param clazz        the class where the method should be added
   * @param templateName the name of the template that is used for the method
   */
  public void addAttribute(ASTCDClass clazz, String templateName) {
    checkInitialized();
    final Optional<ASTCDAttribute> attribute = createAttribute(clazz, templateName);
    if (!attribute.isPresent()) {
      Log.error("0x11022: There was no attribute created in the template '" + templateName + "'");
      return;
    }

    setEnclosingScopeTo(attribute.get(), clazz.getSpannedScope());

    this.classAttrPredicates.forEach((p -> p.test(clazz, attribute.get())));

    clazz.addCDMember(attribute.get());
  }

  /**
   * Use this method to describe the signature (with concrete syntax) in templates
   *
   * @param attributeSignature the attribute signature as {@link de.monticore.cdbasis._ast.ASTCDAttribute}
   */
  public void attribute(String attributeSignature) {
    checkInitialized();
    final CD4CTemplateMethodHelper m = new CD4CTemplateMethodHelper();
    m.attribute(attributeSignature);
    methodQueue.add(m);
  }

  /***************************************************************************/
  /* internal methods                                                        */
  /***************************************************************************/

  /**
   * execute the template and add the body to the created method
   *
   * @param clazz        the class where information can be read from
   * @param templateName the name of the template that is used for the method
   * @param arguments    the arguments for the template
   * @return the created method with connected method body
   */
  protected Optional<CD4CTemplateMethodHelper> createMethodSignatureAndBody(ASTCDClass clazz, String templateName, Object... arguments) {
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
  protected Optional<CD4CTemplateMethodHelper> addMethodBody(String body) {
    checkInitialized();
    if (methodQueue.isEmpty()) {
      throw new RuntimeException("0x110A0: cannot add method body: no previous method present");
    }

    final CD4CTemplateMethodHelper methodHelper = methodQueue.pop();
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
          !typesCalculator.calculateType(p.getMCType()).isPresent()
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
        if (!new DeriveSymTypeOfCD4Code().calculateType(method.getMCReturnType()).isPresent()) {
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
        if (!new DeriveSymTypeOfCD4Code().calculateType(attribute.getMCType()).isPresent()) {
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
          .map(p -> typesCalculator.calculateType(p.getMCType()).get().getTypeInfo().getFullName())
          .collect(Collectors.toList());
      if (c.getCDMethodSignatureList()
          .stream()
          .anyMatch(cm -> {
            final List<String> parameter = cm.getCDParameterList().stream()
                .map(p -> typesCalculator.calculateType(p.getMCType()).get().getTypeInfo().getFullName())
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
      final String attrType = typesCalculator.calculateType(a.getMCType()).get().getTypeInfo().getFullName();
      if (c.getCDAttributeList()
        .stream()
        .anyMatch(ca -> {
          return attrType.equals(typesCalculator.calculateType(ca.getMCType()).get().getTypeInfo().getFullName());
        })) {
        Log.error("0x110C9: The class '" + c.getName() + "' already has a attribute named '" + a.getName() + "'");
        return false;
      }
      return true;
    });

    return this;
  }
}
