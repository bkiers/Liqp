#!/usr/bin/env ruby

require 'pp'
require_relative '_helpers.rb'

# pp render({"val" => "here"}, "{{ val }}")

if isJekyll
  context = jekyllContext("includes_dir" => "cases_variable_inside_import/_includes")
  # jekyll throw on `with` syntax
  assertRaise do
    render({"var" => "there"}, "{% include 'color' with 'red' %}", context)
  end
  # VARIABLE_SYNTAX example
  assertEqual("TEST", render({ "var" => "TEST", "tmpl" => "include_read_var.liquid"}, "{% include {{ tmpl }} %}", context))
  # jekyll variable evaluate is wrong
  assertRaise do
    render({"var" => "there", "tmpl" => "include_read_var.liquid"}, "{% include tmpl %}", context)
  end
  # jekyll regular case
  assertEqual("TEST", render({ "var" => "TEST"}, "{% include include_read_var.liquid %}", context))
  # jekyll regular case with proper string is wrong
  assertRaise do
    render({ "var" => "TEST"}, "{% include 'include_read_var.liquid' %}", context)
  end
  assertEqual("TEST", render({ "var" => "TEST"}, "{% include include_read_var.liquid param='value' %}", context))
  # space is a separator, so even if file exists, this syntax not allow to see it
  assertRaise do
    render({ "var" => "TEST"}, "{% include impossible include.liquid param='value' %}", context)
  end

else
  Liquid::Template.file_system = Liquid::LocalFileSystem.new("cases_variable_inside_import/_includes", "%s.liquid")
  assertEqual("color: 'red'", render({"var" => "there"}, "{% include 'color' with 'red' %}"))
  assertEqual("color: 'blue'", render({"clr" => "blue"}, "{% include 'color' with clr %}"))
  assertEqual("color: 'yellow'", render({}, "{% assign clr = 'yellow' %}{% include 'color' with clr %}"))
  # liquid variable evaluate
  assertEqual("there", render({"var" => "there", "tmpl" => "include_read_var"}, "{% include tmpl %}"))
end

