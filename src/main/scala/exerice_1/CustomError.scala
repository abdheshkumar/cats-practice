package exerice_1

sealed trait CustomError extends Error

case object CustomError1 extends CustomError

case object CustomError2 extends CustomError
