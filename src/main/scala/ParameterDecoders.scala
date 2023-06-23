import cats.syntax.either._
import org.http4s.dsl.io.ValidatingQueryParamDecoderMatcher
import org.http4s.{ParseFailure, QueryParamDecoder}

object GeoDecoders {

  val latitudeDecoder: QueryParamDecoder[Double] =
    QueryParamDecoder.doubleQueryParamDecoder
      .emap(lat =>
        if (lat < -90 || lat > 90)
          ParseFailure(
            sanitized = "lat must be between -90 and 90",
            details = s"Range check lat parameter $lat as a latitude"
          ).asLeft
        else
          lat.asRight
      )

  val longitudeDecoder: QueryParamDecoder[Double] =
    QueryParamDecoder.doubleQueryParamDecoder
      .emap(lon =>
        if (lon < -180 || lon > 180)
          ParseFailure(
            sanitized = "lon must be between -180 and 180",
            details = s"Range check lon parameter: $lon"
          ).asLeft
        else
          lon.asRight
      )
}

object LatitudeQueryParamMatcher
  extends ValidatingQueryParamDecoderMatcher[Double]("lat")(GeoDecoders.latitudeDecoder)

object LongitudeQueryParamMatcher
  extends ValidatingQueryParamDecoderMatcher[Double]("lon")(GeoDecoders.longitudeDecoder)
