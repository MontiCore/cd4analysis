<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}

<#assign MCCollectionSymTypeRelations = glex.getGlobalVar("mcCollectionSymTypeRelations")>

<#if MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())>
this.${attribute.getName()} = Optional.ofNullable(${attribute.getName()});
<#else>
this.${attribute.getName()} = ${attribute.getName()};
</#if>
return this.realBuilder;
