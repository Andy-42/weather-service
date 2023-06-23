import cats.data.Validated.{Invalid, Valid}
import cats.effect._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, Method, Request}

case class CurrentWeatherService(config: OpenWeatherConfig, client: Client[IO]) {

  private val oneCallApiUri = config.oneCallApiUri
    .withQueryParam("units", "metric")
    .withQueryParam("lang", "en")
    .withQueryParam("appid", config.apiKey)
    .withQueryParam("exclude", "minutely,hourly,daily")

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "current-weather" :? LongitudeQueryParamMatcher(lon) +& LatitudeQueryParamMatcher(lat) =>

      (lat, lon) match {
        case (Valid(lat), Valid(lon)) =>
          val oneCallApiRequest = Request[IO](
            method = Method.GET,
            uri = oneCallApiUri.withQueryParam("lat", lat).withQueryParam("lon", lon)
          )

          import OpenWeatherDataDecode.oneCallApiResponseDecoder

          client
            .expect[OneCallResponse](oneCallApiRequest)
            .flatMap { oneCallResponse => Ok(oneCallResponse.toCurrentWeather.asJson) }

        case (Valid(_), Invalid(e)) => BadRequest(e.head.sanitized)
        case (Invalid(e), Valid(_)) => BadRequest(e.head.sanitized)
        case (Invalid(e1), Invalid(e2)) => BadRequest(e1.head.sanitized + "\n" + e2.head.sanitized)
      }
  }
}
