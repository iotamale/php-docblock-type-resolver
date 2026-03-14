# PHP DocBlock Type Resolver

### Expected Behavior & Examples
* **Standard Type:** `/** @var User */` for `$user` should return `User`.
* **Union Type:** `/** @var string|int */` for `$id` should return a `UnionType` of `string` and `int`.
* **Named Tag:** `/** @var Logger $log */` for variable `$log` should return `Logger`.
* **Name Mismatch:** `/** @var Admin $adm */` for variable `$guest` should return `mixed`. *(The tag is for a different variable, so it must be ignored).*
* **Multiple Tags:** If a DocBlock has `/** @var int $id */` and `/** @var string $name */`, and we are inspecting `$name`, the function should return `string`.
* **Fallback:** If no DocBlock exists or no matching tag is found, return `mixed` using `TypeFactory.createType("mixed")`.

### Asumptions made during implementation
* `/** @var int| $id */` should return `int` instead of `UnionType`.
* if a `DocBlock` has `/** @var int $id */` and `/** @var string */`, for variable `$adm` it should return `string`.
* UnionTypes are always declared without any whitespaces between `type1` and `type2`:   `/** @var int | string $id */` would be invalid, since it contains a space between `int` and `|` .