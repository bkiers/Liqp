# Liqp &nbsp; [![Build Status](https://github.com/bkiers/Liqp/actions/workflows/ci.yml/badge.svg)](https://github.com/bkiers/Liqp/actions/workflows/ci.yml) [![Maven Central](https://img.shields.io/maven-central/v/nl.big-o/liqp.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22nl.big-o%22%20AND%20a%3A%22liqp%22)

A Java implementation of the [Liquid templating engine](https://shopify.github.io/liquid) backed
up by an ANTLR grammar.

## Installation

### Gradle

Add the dependency:

```groovy
dependencies {
  compile 'nl.big-o:liqp:0.8.3.1'
}
```

### Maven

Add the following dependency:

```xml
<dependency>
  <groupId>nl.big-o</groupId>
  <artifactId>liqp</artifactId>
  <version>0.8.3.1</version>
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
* a JSON string
* any POJO that is marked with special marker interface `liqp.parser.Inspectable`. In this case the object is converted to `java.util.Map` using jackson's mapper, and so all recipes for configuring jackson conversation will work here.
* any object that extend special interface `liqp.parser.LiquidSupport` and it is designed for lazy field values computing. It's method `LiquidSupport#toLiquid()` is called only if/when the object is going to be rendered. Since `LiquidSupport` extends `Inspectable` simply use same variant of the `render(...)` method.

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

#### Inspectable example

```java
class MyParams implements Inspectable {
  public String name = "tobi";
};
Template template = Template.parse("hi {{name}}");
String rendered =template.render(new MyParams());
System.out.println(rendered);
/*
    hi tobi
*/
```

#### LiquidSupport example
```java
class MyLazy implements LiquidSupport {
    @Override
    public Map<String, Object> toLiquid() {
        return Collections.singletonMap("name", "tobi");
    }
};
Template template = Template.parse("hi {{name}}");
String rendered = template.render(new MyLazy());
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

#### Eager and Lazy evaluate mode
There exists two rendering modes: lazy and eager.
* In `lazy` mode the template parameters are evaluating on demand and specific properties are read from there only if they are needed. Each filter/tag trying to do its work with its own parameter object, that can be literally anything.
* In `eager` the entire parameter object is converted into plain data tree structure that are made <strong>only</strong> from maps and lists, so tags/filters do know how to work with these kinds of objects. Special case - temporal objects, they are consumed as is.

By <strong>default</strong>, the `lazy` one is used. This should do the work in most cases.

Switching mode is possible via providing special `RenderSettings`.
Example usage of `lazy` mode:
```java
RenderSettings renderSettings = new RenderSettings.Builder()
    .withEvaluateMode(RenderSettings.EvaluateMode.EAGER)
    .build();

Map<String, Object> in = Collections.singletonMap("a", new Object() {
    public String val = "tobi";
});

String res = Template.parse("hi {{a.val}}")
        .withRenderSettings(renderSettings)
        .render(in);
System.out.println(res);
/*
    hi tobi
*/
```

### 2.1 Custom filters

Let's say you want to create a custom filter, called `b`, that changes a string like
`*text*` to `<strong>text</strong>`.

You can do that as follows:

```java
// first register your custom filter
Filter.registerFilter(new Filter("b"){
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        // create a string from the  value
        String text = super.asString(value, context);

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
    public Object apply(Object value, TemplateContext context, Object... params) {

        // get the text of the value
        String text = super.asString(value, context);

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
    public Object apply(Object value, TemplateContext context, Object... params) {

        Object[] numbers = super.asArray(value, context);

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



### 2.2 Tags and blocks
Both blocks and tags are kinds of insertions. Literally: `Block extends Insertion` and `Tag extends Insertion`. Class `Insertion` used as basic abstraction for parser. 
Let's define the difference between tags and blocks. 

#### Tags
Tag is simple insertion in the template that will be processed and the result of it will be replaced in output, if any. Example is `include` tag: 
```liquid
See these data: {% include data.liquid %}
```
Another example is `assign` tag: 
```liquid
{% assign name='Joe' %}
Hello {{name}}!
```
It has no input but still is an insertion.

#### Blocks
Block is a kind of insertions that wraps some text and/or other blocks or tags and perform some operations on the given input. Blocks have opening and closing tags. Example of block is `if`:
```liquid
{% if user %} Hello {{ user.name }} {% endif %}"
```
where `{% if user %}` is opening tag and `{% endif %}` is closing one. The `user` in this sample is just a parameter for given block.

#### Custom Tags and Blocks
Let's say you would like to create a block that makes it easy to loop for a fixed amount of times,
executing a block of Liquid code.

Here's a way to create, and use, such a custom `loop` block:

```java
Block.registerBlock(new Block("loop"){
    @Override
    public Object render(TemplateContext context, LNode... nodes) {
        int n = super.asNumber(nodes[0].render(context)).intValue();
        LNode block = nodes[1];
        StringBuilder builder = new StringBuilder();
        while(n-- > 0) {
            builder.append(super.asString(block.render(context), context));
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
Note: `Block.registerBlock` is just a fluent shortcut for `Insertion.registerInsertion`.
For registering custom Tags there exists equivalent `Tags.registerTag` function (that also wraps `Insertion.registerInsertion`). 

Note that all of:
 * `Insertion.registerInsertion(Insertion)`
 * `Block.registerBlock(Block)`
 * `Tag.registerTag(Tag)`
 * `Filer.registerFilter(Filter)` 

will add tags, blocks and filters per JVM instance. If you want templates to use specific filters, create your `Template` instance as follows:

```java


Template.parse(source, new ParseSettings.Builder()
                .with(tag)
                .build());

Template.parse(source, new ParseSettings.Builder()
                .with(block)
                .build());

Template.parse(source, new ParseSettings.Builder()
                .with(filter)
                .build());

// Or combine them:
Template.parse(source, new ParseSettings.Builder()
                .with(filter)
                .with(tag)
                .build());
```

For example, using the `sum` filter for just 1 template, would look like this:

```java
Template template = Template.parse("{{ numbers | sum }}", new ParseSettings.Builder()
                .with(new Filter("sum"){
                    @Override
                    public Object apply(Object value, TemplateContext context, Object... params) {

                        Object[] numbers = super.asArray(value, context);
                        double sum = 0;

                        for(Object obj : numbers) {
                            sum += super.asNumber(obj).doubleValue();
                        }

                        return sum;
                    }
                }).build());

        String rendered = template.render("{\"numbers\" : [1, 2, 3, 4, 5]}");
        System.out.println(rendered);
/*
    15.0
*/
```
Also note that `ParseSettings` object have unmodifiable fields and so can be shared across templates without side effects.

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
 mvn release:clean release:prepare release:perform -P ossrh-release
```
Make sure having in `~/.m2/settings.xml` this config(with your values):
```xml
<settings>
<servers>
  <server>
    <id>ossrh</id>
    <username>MY_OSSRH_USERNAME</username>
    <password>MY_OSSRH_PASSWORD</password>
  </server>
</servers>
  <profiles>
    <profile>
      <id>ossrh-release</id>
      <activation>
        <activeByDefault>false</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg2</gpg.executable>
        <gpg.passphrase>GPG_PRIVATE_KEY_PASSWORD</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```
After executing this go to https://oss.sonatype.org/index.html#stagingRepositories, ensure all is OK, after you can "Close" the staging for promoting to the realease and after do "Release".
