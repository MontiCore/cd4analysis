<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ObserverList","ObserverType")}
for(${ObserverType} observer : this.${ObserverList}) {
    observer.update(this);
}
