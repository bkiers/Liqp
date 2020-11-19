begin
  require "jekyll"
rescue LoadError
  require "liquid"
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

pp render({}, "{% ifchanged %}1{% endifchanged %}{%for item in (1..4) %}{% ifchanged %}{{ item }}{% endifchanged %}{% endfor %}{% ifchanged %}4{% endifchanged %}{% ifchanged %}5{% endifchanged %}")
assertEqual("12345", render({}, "{% ifchanged %}1{% endifchanged %}{%for item in (1..4) %}{% ifchanged %}{{ item }}{% endifchanged %}{% endfor %}{% ifchanged %}4{% endifchanged %}{% ifchanged %}5{% endifchanged %}"))