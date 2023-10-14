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
//          resArray = []
//          arr = [98, '98', true, false, nil]
//          ops = ['>', '>=', '<', '<=', '==', '!=']
//          arr.product(arr).product(ops).each do |pair|
//            operator = pair[1]
//            first = pair[0][0]
//            second = pair[0][1]
//            begin
//
//              if first.class == String then first = "'#{first}'" end
//              if first === nil then first = 'nil' end
//              if second.class == String then second = "'#{second}'" end
//              if second === nil then second = 'nil' end
//              res = render({}, "{% if #{first} #{operator} #{second} %}true{% else %}false{% endif %}")
//              if res === 'true' then resArray.push(1) else resArray.push(0) end
//              # pp "#{first} #{operator} #{second} = #{res}"
//            rescue Exception => e
//              if first.class == String then first = "'#{first}'" end
//              if first === nil then first = 'nil' end
//              if second.class == String then second = "'#{second}'" end
//              if second === nil then second = 'nil' end
//              resArray.push('e')
//              # pp "#{first} #{operator} #{second} = ERROR: #{e.message[/.*/]}"
//            end
//
//          end
//
//          pp resArray.join('')

    // 010110eeee01000001000001000001eeee01010110000001000001000001000001000001000010000001000001000001000001000001000010000001000001000001000001000001000010


    @Test
    public void testCombinationsStrict() {

        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.JEKYLL)
                .withStrictTypedExpressions(true)
                .build();


        String expected = "010110eeee01000001000001000001eeee01010110000001000001000001000001000001000010000001000001000001000001000001000010000001000001000001000001000001000010";
        testCombunationsStrctWithExpectation(expected, parser);
    }

    // js code for the same:
    // const f = (a, b) => [].concat(...a.map(d => b.map(e => [].concat(d, e))));
    //const cartesian = (a, b, ...c) => (b ? cartesian(f(a, b), ...c) : a);
    //const isString = (el) => (typeof el === 'string' || el instanceof String);
    //const wrapString = (str) => isString(str) ?  '\'' + str + '\'' : str;
    //
    //resArray = [];
    //arr = [98, '97', true, false, null];
    //ops = ['>', '>=', '<', '<=', '==', '!=']
    //
    //cartesian(arr, arr, ops).forEach(combo => {
    //    var operator = combo[2];
    //    var first = combo[0];
    //    var second = combo[1];
    //    var res;
    //    try {
    //        switch (operator) {
    //            case '>':
    //                resArray.push();
    //                res = first >  second ? 1 : 0;
    //                break;
    //            case '>=':
    //                res = first >= second ? 1 : 0;
    //                break;
    //            case '<':
    //                res = first < second ? 1 : 0;
    //                break;
    //            case '<=':
    //                res = first <= second ? 1 : 0;
    //                break;
    //            case '==':
    //                res = first == second ? 1 : 0;
    //                break;
    //            case '!=':
    //                res = first != second ? 1 : 0;
    //                break;
    //        }
    //    } catch(e) {
    //        res = 'e';
    //    }
    //    resArray.push(res);
    //    console.log(wrapString(first) + ' ' + operator + ' ' + wrapString(second) + ' = ' + res);
    //
    //})
    //console.log(resArray.join(''))
    @Test
    public void testCombinationsNonStrict() {

        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.JEKYLL)
                .withStrictTypedExpressions(false)
                .build();

        // non-strict version does not have errors in it
        String expected = "010110110001110001110001110001001101010110110001110001110001001101001101010110110001110001001101001101001101010110010101001101001101001101010101010110";
        testCombunationsStrctWithExpectation(expected, parser);
    }

    private void testCombunationsStrctWithExpectation(String expected, TemplateParser parser) {
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

    @Test
    public void testStringComparisonAlphabetically() {
        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.JEKYLL)
                .withEvaluateInOutputTag(true)
                .withStrictTypedExpressions(false)
                .build();

        String res = parser.parse("{{'98' > '197'}}").render();
        assertEquals("true", res);
    }


    @Test
    public void testBugNo286Case1() {
        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.JEKYLL)
                .build();

        String res = parser.parse("{%- assign value = 5.0 | round: 1 -%} {%- if value >= 4.0 and value <= 4.2 -%} Très bien {%- elsif value >= 4.3 and value <= 4.7 -%} Superbe {%- elsif value >= 4.8 and value <= 4.9 -%} Fabuleux {%- elsif value != 5.0 -%} Exceptionnel {% endif %}").render();

        assertEquals("", res.trim());
    }

    @Test
    public void testBugNo286Case2() {
        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.JEKYLL)
                .build();

        String res = parser.parse("{%- assign value = 5.0 | round: 1 -%} {%- if value >= 4.0 and value <= 4.2 -%} Très bien {%- elsif value >= 4.3 and value <= 4.7 -%} Superbe {%- elsif value >= 4.8 and value <= 4.9 -%} Fabuleux {%- elsif value == 5.0 -%} Exceptionnel {% endif %}").render();

        assertEquals("Exceptionnel", res.trim());
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