import cats.syntax.either._
import org.http4s.dsl.io.QueryParamDecoderMatcher
import org.http4s.{ParseFailure, QueryParamDecoder}

object LatitudeQueryParamMatcher extends QueryParamDecoderMatcher[String]("lat") {

  implicit val latitudeQueryParamDecoder: QueryParamDecoder[String] =
    QueryParamDecoder[Double]
      .emap(lon =>
        if (lon < -90 || lon > 90)
          ParseFailure(
            sanitized = "lat must be a double between -90 and 90",
            details = s"Range check lat parameter $lon as a latitude"
          ).asLeft
        else
          lon.toString.asRight
      )
}

object LongitudeQueryParamDecoder extends QueryParamDecoderMatcher[String]("lon") {

  implicit val longitudeQueryParamDecoder: QueryParamDecoder[String] =
    QueryParamDecoder[Double]
      .emap(lat =>
        if (lat < -180 || lat > 180)
          ParseFailure(
            sanitized = "lon must be a double between -180 and 180",
            details = s"Range check lon parameter: $lat"
          ).asLeft
        else
          lat.toString.asRight
      )
}
