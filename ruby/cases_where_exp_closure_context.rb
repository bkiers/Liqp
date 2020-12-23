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

# simple case
assertEqual("12", render({ "var" => [ {"key" => 1}, {"key" => 12}]}, "{{ var | where_exp: 'item', 'item.key == 12' | map: 'key'}}"))
# global var
assertEqual("good", render({ "var" => [ {"key" => 1, "marker" => "wrong"}, {"key" => 12, "marker" => "good"}]}, "{% assign key = 12 %}{{ var | where_exp: 'item', 'item.key == key' | map: 'marker'}}"))
# local var
assertEqual("good", render({ "var" => [ {"key" => 1, "marker" => "wrong"}, {"key" => 12, "marker" => "good"}]}, "{% for ii in (12..12) %}{{ var | where_exp: 'item', 'item.key == ii' | map: 'marker'}}{% endfor %}"))
# complex objects
assertEqual("good", render({ "var" => [
  {"key" => 1, "marker" => "wrong"},
  {"key" => 12, "marker" => "good"}
], "groups" => [11,12,13]}, "{{ var | where_exp: 'item', 'groups contains item.key' | map: 'marker'}}"))
