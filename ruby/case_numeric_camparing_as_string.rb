#!/usr/bin/env ruby

require_relative '_helpers.rb'

# pp render({}, "{% assign comparingValue = 98.0 %}{{ 98 > comparingValue }}")
# assertEqual("true", render({}, "{% assign comparingValue = 98.0 %}{{ '98' == comparingValue }}"))

      # product of two operators of types:
      # number
      # string
      # true
      # false
      # null
      # with operators:
      # >
      # >=
      # <
      # <=
      # ==
      # !=

      resArray = []
      arr = [98, '97', true, false, nil]
      ops = ['>', '>=', '<', '<=', '==', '!=']
      arr.product(arr).product(ops).each do |pair|
        operator = pair[1]
        first = pair[0][0]
        second = pair[0][1]
        begin
          # just trigger calculation to raise exception if it will be
          first.method(operator).(second)
          # res = first.public_send(operator, second)

          if first.class == String then first = "'#{first}'" end
          if first === nil then first = 'nil' end
          if second.class == String then second = "'#{second}'" end
          if second === nil then second = 'nil' end
          res = render({}, "{% if #{first} #{operator} #{second} %}true{% else %}false{% endif %}")
          if res === 'true' then resArray.push(1) else resArray.push(0) end
          pp "#{first} #{operator} #{second} = #{res}"
        rescue Exception => e
          if first.class == String then first = "'#{first}'" end
          if first === nil then first = 'nil' end
          if second.class == String then second = "'#{second}'" end
          if second === nil then second = 'nil' end
          resArray.push('e')
          pp "#{first} #{operator} #{second} = ERROR: #{e.message[/.*/]}"
        end

      end

      pp resArray.join('')

# assertRaise do
#   assertEqual("true", render({}, "{% if 98 > '98' %}true{% else %}false{% endif %}"))
# end
# assertEqual(" false ", render({}, "{% if null <= 0 %} true {% else %} false {% endif %}"))
# assertEqual("no", render({}, "{% if 42.1 >= false %}yes{% else %}no{% endif %}"))
# assertEqual("no", render({}, "{% if true > false %}yes{% else %}no{% endif %}"))
# assertEqual("no", render({}, "{% if true < false %}yes{% else %}no{% endif %}"))
