<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("cd2java.JavaDoc")}
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("AttributeContent:Annotations")}
${cdPrinter.printSimpleModifier(ast.getModifier())} ${cdPrinter.printType(ast.getMCType())} ${ast.getName()}
<#if ast.isPresentInitial()>
  <#t> = ${cdPrinter.printExpression(ast.getInitial())}
<#else>
  ${tc.include("cd2java.Value")}
</#if>
<#lt>;
