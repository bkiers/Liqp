
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
    raise "#{real} is not #{expected}"
  end
end

def assert_equal(expected, real)
  assertEqual(expected, real)
end


def assertRaise(&block)
  begin
    block.call
  rescue
    return
  end
  raise 'expected exception'
end

def assert_raises *exp
  raise "assert_raises requires a block to capture errors." unless block_given?

  msg = "#{exp.pop}.\n" if String === exp.last
  exp << StandardError if exp.empty?

  begin
    yield
  rescue *exp => e
    return e
  rescue Minitest::Skip, Minitest::Assertion
    # don't count assertion
    raise
  rescue SignalException, SystemExit
    raise
  rescue Exception => e
    raise e
  end
  raise "expected exception  but nothing was raised."
end

def assert_nil(arg)
  assert_equal nil, arg
end

def parse(source, options = {})
  Liquid::Template.parse(source, options)
end

def render(source, data = {}, options = {:strict_variables => true})
  parse(source).render!(data, options);
end


if isJekyll

  def default_configuration
    Marshal.load(Marshal.dump(Jekyll::Configuration::DEFAULTS))
  end

  def root_dir(*subdirs)
    File.expand_path(File.join("..", *subdirs), __dir__)
  end

  def test_dir(*subdirs)
    root_dir("test", *subdirs)
  end

  def source_dir(*subdirs)
    test_dir("source", *subdirs)
  end

  def dest_dir(*subdirs)
    test_dir("dest", *subdirs)
  end

  def build_configs(overrides, base_hash = default_configuration)
    Jekyll::Utils.deep_merge_hashes(base_hash, overrides)
  end

  def site_configuration(overrides = {})
    full_overrides = build_configs(overrides, build_configs(
        "destination" => dest_dir,
        "incremental" => false
    ))
    Jekyll::Configuration.from(full_overrides.merge(
        "source" => source_dir
    ))
  end

  class JekyllFilter
    include Jekyll::Filters
    attr_accessor :site, :context

    def initialize(opts = {})
      @site = Jekyll::Site.new(opts.merge("skip_config_files" => true))
      @context = Liquid::Context.new(@site.site_payload, {}, :site => @site)
    end
  end

  class Value
    def initialize(value)
      @value = value
    end

    def to_s
      @value.respond_to?(:call) ? @value.call : @value.to_s
    end
  end

  def make_filter_mock(opts = {})
    JekyllFilter.new(site_configuration(opts)).tap do |f|
      tz = f.site.config["timezone"]
      Jekyll.set_timezone(tz) if tz
    end
  end

end
