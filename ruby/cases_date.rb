#!/usr/bin/env ruby

require 'pp'
require_relative '_helpers.rb'

pp "new date: %s" % Date.new
pp "new Time: %s" % Time.new
pp "now Time: %s" % Time.now

pp render({ "a" => Time.now }, "{{ a | date: '%Y'}}")

tn = Time.now
t = Time.new(2007,11,1,15,25,0, "+09:00")
t_str = t.to_s
# target is string representation, source is iterated as collection(and so = match in "year" part)
# pp render({"a" => [{ "time" => t }], "b" => "2007"}, "target is string representation: {{ a | where: 'time', b | map: 'time'}}")
# pp render({"a" => [{ "time" => tn }], "b" => tn.year.to_s}, "target is string representation: {{ a | where: 'time', b | map: 'time'}}")
pp render({"a" => t, "b" => "2007"}, "target is string representation: {{ a | where: 'time', b | map: 'time'}}")


# source is string representation that match the date
pp render({"a" => [{ "time" => t_str }], "b" => t}, "source is string representation: {{ a | where: 'time', b | map: 'time'}}")

