#!/usr/bin/env ruby

require 'pp'
require_relative '_helpers.rb'
require 'date'

# pp "new date: %s" % Date.new
# pp "new Time: %s" % Time.new
# pp "now Time: %s" % Time.now

# pp render({ "a" => Time.now }, "{{ a | date: '%Y'}}")

tn = Time.now
t = Time.new(2007,11,1,15,25,0, "+09:00")
t_str = t.to_s
pp "time: " + Time.parse("Aug 28 02:55:50 68").strftime('%Y-%m-%d %H:%M:%S')
pp "time: " + Time.parse("Aug 28 02:55:50 69").strftime('%Y-%m-%d %H:%M:%S')
pp "time: " + Time.parse("Sun, 06 Nov 94 08:49 GMT").strftime('%Y-%m-%d %H:%M:%S %z')
pp "time: " + Time.parse(" 01/03 ").strftime('%Y-%m-%d %H:%M:%S %z')
pp "time: " + Time.parse("3/2024").strftime('%Y-%m-%d %H:%M:%S %z')
pp "time: " + Time.parse(" 11 december, 10").strftime('%Y-%m-%d %H:%M:%S %z')
pp "time: " + Time.parse(" 11 ,december 10").strftime('%Y-%m-%d %H:%M:%S %z')
pp "time: " + Time.parse(" 11 ;december, 10").strftime('%Y-%m-%d %H:%M:%S %z')
pp "time: " + Time.parse(" 11 ,december; 10").strftime('%Y-%m-%d %H:%M:%S %z')
pp "time: " + Time.parse(" 11 =december, 10").strftime('%Y-%m-%d %H:%M:%S %z')
pp "time: " + Time.parse(" 11 ,december= 10").strftime('%Y-%m-%d %H:%M:%S %z')
pp "time: " + Time.parse(" 11 -december, 10").strftime('%Y-%m-%d %H:%M:%S %z')
pp "time: " + Time.parse(" 11 ,december- 10").strftime('%Y-%m-%d %H:%M:%S %z')

# abs filter ignores fact the time is numeric
# assertTrue(render({"a" => Time.now}, "{{ a | append: 'k' }}").end_with?("k"))
# assertEqual("0", render({"a" => Time.now}, "{{ a | abs }}"))
# assertEqual("-1", render({"a" => Time.now}, "{{ a | minus: 1 }}"))
# assertEqual("0.3", render({ }, "{{  '0.2' | plus: '0.1' | abs }}"))
# assertEqual("size of object: 2", render({ "a" => {"a" => 1, "b" => 2}}, "size of object: {{  a | size }}"))
# assertEqual("size of date: 0", render({"a" => Time.now}, "size of date: {{ a | size }}"))
# assertEqual("times: 6.3", render({ }, "times: {{ '2.1' | times:3 }}"))
# assertEqual("times date: 0", render({"a" => Time.now}, "times date: {{ a | times:3 }}"))

# assertEqual("true(2021-02-10 03:49:51 +0200)", render({"a" => Time.parse("2021-02-10 03:49:51"), "b" => Time.parse("2021-02-10 03:49:52")}, "{% if a and b %}true({{ a and b }}){% else %} false({{ a and b }}) {% endif %}"))
# assertEqual("yes", render({ "a" => Time.now, "b" => Time.now - 90*24*60*60 }, "{% if a >= b %}yes{% else %}no{% endif %}"));
# assertEqual("true", render({"true" => "bad"}, "{{true}}"))
# assertEqual("false", render({"false" => "bad"}, "{{false}}"))
# assertEqual("", render({"nil" => "bad"}, "{{nil}}"))
# assertEqual("", render({"null" => "bad"}, "{{null}}"))
# assertEqual("", render({"empty" => "bad"}, "{{empty}}"))
# assertEqual("", render({"blank" => "bad"}, "{{blank}}"))
# assertEqual("2007-11-01...", render({"a" => t }, "{{ a | truncate: 13 }}"))

if isJekyll
  pp render({"a" => '2004-12-31'}, "{{ a | date: '%Y-%m-%d %H:%M:%S %z'}}") # "2004-12-31 00:00:00 +0200"
  pp render({"a" => '31 December'}, "{{ a | date: '%Y-%m-%d %H:%M:%S %z'}}") # "2025-12-31 00:00:00 +0200"
  pp render({"a" => '12:00'}, "{{ a | date: '%Y-%m-%d %H:%M:%S %z'}}") # "2025-01-04 12:00:00 +0200"
  pp render({"a" => 'Friday'}, "{{ a | date: '%Y-%m-%d %H:%M:%S %z'}}") # Friday
  pp render({"a" => 'Friday 12/24'}, "{{ a | date: '%Y-%m-%d %H:%M:%S %z'}}") # "2025-12-24 00:00:00 +0200"
  pp render({"a" => '2004-12-31 11:23:58 Z'}, "{{ a | date: '%Y-%m-%d %H:%M:%S %z'}}") # "2004-12-31 11:23:58 +0000"
  pp render({"a" => 'September 1969'}, "{{ a | date: '%Y-%m-%d %H:%M:%S %z'}}") # "1969-09-01 00:00:00 +0300"
  pp render({"a" => '06 Nov 04'}, "{{ a | date: '%Y-%m-%d %H:%M:%S %z'}}") # "2004-11-06 00:00:00 +0200"
  pp render({"a" => '1994-11-06T08'}, "{{ a | date: '%Y-%m-%d %H:%M:%S %z'}}") # "1994-11-06 00:00:00 +0200"
  pp render({"a" => 'Sun, 06 Nov 24 08:49:37 GMT'}, "{{ a | date: '%Y-%m-%d %H:%M:%S %z %a'}}") # if default century is 20 then weekday wrong

  # target is string representation, source is iterated as collection(and so = match in "year" part)
  assertEqual("target is string representation: 2007-11-01 15:25:00 +0900", render({"a" => [{ "time" => t }], "b" => "2007"}, "target is string representation: {{ a | where: 'time', b | map: 'time'}}"))



  assertEqual(Time.now.to_s, render({"a" => [{ "time" => tn }], "b" => tn.year.to_s}, "{{ a | where: 'time', b | map: 'time'}}"))
  assertRaise do
    # time is not inspectable in ruby too...
    # well, that is a bit more complicated:
    # we iterate date as array of integers and strings (parts)
    # and for each part we do index operator function with search property,
    # which is always missing
    # and so - is not resolve given property(till they are not
    # numbers themself - in that case we do access bit at "index" position
    #
    render({"a" => t, "b" => "2007"}, "target is string representation: {{ a | where: 'time', b | map: 'time'}}")
  end

  # source is string representation that match the date
  assertEqual("source is string representation: 2007-11-01 15:25:00 +0900", render({"a" => [{ "time" => t_str }], "b" => t},
                                                                                   "source is string representation: {{ a | where: 'time', b | map: 'time'}}"))

else # liquid
  assertEqual("where in liquid respect only object equality : 2007-11-01 15:25:00 +0900", render({"a" => [{ "time" => t }], "b" => t}, "where in liquid respect only object equality : {{ a | where: 'time', b | map: 'time'}}"))
  # same error: time is represented as array, but property reading from it fails
  # pp render({"a" => t, "b" => t}, "where in liquid respect only object equality : {{ a | where: 'time', b | map: 'time'}}")
end
