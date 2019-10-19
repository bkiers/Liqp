begin
  require "jekyll"
  puts "testing cases using jekyll"
rescue LoadError
  require "liquid"
  puts "testing cases using liquid"
end

Liquid::Template.error_mode = :strict

def assertEqual(expected, real)
  if expected != real
    raise
  end
end

def assertRaise(&block)
  begin
    block.call
  rescue
    return
  end
  raise 'expected exception'
end


def parse(source, options = {})
  Liquid::Template.parse(source, options)
end

def render(source, data = {}, options = {:strict_variables => true})
  parse(source).render!(data, options);
end
