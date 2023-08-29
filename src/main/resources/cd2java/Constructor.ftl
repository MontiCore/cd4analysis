<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("ConstructorContent:Annotations")}
${cdPrinter.printSimpleModifier(ast.getModifier())} ${ast.getName()}(${cdPrinter.printCDParametersDecl(ast.getCDParameterList())})
<#if ast.isPresentCDThrowsDeclaration()> ${cdPrinter.printThrowsDecl(ast.getCDThrowsDeclaration())}</#if> {
  ${tc.include("cd2java.EmptyBody")}
}
