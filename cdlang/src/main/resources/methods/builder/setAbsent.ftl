<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}

<#assign MCCollectionSymTypeRelations = glex.getGlobalVar("mcCollectionSymTypeRelations")>

<#if MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType())>
this.${attribute.name} = new ArrayList<>()
  <#else>
  <#if MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType())>
this.${attribute.name} = new HashSet<>();
    <#else>
    <#if MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())>
this.${attribute.name} = Optional.empty();
    </#if>
  </#if>
</#if>
return this.realBuilder;


