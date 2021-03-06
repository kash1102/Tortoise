// (C) Uri Wilensky. https://github.com/NetLogo/Tortoise

package org.nlogo.tortoise
package dock

import org.nlogo.core.{ Model, View }

class TestBreeds extends DockingSuite {
  test("create breeds") { implicit fixture => import fixture._
    declare("""| breed [mice mouse]
               | breed [frogs frog]""".stripMargin)
    testCommand("output-print count frogs")
    testCommand("output-print count turtles")
    testCommand("create-turtles 1")
    testCommand("create-frogs 1")
    testCommand("output-print count frogs")
    testCommand("output-print count turtles")
  }

  test("is breed", org.scalatest.Tag("foo")) { implicit fixture => import fixture._
    declare("""| breed [mice mouse]
               | breed [frogs frog]
               | globals [glob1]""".stripMargin)
    testCommand("create-turtles 1")
    testCommand("output-print is-frog? turtle 0")
    testCommand("create-frogs 1")
    testCommand("output-print is-frog? turtle 1")
    testCommand("output-print is-mouse? turtle 1")
    testCommand("set glob1 turtle 1")
    testCommand("output-print is-frog? glob1")
  }

  test("breed shapes") { implicit fixture => import fixture._
    declare("""| breed [frogs frog] breed [mice mouse]""".stripMargin)
    testCommand("""set-default-shape turtles "sheep" """)
    testCommand("create-turtles 1")
    testCommand("create-frogs 1")
    testCommand("""set-default-shape frogs "WOLF" """)
    testCommand("create-turtles 1")
    testCommand("create-frogs 1")
    testCommand("create-mice 1")
    testCommand("""set-default-shape turtles "Square" """)
    testCommand("create-turtles 1")
    testCommand("create-frogs 1")
    testCommand("create-mice 1")
    compare("[ shape ] of turtles")
  }

  test("breed hatch") { implicit fixture => import fixture._
    declare("""| breed [frogs frog]""".stripMargin)
    testCommand("""set-default-shape turtles "SHEEP" """)
    testCommand("create-turtles 2")
    testCommand("""set-default-shape frogs "wolf" """)
    testCommand("create-frogs 2")
    testCommand("ask turtles [ hatch 3 ]")
    testCommand("ask frogs [ hatch 5 ]")
    testCommand("output-print count turtles")
    testCommand("output-print count frogs")
    testCommand("ask turtles [ hatch-frogs 3 ]")
    testCommand("output-print count turtles")
    testCommand("output-print count frogs")
  }

  test("breed sprout") { implicit fixture => import fixture._
    declare("""| breed [frogs frog]""".stripMargin)
    testCommand("ask patches [ sprout 1 ]")
    testCommand("ask patches [ sprout-frogs 1 ]")
  }

  test("hatch other variables") { implicit fixture => import fixture._
    declare("""| turtles-own [energy]""".stripMargin)
    testCommand("create-turtles 1")
    testCommand("ask turtles [ output-print energy ]")
    testCommand("ask turtles [ set energy 9 ]")
    testCommand("ask turtles [ output-print energy ]")
    testCommand("ask turtles [ hatch 1 ]")
    testCommand("ask turtles [ output-print energy ]")
  }

  test("breed here") { implicit fixture => import fixture._
    declare(Model(code = """| breed [mice mouse]
                            | breed [frogs frog]""".stripMargin,
                  widgets = List(View.square(1))))
    testCommand("create-frogs 40")
    testCommand("create-mice 40")
    compare("count mice")
    compare("count frogs")
    compare("[ [ who ] of mice-here ] of frogs")
    compare("[ [ who ] of frogs-here ] of mice")
    compare("[ [ who ] of mice-here ] of mice")
    testCommand("ask mice [ fd 2 ]")
    compare("[ [ who ] of mice-here ] of mice")
    compare("[ [ who ] of frogs-here ] of mice")
    testCommand("ask frogs [ fd 2 ]")
    compare("[ [ who ] of mice-here ] of frogs")
    compare("[ [ who ] of frogs-here ] of mice")
  }

  test("set breed") { implicit fixture => import fixture._
    declare("""| breed [mice mouse]
               | breed [frogs frog]""".stripMargin)
    testCommand("""set-default-shape mice "sheep" """)
    testCommand("create-mice 1")
    testCommand("""set-default-shape frogs "WOLF" """)
    testCommand("create-frogs 1")
    testCommand("ask turtle 0 [ set breed frogs ]")
    testCommand("output-print count frogs output-print count mice")
    testCommand("ask turtle 0 [ set breed mice ]")
    testCommand("output-print count frogs output-print count mice")
    testCommand("ask turtle 1 [ set breed frogs ]")
    testCommand("output-print count frogs output-print count mice")
    testCommand("ask turtle 1 [ set breed mice ]")
    testCommand("output-print count frogs output-print count mice")
  }

  test("breeds own") { implicit fixture => import fixture._
    declare("""| breed [mice mouse]
               | mice-own [ cheese ]""".stripMargin)
    testCommand("create-mice 1")
    compare("[ cheese ] of mouse 0")
    testCommand("ask mouse 0 [ set cheese 2 ]")
    compare("[ cheese ] of mouse 0")
    testCommand("ask mouse 0 [ set cheese \"Hello\" ]")
    compare("[ cheese ] of mouse 0")
  }

  test("breeds own change") { implicit fixture => import fixture._
    declare("""| breed [mice mouse]
               | breed [cats cat]
               | mice-own [ cheese life ]
               | cats-own [ food life ]""".stripMargin)
    testCommand("create-mice 1")
    testCommand("create-cats 1")
    testCommand("ask turtle 0 [ set cheese 2 set life 4]")
    compare("[ cheese ] of turtle 0")
    compare("[ food ] of turtle 1")
    compare("[ life ] of turtle 0")
    testCommand("ask turtle 0 [ set breed cats ]")
    testCommand("ask mice [ output-print life ]")
    testCommand("ask cats [ output-print life ]")
    testCommand("ask cats [ output-print food ]")
  }

  test("breed equality") { implicit fixture => import fixture._
    declare("breed [mice mouse]")
    testCommand("create-mice 3 ask [ breed ] of turtle 0 [ output-print who ]")
    testCommand("ask turtle 0 [ output-print breed = turtles ]")
    testCommand("create-mice 3 crt 2 ask turtle 0 [ output-print breed = turtles ]")
    testCommand("crt 3 ask turtle 0 [ set color red ] ask turtle 0 [ output-print breed = turtles with [ color != red ]]")
  }

  test("breed storage") {implicit fixture => import fixture._
    declare("""| breed [ foos foo ]
               | globals [ bar ]""".stripMargin)
    testCommand("create-foos 20")
    testCommand("set bar foos")
    testCommand("ask foos with [ who > 10 ] [ die ]")
    testCommand("output-print count bar")
  }
}
