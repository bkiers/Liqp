#!/usr/bin/env ruby

require 'pp'
require_relative '_helpers.rb'

# pp render({"val" => "here"}, "{{ val }}")

if isJekyll
  context = jekyllContext("includes_dir" => "cases_variable_inside_import/_includes")
  # Tag 'render' is not defined in Jekyll. Use 'include' or 'include_relative'
  assertRaise do
    assertEqual("TEST", render({}, "{% render color.liquid %}", context))
  end
else
  Liquid::Template.file_system = Liquid::LocalFileSystem.new("cases_variable_inside_import/_includes", "%s.liquid")

  # liquid template name must be a quoted string
  assertRaise do
    assertEqual("there", render({"var" => "there", "tmpl" => "include_read_var"}, "{% render tmpl %}"))
  end
  # render variables
  assertEqual("", render({"var" => "there"}, "{% render 'include_read_var' %}"))
  assertEqual("here", render({"var" => "there"}, "{% render 'include_read_var', var: 'here' %}"))
  assertEqual("there", render({"var" => "there"}, "{% render 'include_read_var', var: var %}"))
  assertEqual("color: ''", render({"color" => "red"}, "{% render 'color' %}"))
  assertEqual("color: 'blue'", render({"color" => "red"}, "{% render 'color', color: 'blue' %}"))
  assertEqual("color: 'red'", render({"color" => "red"}, "{% render 'color', color: color %}"))
  # render with
  assertEqual("color: 'red'", render({"var" => "there"}, "{% render 'color' with 'red' %}"))
  assertEqual("color: 'red'", render({"var" => "there"}, "{% render 'color' with 'red', color: 'blue' %}"))
  assertEqual("color: 'blue'", render({"clr" => "blue"}, "{% render 'color' with clr %}"))
  assertEqual("color: 'blue'", render({"clr" => "blue"}, "{% render 'color' with clr, color: 'green' %}"))
  # render for
  assertEqual("color: 'r'color: 'g'color: 'b'", render({"colors" => ["r", "g", "b"]}, "{% render 'color' for colors %}"))
  assertEqual("rgb", render({"colors" => ["r", "g", "b"]}, "{% render 'include_read_var' for colors as var %}"))
  assertEqual("foofoofoo", render({"colors" => ["r", "g", "b"]}, "{% render 'include_read_var' for colors as clr, var: 'foo' %}"))
end

