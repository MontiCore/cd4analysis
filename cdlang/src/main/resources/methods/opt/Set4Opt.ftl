<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
${defineHookPoint("Setter:Before")}
this.${attribute.getName()} = Optional.ofNullable(${attribute.getName()});
${defineHookPoint("Setter:After")}
