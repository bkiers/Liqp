$is_Jekyll = false
begin
  require "jekyll"
  puts "testing cases using jekyll"
  $is_Jekyll = true
rescue LoadError
  require "liquid"
  puts "testing cases using liquid"
end

Liquid::Template.error_mode = :strict

def isJekyll
  $is_Jekyll
end

def assertEqual(expected, real)
  if expected != real
    raise "{#{real}} is not {#{expected}}"
  end
end

def assertTrue(real)
  raise "{#{real}} is not true" unless real
end

def assert_equal(expected, real)
  assertEqual(expected, real)
end


def assertRaise(&block)
  begin
    yield block
  rescue
    return
  end
  raise 'expected exception'
end


def assert_nil(arg)
  assert_equal nil, arg
end

if isJekyll

  def jekyllContext(overrides = {})
    config = Jekyll::Utils.deep_merge_hashes(Marshal.load(Marshal.dump(Jekyll::Configuration::DEFAULTS)), {
      "destination" => "dest",
      "incremental" => false,
      "includes_dir" => "cases_variable_inside_import/_includes/",
      "source" => ".",
      "skip_config_files" => true,
      "timezone" => "UTC",
      "url" => "http://example.com",
      "baseurl" => "/base",
      "disable_disk_cache" => true,
    })
    config = Jekyll::Utils.deep_merge_hashes(config, overrides)
    site = Jekyll::Site.new(Jekyll::Configuration.from(config))
    Liquid::Context.new({ }, {}, :site => site)
  end

  class JekyllFilter
    include Jekyll::Filters
    attr_accessor :site, :context

    def initialize(opts = {})
      @context = jekyllContext(opts)
      @site = @context.registers[:site]
    end
  end

  def make_filter_mock(opts = {})
    JekyllFilter.new(opts).tap do |f|
      tz = f.site.config["timezone"]
      Jekyll.set_timezone(tz) if tz
    end
  end


  def render(data, source, context = {})
    unless context.kind_of? Liquid::Context
      context = jekyllContext({})
    end
    context.scopes[0] = data
    Liquid::Template.parse(source, "strict_variables" => true).render!(context)
  end

else

  def render(data = {}, source)
    Liquid::Template.parse(source, {:strict_variables => true}).render!(data)
  end
end
