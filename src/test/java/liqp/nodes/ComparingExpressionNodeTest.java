package liqp.nodes;

import liqp.TemplateParser;
import liqp.parser.Flavor;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ComparingExpressionNodeTest {

    /**
     * this is ruby code that create product of all types and operators. the order is predictable
     * so lets code result as single string
     * where 1 is true, 0 is false and e is error
     */
    //      resArray = []
    //      arr = [98, '98', true, false, nil]
    //      ops = ['>', '>=', '<', '<=', '==', '!=']
    //      arr.product(arr).product(ops).each do |pair|
    //        operator = pair[1]
    //        first = pair[0][0]
    //        second = pair[0][1]
    //        begin
    //          # just trigger calculation to raise exception if it will be
    //          first.method(operator).(second)
    //          # res = first.public_send(operator, second)
    //
    //          if first.class == String then first = "'#{first}'" end
    //          if first === nil then first = 'nil' end
    //          if second.class == String then second = "'#{second}'" end
    //          if second === nil then second = 'nil' end
    //          res = render({}, "{% if #{first} #{operator} #{second} %}true{% else %}false{% endif %}")
    //          if res === 'true' then resArray.push(1) else resArray.push(0) end
    //          # pp "#{first} #{operator} #{second} = #{res}"
    //        rescue Exception => e
    //          if first.class == String then first = "'#{first}'" end
    //          if first === nil then first = 'nil' end
    //          if second.class == String then second = "'#{second}'" end
    //          if second === nil then second = 'nil' end
    //          resArray.push('e')
    //          # pp "#{first} #{operator} #{second} = ERROR: #{e.message[/.*/]}"
    //        end
    //
    //      end
    //
    //      pp resArray.join('')

    // 010110eeee01eeee01eeee01eeee01eeee01010110eeee01eeee01eeee01eeee01eeee01eeee10eeee01eeee01eeee01eeee01eeee01eeee10eeee01eeee01eeee01eeee01eeee01eeee10


    @Test
    public void testCombinations() {

        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.JEKYLL)
                .build();


        String expected = "010110eeee01eeee01eeee01eeee01eeee01010110eeee01eeee01eeee01eeee01eeee01eeee10eeee01eeee01eeee01eeee01eeee01eeee10eeee01eeee01eeee01eeee01eeee01eeee10";
        List<List<Serializable>> cases = cartesianProduct(Arrays.asList(
                Arrays.asList(98, "97", true, false, null),
                Arrays.asList(98, "97", true, false, null),
                // ['>', '>=', '<', '<=', '==', '!=']
                Arrays.asList(">", ">=", "<", "<=", "==", "!=")
        ));
        // iterate each char in expected string:
        for (int i = 0; i < expected.length(); i++) {
            List<Serializable> pair = cases.get(i);
            String operator = (String) pair.get(2);
            Serializable first = pair.get(0);
            if ("97".equals(first)) first = "'97'";
            if (first == null) first = "nil";
            Serializable second = pair.get(1);
            if ("97".equals(second)) second = "'97'";
            if (second == null) second = "nil";
            String expectedRes = expected.substring(i, i + 1);
            switch (expectedRes) {
                case "0":
                    expectedRes = "false";
                    break;
                case "1":
                    expectedRes = "true";
                    break;
                case "e":
                    expectedRes = "error";
                    break;
            }
            String res = null;
            try {
                res = parser.parse("{% if " + first + " " + operator + " " + second + " %}true{% else %}false{% endif %}").render();
            } catch (Exception e) {
                res = "error";
            }
            String errorMessage = String.format("%s %s %s = %s, but was %s" , first, operator, second, expectedRes, res);
            assertEquals(errorMessage, expectedRes, res);
        }

    }

    // from https://stackoverflow.com/questions/714108/cartesian-product-of-an-arbitrary-number-of-sets
    protected <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> resultLists = new ArrayList<List<T>>();
        if (lists.isEmpty()) {
            resultLists.add(new ArrayList<T>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<T>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }
}