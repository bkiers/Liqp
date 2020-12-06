$is_Jekyll = false
begin
  require "jekyll"
  puts "testing cases using jekyll"
  $is_Jekyll = true
rescue LoadError
  require "liquid"
  puts "testing cases using liquid"
end

def isJekyll
  $is_Jekyll
end


def assertEqual(expected, real)
  if expected != real
    raise "#{real} is not #{expected}"
  end
end


Liquid::Template.error_mode = :strict


def render(data = {}, source)
  Liquid::Template.parse(source, {:strict_variables => true}).render!(data);
end

# pp render({ "array" => { "items" => [1,2,3,4,5,6,7,8,9] } }, "{%for i in array.items limit:8 %}{%endfor%}{% assign continue = 0 %}{%for i in array.items offset:continue %}{{i}}{%endfor%}")
# # as we see here: continue works with ranges
# pp render({ }, "{%for i in (1..9) limit:8 %}{%endfor%}{%for i in (1..9) offset:continue %}{{i}}{%endfor%}")
# pp render({ }, "{%for i in (1..9) limit:8 %}{%endfor%}{% assign continue = 0 %}{%for i in (1..9) offset:continue %}{{i}}{%endfor%}")
# pp render({}, "{% for x in (1..9) %}{{forloop.name}}-{{x}}{%endfor%}")
# pp render({}, "{% assign st = 1 %}{% assign end = 7 %}{% for x in (st..end) %}{{forloop.name}}-{{x}}{%endfor%}")
# pp render({"end" => {"data" => [{"here" => 5}]}}, "{% assign st = 1 %}{% for x in (st..end['data'][0].here) %}{{forloop.name}}-{{x}}{%endfor%}")
# pp render({"end" => {"data" => [{"here" => [1,2,3,4,5,6]}]}}, "{% for x in end['data'][0].here %}{{forloop.name}}-{{x}}{%endfor%}")
# pp render({"end" => {"data" => [{"here" => [1,2,3,4,5,6]}]}}, "{%for i in end['data'][0].here limit:5 %}{%endfor%}{%for i in end['data'][0].here offset:continue %}[{{forloop.name}}]-{{i}}{%endfor%}")
# pp render({"a" => [1,2,3,4,5,6], "b" => [1,2,3,4,5,6]}, "{%for i in a limit:5 %}{%endfor%}{%for i in b offset:continue %}[{{forloop.name}}]-{{i}}{%endfor%}")
#
# pp render({"a" => {"prop" => [1,2,3,4,5,6]}}, "{%for i in a.prop limit:5 %}{%endfor%}{%for i in a['prop'] offset:continue %}[{{forloop.name}}]-{{i}}{%endfor%}")
# pp render({"a" => {"prop" => [1,2,3,4,5,6]}}, "{%for i in a.prop limit:5 %}{%endfor%}{%for i in a.prop offset:continue %}[{{forloop.name}}]-{{i}}{%endfor%}")
#
# pp render({"a" => [1,2]}, "{% for i in a reversed %}{{i}} - forloop.first: {{forloop.first}} forloop.index: {{forloop.index
# }} forloop.index0: {{forloop.index0}} forloop.last: {{forloop.last}} forloop.length: {{forloop.length}} forloop.rindex: {{forloop.rindex}} forloop.rindex0: {{forloop.rindex0}} \n
# {% endfor %}")
# pp render({}, "{%for i in (116..121) reversed offset: 4 %}{{i}}:{%endfor%}")
# pp render({}, "{%for i in (221..116) reversed offset: 4 %}{{i}}:{%endfor%}")
# pp render({}, "{% for i in (1..nil) %}{{ i }}{% endfor %}")
 # # critical one:!!!
# pp render({}, "{% for i in (XYZ..7) %}{{ i }}{% endfor %}")
# assert_template_result(' 0  1  2  3 ', '{% for item in (a..3) %} {{item}} {% endfor %}', "a" => "invalid integer")
# pp render({"a" => "invalid integer"}, '{% for item in (a..3) %} {{item}} {% endfor %}')
# pp render({"empty" => [1,2,3] }, "{% for item in empty %} {{item}} {% endfor %}")
assertEqual(1, render({"num" => "1.2foo"}, "{{ num | round: 2 }}"))