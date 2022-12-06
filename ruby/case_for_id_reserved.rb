#!/usr/bin/env ruby

require_relative '_helpers.rb'

assertEqual("123", render({}, "{% for offset in (1..3) %}{{ offset }}{% endfor %}"))
assertEqual("123", render({}, "{% for capture in (1..3) %}{{ capture }}{% endfor %}"))
assertEqual("123", render({}, "{% for raw in (1..3) %}{{ raw }}{% endfor %}"))
assertEqual("123", render({}, "{% for else in (1..3) %}{{ else }}{% endfor %}"))


# assertRaise do
#   # because array dont support "blank" message
#   assertEqual("  true  ", parse(" {% if array == blank %} true {% else %} false {% endif %} ").render!({"array" => []}, :strict_variables => true))
# end