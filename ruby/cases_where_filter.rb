#!/usr/bin/env ruby

require_relative '_helpers.rb'


if isJekyll
  filter = make_filter_mock(
      "timezone" => "UTC",
      "url" => "http://example.com",
      "baseurl" => "/base",
      "dont_show_posts_before" => @sample_time
  )

  array_of_objects = [
      {"color" => "teal", "size" => "large"},
      {"color" => "red", "size" => "large"},
      {"color" => "red", "size" => "medium"},
      {"color" => "blue", "size" => "medium"},
  ]

  # data = {
  #     "x" => [
  #         {
  #             "a" => {"aa" => 1},
  #             "b" => "jekyll match"
  #         }, {
  #             "a" => {},
  #             "b" => "liquid match"
  #         }
  #     ],
  #     "t" => {"aa" => 1}
  # }
  #
  # res = Liquid::Template.parse("{{ x | where: 'a', t | map: 'b' }}")
  #          .render(data)
  # assertEqual '', res

  data = {
      "x" => nil
  }

  res = Liquid::Template.parse("{{ x | where: 'a', t}}")
            .render(data)
  assertEqual '', res

  # should "return any input that is not an array" do
    assertEqual "some string", filter.where("some string", "la", "le")
  # end

  #
  # should "filter objects in a hash appropriately" do
    hash = {"a" => {"color" => "red"}, "b" => {"color" => "blue"}}
    assertEqual 1, filter.where(hash, "color", "red").length
    assertEqual [{"color" => "red"}], filter.where(hash, "color", "red")
  # end
  #
  #
  # should "filter objects appropriately" do
    filterRes = filter.where(array_of_objects, "color", "red")
    assertEqual 2, filterRes.length
  # end
  #
  # should "filter objects with null properties appropriately" do
    array = [{}, {"color" => nil}, {"color" => ""}, {"color" => "text"}]
    filterRes = filter.where(array, "color", nil)
    assertEqual 2, filterRes.length
  # end
  #
  # should "filter array properties appropriately" do
    hash = {
        "a" => {"tags" => %w(x y)},
        "b" => {"tags" => ["x"]},
        "c" => {"tags" => %w(y z)},
    }
    assertEqual 2, filter.where(hash, "tags", "x").length
  # end
  #
  # should "filter array properties alongside string properties" do
    hash = {
        "a" => {"tags" => %w(x y)},
        "b" => {"tags" => "x"},
        "c" => {"tags" => %w(y z)},
    }
    assertEqual 2, filter.where(hash, "tags", "x").length
  # end
  #
  # should "filter hash properties with null and empty values" do
    hash = {
        "a" => {"tags" => {}},
        "b" => {"tags" => ""},
        "c" => {"tags" => nil},
        "d" => {"tags" => ["x", nil]},
        "e" => {"tags" => []},
        "f" => {"tags" => "xtra"},
    }

    assertEqual [{"tags" => nil}], filter.where(hash, "tags", nil)

    assertEqual(
        [{"tags" => ""}, {"tags" => ["x", nil]}],
        filter.where(hash, "tags", "")
    )

    # `{{ hash | where: 'tags', empty }}`
    assertEqual(
        [{"tags" => {}}, {"tags" => ""}, {"tags" => nil}, {"tags" => []}],
        filter.where(hash, "tags", Liquid::Expression::LITERALS["empty"])
    )

    # `{{ `hash | where: 'tags', blank }}`
    assertEqual(
        [{"tags" => {}}, {"tags" => ""}, {"tags" => nil}, {"tags" => []}],
        filter.where(hash, "tags", Liquid::Expression::LITERALS["blank"])
    )
  # end
  #
  # should "not match substrings" do
  #   hash = {
  #       "a" => {"category" => "bear"},
  #       "b" => {"category" => "wolf"},
  #       "c" => {"category" => %w(bear lion)},
  #   }
  #   assert_equal 0, @filter.where(hash, "category", "ear").length
  # end
  #
  # should "stringify during comparison for compatibility with liquid parsing" do
    hash = {
        "The Words" => {"rating" => 1.2, "featured" => false},
        "Limitless" => {"rating" => 9.2, "featured" => true},
        "Hustle" => {"rating" => 4.7, "featured" => true},
    }

    results = filter.where(hash, "featured", "true")
    assertEqual 2, results.length
    assertEqual 9.2, results[0]["rating"]
    assertEqual 4.7, results[1]["rating"]

    results = filter.where(hash, "rating", 4.7)
    assertEqual 1, results.length
    assertEqual 4.7, results[0]["rating"]



  data = {"x" => hash }
  res = Liquid::Template.parse("{{ x | where: 'rating', 4.7 | first | last | map: 'rating' }}")
           .render(data)
  assertEqual '', res

  # end
  #
  # should "always return an array if the object responds to 'select'" do
  #   results = @filter.where(SelectDummy.new, "obj", "1 == 1")
  #   assert_equal [], results
  # end
  #

else

  pp 'start liquid section'

  class Filters
    include Liquid::StandardFilters
  end
  filters = Filters.new

  # def test_where
    input = [
        {"handle" => "alpha", "ok" => true},
        {"handle" => "beta", "ok" => false},
        {"handle" => "gamma", "ok" => false},
        {"handle" => "delta", "ok" => true},
    ]

    expectation = [
        {"handle" => "alpha", "ok" => true},
        {"handle" => "delta", "ok" => true},
    ]

    assert_equal(expectation, filters.where(input, "ok", true))
    assert_equal(expectation, filters.where(input, "ok"))
  # end

  # def test_where_no_key_set
    input = [
        {"handle" => "alpha", "ok" => true},
        {"handle" => "beta"},
        {"handle" => "gamma"},
        {"handle" => "delta", "ok" => true},
    ]

    expectation = [
        {"handle" => "alpha", "ok" => true},
        {"handle" => "delta", "ok" => true},
    ]

    assert_equal(expectation, filters.where(input, "ok", true))
    assert_equal(expectation, filters.where(input, "ok"))
  # end

  # def test_where_non_array_map_input
    assert_equal([{"a" => "ok"}], filters.where({"a" => "ok"}, "a", "ok"))
    assert_equal([], filters.where({"a" => "not ok"}, "a", "ok"))
  # end

  # def test_where_indexable_but_non_map_value
    assert_raises(Liquid::ArgumentError) { filters.where(1, "ok", true) }
    assert_raises(Liquid::ArgumentError) { filters.where(1, "ok") }
  # end

  # def test_where_non_boolean_value
    input = [
        {"message" => "Bonjour!", "language" => "French"},
        {"message" => "Hello!", "language" => "English"},
        {"message" => "Hallo!", "language" => "German"},
    ]

    assert_equal([{"message" => "Bonjour!", "language" => "French"}], filters.where(input, "language", "French"))
    assert_equal([{"message" => "Hallo!", "language" => "German"}], filters.where(input, "language", "German"))
    assert_equal([{"message" => "Hello!", "language" => "English"}], filters.where(input, "language", "English"))
  # end

  # def test_where_array_of_only_unindexable_values
    assert_nil(filters.where([nil], "ok", true))
    assert_nil(filters.where([nil], "ok"))
  # end

  # def test_where_no_target_value
    input = [
        {"foo" => false},
        {"foo" => true},
        {"foo" => "for sure"},
        {"bar" => true},
    ]

    assert_equal([{"foo" => true}, {"foo" => "for sure"}], filters.where(input, "foo"))
  # end

  # this not work by design
  begin
    res = Liquid::Template.parse("{% assign items = x | where: 'foo'  | sort: 'foo' %}" +
                                     "{% for item in items %}" +
                                     "{{ item.foo }}" +
                                     "{% endfor %}")
              .render({"x" => input})
    assertEqual '2truefor sure', res
  rescue
    #
  end

end
