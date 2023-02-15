<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("glex", "cdGenerator")}

<#assign decorator = tc.instantiate("de.monticore.cd.codegen.ConstructorDecorator")>

${decorator.decorate(ast)}

${cdGenerator.generate(ast)}
