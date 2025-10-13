<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
${defineHookPoint("Setter:Before")}
this.${attribute.getName()}.add(index, ${attribute.getName()});
${defineHookPoint("Setter:After")}
