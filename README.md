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

...
