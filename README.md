# Liqp &nbsp; [![Build Status](https://github.com/bkiers/Liqp/actions/workflows/ci.yml/badge.svg)](https://github.com/bkiers/Liqp/actions/workflows/ci.yml) [![Maven Central](https://img.shields.io/maven-central/v/nl.big-o/liqp.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22nl.big-o%22%20AND%20a%3A%22liqp%22) ![Branches](.github/badges/branches.svg?)

A Java implementation of the [Liquid templating engine](https://shopify.github.io/liquid) backed
up by an ANTLR grammar.

## Installation

### Gradle

Add the dependency:

```groovy
dependencies {
  compile 'nl.big-o:liqp:0.9.0.2'
}
```

### Maven

Add the following dependency:

```xml
<dependency>
  <groupId>nl.big-o</groupId>
  <artifactId>liqp</artifactId>
  <version>0.9.0.2</version>
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
        "<ul id=\"products\">                                               \n" +
                "  {% for product in products %}                            \n" +
                "    <li>                                                   \n" +
                "      <h2>{{ product.name }}</h2>                          \n" +
                "      Only {{ product.price | price }}                     \n" +
                "                                                           \n" +
                "      {{ product.description | prettyprint | paragraph }}  \n" +
                "    </li>                                                  \n" +
                "  {% endfor %}                                             \n" +
                "</ul>                                                      \n";
Template template = TemplateParser.DEFAULT.parse(input);

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
TemplateParser parser = new TemplateParser.Builder().build();
Template template = parser.parse("hi {{name}}");
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
Template template = new TemplateParser.Builder().build().parse("hi {{name}}");
Map<String, Object> map = new HashMap<>();
map.put("name", "tobi");
String rendered = template.render(map);
System.out.println(rendered);
/*
    hi tobi
*/
```

#### JSON example

```java
Template template = new TemplateParser.Builder().build().parse("hi {{name}}");
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
Template template = TemplateParser.DEFAULT.parse("hi {{name}}");
String rendered = template.render(new MyParams());
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
Template template = TemplateParser.DEFAULT.parse("hi {{name}}");
String rendered = template.render(new MyLazy());
System.out.println(rendered);
/*
    hi tobi
*/
```

#### Controlling library behavior
The library has a set of keys to control the parsing/rendering process. Even if you might think that's too many of them, the defaults will work for you in most cases. All of them are set on `TemplateParser.Builder` class. Here they are:
* `withFlavor(Flavor flavor)` - flavor of the liquid language. Flavor is nothing else than a predefined set of other settings. Here are supported flavors:
  * `Flavor.JEKYLL` - flavor that defines all settings, so it tries to behave like jekyll's templates
  * `Flavor.LIQUID` - the same for liquid's templates
  * `Flavor.LIQP` (default) - developer of this library found some default behavior of two flavors above somehow weird in selected cases. So this flavor appears.
* `withStripSingleLine(boolean stripSingleLine)`- if `true` then all blank lines left by outputless tags are removed. Default is `false`.
* `withStripSpaceAroundTags(boolean stripSpacesAroundTags)` - if `true` then all whitespaces around tags are removed. Default is `false`.
* `withObjectMapper(ObjectMapper mapper)` - if provided then this mapper is used for converting json strings to objects and internal object conversion. If not provided, then default mapper is used. Default one is good. Also, the default one is always accessible via TemplateContext instance:`context.getParser().getMapper();`
* `withTag(Tag tag)` - register custom tag to be used in templates.
* `withBlock(Block block)` - register custom block to be used in templates. The difference between tag and block is that block has open and closing tag and can contain other content like a text, tags and blocks.
* `withFilter(Filter filter)` - register custom filter to be used in templates. See below for examples.
* `withEvaluateInOutputTag(boolean evaluateInOutputTag)` - both `Flavor.JEKYLL` and `Flavor.LIQUID` are not allows to evaluate expressions in output tags, simply ignoring the expression and printing out very first token of the expression.  Yes, this: `{{ 97 > 96 }}` will print `97`. This is known [bug/feature](https://github.com/Shopify/liquid/issues/1102) of those templators. If you want to change this behavior and evaluate those expressions, set this flag to `true`. Also, the default flavor `Flavor.LIQP` has this flag set to `true` already.
* `withStrictTypedExpressions(boolean strictTypedExpressions)` - ruby is strong-typed language. So comparing different types is not allowed there. This library tries to mimic ruby's type system in a way so all not explicit types (created or manipulated inside of templates) are converted with this schema: `nil` -> `null`; `boolean` -> `boolean`; `string` -> `java.lang.String`; any numbers -> `java.math.BigDecimal`, any datetime -> `java.time.ZonedDateTime`. If you want to change this behavior, and allow comparing in expressions in a less restricted way, set this flag to `true`, then the lax (javascript-like) approach for comparing in expressions will be used. Also, the default flavor `Flavor.LIQP` has this flag set to `true` already, others have it `false` by default.
* `withLiquidStyleInclude(boolean liquidStyleInclude)` - if `true` then include tag will use [syntax from liquid](https://shopify.dev/docs/api/liquid/tags/include), otherwice [jekyll syntax](https://jekyllrb.com/docs/includes/) will be used. Default depends on flavor. `Flavor.LIQUID` and `Flavor.LIQP` has this flag set to `true` already. `Flavor.JEKYLL` has it `false`.
* `withStrictVariables(boolean strictVariables)` - if set to `true` then all variables must be defined before usage, if some variable is not defined, the exception will be thrown. If `false` then all undefined variables will be treated as `null`. Default is `false`.
* `withShowExceptionsFromInclude(boolean showExceptionsFromInclude)` - if set to `true` then all exceptions from included templates will be thrown. If `false` then all exceptions from included templates will be ignored. Default is `true`.
* `withEvaluateMode(TemplateParser.EvaluateMode evaluateMode)` - there exists two rendering modes: `TemplateParser.EvaluateMode.LAZY` and `TemplateParser.EvaluateMode.EAGER`. By default, the `lazy` one is used. This should do the work in most cases. 
  * In `lazy` mode the template parameters are evaluating on demand and specific properties are read from there only if they are needed. Each filter/tag trying to do its work with its own parameter object, that can be literally anything.
  * In `eager` the entire parameter object is converted into plain data tree structure made **only** from maps and lists, so tags/filters do know how to work with these kinds of objects. Special case - temporal objects, they are consumed as is.
* `withRenderTransformer(RenderTransformer renderTransformer)` - even if most of elements (filters/tags/blocks) returns its results most cases as `String`, the task of combining all those strings into a final result is a task of `liqp.RenderTransformer` implementation. The default `liqp.RenderTransformerDefaultImpl` uses `StringBuilder` for that task, so template rendering is fast. Although, you might have special needs or environment to render the results.
* `withLocale(Locale locale)` - locale to be used for rendering. Default is `Locale.ENGLISH`. Used mostly for time rendering.
* `withDefaultTimeZone(ZoneId defaultTimeZone)` - default time zone to be used for rendering. Default is `ZoneId.systemDefault()`. Used mostly for time rendering.
* `withEnvironmentMapConfigurator(Consumer<Map<String, Object>> configurator)` - if provided then this configurator is called before each template rendering. It can be used to set some global variables for all templates built with given `TemplateParser`. 
* `withSnippetsFolderName(String snippetsFolderName)` - define folder to be used for searching files by `include` tag. Defaults depend on flavor: `Flavor.LIQUID` and `Flavor.LIQP` has this set to `snippets`; `Flavor.JEKYLL` uses `_includes`.
* `withNameResolver(NameResolver nameResolver)` - if provided then this resolver is used for resolving names of included files. If not provided, then default resolver is used. Default resolver is `liqp.antlr.LocalFSNameResolver` that uses `java.nio.file.Path` for resolving names in local file system. That can be changed to any other resolver, for example, to resolve names in classpath or in remote file system or even build templates dynamically by name.
* `withMaxIterations(int maxIterations)` - maximum number of iterations allowed in a template. Default is `Integer.MAX_VALUE`. Used to prevent infinite loops. Example use: evaluate templates from untrusted sources.
* `withMaxSizeRenderedString(int maxSizeRenderedString)` - maximum size of rendered string. Default is `Integer.MAX_VALUE`. Used to prevent out of memory errors. Example use: evaluate templates from untrusted sources.
* `withMaxRenderTimeMillis(long maxRenderTimeMillis)` - maximum time allowed for template rendering. Default is `Long.MAX_VALUE`. Used to prevent never-ending rendering. Example use: evaluate templates from untrusted sources.
* `withMaxTemplateSizeBytes(long maxTemplateSizeBytes)` - maximum size of template. Default is `Long.MAX_VALUE`. Used to prevent out of memory errors. Example use: evaluate templates from untrusted sources.
* `withErrorMode(ErrorMode errorMode)` - error mode to be used. Default is `ErrorMode.STRICT`. 


### 2.1 Custom filters

Let's say you want to create a custom filter, called `b`, that changes a string like
`*text*` to `<strong>text</strong>`.

You can do that as follows:

```java
 // first create template parser with new filter
TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("b") {
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        // create a string from the value
        String text = super.asString(value, context);

        // replace and return *...* with <strong>...</strong>
        return text.replaceAll("\\*(\\w(.*?\\w)?)\\*", "<strong>$1</strong>");
    }
}).build();

// use your filter
Template template = parser.parse("{{ wiki | b }}");
String rendered = template.render("{\"wiki\" : \"Some *bold* text *in here*.\"}");
System.out.println(rendered);
/*
    Some <strong>bold</strong> text <strong>in here</strong>.
*/
```
And to use an optional parameter in your filter, do something like this:

```java
// first create template parser with your filter
TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("repeat"){
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
}).build();

// use your filter
Template template = parser.parse("{{ 'a' | repeat }}\n{{ 'b' | repeat:5 }}");
String rendered = template.render();
System.out.println(rendered);
/*
a
bbbbb
*/
```
You can use an array (or list) as well, and can also return a numerical value:

```java
TemplateParser parser = new TemplateParser.Builder().withFilter((new Filter("sum"){
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        Object[] numbers = super.asArray(value, context);

        double sum = 0;

        for(Object obj : numbers) {
            sum += super.asNumber(obj).doubleValue();
        }

        return sum;
    }
})).build();

Template template = parser.parse("{{ numbers | sum }}");
String rendered = template.render("{\"numbers\" : [1, 2, 3, 4, 5]}");
System.out.println(rendered);
/*
    15.0
*/
```
In short, override one of the `apply()` methods of the `Filter` class to create your own custom filter behavior.



### 2.2 Tags and blocks
Both blocks and tags are kinds of insertions. Literally: `Block extends Insertion` and `Tag extends Insertion`. Class `Insertion` used as basic abstraction for parser. 
Below is defined the difference between tags and blocks. 

#### Tags
Tag is a simple insertion in the template that will be processed and the result of it will be replaced in output, if any. Example is `include` tag: 
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
Block is a kind of insertions that wraps some text and/or other blocks or tags and performs some operations on the given input. Blocks have opening and closing tags. Example of block is `if`:
```liquid
{% if user %} Hello {{ user.name }} {% endif %}"
```
where `{% if user %}` is opening tag and `{% endif %}` is closing one. The `user` in this sample is just a parameter for given block.

#### Custom Tags and Blocks
Let's say you would like to create a block that makes it easy to loop for a fixed number of times executing a block of Liquid code.

Here's a way to create, and use, such a custom `loop` block:

```java
TemplateParser parser = new TemplateParser.Builder().withBlock(new Block("loop"){
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
}).build();

Template template = parser.parse("{% loop 5 %}looping!\n{% endloop %}");
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

For registering custom Tags there exists equivalent `Builder.withTag` function.

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
