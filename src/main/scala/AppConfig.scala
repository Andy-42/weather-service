import cats.effect.IO
import cats.implicits._
import com.comcast.ip4s.{Host, Port}
import org.http4s.{ParseFailure, Uri}
import pureconfig._
import pureconfig.error._
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

import scala.annotation.unused

case class AppConfig(httpServer: HttpServerConfig, openWeather: OpenWeatherConfig)

case class HttpServerConfig(host: Host, port: Port)

case class OpenWeatherConfig(oneCallApiUri: Uri, apiKey: String)

object AppConfig {

  @unused
  private implicit val hostReader: ConfigReader[Host] = ConfigReader.fromCursor[Host] { cur =>
    cur.asString.flatMap { str =>
      Host.fromString(str)
        .fold[ConfigReader.Result[Host]](cur.failed(CannotConvert(str, "Host", s"$str is not a valid Host")))(_.asRight)
    }
  }

  @unused
  private implicit val portReader: ConfigReader[Port] = ConfigReader.fromCursor[Port] { cur =>
    cur.asString.flatMap { str =>
      Port.fromString(str)
        .fold[ConfigReader.Result[Port]](cur.failed(CannotConvert(str, "Port", s"$str is not a valid Port")))(_.asRight)
    }
  }

  @unused
  private implicit val uriReader: ConfigReader[Uri] = ConfigReader.fromCursor[Uri] { cur =>
    cur.asString.flatMap { str =>
      Uri.fromString(str)
        .fold(parseFailure => cur.failed(CannotConvert(str, "Uri", parseFailure.message)), _.asRight)
    }
  }


  @unused
  private implicit def hint[A]: ProductHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, KebabCase))

  val fromDefaultApplicationResource: IO[AppConfig] = ConfigSource.defaultApplication.loadF[IO, AppConfig]()
}
