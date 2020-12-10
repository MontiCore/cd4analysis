/* generated from model CDBasis */
/* generated by template core.Class*/

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable.phased;

/* generated by template core.Imports*/

import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._symboltable.*;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolBuilder;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Optional;

 public   class CDBasisScopeSkeletonCreator implements de.monticore.cdbasis._visitor.CDBasisVisitor {

   /* generated by template core.Attribute*/
protected  Deque<ICDBasisScope> scopeStack = new ArrayDeque<>();

   /* generated by template core.Attribute*/
private  de.monticore.cdbasis._visitor.CDBasisVisitor realThis = this;

   /* generated by template core.Attribute*/
protected  ICDBasisScope firstCreatedScope ;


   /* generated by template core.Constructor*/
public CDBasisScopeSkeletonCreator(ICDBasisScope enclosingScope)  {
   putOnStack(Log.errorIfNull(enclosingScope));
}

   /* generated by template core.Constructor*/
public CDBasisScopeSkeletonCreator(Deque<? extends ICDBasisScope> scopeStack)  {
   this.scopeStack = Log.errorIfNull((Deque<ICDBasisScope>)scopeStack);
}


   /* generated by template core.Method*/
public  de.monticore.cdbasis._visitor.CDBasisVisitor getRealThis ()  {
     /* generated by template methods.Get*/

return this.realThis;

}

   /* generated by template core.Method*/
public  void setRealThis (de.monticore.cdbasis._visitor.CDBasisVisitor realThis)  {
     /* generated by template methods.Set*/

this.realThis = realThis;

}

   /* generated by template core.Method*/
public  ICDBasisScope getFirstCreatedScope ()  {
     /* generated by template methods.Get*/

return this.firstCreatedScope;

}

   /* generated by template core.Method*/
public ICDBasisArtifactScope createFromAST (de.monticore.cdbasis._ast.ASTCDCompilationUnit rootNode)  {
     /* generated by template _symboltable.symboltablecreator.CreateFromAST*/

 Log.errorIfNull(rootNode, "0xA7004x1802299331 Error by creating of the CDBasisSymbolTableCreator symbol table: top ast node is null");
 ICDBasisArtifactScope artifactScope = de.monticore.cdbasis.CDBasisMill.cDBasisArtifactScopeBuilder()
   .setPackageName("")
   .setImportsList(new ArrayList<>())
   .build();
 putOnStack(artifactScope);
 rootNode.accept(getRealThis());
 return artifactScope;
}

   /* generated by template core.Method*/
public  void putOnStack (ICDBasisScope scope)  {
     /* generated by template _symboltable.symboltablecreator.PutOnStack*/
 Log.errorIfNull(scope);

if (scope.getEnclosingScope() == null && getCurrentScope().isPresent()) {
   scope.setEnclosingScope(getCurrentScope().get());
   getCurrentScope().get().addSubScope(scope);
} else if (scope.getEnclosingScope() != null && getCurrentScope().isPresent()) {
   if (scope.getEnclosingScope() != getCurrentScope().get()) {
     Log.warn("0xA1043 The enclosing scope is not the same as the current scope on the stack.");
   }
 }

 if (firstCreatedScope == null) {
   firstCreatedScope = scope;
 }

 scopeStack.addLast(scope);
}

   /* generated by template core.Method*/
public  final  Optional<ICDBasisScope> getCurrentScope ()  {
     return Optional.ofNullable(scopeStack.peekLast());
}

   /* generated by template core.Method*/
public  final  Optional<ICDBasisScope> removeCurrentScope ()  {
     return Optional.ofNullable(scopeStack.pollLast());
}

   /* generated by template core.Method*/
protected  void setCDCompilationUnitScopeStack (Deque<ICDBasisScope> scopeStack)  {
     this.scopeStack = scopeStack;
}

   /* generated by template core.Method*/
public  ICDBasisScope createScope (boolean shadowing)  {
     /* generated by template _symboltable.symboltablecreator.CreateScope*/

 ICDBasisScope scope = de.monticore.cdbasis.CDBasisMill.cDBasisScopeBuilder().build();
 scope.setShadowing(shadowing);
 return scope;
}

   /* generated by template core.Method*/
public  void visit (de.monticore.cdbasis._ast.ASTCDPackage node)  {
     /* generated by template _symboltable.symboltablecreator.Visit4STC*/

 CDPackageSymbol symbol = create_CDPackage(node).build();
 addToScopeAndLinkWithNode(symbol, node);
}

   /* generated by template core.Method*/
public  void endVisit (de.monticore.cdbasis._ast.ASTCDPackage node)  {
     /* generated by template _symboltable.symboltablecreator.EndVisitSymbol*/

removeCurrentScope();

}

   /* generated by template core.Method*/
protected CDPackageSymbolBuilder create_CDPackage (de.monticore.cdbasis._ast.ASTCDPackage ast)  {
  return CDBasisMill.cDPackageSymbolBuilder().setName(ast.getName());
}

   /* generated by template core.Method*/
public  void addToScopeAndLinkWithNode (CDPackageSymbol symbol,de.monticore.cdbasis._ast.ASTCDPackage ast)  {
     /* generated by template _symboltable.symboltablecreator.AddToScopeAndLinkWithNode*/

 addToScope(symbol);
   ICDBasisScope scope = createScope(false);
 putOnStack(scope);
 symbol.setSpannedScope(scope);
 setLinkBetweenSymbolAndNode(symbol, ast);
}

   /* generated by template core.Method*/
public  void setLinkBetweenSymbolAndNode (CDPackageSymbol symbol,de.monticore.cdbasis._ast.ASTCDPackage ast)  {
     /* generated by template _symboltable.symboltablecreator.SetLinkBetweenSymbolAndNode*/

 // symbol -> ast
 symbol.setAstNode(ast);

 // ast -> symbol
 ast.setSymbol(symbol);
 ast.setEnclosingScope(symbol.getEnclosingScope());

 // ast -> spannedScope
 ast.setSpannedScope(symbol.getSpannedScope());

}

   /* generated by template core.Method*/
public  void setLinkBetweenSpannedScopeAndNode (ICDBasisScope scope,de.monticore.cdbasis._ast.ASTCDPackage ast)  {
     /* generated by template _symboltable.symboltablecreator.SetLinkBetweenSpannedScopeAndNode*/
 // scope -> ast
 scope.setAstNode(ast);

 // ast -> scope
 ast.setSpannedScope(scope);
}

   /* generated by template core.Method*/
public  void visit (de.monticore.cdbasis._ast.ASTCDAttribute node)  {
     /* generated by template _symboltable.symboltablecreator.Visit4STC*/

 de.monticore.symbols.oosymbols._symboltable.FieldSymbol symbol = create_CDAttribute(node).build();
 addToScopeAndLinkWithNode(symbol, node);
}

   /* generated by template core.Method*/
public  void endVisit (de.monticore.cdbasis._ast.ASTCDAttribute node)  {
     /* generated by template _symboltable.symboltablecreator.EndVisitSymbol*/


}

   /* generated by template core.Method*/
protected  de.monticore.symbols.oosymbols._symboltable.FieldSymbolBuilder create_CDAttribute (de.monticore.cdbasis._ast.ASTCDAttribute ast)  {
  return CDBasisMill.fieldSymbolBuilder().setName(ast.getName());
}

   /* generated by template core.Method*/
public  void addToScopeAndLinkWithNode (de.monticore.symbols.oosymbols._symboltable.FieldSymbol symbol,de.monticore.cdbasis._ast.ASTCDAttribute ast)  {
     /* generated by template _symboltable.symboltablecreator.AddToScopeAndLinkWithNode*/

 addToScope(symbol);
 setLinkBetweenSymbolAndNode(symbol, ast);
}

   /* generated by template core.Method*/
public  void setLinkBetweenSymbolAndNode (de.monticore.symbols.oosymbols._symboltable.FieldSymbol symbol,de.monticore.cdbasis._ast.ASTCDAttribute ast)  {
     /* generated by template _symboltable.symboltablecreator.SetLinkBetweenSymbolAndNode*/

 // symbol -> ast
 symbol.setAstNode(ast);

 // ast -> symbol
 ast.setSymbol(symbol);
 ast.setEnclosingScope(symbol.getEnclosingScope());


}

   /* generated by template core.Method*/
public  void visit (de.monticore.cdbasis._ast.ASTCDClass node)  {
     /* generated by template _symboltable.symboltablecreator.Visit4STC*/

 CDTypeSymbol symbol = create_CDClass(node).build();
 addToScopeAndLinkWithNode(symbol, node);
}

   /* generated by template core.Method*/
public  void endVisit (de.monticore.cdbasis._ast.ASTCDClass node)  {
     /* generated by template _symboltable.symboltablecreator.EndVisitSymbol*/

removeCurrentScope();

}

   /* generated by template core.Method*/
protected  CDTypeSymbolBuilder create_CDClass (de.monticore.cdbasis._ast.ASTCDClass ast)  {
  return CDBasisMill.cDTypeSymbolBuilder().setName(ast.getName());
}


   /* generated by template core.Method*/
public  void addToScopeAndLinkWithNode (CDTypeSymbol symbol,de.monticore.cdbasis._ast.ASTCDClass ast)  {
     /* generated by template _symboltable.symboltablecreator.AddToScopeAndLinkWithNode*/

 addToScope(symbol);
   ICDBasisScope scope = createScope(false);
 putOnStack(scope);
 symbol.setSpannedScope(scope);
 setLinkBetweenSymbolAndNode(symbol, ast);
}

   /* generated by template core.Method*/
public  void setLinkBetweenSymbolAndNode (CDTypeSymbol symbol,de.monticore.cdbasis._ast.ASTCDClass ast)  {
     /* generated by template _symboltable.symboltablecreator.SetLinkBetweenSymbolAndNode*/

 // symbol -> ast
 symbol.setAstNode(ast);

 // ast -> symbol
 ast.setSymbol(symbol);
 ast.setEnclosingScope(symbol.getEnclosingScope());

 // ast -> spannedScope
 ast.setSpannedScope(symbol.getSpannedScope());

}

   /* generated by template core.Method*/
public  void setLinkBetweenSpannedScopeAndNode (ICDBasisScope scope,de.monticore.cdbasis._ast.ASTCDClass ast)  {
     /* generated by template _symboltable.symboltablecreator.SetLinkBetweenSpannedScopeAndNode*/
 // scope -> ast
 scope.setAstNode(ast);

 // ast -> scope
 ast.setSpannedScope(scope);
}

   /* generated by template core.Method*/
public  void visit (de.monticore.cdbasis._ast.ASTCDCompilationUnit node)  {
     /* generated by template _symboltable.symboltablecreator.VisitNoSymbol*/
 if (getCurrentScope().isPresent()) {
   node.setEnclosingScope(getCurrentScope().get());
 }
 else {
   Log.error("Could not set enclosing scope of ASTNode \"" + node
             + "\", because no scope is set yet!");
 }
}

   /* generated by template core.Method*/
public  void visit (de.monticore.cdbasis._ast.ASTCDPackageStatement node)  {
     /* generated by template _symboltable.symboltablecreator.VisitNoSymbol*/
 if (getCurrentScope().isPresent()) {
   node.setEnclosingScope(getCurrentScope().get());
 }
 else {
   Log.error("Could not set enclosing scope of ASTNode \"" + node
             + "\", because no scope is set yet!");
 }
}

   /* generated by template core.Method*/
public  void visit (de.monticore.cdbasis._ast.ASTCDTargetImportStatement node)  {
     /* generated by template _symboltable.symboltablecreator.VisitNoSymbol*/
 if (getCurrentScope().isPresent()) {
   node.setEnclosingScope(getCurrentScope().get());
 }
 else {
   Log.error("Could not set enclosing scope of ASTNode \"" + node
             + "\", because no scope is set yet!");
 }
}

   /* generated by template core.Method*/
public  void visit (de.monticore.cdbasis._ast.ASTCDDefinition node)  {
     /* generated by template _symboltable.symboltablecreator.VisitNoSymbol*/
 if (getCurrentScope().isPresent()) {
   node.setEnclosingScope(getCurrentScope().get());
 }
 else {
   Log.error("Could not set enclosing scope of ASTNode \"" + node
             + "\", because no scope is set yet!");
 }
}

   /* generated by template core.Method*/
public  void visit (de.monticore.cdbasis._ast.ASTCDInterfaceUsage node)  {
     /* generated by template _symboltable.symboltablecreator.VisitNoSymbol*/
 if (getCurrentScope().isPresent()) {
   node.setEnclosingScope(getCurrentScope().get());
 }
 else {
   Log.error("Could not set enclosing scope of ASTNode \"" + node
             + "\", because no scope is set yet!");
 }
}

   /* generated by template core.Method*/
public  void visit (de.monticore.cdbasis._ast.ASTCDExtendUsage node)  {
     /* generated by template _symboltable.symboltablecreator.VisitNoSymbol*/
 if (getCurrentScope().isPresent()) {
   node.setEnclosingScope(getCurrentScope().get());
 }
 else {
   Log.error("Could not set enclosing scope of ASTNode \"" + node
             + "\", because no scope is set yet!");
 }
}

   /* generated by template core.Method*/
public  void addToScope (CDPackageSymbol symbol)  {
     /* generated by template _symboltable.symboltablecreator.AddToScope*/

if (getCurrentScope().isPresent()) {
   getCurrentScope().get().add(symbol);
 } else {
   Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
 }

}

   /* generated by template core.Method*/
public  void addToScope (CDTypeSymbol symbol)  {
     /* generated by template _symboltable.symboltablecreator.AddToScope*/

 addToScope((de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol) symbol);
if (getCurrentScope().isPresent()) {
   getCurrentScope().get().add(symbol);
 } else {
   Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
 }

}

   /* generated by template core.Method*/
public  void addToScope (de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol symbol)  {
     /* generated by template _symboltable.symboltablecreator.AddToScope*/

 addToScope((de.monticore.symbols.basicsymbols._symboltable.TypeSymbol) symbol);
if (getCurrentScope().isPresent()) {
   getCurrentScope().get().add(symbol);
 } else {
   Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
 }

}

   /* generated by template core.Method*/
public  void addToScope (de.monticore.symbols.oosymbols._symboltable.FieldSymbol symbol)  {
     /* generated by template _symboltable.symboltablecreator.AddToScope*/

 addToScope((de.monticore.symbols.basicsymbols._symboltable.VariableSymbol) symbol);
if (getCurrentScope().isPresent()) {
   getCurrentScope().get().add(symbol);
 } else {
   Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
 }

}

   /* generated by template core.Method*/
public  void addToScope (de.monticore.symbols.oosymbols._symboltable.MethodSymbol symbol)  {
     /* generated by template _symboltable.symboltablecreator.AddToScope*/

 addToScope((de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol) symbol);
if (getCurrentScope().isPresent()) {
   getCurrentScope().get().add(symbol);
 } else {
   Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
 }

}

   /* generated by template core.Method*/
public  void addToScope (de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol symbol)  {
     /* generated by template _symboltable.symboltablecreator.AddToScope*/

if (getCurrentScope().isPresent()) {
   getCurrentScope().get().add(symbol);
 } else {
   Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
 }

}

   /* generated by template core.Method*/
public  void addToScope (de.monticore.symbols.basicsymbols._symboltable.TypeSymbol symbol)  {
     /* generated by template _symboltable.symboltablecreator.AddToScope*/

if (getCurrentScope().isPresent()) {
   getCurrentScope().get().add(symbol);
 } else {
   Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
 }

}

   /* generated by template core.Method*/
public  void addToScope (de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol symbol)  {
     /* generated by template _symboltable.symboltablecreator.AddToScope*/

 addToScope((de.monticore.symbols.basicsymbols._symboltable.TypeSymbol) symbol);
if (getCurrentScope().isPresent()) {
   getCurrentScope().get().add(symbol);
 } else {
   Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
 }

}

   /* generated by template core.Method*/
public  void addToScope (de.monticore.symbols.basicsymbols._symboltable.VariableSymbol symbol)  {
     /* generated by template _symboltable.symboltablecreator.AddToScope*/

if (getCurrentScope().isPresent()) {
   getCurrentScope().get().add(symbol);
 } else {
   Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
 }

}

   /* generated by template core.Method*/
public  void addToScope (de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol symbol)  {
     /* generated by template _symboltable.symboltablecreator.AddToScope*/

if (getCurrentScope().isPresent()) {
   getCurrentScope().get().add(symbol);
 } else {
   Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
 }

}

}
