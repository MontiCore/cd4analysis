/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.trafo;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.SourceVersion;

/** Adapts association roles to Java identifiers before fields and methods are generated. */
public class JavaAssociationRoleNameTrafo implements CDAssociationVisitor2, CDBasisVisitor2 {
  
  protected final List<ASTCDRole> roles = new ArrayList<>();
  protected final Map<OOTypeSymbol, Set<OOTypeSymbol>> hierarchies = new LinkedHashMap<>();
  
  @Override
  public void visit(ASTCDClass node) {
    Set<OOTypeSymbol> ancestors = new LinkedHashSet<>();
    collectAncestors(node.getSymbol(), ancestors);
    hierarchies.put(node.getSymbol(), ancestors);
  }
  
  protected void collectAncestors(OOTypeSymbol type, Set<OOTypeSymbol> ancestors) {
    if (ancestors.add(type)) {
      type.getSuperTypesList().forEach(superType -> {
        if (superType.getTypeInfo() instanceof OOTypeSymbol) {
          collectAncestors((OOTypeSymbol) superType.getTypeInfo(), ancestors);
        }
      });
    }
  }
  
  @Override
  public void visit(ASTCDRole node) {
    roles.add(node);
  }
  
  public void transform(Collection<ASTCDCompilationUnit> asts) {
    roles.clear();
    hierarchies.clear();
    var traverser = CD4CodeMill.inheritanceTraverser();
    traverser.add4CDBasis(this);
    traverser.add4CDAssociation(this);
    asts.forEach(ast -> ast.accept(traverser));
    for (ASTCDRole role : roles) {
      if (!SourceVersion.isKeyword(role.getName())) {
        continue;
      }
      var symbol = role.getSymbol();
      var scope = symbol.getEnclosingScope();
      Set<String> occupied = new LinkedHashSet<>();
      scope.getLocalFieldSymbols().forEach(field -> occupied.add(field.getName()));
      scope.getLocalCDRoleSymbols().forEach(other -> occupied.add(other.getName()));
      // Reserve inherited members and members of subclasses, independent of traversal order.
      hierarchies.forEach((type, ancestors) -> {
        if (ancestors.stream().anyMatch(ancestor -> ancestor.getSpannedScope() == scope)) {
          ancestors.forEach(ancestor -> collectNames(ancestor, occupied));
          collectNames(type, occupied);
        }
      });
      String name = role.getName() + "_";
      while (occupied.contains(name)) {
        name += "_";
      }
      // Re-index the scope as well as updating the AST and symbol used by navigable setters.
      scope.remove(symbol);
      role.setName(name);
      symbol.setName(name);
      symbol.setFullName(null);
      scope.add(symbol);
    }
  }
  
  protected void collectNames(OOTypeSymbol type, Set<String> names) {
    type.getFieldList().forEach(field -> names.add(field.getName()));
    if (type.getSpannedScope() instanceof ICDAssociationScope) {
      ((ICDAssociationScope) type.getSpannedScope()).getLocalCDRoleSymbols().forEach(role -> names
          .add(role.getName()));
    }
  }
  
}
