import cats.effect.IO
import cats.implicits._
import com.comcast.ip4s.{Host, Port}
import org.http4s.Uri
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
      Host.fromString(str) match {
        case Some(host) => host.asRight
        case None => cur.failed(CannotConvert(str, "Host", s"$str is not a valid Host"))
      }
    }
  }

  @unused
  private implicit val portReader: ConfigReader[Port] = ConfigReader.fromCursor[Port] { cur =>
    cur.asString.flatMap { str =>
      Port.fromString(str) match {
        case Some(port) => port.asRight
        case None => cur.failed(CannotConvert(str, "Port", s"$str is not a valid Port"))
      }
    }
  }

  @unused
  private implicit val uriReader: ConfigReader[Uri] = ConfigReader.fromCursor[Uri] { cur =>
    cur.asString.flatMap { str =>
      Uri.fromString(str) match {
        case Right(uri) => uri.asRight
        case Left(parseFailure) =>
          cur.failed(CannotConvert(str, "Uri", s"$str is not a valid Uri: ${parseFailure.message}"))
      }
    }
  }

  @unused
  private implicit def hint[A]: ProductHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, KebabCase))

  val fromDefaultApplicationResource: IO[AppConfig] = ConfigSource.defaultApplication.loadF[IO, AppConfig]()
}
