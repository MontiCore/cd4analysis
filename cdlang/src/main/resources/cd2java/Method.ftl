<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("cd2java.JavaDoc")}
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("MethodContent:Annotations")}
<#assign isAbstract = ast.getModifier().isAbstract()>
${cdPrinter.printSimpleModifier(ast.getModifier())} ${cdPrinter.printType(ast.getMCReturnType())} ${ast.getName()} (${cdPrinter.printCDParametersDecl(ast.getCDParameterList())})
<#if ast.isPresentCDThrowsDeclaration()> ${cdPrinter.printThrowsDecl(ast.getCDThrowsDeclaration())}</#if>
<#if isAbstract>;<#else> {
    ${tc.include("cd2java.EmptyBody")}
}
</#if>
