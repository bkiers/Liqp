## About this folder

This folder is a playground where developers can test original liquid/jekyll code with real ruby via docker machines without need of installing ruby on local machine, as well as these libraries.

### How to use
Simply write your ruby script and run it with predefined runners. 

Run in jekyll: ` ./run_with_jekyll.sh your_script.rb`

Run in liquid: ` ./run_with_liquid.sh your_script.rb`

In fact, this may be same script, just unsure yourself you are safe in both cases:
```ruby

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

# write your code here

if isJekyll
  # add jekyll-specific code here
else
  # add liquid-specific code here
end
```

For more complex tuning of *jekyll* environment, where the creation of `site` variable needed, here`s example how you can do that:
```ruby

config = Jekyll::Utils.deep_merge_hashes(Marshal.load(Marshal.dump(Jekyll::Configuration::DEFAULTS)), {
      "destination" => "dest",
      "incremental" => false,
      "includes_dir" => "_includes",
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
```

*Liquid* is more simple one:
```ruby
Liquid::Template.file_system = Liquid::LocalFileSystem.new("_includes/", "%s.liquid")

def render(data = {}, source)
  Liquid::Template.parse(source, {:strict_variables => true}).render!(data);
end
```
