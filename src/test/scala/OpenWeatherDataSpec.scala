import cats.data.NonEmptyList
import cats.implicits._
import io.circe.parser.decode
import org.scalatest.funsuite.AnyFunSuite

class OpenWeatherDataSpec extends AnyFunSuite {

  val eventDescription = "FLOOD ADVISORY IN EFFECT UNTIL 130 PM EDT THIS AFTERNOON..."

  val sampleOneCallAPIResponse: String =
    s"""
       |{
       |    "lat": 33.9206,
       |    "lon": -80.3417,
       |    "timezone": "America/New_York",
       |    "timezone_offset": -14400,
       |    "current": {
       |        "dt": 1687279686,
       |        "sunrise": 1687255831,
       |        "sunset": 1687307715,
       |        "temp": 22.76,
       |        "feels_like": 23.36,
       |        "pressure": 1010,
       |        "humidity": 87,
       |        "dew_point": 20.48,
       |        "uvi": 7.73,
       |        "clouds": 20,
       |        "visibility": 10000,
       |        "wind_speed": 2.57,
       |        "wind_deg": 60,
       |        "weather": [
       |            {
       |                "id": 801,
       |                "main": "Clouds",
       |                "description": "few clouds",
       |                "icon": "02d"
       |            }
       |        ]
       |    },
       |    "alerts": [
       |        {
       |            "sender_name": "NWS Columbia (Central South Carolina)",
       |            "event": "Flood Advisory",
       |            "start": 1687277880,
       |            "end": 1687299300,
       |            "description": "$eventDescription",
       |            "tags": [
       |                "Flood"
       |            ]
       |        }
       |    ]
       |}""".stripMargin

  test("Should be able to decode sample One Call API data and transform it to a CurrentWeather") {

    import OpenWeatherDataDecode.oneCallApiResponseDecoder

    val expected = OneCallResponse(
      current = Current(
        feels_like = 23.36,
        weather = NonEmptyList.one(
          Weather(
            main = "Clouds",
            description = "few clouds"
          )
        )
      ),
      alerts = Some(
        List(
          Alert(
            event = "Flood Advisory",
            description = eventDescription,
            tags = List("Flood")
          )
        )
      )
    ).asRight

    val expectedTransformed = CurrentWeather(
      conditions = "Clouds",
      temperature = "Pleasant",
      alerts = List(
        WeatherAlert(
          event = "Flood Advisory",
          description = eventDescription,
          conditions = List("Flood")
        )
      )
    ).asRight

    val actualDecoded = decode[OneCallResponse](sampleOneCallAPIResponse)
    val actualTransformed = actualDecoded.map(_.toCurrentWeather)

    assert(actualDecoded == expected)
    assert(actualTransformed == expectedTransformed)
  }
}
