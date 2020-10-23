`SymbolTableCreator`
- auf deprecated setzen

`ScopeSkeletonCreator`
- wie normaler STC
- `create_X` returns builder
- remove `initialize_X` (and from endVisit call)
    - check if needed when its a scope (`scope.setName(ast.getName());`)
- `visit`: add `.build()` call
- visit/endvisit of ast node: isPresentName() when name is optional

`ScopeSkeletonCreatorDelegator` (vgl. SymbolTableCreatorDelegator)
- use `XScopeSkeletonCreator`

- `CD4CodeScopeBuilder extends CD4AnalysisScopeBuilder`
- CD4AnalysisMillForCD4Code
  - automatisch überschriebene `_cD4AnalysisScopeBuilder()`
- STCompleteTypes nur Symbol/Scope visit nutzen