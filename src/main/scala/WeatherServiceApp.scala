import cats.effect.{ExitCode, IO, IOApp, Resource}
import org.http4s.HttpRoutes
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.ErrorHandling

object WeatherServiceApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      appConfig <- AppConfig.fromDefaultApplicationResource

      _ <- makeClient.use { client =>

        val currentWeatherService = CurrentWeatherService(appConfig.openWeather, client)

        makeServer(appConfig.httpServer, currentWeatherService.routes).use { _ => IO.never }
      }
    } yield ExitCode.Success

  private def makeClient: Resource[IO, Client[IO]] =
    EmberClientBuilder
      .default[IO]
      .build

  private def makeServer(config: HttpServerConfig, routes: HttpRoutes[IO]): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(config.host)
      .withPort(config.port)
      .withHttpApp(ErrorHandling.httpRoutes(routes).orNotFound)
      .build
}
