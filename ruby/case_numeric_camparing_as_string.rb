#!/usr/bin/env ruby

require_relative '_helpers.rb'

# pp render({}, "{% assign comparingValue = 98.0 %}{{ 98 > comparingValue }}")
# assertEqual("true", render({}, "{% assign comparingValue = 98.0 %}{{ '98' == comparingValue }}"))

assertRaise do
  assertEqual("true", render({}, "{% if 98 > '98' %}true{% else %}false{% endif %}"))
end
assertEqual(" false ", render({}, "{% if null <= 0 %} true {% else %} false {% endif %}"))
assertEqual("no", render({}, "{% if 42.1 >= false %}yes{% else %}no{% endif %}"))
assertEqual("no", render({}, "{% if true > false %}yes{% else %}no{% endif %}"))
assertEqual("no", render({}, "{% if true < false %}yes{% else %}no{% endif %}"))
