<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute","oldValueName")}
<#assign MCCollectionSymTypeRelations = glex.getGlobalVar("mcCollectionSymTypeRelations")>

${attribute.getMCType().printType()} ${oldValueName} = this.${attribute.getName()};
<#if MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())>
this.${attribute.getName()} = Optional.ofNullable(${attribute.getName()});
<#else>
this.${attribute.getName()} = ${attribute.getName()};
</#if>
notifyObserver${attribute.getName()?cap_first}(this,${oldValueName});
