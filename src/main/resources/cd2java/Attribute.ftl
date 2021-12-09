<#-- (c) https://github.com/MontiCore/monticore -->
${cdPrinter.printSimpleModifier(ast.getModifier())} ${ast.printType()} ${ast.getName()}
<#if ast.isPresentInitial()>
  <#t> ${cdPrinter.printExpression(ast.getInitial())}
</#if>
<#lt>;
