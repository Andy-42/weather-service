import cats.effect._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.dsl.io._
import org.http4s.{EntityDecoder, HttpRoutes, Method, Request, Response}

case class CurrentWeatherService(config: OpenWeatherConfig, client: Client[IO]) {

  private val oneCallApiUri = config.oneCallApiUri
    .withQueryParam("units", "metric")
    .withQueryParam("lang", "en")
    .withQueryParam("appid", config.apiKey)
    .withQueryParam("exclude", "minutely,hourly,daily")

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "current-weather" :? LongitudeQueryParamDecoder(lon) +& LatitudeQueryParamMatcher(lat) =>

      val oneCallApiRequest = Request[IO](
        method = Method.GET,
        uri = oneCallApiUri.withQueryParam("lat", lat).withQueryParam("lon", lon)
      )

      import OpenWeatherDataDecode.oneCallApiResponseDecoder
      implicit val oneCallApiResponseEntityDecoder: EntityDecoder[IO, OneCallResponse] = jsonOf[IO, OneCallResponse]

      Ok(client.expect[OneCallResponse](oneCallApiRequest).map(_.toCurrentWeather.asJson))
  }
}
