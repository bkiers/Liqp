#!/usr/bin/env ruby


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

assertEqual("-1-2-2", render({}, "{% decrement var %}{% decrement var %}{{ var }}"))
assertEqual("0152", render({}, "{% increment var %}{% assign var=5 %}{% increment var %}{{ var }}{% increment var %}"))