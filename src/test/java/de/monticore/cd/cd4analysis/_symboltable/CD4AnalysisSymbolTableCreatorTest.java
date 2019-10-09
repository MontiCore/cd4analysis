/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import static de.monticore.symboltable.modifiers.BasicAccessModifier.*;
import static org.junit.Assert.*;

public class CD4AnalysisSymbolTableCreatorTest {
  
  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSymbolTableCreation() {
    final
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    
    final CDDefinitionSymbol cdSymbol = globalScope.resolveCDDefinition(
        "de.monticore.umlcd4a.symboltable.CD1").orElse(null);
    assertNotNull(cdSymbol);

    // Scope Hierarchy: GlobalScope -> ArtifactScope -> ClassDiagramScope ->* ...
    assertEquals(1, globalScope.getSubScopes().size());
    final CD4AnalysisArtifactScope artifactScope = (CD4AnalysisArtifactScope) globalScope.getSubScopes().get(0);
    assertSame(artifactScope, cdSymbol.getEnclosingScope());

    assertEquals(1, artifactScope.getSubScopes().size());

    // TODO PN find better solution
    // Quickfix for using default types: add built-in types
    globalScope.add(new CDTypeSymbol("int"));
    globalScope.add(new CDTypeSymbol("boolean"));
    globalScope.add(new CDTypeSymbol("String"));
    final CDTypeSymbol builtInList = new CDTypeSymbol("java.util.List");
   // TODO Check builtInList.setPackageName("java.util");
    globalScope.add(builtInList);


    final CDTypeSymbol creatureType = cdSymbol.getSpannedScope().resolveCDType("Creature").orElse(null);

    assertNotNull(creatureType.getSpannedScope());
    assertSame(creatureType, creatureType.getSpannedScope().getSpanningSymbol());
    assertEquals("Creature", creatureType.getName());
    assertTrue(creatureType.isIsAbstract());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Creature", creatureType.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", creatureType.getPackageName());
    assertTrue(creatureType.isIsPublic());
    // AST
    assertTrue(creatureType.isPresentAstNode());
    assertTrue(creatureType.getAstNode() instanceof ASTCDClass);
    assertSame(creatureType, creatureType.getAstNode().getSymbol());
    assertSame(creatureType.getEnclosingScope(), creatureType.getAstNode().getEnclosingScope());
    // Fields
    assertEquals(1, creatureType.getFields().size());
    final CDFieldSymbol extinctField = creatureType.getSpannedScope().resolveCDField("extinct").get();
    assertEquals("extinct", extinctField.getName());


    final CDTypeSymbol personType = globalScope.resolveCDType(
        "de.monticore.umlcd4a.symboltable.CD1.Person").orElse(null);
    assertNotNull(personType);
    assertNotNull(personType.getSpannedScope());
    assertSame(personType, personType.getSpannedScope().getSpanningSymbol());
    assertEquals("Person", personType.getName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Person", personType.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", personType.getPackageName());
    assertTrue(personType.isIsPublic());
    // AST
    assertTrue(personType.isPresentAstNode());
    assertTrue(personType.getAstNode() instanceof ASTCDClass);
    assertSame(personType, personType.getAstNode().getSymbol());
    assertSame(personType.getEnclosingScope(), personType.getAstNode().getEnclosingScope());
    // Associations
    assertEquals(1, personType.getAssociations().size());
    // Fields
    assertEquals(4, personType.getFields().size());
    final CDFieldSymbol nameField = personType.getSpannedScope().resolveCDField("name").get();
    assertEquals("name", nameField.getName());
    assertTrue(nameField.isIsPublic());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Person.name", nameField.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", nameField.getPackageName());
    assertEquals("String", nameField.getType().getName());
    // AST
    assertTrue(nameField.isPresentAstNode());
    assertTrue(nameField.getAstNode() instanceof ASTCDField);
    assertSame(nameField, nameField.getAstNode().getSymbol());
    assertSame(nameField.getEnclosingScope(), nameField.getAstNode().getEnclosingScope());
    final CDFieldSymbol secondNameField = personType.getSpannedScope().resolveCDField("secondName").get();
    assertEquals("secondName", secondNameField.getName());
    assertTrue(secondNameField.isIsPrivate());
    final CDFieldSymbol ageField = personType.getSpannedScope().resolveCDField("age").get();
    assertEquals("age", ageField.getName());
    assertTrue(personType.getSpannedScope().resolveCDField("age").get().isIsProtected());

    // Field Stereotypes
    assertEquals(1, nameField.getStereotypes().size());
    assertEquals("SF", nameField.getStereotype("SF").get().getName());
    assertEquals("SF", nameField.getStereotype("SF").get().getValue());
    // Stereotypes
    assertEquals(2, personType.getStereotypes().size());
    assertEquals("S1", personType.getStereotype("S1").get().getName());
    assertEquals("S2", personType.getStereotype("S2").get().getName());

    assertEquals("", personType.getStereotype("S1").get().getValue());
    assertEquals("", personType.getStereotype("S2").get().getValue());
    // Methods
    assertEquals(2, personType.getMethods().size());
    final CDMethOrConstrSymbol setNameMethod = personType.getSpannedScope().resolveCDMethOrConstr("setName").orElse(null);
    assertNotNull(setNameMethod);
    assertEquals("setName", setNameMethod.getName());
    assertSame(personType, setNameMethod.getDefiningType());
    assertTrue(setNameMethod.isIsPublic());
    assertFalse(setNameMethod.isIsConstructor());
    assertFalse(setNameMethod.isIsFinal());
    assertFalse(setNameMethod.isIsAbstract());
    assertFalse(setNameMethod.isIsEllipsis());
    assertEquals("String", setNameMethod.getReturnType().getName());
    // Parameters
    assertEquals(2, setNameMethod.getParameters().size());
    assertEquals("name", setNameMethod.getParameters().get(0).getName());
    assertTrue(setNameMethod.getParameters().get(0).isIsParameter());
    final CDFieldSymbol prefixParameter = setNameMethod.getParameters().get(1);
    assertEquals("prefix", prefixParameter.getName());
    assertTrue(prefixParameter.isIsParameter());
    assertEquals("String", prefixParameter.getType().getName());
    // AST
    assertTrue(setNameMethod.isPresentAstNode());
    assertTrue(setNameMethod.getAstNode() instanceof ASTCDMethod);
    assertSame(setNameMethod, setNameMethod.getAstNode().getSymbol());
    assertSame(setNameMethod.getEnclosingScope(), setNameMethod.getAstNode().getEnclosingScope());

    assertTrue(personType.getSpannedScope().resolveCDMethOrConstr("getAge").isPresent());
    assertTrue(personType.getSpannedScope().resolveCDMethOrConstr("getAge").get().isIsPrivate());
    assertEquals(0, personType.getSpannedScope().resolveCDMethOrConstr("getAge").get().getParameters().size());


    final CDTypeSymbol profType = cdSymbol.getType("Prof").orElse(null);
    assertNotNull(profType);
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Prof", profType.getFullName());
    assertTrue(profType.isIsPrivate());
    assertEquals(2, profType.getFields().size());
    assertEquals("uni", profType.getSpannedScope().resolveCDField("uni").get().getName());
    assertTrue(profType.getSpannedScope().resolveCDField("uni").get().isIsDerived());
    final CDFieldSymbol profFieldPP = profType.getSpannedScope().resolveCDField("pp").orElse(null);
    assertNotNull(profFieldPP);
    final CDTypeSymbolReference personList = profFieldPP.getType();
    assertEquals("List", personList.getName());
    Assert.assertEquals("List<Person>", personList.getStringRepresentation());
    // Super class
    assertTrue(profType.isPresentSuperClass());
    assertEquals(personType.getName(), profType.getSuperClass().getName());
    // The referenced symbol is the SAME as the one in the symbol table.
    assertSame(personType, ((CDTypeSymbolReference) profType.getSuperClass())
        .getReferencedSymbol());
    // Interfaces
    assertEquals(2, profType.getCdInterfaceList().size());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Printable", profType.getCdInterfaceList().get(0).getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Callable", profType.getCdInterfaceList().get(1).getFullName());
    assertEquals(3, profType.getSuperTypes().size());
    // Associations
    assertEquals(1, profType.getAssociations().size());

    final CDTypeSymbol printableType = cdSymbol.getType("Printable").orElse(null);
    assertNotNull(printableType);
    assertEquals("Printable", printableType.getName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Printable", printableType.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", printableType.getPackageName());
    assertTrue(printableType.isIsInterface());
    assertTrue(printableType.isIsProtected());
    // Methods
    final CDMethOrConstrSymbol printMethod = printableType.getSpannedScope().resolveCDMethOrConstr("print").orElse(null);
    assertNotNull(printMethod);
    assertEquals("print", printMethod.getName());
    assertTrue(printMethod.isIsProtected());
    assertFalse(printMethod.isIsConstructor());
    assertFalse(printMethod.isIsFinal());
    assertTrue(printMethod.isIsAbstract());
    assertTrue(printMethod.isIsEllipsis());
    assertEquals(1, printMethod.getParameters().size());
    assertEquals("s", printMethod.getParameters().get(0).getName());
    // AST
    assertTrue(printableType.isPresentAstNode());
    assertTrue(printableType.getAstNode() instanceof ASTCDInterface);
    assertSame(printableType, printableType.getAstNode().getSymbol());
    assertSame(printableType.getEnclosingScope(), printableType.getAstNode().getEnclosingScope());
    // Associations
    assertEquals(0, printableType.getAssociations().size());


    final CDTypeSymbol callableType = cdSymbol.getType("Callable").orElse(null);
    assertNotNull(callableType);
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Callable", callableType.getFullName());
    assertTrue(callableType.isIsInterface());
    assertTrue(callableType.isIsPublic());
    assertEquals(1, callableType.getCdInterfaceList().size());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Printable", callableType.getCdInterfaceList().get(0).getFullName());

    final CDTypeSymbol enumType = cdSymbol.getType("E").orElse(null);
    assertNotNull(enumType);
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.E", enumType.getFullName());
    assertTrue(enumType.isIsEnum());
    assertTrue(enumType.isIsPublic());
    // Enum Constants
    assertEquals(2, enumType.getEnumConstants().size());
    assertEquals("A", enumType.getEnumConstants().get(0).getName());
    assertEquals(enumType.getName(), enumType.getEnumConstants().get(0).getType().getName());
    assertEquals("B", enumType.getEnumConstants().get(1).getName());
    assertEquals(enumType.getName(), enumType.getEnumConstants().get(1).getType().getName());
    assertEquals(enumType.getEnumConstants(), enumType.getFields());
    // Interfaces
    assertEquals(1, enumType.getCdInterfaceList().size());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Printable", enumType.getCdInterfaceList().get(0).getFullName());
    // AST
    assertTrue(enumType.isPresentAstNode());
    assertTrue(enumType.getAstNode() instanceof ASTCDEnum);
    assertSame(enumType, enumType.getAstNode().getSymbol());
    assertSame(enumType.getEnclosingScope(), enumType.getAstNode().getEnclosingScope());

    final ICD4AnalysisScope cdScope = cdSymbol.getSpannedScope();
    // Bidirectional association A <-> B is splitted into two associations A -> B and A <- B.
    // A -> B
    final CDAssociationSymbol memberAssocLeft2Right = (CDAssociationSymbol)
        cdScope.resolveCDAssociationDown("member",  BasicAccessModifier.ALL_INCLUSION,
            new CDAssociationNameAndTargetNamePredicate("member", "Prof")).orElse(null);
    assertNotNull(memberAssocLeft2Right);
    assertEquals("member", memberAssocLeft2Right.getName());
    assertEquals("member", memberAssocLeft2Right.getAssocName().orElse(""));
    assertTrue(memberAssocLeft2Right.isBidirectional());
    assertEquals(personType.getName(), memberAssocLeft2Right.getSourceType().getName());
    assertEquals(profType.getName(), memberAssocLeft2Right.getTargetType().getName());
    assertEquals(0, memberAssocLeft2Right.getSourceCardinality().getMin());
    assertEquals(Cardinality.STAR, memberAssocLeft2Right.getSourceCardinality().getMax());
    assertTrue(memberAssocLeft2Right.getSourceCardinality().isMultiple());
    assertEquals(1, memberAssocLeft2Right.getTargetCardinality().getMin());
    assertEquals(1, memberAssocLeft2Right.getTargetCardinality().getMax());
    assertFalse(memberAssocLeft2Right.getTargetCardinality().isMultiple());
    // AST
    assertTrue(memberAssocLeft2Right.isPresentAstNode());
    assertTrue(memberAssocLeft2Right.getAstNode() instanceof ASTCDAssociation);
    ASTCDAssociation left2RightNode = (ASTCDAssociation) memberAssocLeft2Right.getAstNode();
    assertTrue(left2RightNode.getLeftToRightSymbol().isPresent());
    assertSame(memberAssocLeft2Right, left2RightNode.getLeftToRightSymbol().get());

    // A <- B
    final CDAssociationSymbol memberAssocRight2Left = cdScope.resolveCDAssociation("member",
        BasicAccessModifier.ALL_INCLUSION,new CDAssociationNameAndTargetNamePredicate("member", "Person")).orElse(null);
    assertNotNull(memberAssocRight2Left);
    assertEquals("member", memberAssocRight2Left.getName());
    assertEquals("member", memberAssocRight2Left.getAssocName().orElse(""));
    assertTrue(memberAssocRight2Left.isBidirectional());
    assertEquals(profType.getName(), memberAssocRight2Left.getSourceType().getName());
    assertEquals(personType.getName(), memberAssocRight2Left.getTargetType().getName());
    assertEquals(1, memberAssocRight2Left.getSourceCardinality().getMin());
    assertEquals(1, memberAssocRight2Left.getSourceCardinality().getMax());
    assertFalse(memberAssocRight2Left.getSourceCardinality().isMultiple());
    assertEquals(0, memberAssocRight2Left.getTargetCardinality().getMin());
    assertEquals(Cardinality.STAR, memberAssocRight2Left.getTargetCardinality().getMax());
    assertTrue(memberAssocRight2Left.getTargetCardinality().isMultiple());
    // AST
    assertTrue(memberAssocRight2Left.isPresentAstNode());
    assertTrue(memberAssocRight2Left.getAstNode() instanceof ASTCDAssociation);
    ASTCDAssociation right2LeftNode = (ASTCDAssociation) memberAssocRight2Left.getAstNode();
    assertTrue(right2LeftNode.getLeftToRightSymbol().isPresent());
    assertSame(memberAssocRight2Left, right2LeftNode.getRightToLeftSymbol().get());
    // Stereotype
    assertEquals(1, memberAssocRight2Left.getStereotypes().size());
    assertEquals("SA", memberAssocRight2Left.getStereotype("SA").get().getValue());
    assertEquals("SA", memberAssocRight2Left.getStereotype("SA").get().getName());

    // A -> B
    final CDAssociationSymbol ecAssocLeft2Right = cdScope.resolveCDAssociation("ec",
        BasicAccessModifier.ALL_INCLUSION,new CDAssociationNameAndTargetNamePredicate("ec", "Callable")).orElse(null);
    assertNotNull(ecAssocLeft2Right);
    assertEquals("ec", ecAssocLeft2Right.getName());
    assertEquals("ec", ecAssocLeft2Right.getAssocName().orElse(""));
    assertTrue(ecAssocLeft2Right.isBidirectional());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.E", ecAssocLeft2Right.getSourceType().getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Callable", ecAssocLeft2Right.getTargetType().getFullName());
    assertEquals(1, ecAssocLeft2Right.getSourceCardinality().getMin());
    assertEquals(Cardinality.STAR, ecAssocLeft2Right.getSourceCardinality().getMax());
    assertTrue(ecAssocLeft2Right.getSourceCardinality().isMultiple());
    assertEquals(0, ecAssocLeft2Right.getTargetCardinality().getMin());
    assertEquals(1, ecAssocLeft2Right.getTargetCardinality().getMax());
    assertFalse(ecAssocLeft2Right.getTargetCardinality().isMultiple());
    // A <- B
    final CDAssociationSymbol ecAssocRight2Left = cdScope.resolveCDAssociation("ec",
        BasicAccessModifier.ALL_INCLUSION,new CDAssociationNameAndTargetNamePredicate("ec", "E")).orElse(null);
    assertNotNull(ecAssocRight2Left);
    assertEquals("ec", ecAssocRight2Left.getName());
    assertEquals("ec", ecAssocRight2Left.getAssocName().orElse(""));
    assertTrue(ecAssocRight2Left.isBidirectional());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Callable", ecAssocRight2Left.getSourceType().getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.E", ecAssocRight2Left.getTargetType().getFullName());
    assertEquals(0, ecAssocRight2Left.getSourceCardinality().getMin());
    assertEquals(1, ecAssocRight2Left.getSourceCardinality().getMax());
    assertFalse(ecAssocRight2Left.getSourceCardinality().isMultiple());
    assertEquals(1, ecAssocRight2Left.getTargetCardinality().getMin());
    assertEquals(Cardinality.STAR, ecAssocRight2Left.getTargetCardinality().getMax());
    assertTrue(ecAssocRight2Left.getTargetCardinality().isMultiple());


    // Modifier Test //

    // Class is public
    assertTrue(cdScope.resolveCDType("Person", PUBLIC).isPresent());
    assertTrue(cdScope.resolveCDType("Person", PROTECTED)
        .isPresent());
    assertTrue(cdScope.resolveCDType("Person", PRIVATE).isPresent());

    // Prof is private
    assertFalse(cdScope.resolveCDType("Prof", PUBLIC).isPresent());
    assertFalse(cdScope.resolveCDType("Prof", PROTECTED).isPresent());
    assertTrue(cdScope.resolveCDType("Prof", PRIVATE).isPresent());

    // Printable is protected
    assertFalse(cdScope.resolveCDType("Printable", PUBLIC)
        .isPresent());
    assertTrue(cdScope.resolveCDType("Printable", PROTECTED).isPresent());
    assertTrue(cdScope.resolveCDType("Printable", PRIVATE).isPresent());


    // Resolve fields from super class //
    // public fields can be resolved
    /* TODOD Muss noch implementiert werden
    assertTrue(profType.getSpannedScope().resolveCDField("name").isPresent());

    // protected fields can be resolved
    assertTrue(profType.getSpannedScope().resolveCDField("age").isPresent());

    // private fields CANNOT be resolved...
    assertFalse(profType.getSpannedScope().resolveCDField("secondName").isPresent());
    // ... even if resolving with the private access modifier.
    assertFalse(profType.getSpannedScope().resolveCDField("secondName", PRIVATE).isPresent());


    // Resolve methods from super types //
    // public methods can be resolved
    assertTrue(profType.getSpannedScope().resolveCDMethOrConstr("setName").isPresent());
    assertSame(setNameMethod, profType.getSpannedScope().resolveCDMethOrConstr("setName").get());

    // protected methods can be resolved
    assertTrue(profType.getSpannedScope().resolveCDMethOrConstr("print").isPresent());
    assertSame(printMethod, profType.getSpannedScope().resolveCDMethOrConstr("print").get());

    // private methods CANNOT be resolved...
    assertFalse(profType.getSpannedScope().resolveCDMethOrConstr("getAge").isPresent());
    // ... even if resolving with the private access modifier.
    assertFalse(profType.getSpannedScope().resolveCDMethOrConstr("getAge", PRIVATE).isPresent());
*/


    // resolve method by signature
    assertTrue(personType.getSpannedScope().resolveCDMethOrConstr("setName", BasicAccessModifier.ALL_INCLUSION,
        new CDMethodSignaturePredicate("setName", "String", "String")).isPresent());
    assertSame(setNameMethod, personType.getSpannedScope().resolveCDMethOrConstr("setName", BasicAccessModifier.ALL_INCLUSION,
        new CDMethodSignaturePredicate("setName", "String", "String")).get());

    assertFalse(personType.getSpannedScope().resolveCDMethOrConstr("setName", BasicAccessModifier.ALL_INCLUSION,
        new CDMethodSignaturePredicate("setName", "String")).isPresent());

    assertFalse(personType.getSpannedScope().resolveCDMethOrConstr("setName", BasicAccessModifier.ALL_INCLUSION,
        new CDMethodSignaturePredicate("setName", "String", "int")).isPresent());

    assertFalse(personType.getSpannedScope().resolveCDMethOrConstr("setName", BasicAccessModifier.ALL_INCLUSION,
        new CDMethodSignaturePredicate("setName", "String", "String", "String")).isPresent());

    assertFalse(personType.getSpannedScope().resolveCDMethOrConstr("setName", BasicAccessModifier.ALL_INCLUSION,
        new CDMethodSignaturePredicate("getAge", "String", "String")).isPresent());



    // getAllVisibleFieldsOfSuperTypes()
    final Collection<CDFieldSymbol> superFieldsOfProf = profType.getAllVisibleFieldsOfSuperTypes();

    assertEquals(3, superFieldsOfProf.size());
    // fields of direct super class //
    assertTrue(superFieldsOfProf.contains(nameField));
    assertTrue(superFieldsOfProf.contains(ageField));
    // secondName is private
    assertFalse(superFieldsOfProf.contains(secondNameField));
    // fields of super super class //
    assertTrue(superFieldsOfProf.contains(extinctField));

    // hasSuperType()
    assertTrue(profType.hasSuperType("Prof"));
    assertTrue(profType.hasSuperType("Person"));
    assertTrue(profType.hasSuperType("Creature"));

    // hasSuperTypeByFullName()
    assertTrue(profType.hasSuperTypeByFullName("de.monticore.umlcd4a.symboltable.CD1.Prof"));
    assertTrue(profType.hasSuperTypeByFullName("de.monticore.umlcd4a.symboltable.CD1.Person"));
    assertTrue(profType.hasSuperTypeByFullName("de.monticore.umlcd4a.symboltable.CD1.Creature"));
  }

  @Test
  public void testTypeSymbolReferencesForGenerics() {
    final CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    final CDDefinitionSymbol cdSymbol = globalScope.resolveCDDefinition(
        "de.monticore.umlcd4a.symboltable.Generics").orElse(null);
    assertNotNull(cdSymbol);

    final CDTypeSymbol clazz = globalScope.resolveCDType(
        "de.monticore.umlcd4a.symboltable.Generics.A").orElse(null);
    assertNotNull(clazz);
    assertEquals("de.monticore.umlcd4a.symboltable.Generics.A", clazz.getFullName());

    //Type arguments
    CDFieldSymbol attribute = clazz.getSpannedScope().resolveCDField("g1").orElse(null);
    assertNotNull(attribute);
    CDTypeSymbolReference attributeType =  attribute.getType();
    assertEquals("List", attributeType.getName());
    Assert.assertEquals("List<String>", attributeType.getStringRepresentation());

    assertEquals(1, attributeType.getActualTypeArguments().size());
    CDTypeSymbolReference typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("String", typeArgument.getName());

    
    attribute = clazz.getSpannedScope().resolveCDField("g2").orElse(null);
    assertNotNull(attribute);
    attributeType = attribute.getType();
    assertEquals("List", attributeType.getName());
    Assert.assertEquals("List<B>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("B", typeArgument.getName());

    attribute = clazz.getSpannedScope().resolveCDField("g3").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    Assert.assertEquals("List<C>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("C", typeArgument.getName());

    /*
    todo: delete tests or delete fullgenerictypes in examples
    attribute = clazz.getField("g4").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<?>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("?", typeArgument.getType().getName());
    assertFalse(typeArgument.isLowerBound());
    assertFalse(typeArgument.isUpperBound());

    attribute = clazz.getField("g5").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<? extends B>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("B", typeArgument.getType().getName());
    assertFalse(typeArgument.isLowerBound());
    assertTrue(typeArgument.isUpperBound());

    attribute = clazz.getField("g6").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<? super B>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("B", typeArgument.getType().getName());
    assertTrue(typeArgument.isLowerBound());
    assertFalse(typeArgument.isUpperBound());

    attribute = clazz.getField("g7").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<? extends C>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("C", typeArgument.getType().getName());
    assertFalse(typeArgument.isLowerBound());
    assertTrue(typeArgument.isUpperBound());

    attribute = clazz.getField("g8").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<? super C>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("C", typeArgument.getType().getName());
    assertTrue(typeArgument.isLowerBound());
    assertFalse(typeArgument.isUpperBound());

    attribute = clazz.getField("g9").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<List<String>>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    CDTypeSymbolReference innerTypeArgument = (CDTypeSymbolReference) typeArgument.getType();
    assertEquals("List", innerTypeArgument.getName());
    assertEquals("List<String>", innerTypeArgument.getStringRepresentation());
    assertEquals(1, innerTypeArgument.getActualTypeArguments().size());
    assertEquals("String", innerTypeArgument.getActualTypeArguments().get(0).getType().getName());

    attribute = clazz.getField("g10").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<List<B>>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    innerTypeArgument = (CDTypeSymbolReference) typeArgument.getType();
    assertEquals("List", innerTypeArgument.getName());
    assertEquals("List<B>", innerTypeArgument.getStringRepresentation());
    assertEquals(1, innerTypeArgument.getActualTypeArguments().size());
    assertEquals("B", innerTypeArgument.getActualTypeArguments().get(0).getType().getName());

    attribute = clazz.getField("g11").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<List<C>>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    innerTypeArgument = (CDTypeSymbolReference) typeArgument.getType();
    assertEquals("List", innerTypeArgument.getName());
    assertEquals("List<C>", innerTypeArgument.getStringRepresentation());
    assertEquals(1, innerTypeArgument.getActualTypeArguments().size());
    assertEquals("C", innerTypeArgument.getActualTypeArguments().get(0).getType().getName());

    attribute = clazz.getField("g12").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<List<?>>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    innerTypeArgument = (CDTypeSymbolReference) typeArgument.getType();
    assertEquals("List", innerTypeArgument.getName());
    assertEquals("List<?>", innerTypeArgument.getStringRepresentation());
    assertEquals(1, innerTypeArgument.getActualTypeArguments().size());
    assertEquals("?", innerTypeArgument.getActualTypeArguments().get(0).getType().getName());
  */
  }

}
