<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("AttributeContent:Annotations")}
${cdPrinter.printSimpleModifier(ast.getModifier())} ${ast.printType()} ${ast.getName()}
<#if ast.isPresentInitial()>
  <#t> = ${cdPrinter.printExpression(ast.getInitial())}
<#else>
  ${tc.include("cd2java.Value")}
</#if>
<#lt>;
