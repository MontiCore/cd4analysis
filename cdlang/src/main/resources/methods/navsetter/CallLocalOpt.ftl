<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "roleName", "method")}
this.${attribute.getName()}.ifPresent( ${attribute.getName()}_ -> ${attribute.getName()}_.${method}${roleName?cap_first}Local(this));
