#!/usr/bin/env ruby

require 'pp'
require_relative '_helpers.rb'

pp "new date: %s" % Date.new
pp "new Time: %s" % Time.new
pp "now Time: %s" % Time.now

pp render({ "a" => Time.now }, "{{ a | date: '%Y'}}")
pp render({ "a" => Time.now }, "{{ a.isdst }}")
