import cats.effect._
import cats.syntax.all._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, Method, Request, Response}

case class CurrentWeatherService(config: OpenWeatherConfig, client: Client[IO]) {

  private val oneCallApiUri = config.oneCallApiUri
    .withQueryParam("units", "metric")
    .withQueryParam("lang", "en")
    .withQueryParam("appid", config.apiKey)
    .withQueryParam("exclude", "minutely,hourly,daily")

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "current-weather" :?
      LongitudeQueryParamMatcher(lonParam) +& LatitudeQueryParamMatcher(latParam) =>

      (latParam, lonParam)
        .mapN(currentWeather)
        .valueOr { parseFailures =>
          IO(Response(status = BadRequest).withEntity(parseFailures.toList.map(_.sanitized).mkString("\n")))
        }
  }

  private def currentWeather(lat: Double, lon: Double): IO[Response[IO]] = {

    val oneCallApiRequest = Request[IO](
      method = Method.GET,
      uri = oneCallApiUri.withQueryParam("lat", lat).withQueryParam("lon", lon)
    )

    import OpenWeatherDataDecode.oneCallApiResponseDecoder

    client
      .expect[OneCallResponse](oneCallApiRequest)
      .flatMap(oneCallResponse => Ok(oneCallResponse.toCurrentWeather.asJson))
  }
}
