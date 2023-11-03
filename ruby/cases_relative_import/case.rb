#!/usr/bin/env ruby

require_relative '../_helpers.rb'

assertEqual("Welcome back Dear Reader!", render({}, "{% include_relative snippets/welcome_para.md %}"))
