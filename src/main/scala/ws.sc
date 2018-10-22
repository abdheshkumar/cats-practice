import cats.{Applicative, ApplicativeError, Functor, Id, Monad, MonadError}
import cats.mtl.{ApplicativeAsk, MonadState}
import cats.implicits._

sealed trait TempUnit
case object Celcius extends TempUnit
case object Fahern extends TempUnit


case class Temperature(
                        value:Int,
                        unit:TempUnit = Celcius)

case class Forcast(temperature:Temperature)
case class City(name:String)

class WeatherClient(host:String, port:Int){
  def forcast(city:City):Forcast= city match{
    case City("London") => Forcast(Temperature(28))
    case City("Chennai") => Forcast(Temperature(34))
  }
}

case class Config(host:String, port:Int)
sealed trait Error
case class UnknownCity(city:String) extends Error

trait Console[F[_]]{
  def readLine:F[String]
  def printLn(line:String):F[Unit]
}

object Console{
  def apply[F[_]](implicit n: Console[F]): Console[F] = n

}

trait Weather[F[_]]{
  def forcast(city: City):F[Forcast]
}

type Requests = Map[City, Forcast]
object Requests{
  def empty:Requests = Map.empty[City, Forcast]
  def hottest(requests:Requests):(City, Forcast)= ???
}

type ConfigAsk[F[_]] = ApplicativeAsk[F, Config]
type ErrorHandler[F[_]] = MonadError[F, Error]
type RequestState[F[_]] = MonadState[F, Requests]

def host[F[_]:ConfigAsk]:F[String] = implicitly[ConfigAsk[F]].reader(_.host)
def port[F[_]:ConfigAsk]:F[Int] = implicitly[ConfigAsk[F]].reader(_.port)

def cityByName[F[_]:ErrorHandler](cityName:String):F[City] = cityName match{
  case "London" => City(cityName).pure[F]
  case "Chennai" => City(cityName).pure[F]
  case _ => implicitly[ErrorHandler[F]].raiseError(UnknownCity(cityName))
}

def hottestCity[F[_]:RequestState]:F[(City, Temperature)]={
  implicitly[RequestState[F]].inspect(requests => {
    val result = Requests.hottest(requests)
    (result._1, result._2.temperature)
  })
}

def askCity[F[_]:Console : Monad]:F[String]={
  for{
    _ <- implicitly[Console[F]].printLn("Whats the next city")
    cityName <- implicitly[Console[F]].readLine
  }yield cityName
}

def fetchForcast[F[_]:Weather:RequestState:Monad](city: City):F[Forcast]={
  for{
    maybeForcast <- implicitly[RequestState[F]].inspect(_.get(city))
    forcast <- maybeForcast match{
      case Some(r) => r.pure[F]
      case None => implicitly[Weather[F]].forcast(city)
    }
    _<- implicitly[RequestState[F]].modify(_ + (city -> forcast))
  }yield forcast
}


def askFetchJudge[F[_]: Console : Weather : RequestState : ErrorHandler]:F[Unit]={
  for{
    cityName <- askCity[F]
    city <- cityByName[F](cityName)
    forcast <- fetchForcast[F](city)
    _ <- Console[F].printLn(s"forcast for $city is ${forcast.temperature}")
    hottest <- hottestCity[F]
    _ <- Console[F].printLn(s"hottest city found so far is $hottest")
  }yield ()

}