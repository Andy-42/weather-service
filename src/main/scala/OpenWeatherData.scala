import cats.data.NonEmptyList
import io.circe.Decoder
import io.circe.generic.semiauto._

import scala.annotation.unused

// Bindings for a subset of the OpenWeather One Call API 3.0 response

case class Current(feels_like: Double, weather: NonEmptyList[Weather])

case class Weather(main: String, description: String)

case class Alert(event: String, description: String, tags: List[String])

case class OneCallResponse(current: Current, alerts: Option[List[Alert]]) {

  def toCurrentWeather: CurrentWeather =
    CurrentWeather(
      conditions = current.weather.head.main,

      temperature =
        if (current.feels_like > 30) "Hot"
        else if (current.feels_like > 25) "Warm"
        else if (current.feels_like > 20) "Pleasant"
        else if (current.feels_like > 10) "Cool"
        else if (current.feels_like > 0) "Cold"
        else "Freezing",

      alerts = alerts.fold(List.empty[WeatherAlert])(
        _.map(alert =>
          WeatherAlert(
            event = alert.event,
            description = alert.description,
            conditions = alert.tags
          )
        )
      )
    )
}

case class CurrentWeather(
                           conditions: String, // weather conditions in that area (snow, rain, etc)
                           temperature: String, // whether it’s hot, cold, or moderate outside
                           alerts: List[WeatherAlert] // alerts, if any
                         )

case class WeatherAlert(event: String, description: String, conditions: List[String])


object OpenWeatherDataDecode {
  @unused private implicit val weatherDecoder: Decoder[Weather] = deriveDecoder
  @unused private implicit val currentDecoder: Decoder[Current] = deriveDecoder
  @unused private implicit val windDecoder: Decoder[Alert] = deriveDecoder

  implicit val oneCallApiResponseDecoder: Decoder[OneCallResponse] = deriveDecoder[OneCallResponse]
}
