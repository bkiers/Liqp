
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

assertEqual("123", render({}, "{% cycle 1,2,3 %}{% assign list = \"1\" | split: \",\" %}{% for n in list %}{% cycle 1,2,3 %}{% endfor %}{% cycle 1,2,3 %}"))
