#!/usr/bin/env ruby

require "jekyll"

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
site = Jekyll::Site.new(Jekyll::Configuration.from(config))
@context = Liquid::Context.new({}, {}, :site => site)

def render(data, source)
  @context.scopes[0] = data
  Liquid::Template.parse(source).render!(@context)
end

def assertEqual(expected, real)
  if expected != real
    raise "#{real} is not #{expected}"
  end
end


Liquid::Template.error_mode = :strict



res =  render({"site" => { "baseurl" => "/base"}}, "{{ '/some/path?with=extra&parameters=true#anchorhere' | relative_url }}");
assertEqual("/base/some/path?with=extra&parameters=true#anchorhere", res);

