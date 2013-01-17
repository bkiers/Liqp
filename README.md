## Liqp

An ANTLR based 'Liquid Template' parser and rendering engine.

### Examples

Below are some examples of how to use this library to render Liquid-templates.

-----------------------------------

`input`

```java
String source = "hi {{name}}";

Template template = Template.parse(source);

Map<String, Object> variables = new HashMap<String, Object>();
variables.put("name", "tobi");

String rendered = template.render(variables);

System.out.println(rendered);
```

`output`

```
hi tobi
```

-----------------------------------

`input`

```java
String source =
        "<ul id=\"products\">" +
        "  {% for p in products %}\n" +
        "  <li>\n" +
        "    {{ p.name }} costs ${{ p.price }},-: {{ p.description | downcase }}\n" +
        "  </li>" +
        "  {% endfor %}\n" +
        "</ul>\n";

Template template = Template.parse(source);

String variables =
        "{\"products\":[" +
        "    {\"name\":\"A\", \"price\":1, \"description\":\"BLA\"}," +
        "    {\"name\":\"B\", \"price\":2, \"description\":\"some more text\"}," +
        "    {\"name\":\"C\", \"price\":3, \"description\":\"and the LAST one\"}" +
        "  ]" +
        "}";

String rendered = template.render(variables);

System.out.println(rendered);
```

`output`

```
<ul id="products">  
  <li>
    A costs $1,-: bla
  </li>  
  <li>
    B costs $2,-: some more text
  </li>  
  <li>
    C costs $3,-: and the last one
  </li>  
</ul>
```

*more documentation to follow*
