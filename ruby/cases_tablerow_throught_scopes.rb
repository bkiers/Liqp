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


assertEqual("<tr class=\"row1\">\n" +
                "<td class=\"col1\">id1</td><td class=\"col2\">id2</td></tr>\n" +
                "-->[id1]<--\n" +
                "<tr class=\"row1\">\n" +
                "<td class=\"col1\">id1</td><td class=\"col2\">id2</td></tr>\n" +
                "-->[id2]<--\n",
            render({"array" => [{"id"=>"id1"}, {"id"=>"id2"}]}, "{% for item in array %}{% tablerow item in array%}{{ item.id }}{% endtablerow %}-->[{{ item.id }}]<--\n{% endfor %}"))

assertEqual("<tr class=\"row1\">\n" +
                "<td class=\"col1\">id1</td><td class=\"col2\">id2</td></tr>\n",
            render({"array" => [{"id"=>"id1"}, {"id"=>"id2"}]}, "{% tablerow item in array%}{{ item.id }}{% endtablerow %}{{ item.id }}"))
