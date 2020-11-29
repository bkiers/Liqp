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

pp render({ "array" => { "items" => [1,2,3,4,5,6,7,8,9] } }, "{%for i in array.items limit:8 %}{%endfor%}{% assign continue = 0 %}{%for i in array.items offset:continue %}{{i}}{%endfor%}")
# as we see here: continue works with ranges
pp render({ }, "{%for i in (1..9) limit:8 %}{%endfor%}{%for i in (1..9) offset:continue %}{{i}}{%endfor%}")
pp render({ }, "{%for i in (1..9) limit:8 %}{%endfor%}{% assign continue = 0 %}{%for i in (1..9) offset:continue %}{{i}}{%endfor%}")