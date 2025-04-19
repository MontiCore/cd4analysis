<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzName", "attributeList","hasSetterList")}
<#assign MCTypeFacade = glex.getGlobalVar("mcTypeFacade")>
<#assign MCCollectionSymTypeRelations = glex.getGlobalVar("mcCollectionSymTypeRelations")>
<#list 0..attributeList?size-1 as i>
<#if MCTypeFacade.getInstance().isBooleanType(attributeList[i].getMCType())>
<#------------------------------------>
  <#else>
    <#if MCCollectionSymTypeRelations.isSet(attributeList[i].getSymbol().getType()) || MCCollectionSymTypeRelations.isList(attributeList[i].getSymbol().getType())>
<#------------------------------------>
    <#else>
      <#if MCCollectionSymTypeRelations.isOptional(attributeList[i].getSymbol().getType())>
<#------------------------------------>
      <#else>

<#------------------------------------>
    </#if>
  </#if>
</#if>
</#list>
