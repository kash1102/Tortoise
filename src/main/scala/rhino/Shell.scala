// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.tortoise
package rhino

import org.nlogo.{ api, workspace }

object Shell extends workspace.Shell {

  object Defaults {
    val src = ""
    val dim = api.WorldDimensions.square(16)
  }

  val rhino = new Rhino

  def main(argv: Array[String]) {
    workspace.AbstractWorkspace.setHeadlessProperty()
    val (js, program, procedures) =
      Compiler.compileProcedures(
        Defaults.src, dimensions = Defaults.dim)
    rhino.eval(js)
    System.err.println("Tortoise Shell 1.0")
    for(line <- input.takeWhile(!isQuit(_)))
      printingExceptions {
        run(Compiler.compileCommands(line, procedures, program))
      }
  }

  def run(js: String) {
    printingExceptions {
      val (output, json) = rhino.run(js)
      Seq(output) // , json)
        .filter(_.nonEmpty)
        .foreach(x => println(x.trim))
    }
  }

  private def printingExceptions(body: => Unit) {
    try body
    catch { case e: Exception => println(e) }
  }

}