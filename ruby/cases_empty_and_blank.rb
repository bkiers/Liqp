#!/usr/bin/env ruby

require_relative '_helpers.rb'

assertEqual("  true  ", parse(" {% if array == empty %} true {% else %} false {% endif %} ").render!({"array" => []}, :strict_variables => true))
assertRaise do
  # because array dont support "blank" message
  assertEqual("  true  ", parse(" {% if array == blank %} true {% else %} false {% endif %} ").render!({"array" => []}, :strict_variables => true))
end
# but it do support the 'nil' message
assertEqual("  true  ", parse(" {% if array == nil %} false {% else %} true {% endif %} ").render!({"array" => []}, :strict_variables => true))

assertEqual(" empty=><=", render("{% assign a=empty     %} empty=>{{ a }}<="))
assertEqual(" blank=><=", render("{% assign a=blank     %} blank=>{{ a }}<="))

# equality verification
assertEqual('', render('{% if true == empty %}?{% endif %}'))
assertEqual('', render('{% if true == null %}?{% endif %}'))
assertEqual('', render('{% if true == blank %}?{% endif %}'))

assertEqual('', render('{% if empty == true %}?{% endif %}'))
assertEqual('', render('{% if empty == null %}?{% endif %}'))
assertEqual('', render('{% if empty == blank %}?{% endif %}'))

assertEqual('', render('{% if null == true %}?{% endif %}'))
assertEqual('', render('{% if null == empty %}?{% endif %}'))
assertEqual('', render('{% if null == blank %}?{% endif %}'))

assertEqual('', render('{% if blank == true %}?{% endif %}'))
assertEqual('', render('{% if blank == empty %}?{% endif %}'))
assertEqual('', render('{% if blank == null %}?{% endif %}'))

# conclusion:
# the 'blank?' not works out of box of liquid till the RoR is not loaded as this message support came from there
# https://stackoverflow.com/a/888877
# there is no chance that RoR will be ported to java
# so our templates will support this as a literal
# but will not mimic the RoR logic behind that


