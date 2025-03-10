package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdgen.CDGenSetup;
import de.monticore.cdgen.decorators.*;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuilderCDTest {

  private static ASTCDClass builderClassWithSetters;
  private static ASTCDClass builderClassWithoutSetters;
  private static ASTCDClass pojoClassWithSetters;
  private static ASTCDClass pojoClassWithoutSetters;
  private static String builderFileContentWithSetters;
  private static String builderFileContentWithoutSetters;

  @BeforeClass
  public static void init() throws IOException {
    LogStub.initPlusLog();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4C.reset();
    CD4C.init(new GeneratorSetup());
    BasicSymbolsMill.initializePrimitives();
    MCCollectionSymTypeRelations.init();
    LogStub.initPlusLog();

    generateWithOutSetters();
    generateWithSetters();
  }

  @Test
  public void testConstructorSignatureOfBuilderClass() {
    // The Builder should have a constructor with the original class as parameter
    Assert.assertEquals(1, builderClassWithSetters.getCDConstructorList().size());
    Assert.assertTrue(builderClassWithSetters.getCDConstructorList().get(0).getModifier().isPublic());
    Assert.assertEquals("OtherCBuilder", builderClassWithSetters.getCDConstructorList().get(0).getName());
    Assert.assertEquals(0, builderClassWithSetters.getCDConstructorList().get(0).getCDParameterList().size());
  }

  @Test
  public void testConstructorBodyOfBuilderClass() {
    // The constructor should set the realBuilder attribute to this
    List<String> constructorBodies = extractConstructorBodies(builderFileContentWithSetters, "OtherCBuilder");
    Assert.assertEquals(1, constructorBodies.size());
    Assert.assertTrue(constructorBodies.get(0).contains("this.realBuilder = (OtherCBuilder) this;"));
  }

  @Test
  public void testBuildSignatureOfBuilderClass() {
    // The Builder should have a build method which generates the original class
    Assert.assertTrue(builderClassWithoutSetters.getCDMethodList().get(8).getModifier().isPublic());
    Assert.assertEquals("build", builderClassWithoutSetters.getCDMethodList().get(8).getName());
    Assert.assertEquals("OtherC", builderClassWithoutSetters.getCDMethodList().get(8).getMCReturnType().printType());
    Assert.assertEquals(0, builderClassWithoutSetters.getCDMethodList().get(8).getCDParameterList().size());
  }

  @Test
  public void testBuildBodyOfBuilderClass() {
    String buildBodiesWithSetters = extractMethodBySignature(builderFileContentWithSetters, "public\\s+OtherC\\s+build");
    String buildBodiesWithoutSetters = extractMethodBySignature(builderFileContentWithoutSetters, "public\\s+OtherC\\s+build");

    //body exists
    Assert.assertNotNull(buildBodiesWithSetters);
    Assert.assertNotNull(buildBodiesWithoutSetters);

    //isValid call
    Assert.assertTrue(buildBodiesWithSetters.contains("if(!isValid()){"));

    // create new instance of original class
    Assert.assertTrue(buildBodiesWithSetters.contains("var v = new OtherC();"));

    // set all attributes of the original class
    // with setters
    Assert.assertTrue(buildBodiesWithSetters.contains("v.setMyInt(this.myInt);"));
    // without setters
    Assert.assertTrue(buildBodiesWithoutSetters.contains("v.myInt = this.myInt;"));

    // boolean attributes with/without setter
    // with setters
    Assert.assertTrue(buildBodiesWithSetters.contains("v.setMyBool(this.myBool);"));
    // without setters
    Assert.assertTrue(buildBodiesWithoutSetters.contains("v.myBool = this.myBool;"));

    // set attributes with cardinality != 1
    // with setters
    Assert.assertTrue(buildBodiesWithSetters.contains("if(this.manyB!=null){"));
    Assert.assertTrue(buildBodiesWithSetters.contains("v.addManyB(this.manyB)"));
    // without setters
    Assert.assertTrue(buildBodiesWithoutSetters.contains("if(this.manyB!=null){"));
    Assert.assertTrue(buildBodiesWithoutSetters.contains("v.manyB = this.manyB;"));

    // optional attribute with cardinality 0..1
    // with setters
    Assert.assertTrue(buildBodiesWithSetters.contains("if(this.optB.isPresent()){"));
    Assert.assertTrue(buildBodiesWithSetters.contains("v.setOptB(this.optB.get());"));
    Assert.assertTrue(buildBodiesWithSetters.contains("}else{"));
    Assert.assertTrue(buildBodiesWithSetters.contains("v.setOptB(null);"));
    // without setters
    Assert.assertTrue(buildBodiesWithoutSetters.contains("if(this.optB.isPresent()){"));
    Assert.assertTrue(buildBodiesWithoutSetters.contains("v.optB = this.optB;"));
    Assert.assertTrue(buildBodiesWithoutSetters.contains("}else{"));
    Assert.assertTrue(buildBodiesWithoutSetters.contains("v.optB = Optional.empty();"));

    // class attribute with cardinality 1
    // with setters
    Assert.assertTrue(buildBodiesWithSetters.contains("v.setOneB(this.oneB);"));
    // without setters
    Assert.assertTrue(buildBodiesWithoutSetters.contains("v.oneB = this.oneB;"));
  }

  @Test
  public void testUnsafeBuildBodyOfBuilderClass() {
    String unsafeBuildBodiesWithSetters = extractMethodBySignature(builderFileContentWithSetters, "public\\s+OtherC\\s+unsafeBuild");
    String unsafeBuildBodiesWithoutSetters = extractMethodBySignature(builderFileContentWithoutSetters, "public\\s+OtherC\\s+unsafeBuild");

    //body exists
    Assert.assertNotNull(unsafeBuildBodiesWithSetters);
    Assert.assertNotNull(unsafeBuildBodiesWithoutSetters);

    //no isValid call
    Assert.assertFalse(unsafeBuildBodiesWithSetters.contains("if(!isValid()){"));

    // create new instance of original class
    Assert.assertTrue(unsafeBuildBodiesWithSetters.contains("var v = new OtherC();"));

    // set all attributes of the original class
    // with setters
    Assert.assertTrue(unsafeBuildBodiesWithSetters.contains("v.setMyInt(this.myInt);"));
    // without setters
    Assert.assertTrue(unsafeBuildBodiesWithoutSetters.contains("v.myInt = this.myInt;"));

    // boolean attributes with/without setter
    // with setters
    Assert.assertTrue(unsafeBuildBodiesWithSetters.contains("v.setMyBool(this.myBool);"));
    // without setters
    Assert.assertTrue(unsafeBuildBodiesWithoutSetters.contains("v.myBool = this.myBool;"));

    // set attributes with cardinality != 1
    // with setters
    Assert.assertFalse(unsafeBuildBodiesWithSetters.contains("if(this.manyB!=null){"));
    Assert.assertTrue(unsafeBuildBodiesWithSetters.contains("v.addManyB(this.manyB)"));
    // without setters
    Assert.assertFalse(unsafeBuildBodiesWithoutSetters.contains("if(this.manyB!=null){"));
    Assert.assertTrue(unsafeBuildBodiesWithoutSetters.contains("v.manyB = this.manyB;"));

    // optional attribute with cardinality 0..1
    // with setters
    //TODO check if we check for the presence of the optional value in the unsafeBuild method
    Assert.assertTrue(unsafeBuildBodiesWithSetters.contains("if(this.optB.isPresent()){"));
    Assert.assertTrue(unsafeBuildBodiesWithSetters.contains("v.setOptB(this.optB.get());"));
    Assert.assertFalse(unsafeBuildBodiesWithSetters.contains("}else{"));
    Assert.assertFalse(unsafeBuildBodiesWithSetters.contains("v.setOptB(null);"));
    // without setters
    Assert.assertTrue(unsafeBuildBodiesWithoutSetters.contains("if(this.optB.isPresent()){"));
    Assert.assertTrue(unsafeBuildBodiesWithoutSetters.contains("v.optB = this.optB;"));
    Assert.assertFalse(unsafeBuildBodiesWithoutSetters.contains("}else{"));
    Assert.assertFalse(unsafeBuildBodiesWithoutSetters.contains("v.optB = Optional.empty();"));


    // class attribute with cardinality 1
    // with setters
    Assert.assertTrue(unsafeBuildBodiesWithSetters.contains("v.setOneB(this.oneB);"));
    // without setters
    Assert.assertTrue(unsafeBuildBodiesWithoutSetters.contains("v.oneB = this.oneB;"));
  }

  @Test
  public void testUnsafeBuildOSignatureOfBuilderClass() {
    // The Builder should have an unsafeBuild method which generates the original class without checking the validity
    Assert.assertTrue(builderClassWithoutSetters.getCDMethodList().get(9).getModifier().isPublic());
    Assert.assertEquals("unsafeBuild", builderClassWithoutSetters.getCDMethodList().get(9).getName());
    Assert.assertEquals("OtherC", builderClassWithoutSetters.getCDMethodList().get(9).getMCReturnType().printType());
    Assert.assertEquals(0, builderClassWithoutSetters.getCDMethodList().get(9).getCDParameterList().size());
  }

  @Test
  public void testIsValidSignatureOfBuilderClass() {
    //isValid method has no parameters and returns a boolean
    //TODO: check if this is correct or if isValid should be public
    Assert.assertTrue(builderClassWithoutSetters.getCDMethodList().get(0).getModifier().isPrivate());
    Assert.assertEquals("isValid", builderClassWithoutSetters.getCDMethodList().get(0).getName());
    Assert.assertEquals("boolean", builderClassWithoutSetters.getCDMethodList().get(0).getMCReturnType().printType());
    Assert.assertEquals(0, builderClassWithoutSetters.getCDMethodList().get(0).getCDParameterList().size());
  }

  @Test
  public void testIsValidBodyOfBuilderClass() {
    //isValid method should return true
    String isValidBody = extractMethodBySignature(builderFileContentWithoutSetters, "private\\s+boolean\\s+isValid");
    Assert.assertNotNull(isValidBody);
    Assert.assertTrue(isValidBody.contains("if (this.oneB == null) {"));
    Assert.assertFalse(isValidBody.contains("if (this.myInt == null) {"));
    Assert.assertFalse(isValidBody.contains("if (this.myBool == null) {"));
    Assert.assertFalse(isValidBody.contains("if (this.manyB == null) {"));
    Assert.assertFalse(isValidBody.contains("if (this.optB == null) {"));
    Assert.assertTrue(isValidBody.contains("return true;"));
  }

  @Test
  public void testAttributesOfBuilderClass() {
    //compare the attributes of the original class with the attributes of the builder class
    for(int i =0; i< pojoClassWithSetters.getCDAttributeList().size(); i++){
      Assert.assertEquals(pojoClassWithSetters.getCDAttributeList().get(i).getName(), pojoClassWithSetters.getCDAttributeList().get(i).getName());
      Assert.assertEquals(pojoClassWithSetters.getCDAttributeList().get(i).getMCType().printType(), pojoClassWithSetters.getCDAttributeList().get(i).getMCType().printType());
    }

    // The Builder should have all attributes of the original class plus the realBuilder attribute
    Assert.assertEquals(6, builderClassWithoutSetters.getCDAttributeList().size());
    Assert.assertEquals("myInt", builderClassWithoutSetters.getCDAttributeList().get(0).getName());
    Assert.assertEquals("int", builderClassWithoutSetters.getCDAttributeList().get(0).getMCType().printType());
    Assert.assertEquals("myBool", builderClassWithoutSetters.getCDAttributeList().get(1).getName());
    Assert.assertEquals("boolean", builderClassWithoutSetters.getCDAttributeList().get(1).getMCType().printType());
    Assert.assertEquals("manyB", builderClassWithoutSetters.getCDAttributeList().get(2).getName());
    Assert.assertEquals("Set<MyCD.B>", builderClassWithoutSetters.getCDAttributeList().get(2).getMCType().printType());
    Assert.assertEquals("optB", builderClassWithoutSetters.getCDAttributeList().get(3).getName());
    Assert.assertEquals("Optional<MyCD.B>", builderClassWithoutSetters.getCDAttributeList().get(3).getMCType().printType());
    Assert.assertEquals("oneB", builderClassWithoutSetters.getCDAttributeList().get(4).getName());
    Assert.assertEquals("MyCD.B", builderClassWithoutSetters.getCDAttributeList().get(4).getMCType().printType());
    Assert.assertEquals("realBuilder", builderClassWithoutSetters.getCDAttributeList().get(5).getName());
    Assert.assertEquals("OtherCBuilder", builderClassWithoutSetters.getCDAttributeList().get(5).getMCType().printType());
  }

  @Test
  public void testSetterSignatureOfBuilderClass() {
    //Setter for every attribute of the original class not to be confused with the setter for the pojo setters
    Assert.assertEquals(10, builderClassWithSetters.getCDMethodList().size());
    Assert.assertEquals("setMyInt", builderClassWithoutSetters.getCDMethodList().get(1).getName());
    Assert.assertEquals("setMyBool", builderClassWithoutSetters.getCDMethodList().get(2).getName());
    Assert.assertEquals("setManyB", builderClassWithoutSetters.getCDMethodList().get(3).getName());
    Assert.assertEquals("setOptB", builderClassWithoutSetters.getCDMethodList().get(4).getName());
    Assert.assertEquals("setOneB", builderClassWithoutSetters.getCDMethodList().get(5).getName());

    //setAbsent method for every attribute with cardinality != 1
    Assert.assertEquals("setManyBAbsent", builderClassWithoutSetters.getCDMethodList().get(6).getName());
    Assert.assertEquals("setOptBAbsent", builderClassWithoutSetters.getCDMethodList().get(7).getName());
  }

  @Test
  public void testSetterBodyOfBuilderClass() {
    List<String> setterMethods = extractAllSetterMethods(builderFileContentWithSetters);
    List.of("this.optB = Optional.ofNullable(optB);\nreturn this.realBuilder;",
            "this.oneB = oneB;\nreturn this.realBuilder;",
            "this.manyB = new HashSet<>();\nreturn this.realBuilder;",
            "this.optB = Optional.empty();\nreturn this.realBuilder;",
            "this.manyB = manyB;\nreturn this.realBuilder;").forEach(setter -> {
      Assert.assertTrue(setterMethods.stream().anyMatch(m -> m.contains(setter)));
    });


  }

  private static Optional<ASTCDCompilationUnit> parseStringToCompilationUnitWithSetters() throws IOException {
    return CD4CodeMill.parser().parse_String("classdiagram MyCD {\n" +
      " <<setter,getter,builder>> public class OtherC { \n" +
      " public int myInt;\n" +
      " public boolean myBool;\n" +
      " -> (manyB) B [*];\n" +
      " -> (optB) B [0..1] ;\n" +
      " -> (oneB) B [1]; \n" +
      " }\n" +
      "<<setter>>public class B { " +
      "}\n " +
      "}");
  }

  private static Optional<ASTCDCompilationUnit> parseStringToCompilationUnitWithoutSetters() throws IOException {
    return CD4CodeMill.parser().parse_String("classdiagram MyCD {\n" +
      " <<getter,builder>> public class OtherC { \n" +
      " public int myInt;\n" +
      " public boolean myBool;\n" +
      " -> (manyB) B [*];\n" +
      " -> (optB) B [0..1] ;\n" +
      " -> (oneB) B [1]; \n" +
      " }\n" +
      "<<setter>>public class B { " +
      "}\n " +
      "}");
  }

  private static void generateWithOutSetters() throws IOException {
    CDGenSetup setup = new CDGenSetup();

    // the execution of the BuilderDecorator depends on the SetterDecorator executed previously
    setup.withDecorator(new SetterDecorator());
    setup.configApplyMatchName(SetterDecorator.class, ("setter"));
    setup.configIgnoreMatchName(SetterDecorator.class, ("noSetter"));

    setup.withDecorator(new BuilderDecorator());
    setup.configApplyMatchName(BuilderDecorator.class, "builder");
    setup.configIgnoreMatchName(BuilderDecorator.class,"noBuilder");

    Optional<ASTCDCompilationUnit> opt = parseStringToCompilationUnitWithoutSetters();

    // After parse Trafos
    CD4AnalysisAfterParseTrafo afterParseTrafo = new CD4AnalysisAfterParseTrafo();
    afterParseTrafo.transform(opt.get());

    // Create ST
    CD4CodeMill.scopesGenitorDelegator().createFromAST(opt.get());

    // Complete ST
    opt.get().accept(new CD4CodeSymbolTableCompleter(opt.get()).getTraverser());

    // Transform with ST
    CDAssociationCreateFieldsFromAllRoles roleTrafo = new CDAssociationCreateFieldsFromNavigableRoles();
    final CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
    traverser.add4CDAssociation(roleTrafo);
    traverser.setCDAssociationHandler(roleTrafo);
    roleTrafo.transform(opt.get());

    // Prepare
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    glex.setGlobalValue("mcTypeFacade", MCTypeFacade.getInstance());
    glex.setGlobalValue("mcCollectionSymTypeRelations", new MCCollectionSymTypeRelations());
    glex.setGlobalValue("cdGenService", new CDGenService());

    ASTCDCompilationUnit decorated = setup.decorate(opt.get(), roleTrafo.getFieldToRoles(), Optional.of(glex));

    // Post-Decorate
    CD4CodeTraverser t = CD4CodeMill.inheritanceTraverser();
    t.add4CDBasis(new CDBasisDefaultPackageTrafo());
    decorated.accept(t);

    ASTCDPackage cdPackage = decorated.getCDDefinition().getCDPackagesList().get(0);
    builderClassWithoutSetters = (ASTCDClass) cdPackage.getCDElement(5);
    pojoClassWithoutSetters = (ASTCDClass) cdPackage.getCDElement(0);

    // only used when analyzing the body's of methods/constructors
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File("target/outtest"));
    generatorSetup.getOutputDirectory().mkdirs();
    CDGenerator generator = new CDGenerator(generatorSetup);
    generator.generate(decorated);

    try {
      // Define the path to the file
      Path filePath = Paths.get("target/outtest/MyCD/OtherCBuilder.java");

      // Read all lines from the file
      List<String> lines = Files.readAllLines(filePath);

      // Convert List<String> to a single String
      StringBuilder stringBuilder = new StringBuilder();
      for (String line : lines) {
        stringBuilder.append(line).append("\n");
      }
      builderFileContentWithoutSetters = stringBuilder.toString();

    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }
  }

  private static void generateWithSetters() throws IOException {
    CDGenSetup setup = new CDGenSetup();

    // the execution of the BuilderDecorator depends on the SetterDecorator executed previously
    setup.withDecorator(new SetterDecorator());
    setup.configApplyMatchName(SetterDecorator.class, ("setter"));
    setup.configIgnoreMatchName(SetterDecorator.class, ("noSetter"));

    setup.withDecorator(new BuilderDecorator());
    setup.configApplyMatchName(BuilderDecorator.class, "builder");
    setup.configIgnoreMatchName(BuilderDecorator.class,"noBuilder");

    Optional<ASTCDCompilationUnit> opt = parseStringToCompilationUnitWithSetters();

    // After parse Trafos
    CD4AnalysisAfterParseTrafo afterParseTrafo = new CD4AnalysisAfterParseTrafo();
    afterParseTrafo.transform(opt.get());

    // Create ST
    CD4CodeMill.scopesGenitorDelegator().createFromAST(opt.get());

    // Complete ST
    opt.get().accept(new CD4CodeSymbolTableCompleter(opt.get()).getTraverser());

    // Transform with ST
    CDAssociationCreateFieldsFromAllRoles roleTrafo = new CDAssociationCreateFieldsFromNavigableRoles();
    final CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
    traverser.add4CDAssociation(roleTrafo);
    traverser.setCDAssociationHandler(roleTrafo);
    roleTrafo.transform(opt.get());

    // Prepare
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    glex.setGlobalValue("mcTypeFacade", MCTypeFacade.getInstance());
    glex.setGlobalValue("mcCollectionSymTypeRelations", new MCCollectionSymTypeRelations());
    glex.setGlobalValue("cdGenService", new CDGenService());

    ASTCDCompilationUnit decorated = setup.decorate(opt.get(), roleTrafo.getFieldToRoles(), Optional.of(glex));

    // Post-Decorate
    CD4CodeTraverser t = CD4CodeMill.inheritanceTraverser();
    t.add4CDBasis(new CDBasisDefaultPackageTrafo());
    decorated.accept(t);

    ASTCDPackage cdPackage = decorated.getCDDefinition().getCDPackagesList().get(0);
    builderClassWithSetters = (ASTCDClass) cdPackage.getCDElement(5);
    pojoClassWithSetters = (ASTCDClass) cdPackage.getCDElement(0);

    // only used when analyzing the body's of methods/constructors
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File("target/outtest"));
    generatorSetup.getOutputDirectory().mkdirs();
    CDGenerator generator = new CDGenerator(generatorSetup);
    generator.generate(decorated);

    try {
      // Define the path to the file
      Path filePath = Paths.get("target/outtest/MyCD/OtherCBuilder.java");

      // Read all lines from the file
      List<String> lines = Files.readAllLines(filePath);

      // Convert List<String> to a single String
      StringBuilder stringBuilder = new StringBuilder();
      for (String line : lines) {
        stringBuilder.append(line).append("\n");
      }
      builderFileContentWithSetters = stringBuilder.toString();

    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }
  }

  private static List<String> extractConstructorBodies(String fileContent,String className) {
    List<String> constructorBodies = new ArrayList<>();

    // Pattern to match constructors
    // This looks for: access modifier, optional class name, method name, parameters, and body between { }
    Pattern pattern = Pattern.compile("(public|private|protected)?\\s+(?!static|void|int|double|float|long|boolean)"+className+"+\\s*\\([^)]*\\)\\s*\\{([^{}]|\\{[^{}]*\\})*\\}");
    Matcher matcher = pattern.matcher(fileContent);

    while (matcher.find()) {
      String fullConstructor = matcher.group(0);
      // Extract just the body (everything between the first { and the last })
      int openBrace = fullConstructor.indexOf('{');
      int closeBrace = fullConstructor.lastIndexOf('}');

      if (openBrace != -1 && closeBrace != -1) {
        String body = fullConstructor.substring(openBrace + 1, closeBrace).trim();
        constructorBodies.add(body);
      }
    }

    return constructorBodies;
  }

  private static String extractMethodBySignature(String fileContent, String signaturePattern) {
    // Pattern to match the specific method signature followed by its body
    // The regex looks for the signature followed by optional whitespace,
    // optional parameters, and then the method body in curly braces
    Pattern pattern = Pattern.compile(signaturePattern + "\\s*\\([^)]*\\)\\s*\\{([^{}]|\\{[^{}]*\\})*\\}");
    Matcher matcher = pattern.matcher(fileContent);

    if (matcher.find()) {
      String fullMethod = matcher.group(0);
      // Extract just the body (everything between the first { and the last })
      int openBrace = fullMethod.indexOf('{');
      int closeBrace = fullMethod.lastIndexOf('}');

      if (openBrace != -1 && closeBrace != -1) {
        return fullMethod.substring(openBrace + 1, closeBrace).trim();
      }
    }

    return null;
  }

  private static List<String> extractAllSetterMethods(String fileContent) {
    List<String> setterMethods = new ArrayList<>();
    Pattern pattern = Pattern.compile(
      "(public|private|protected)\\s+\\w+\\s+set\\w+\\s*\\([^)]*\\)\\s*\\{([^{}]|\\{[^{}]*\\})*\\}"
    );

    Matcher matcher = pattern.matcher(fileContent);

    while (matcher.find()) {
      String setterMethod = matcher.group(0);
      setterMethods.add(setterMethod);
    }

    return setterMethods;
  }
}
