<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("package")}
/* (c) https://github.com/MontiCore/monticore */
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${defineHookPoint("EnumContent:addComment")}

${tc.include("cd2java.Package", package)}
${cdPrinter.printImportList(cd4c.getImportList(ast))}

<#-- Imports hook -->
${defineHookPoint("EnumContent:Imports")}

${tc.include("cd2java.Annotations")}

<#-- Annotations hook -->
${defineHookPoint("EnumContent:Annotations")}

${cdPrinter.printSimpleModifier(ast.getModifier())}  enum ${ast.getName()}
<#if ast.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(ast.getCDInterfaceUsage().getInterfaceList())} </#if>{

<#list ast.getCDEnumConstantList() as enumConst>
    ${enumConst.getName()}
    <#if enumConst.isPresentArguments()>
        <#t>(
        <#list enumConst.getArguments().getExpressionList() as expr>
            <#t>${cdPrinter.printExpression(expr)}<#if !expr?is_last>,</#if>
        </#list>
        <#t>)
    </#if>
    <#if !enumConst?is_last><#t>,</#if>
</#list>
;

<#-- Elements HOOK -->
${defineHookPoint("EnumContent:Elements")}

<#list ast.getCDAttributeList() as attribute>
  ${tc.include("cd2java.Attribute", attribute)}
</#list>

<#list ast.getCDConstructorList() as constructor>
  ${tc.include("cd2java.Constructor", constructor)}
</#list>

<#list ast.getCDMethodList() as method>
  ${tc.include("cd2java.Method", method)}
</#list>
}
