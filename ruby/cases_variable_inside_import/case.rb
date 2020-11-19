#!/usr/bin/env ruby

require 'pp'

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

if isJekyll
  config = Jekyll::Utils.deep_merge_hashes(Marshal.load(Marshal.dump(Jekyll::Configuration::DEFAULTS)), {
      "destination" => "dest",
      "incremental" => false,
      "includes_dir" => "cases_variable_inside_import/_includes/",
      "source" => ".",
      "skip_config_files" => true,
      "timezone" => "UTC",
      "url" => "http://example.com",
      "baseurl" => "/base",
      "disable_disk_cache" => true
  })
  @site = Jekyll::Site.new(Jekyll::Configuration.from(config))
  @context = Liquid::Context.new({}, {}, :site => @site)

  def render(data = {}, source)
    Liquid::Template.parse(source, {:strict_variables => true}).render!(@context, data);
  end

else
  Liquid::Template.file_system = Liquid::LocalFileSystem.new("cases_variable_inside_import/_includes/", "%s.liquid")

  def render(data = {}, source)
    Liquid::Template.parse(source, {:strict_variables => true}).render!(data);
  end
end

def assertEqual(expected, real)
  if expected != real
    raise "#{real} is not #{expected}"
  end
end

# liquid requires template without extension
#
# tested: Decrement, Increment, Cycle, TableRow, ifchanged
# things to test: tags: , , For, ,
# visibility of variables from "with expression",
if isJekyll
  assertEqual("variable", render({}, "{% assign var = 'variable' %}{% include include_read_var.liquid %}"));
  assertEqual("incl_var", render({}, "{% include include_create_new_var.liquid %}{{ incl_var }}"))

  # Like increment, variables declared inside decrement are independent from variables created through assign or capture.
  assertEqual("[-1,0][-2,1][-3,2][-3, 3]", render({}, "[{% decrement var1 %},{% increment var2 %}][{% include include_decrement_var.liquid %}][{% decrement var1 %},{% increment var2 %}][{{ var1 }}, {{ var2 }}]"))
  assertEqual("1234", render({}, "{% cycle 1,2,3,4 %}{% assign list = \"1\" | split: \",\" %}{% for n in list %}{% cycle 1,2,3,4 %}{% endfor %}{% cycle 1,2,3,4 %}{% include include_cycle.liquid %}"))
  assertEqual("12--><--3", render({}, "{% ifchanged %}1{% endifchanged %}{% ifchanged %}2{% endifchanged %}{% include include_ifchanged.liquid %}{% ifchanged %}3{% endifchanged %}"))
else
  assertEqual("variable", render({}, "{% assign var = 'variable' %}{% include 'include_read_var' %}"));
  assertEqual("incl_var", render({}, "{% include 'include_create_new_var' %}{{ incl_var }}"))
  assertEqual("[-1,0][-2,1][-3,2][-3, 3]", render({}, "[{% decrement var1 %},{% increment var2 %}][{% include 'include_decrement_var' %}][{% decrement var1 %},{% increment var2 %}][{{ var1 }}, {{ var2 }}]"))
  assertEqual("1234", render({}, "{% cycle 1,2,3,4 %}{% assign list = \"1\" | split: \",\" %}{% for n in list %}{% cycle 1,2,3,4 %}{% endfor %}{% cycle 1,2,3,4 %}{% include 'include_cycle' %}"))
  assertEqual("12--><--3", render({}, "{% ifchanged %}1{% endifchanged %}{% ifchanged %}2{% endifchanged %}{% include 'include_ifchanged' %}{% ifchanged %}3{% endifchanged %}"))
end