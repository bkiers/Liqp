# Liqp

A Java implmentation of the Liquid templating engine backed up by an ANTLR grammar. 

This library can be used for 2  purposes:

1. to construct an AST (abstract syntax tree) of some Liquid input
2. to render Liquid input source (either files, or input strings)

## 1. Creating an AST

To create an AST from input source, do the following:

```java
String input =
        "<ul id=\"products\">                                       \n" +
        "  {% for product in products %}                            \n" +
        "    <li>                                                   \n" +
        "      <h2>{{ product.name }}</h2>                          \n" +
        "      Only {{ product.price | price }}                     \n" +
        "                                                           \n" +
        "      {{ product.description | prettyprint | paragraph }}  \n" +
        "    </li>                                                  \n" +
        "  {% endfor %}                                             \n" +
        "</ul>                                                      \n";
Template template = Template.parse(input);

CommonTree root = template.getAST();
```

As you can see, the `getAST()` method returns an instance of a 
[`CommonTree`](http://www.antlr.org/api/Java/org/antlr/runtime/tree/CommonTree.html) denoting the root 
node of the input source. To see how the AST is built, you can use `Template#toStringAST()` to print 
an ASCII representation of the tree:

```java
System.out.println(template.toStringAST());
/*
    '- BLOCK
       |- PLAIN='<ul id="products">'
       |- FOR_ARRAY
       |  |- Id='product'
       |  |- LOOKUP
       |  |  '- Id='products'
       |  |- BLOCK
       |  |  |- PLAIN='<li> <h2>'
       |  |  |- OUTPUT
       |  |  |  |- LOOKUP
       |  |  |  |  |- Id='product'
       |  |  |  |  '- Id='name'
       |  |  |  '- FILTERS
       |  |  |- PLAIN='</h2> Only'
       |  |  |- OUTPUT
       |  |  |  |- LOOKUP
       |  |  |  |  |- Id='product'
       |  |  |  |  '- Id='price'
       |  |  |  '- FILTERS
       |  |  |     '- FILTER
       |  |  |        |- Id='price'
       |  |  |        '- PARAMS
       |  |  |- PLAIN=''
       |  |  |- OUTPUT
       |  |  |  |- LOOKUP
       |  |  |  |  |- Id='product'
       |  |  |  |  '- Id='description'
       |  |  |  '- FILTERS
       |  |  |     |- FILTER
       |  |  |     |  |- Id='prettyprint'
       |  |  |     |  '- PARAMS
       |  |  |     '- FILTER
       |  |  |        |- Id='paragraph'
       |  |  |        '- PARAMS
       |  |  '- PLAIN='</li>'
       |  '- ATTRIBUTES
       '- PLAIN='</ul>'
*/
```
Checkout the [ANTLR grammar](https://github.com/bkiers/Liqp/blob/master/src/grammar/Liquid.g) 
to see what the AST looks like exactly.

### 2. Render Liquid

If you're not familiar with Liquid, have a look at their website: [http://liquidmarkup.org](liquidmarkup.org).

In Ruby, you'd render a template like this:

```ruby
@template = Liquid::Template.parse("hi {{name}}")  # Parses and compiles the template
@template.render( 'name' => 'tobi' )               # Renders the output => "hi tobi"
```

With Liqp, the equivalent looks likt  this:

```java
Template template = Template.parse("hi {{name}}");
String rendered = template.render("name", "tobi");
System.out.println(rendered);
/*
    hi tobi
*/
```
The context provided as a parameter to `render(...)` can be:

* a [varargs](http://docs.oracle.com/javase/1.5.0/docs/guide/language/varargs.html) where 
  the 0<sup>th</sup>, 2<sup>nd</sup>, 4<sup>th</sup>, ... indexes must be `String` literals
  denoting the keys. The values can be any `Object`.
* a `Map<String, Object>`
* or a JSON string

The following examples are equivalent to the previous Liqp example:

#### Map example

```java
template = Template.parse("hi {{name}}");
Map<String, Object> map = new HashMap<String, Object>();
map.put("name", "tobi");
rendered = template.render(map);
System.out.println(rendered);
/*
    hi tobi
*/
```

#### JSON example

```java
template = Template.parse("hi {{name}}");
rendered = template.render("{\"name\" : \"tobi\"}");
System.out.println(rendered);
/*
    hi tobi
*/
```
