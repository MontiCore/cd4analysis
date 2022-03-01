/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.trafo;

import de.monticore.ast.ASTNode;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdassociation._symboltable.CDAssociationScopesGenitorDelegator;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdassociation._visitor.CDAssociationHandler;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CDAssociationCreateFieldsFromAllRoles
  implements CDAssociationVisitor2, CDAssociationHandler {
  protected CDAssociationTraverser traverser;
  protected List<ASTMCImportStatement> imports;
  protected ASTMCQualifiedName packageDeclaration;
  protected Map<FieldSymbol, SourcePosition> createdFields = new HashMap<>();

  @Override
  public CDAssociationTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CDAssociationTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void visit(ASTCDRole node) {
    final Stream<FieldSymbol> stream = node.getSymbol().getEnclosingScope().getLocalFieldSymbols().stream();
    String pos = stream
      .filter(f -> f.getName().equals(node.getName()))
      .map(f -> {
        if (f.getSourcePosition().equals(SourcePosition.getDefaultSourcePosition())) {
          // if it is just a Symbol, then it might be created in the createdFields list
          if (createdFields.containsKey(f)) {
            return createdFields.get(f).toString();
          }
          return "unknown source";
        }
        else {
          return f.getSourcePosition().toString();
        }
      })
      .collect(Collectors.joining(", "));
    if (!pos.isEmpty()) {
      final String msg = String.format("0xCD0B7: a FieldSymbol with the name '%s' already exists in '%s' (defined in %s)",
        node.getName(), node.getSymbol().getAssoc().getOtherRole(node.getSymbol()).getType().getTypeInfo().getFullName(), pos);
      Log.error(msg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }

    final CDRoleSymbol symbol = node.getSymbol();
    final ICDAssociationScope enclosingScope = symbol.getEnclosingScope();

    ASTMCType fieldType = calculateType(symbol);
    // Create the ASTNode
    ASTModifier modifier = CDAssociationMill.modifierBuilder()
      .setReadonly(symbol.isIsReadOnly())
      .setPrivate(symbol.isIsPrivate())
      .setProtected(symbol.isIsProtected())
      .setPublic(symbol.isIsPublic())
      .setStatic(symbol.isIsStatic())
      .setFinal(symbol.isIsFinal())
      .build();
    ASTCDAttribute fieldAst = CDAssociationMill.cDAttributeBuilder()
      .setName(node.getName())
      .setMCType(fieldType)
      .setModifier(modifier)
      .build();

    // Build scopes
    CDAssociationScopesGenitorDelegator scopeGenitor = CDAssociationMill.scopesGenitorDelegator();
    scopeGenitor.putOnStack(enclosingScope);
    fieldAst.accept(scopeGenitor.getTraverser());
    // Initialize Symbol
    FieldSymbol fieldSymbol = fieldAst.getSymbol();
    fieldSymbol.setIsReadOnly(symbol.isIsReadOnly());
    fieldSymbol.setIsPrivate(symbol.isIsPrivate());
    fieldSymbol.setIsProtected(symbol.isIsProtected());
    fieldSymbol.setIsPublic(symbol.isIsPublic());
    fieldSymbol.setIsStatic(symbol.isIsStatic());
    fieldSymbol.setIsFinal(symbol.isIsFinal());
    fieldSymbol.setType(calculateSymType(symbol));

    createdFields.put(fieldSymbol, node.get_SourcePositionStart());

    // add field to ast
    if (enclosingScope.isPresentSpanningSymbol() &&enclosingScope.getSpanningSymbol().isPresentAstNode()) {
      ASTNode spannedType = enclosingScope.getSpanningSymbol().getAstNode();
      if (spannedType instanceof ASTCDClass) {
        ((ASTCDClass) spannedType).addCDMember(fieldAst);
      }
    }
  }

  public ASTMCType calculateType(CDRoleSymbol symbol) {
    final ASTMCType type;
    if (!symbol.isPresentCardinality() || symbol.getCardinality().isOne()) {
      type = MCTypeFacade.getInstance().createQualifiedType(symbol.getType().printFullName());
    }
    else {
      final ASTCDCardinality cardinality = symbol.getCardinality();
      if (cardinality.isOpt()) {
        type = MCTypeFacade.getInstance().createOptionalTypeOf(symbol.getType().printFullName());
      }
      else {
        final String container;
        if (symbol.isIsOrdered()) {
          type = MCTypeFacade.getInstance().createListTypeOf(symbol.getType().printFullName());
        }
        else {
          type = MCTypeFacade.getInstance().createCollectionTypeOf(symbol.getType().printFullName());
        }
      }
    }
    return type;
  }

  public SymTypeExpression calculateSymType(CDRoleSymbol symbol) {
    final SymTypeExpression type;
    if (!symbol.isPresentCardinality() || symbol.getCardinality().isOne()) {
      type = symbol.getType();
    }
    else {
      final ASTCDCardinality cardinality = symbol.getCardinality();
      if (cardinality.isOpt()) {
        type = SymTypeExpressionFactory.createGenerics("java.util.Optional", symbol.getEnclosingScope(), symbol.getType());
      }
      else {
        final String container;
        if (symbol.isIsOrdered()) {
          container = "java.util.List";
        }
        else {
          container = "java.util.Set";
        }
        type = SymTypeExpressionFactory.createGenerics(container, symbol.getEnclosingScope(), symbol.getType());
      }
    }

    return type;
  }

  public void transform(ASTCDCompilationUnit compilationUnit)
    throws RuntimeException {
    if (!compilationUnit.getCDDefinition().isPresentSymbol()) {
      final String msg = "0xCD0B8: can't start the transformation, the symbol table is missing";
      Log.error(msg);
      throw new RuntimeException(msg);
    }

    init(compilationUnit);

    compilationUnit.accept(getTraverser());
  }

  public void init(ASTCDCompilationUnit compilationUnit) {
    imports = compilationUnit.getMCImportStatementList();
    packageDeclaration = MCQualifiedNameFacade.createQualifiedName("");
  }
}
