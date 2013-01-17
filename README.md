# Liqp

An ANTLR based 'Liquid Template' parser and rendering engine.

## Some examples

`input`:

```java
String source = "hi {{name}}";

Template template = Template.parse(source);

Map<String, Object> variables = new HashMap<String, Object>();
variables.put("name", "tobi");

String rendered = template.render(variables);

System.out.println(rendered);
```

`output`:

```
hi tobi
```

*more documentation to follow*