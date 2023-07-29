#!/usr/bin/env ruby

require_relative '_helpers.rb'

# pp render({}, "{% assign comparingValue = 98.0 %}{{ 98 > comparingValue }}")
# assertEqual("true", render({}, "{% assign comparingValue = 98.0 %}{{ '98' == comparingValue }}"))

assertEqual("true", render({}, "{% if 98 > '98' %}true{% else %}false{% endif %}"))
