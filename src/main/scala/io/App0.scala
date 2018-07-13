package io

import scala.io.StdIn

object App0 {
  def main(ars: Array[String]): Unit = {
    println("What is your name?" + ars)
    val name = StdIn.readLine()
    println("Hello, " + name + ", welcome to the game!")
    var exec = true
    while (exec) {
      val num = scala.util.Random.nextInt(5) + 1
      println("Dear " + name + ", please guess a number from 1 to 5:" + num)
      val guess = StdIn.readLine().toInt
      if (guess == num) println("You guessed right, " + name + "!")
      else println("You guessed wrong, " + name + "! The number was: " + num)
      println("Do you want to continue, " + name + "?")
      StdIn.readLine() match {
        case "y" => exec = true
        case "n" => exec = false
      }
    }
  }
}
