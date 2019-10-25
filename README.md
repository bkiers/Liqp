# Liqp &nbsp; [![Build Status](https://travis-ci.org/bkiers/Liqp.png)](https://travis-ci.org/bkiers/Liqp) [![Maven Central](https://img.shields.io/maven-central/v/nl.big-o/liqp.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22nl.big-o%22%20AND%20a%3A%22liqp%22)

A Java implementation of the [Liquid templating engine](https://shopify.github.io/liquid) backed
up by an ANTLR grammar.

## Installation

### Gradle

Add the dependency:

```groovy
dependencies {
  compile 'nl.big-o:liqp:0.7.9'
}
```

### Maven

Add the following dependency:

```xml
<dependency>
  <groupId>nl.big-o</groupId>
  <artifactId>liqp</artifactId>
  <version>0.7.9</version>
</dependency>
```

Or clone this repository and run: `mvn install` which will create a JAR of Liqp
in your local Maven repository, as well as in the project's `target/` folder.

# Usage

This library can be used in two different ways:

1. to construct a parse tree of some Liquid input
2. to render Liquid input source (either files, or input strings)

## 1. Creating a parse tree

To create a parse tree from input source, do the following:

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

ParseTree root = template.getParseTree();
```

As you can see, the `getParseTree()` method returns an instance of a
[`ParseTree`](http://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/ParseTree.html) denoting the root
node of the input source. To see how the parse tree is built, you can use `Template#toStringAST()` to print
an ASCII representation of the tree.

## 2. Render Liquid

If you're not familiar with Liquid, have a look at their website: [http://liquidmarkup.org](http://liquidmarkup.org).

In Ruby, you'd render a template like this:

```ruby
@template = Liquid::Template.parse("hi {{name}}")  # Parses and compiles the template
@template.render( 'name' => 'tobi' )               # Renders the output => "hi tobi"
```

With Liqp, the equivalent looks like this:

```java
Template template = Template.parse("hi {{name}}");
String rendered = template.render("name", "tobi");
System.out.println(rendered);
/*
    hi tobi
*/
```
The template variables provided as parameters to `render(...)` can be:

* a [varargs](http://docs.oracle.com/javase/1.5.0/docs/guide/language/varargs.html) where
  the 0<sup>th</sup>, 2<sup>nd</sup>, 4<sup>th</sup>, ... indexes must be `String` literals
  denoting the keys. The values can be any `Object`.
* a `Map<String, Object>`
* or a JSON string

The following examples are equivalent to the previous Liqp example:

#### Map example

```java
Template template = Template.parse("hi {{name}}");
Map<String, Object> map = new HashMap<String, Object>();
map.put("name", "tobi");
String rendered = template.render(map);
System.out.println(rendered);
/*
    hi tobi
*/
```

#### JSON example

```java
Template template = Template.parse("hi {{name}}");
String rendered = template.render("{\"name\" : \"tobi\"}");
System.out.println(rendered);
/*
    hi tobi
*/
```

#### Strict variables example

Strict variables means that value for every key must be provided, otherwise an exception occurs.

```java
Template template = Template.parse("hi {{name}}")
    .withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build());
String rendered = template.render(); // no value for "name"
// exception is thrown
```

### 2.1 Custom filters

Let's say you want to create a custom filter, called `b`, that changes a string like
`*text*` to `<strong>text</strong>`.

You can do that as follows:

```java
// first register your custom filter
Filter.registerFilter(new Filter("b"){
    @Override
    public Object apply(Object value, Object... params) {
        // create a string from the  value
        String text = super.asString(value);

        // replace and return *...* with <strong>...</strong>
        return text.replaceAll("\\*(\\w(.*?\\w)?)\\*", "<strong>$1</strong>");
    }
});

// use your filter
Template template = Template.parse("{{ wiki | b }}");
String rendered = template.render("{\"wiki\" : \"Some *bold* text *in here*.\"}");
System.out.println(rendered);
/*
    Some <strong>bold</strong> text <strong>in here</strong>.
*/
```
And to use an optional parameter in your filter, do something like this:

```java
// first register your custom filter
Filter.registerFilter(new Filter("repeat"){
    @Override
    public Object apply(Object value, Object... params) {

        // get the text of the value
        String text = super.asString(value);

        // check if an optional parameter is provided
        int times = params.length == 0 ? 1 : super.asNumber(params[0]).intValue();

        StringBuilder builder = new StringBuilder();

        while(times-- > 0) {
            builder.append(text);
        }

        return builder.toString();
    }
});

// use your filter
Template template = Template.parse("{{ 'a' | repeat }}\n{{ 'b' | repeat:5 }}");
String rendered = template.render();
System.out.println(rendered);
/*
    a
    bbbbb
*/
```
You can use an array (or list) as well, and can also return a numerical value:

```java
Filter.registerFilter(new Filter("sum"){
    @Override
    public Object apply(Object value, Object... params) {

        Object[] numbers = super.asArray(value);

        double sum = 0;

        for(Object obj : numbers) {
            sum += super.asNumber(obj).doubleValue();
        }

        return sum;
    }
});

Template template = Template.parse("{{ numbers | sum }}");
String rendered = template.render("{\"numbers\" : [1, 2, 3, 4, 5]}");
System.out.println(rendered);
/*
    15.0
*/
```
In short, override one of the `apply()` methods of the `Filter` class to create your own custom filter behaviour.

### 2.2 Custom tags

Let's say you would like to create a tag that makes it easy to loop for a fixed amount of times,
executing a block of Liquid code.

Here's a way to create, and use, such a custom `loop` tag:

```java
Tag.registerTag(new Tag("loop"){
    @Override
    public Object render(Map<String, Object> context, LNode... nodes) {

        int n = super.asNumber(nodes[0].render(context)).intValue();
        LNode block = nodes[1];

        StringBuilder builder = new StringBuilder();

        while(n-- > 0) {
            builder.append(super.asString(block.render(context)));
        }

        return builder.toString();
    }
});

Template template = Template.parse("{% loop 5 %}looping!\n{% endloop %}");
String rendered = template.render();
System.out.println(rendered);
/*
    looping!
    looping!
    looping!
    looping!
    looping!
*/
```

Note that both `Tag.registerTag(Tag)` and `Filer.registerFilter(Filter)` will add
tags and filters per JVM instance. If you want templates to use specific filters,
create your `Template` instance as follows:

```java
Template.parse(source)
        .with(filter);

Template.parse(source)
        .with(tag);

// Or combine them:
Template.parse(source)
        .with(filter)
        .with(tag);
```

For example, using the `sum` filter for just 1 template, would look like this:

```java
Template template = Template.parse("{{ numbers | sum }}").with(new Filter("sum"){
    @Override
    public Object apply(Object value, Object... params) {

        Object[] numbers = super.asArray(value);
        double sum = 0;

        for(Object obj : numbers) {
            sum += super.asNumber(obj).doubleValue();
        }

        return sum;
    }
});

String rendered = template.render("{\"numbers\" : [1, 2, 3, 4, 5]}");
System.out.println(rendered);
/*
    15.0
*/
```

### 2.3 Guards

If you're evaluating templates from untrusted sources, there are a couple of
ways you can guard against unwanted input.

For example, if you'd like the input template to be no larger than 125 characters,
the templating engine should not perform more than 15 iterations in total,
the generated string should not exceed 300 characters and the total rendering (and parsing!)
time should not exceed 100 milliseconds, you could do something like this:

```java
ProtectionSettings protectionSettings = new ProtectionSettings.Builder()
        .withMaxSizeRenderedString(300)
        .withMaxIterations(15)
        .withMaxRenderTimeMillis(100L)
        .withMaxTemplateSizeBytes(125)
        .build();

String rendered = Template.parse("{% for i in (1..10) %}{{ text }}{% endfor %}")
        .withProtectionSettings(protectionSettings)
        .render("{\"text\": \"abcdefghijklmnopqrstuvwxyz\"}");

System.out.println(rendered);
```

Note that not providing a `ProtectionSettings`, is the same as not having any guards in
place (or better, very large limits).


## Build and Release

Use Maven 3.5.0 and run build with

```
mvn clean install
````

Release process into the [Central Repository](http://central.sonatype.org) is
performed with

```
mvn release:prepare release:perform
```


