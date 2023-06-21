# Weather Service Project

## Objective

The brief for this project was:

> Write an HTTP server that uses the Open Weather API that exposes an endpoint that takes in lat/long coordinates. 
This endpoint should return the weather conditions in that area (snow, rain, etc), 
whether it’s hot, cold, or moderate outside (use your own discretion on what temperature equates to each type), 
and whether there are any weather alerts in that area along with the weather conditions related to the alert.
The API can be found here: https://openweathermap.org/api. 
The one-call api returns all the data while the other apis are piece-mealed sections.
You may also find the https://openweathermap.org/faq useful.

## Running the Service

The service requires an Open Weather API Key that has the One Call API 3.0 subscription.
This is defined in the `application.conf` resource file as an environment variable:

``API_KEY=ecfdbdd56ffe97cfccdf2fc1d741ecd3``

To run the service:
- Define the API_KEY environment variable.
- Run the entry point `WeatherServiceApp` (i.e., in the root package).
- Do an HTTP GET on service entry point (e.g., browser, Postman).

For example, a GET using the following URL:

``http://localhost:8080/current-weather?lat=43.677&lon=-79.402``

...will produce a JSON entity similar to:

```json
{
    "conditions": "Clear",
    "temperature": "Pleasant",
    "alerts": []
}
```

## Implementation

This service is implemented using Cats Effect, Circe, http4s and PureConfig.
The [Open Weather One Call 3.0](https://openweathermap.org/api/one-call-3) is
used to provide the weather data.

The implementation uses the IO effect type directly (i.e., not tagless final)
to keep the example as simple as possible.

All the code and resources are in the root package. 
The main Scala implementation files in the project are:
* **AppConfig** - Decoding and validating configuration using PureConfig.
* **CurrentWeatherService** - The implementation of the `weather-service` route that
retrieves data from the Open Weather One Call API service and transforms it to the
the service's current weather summary representation.
* **OpenWeatherData** - The binding of (a subset of) the Open Weather One Call data
to case class representation, as well as a transformation of that representation to
the result format.
* **ParameterDecoders** - Decoders for the `lat` and `lon` query string parameters.
* **WeatherServiceApp** - The entry point that instantiates the HTTP Server.

## Limitations

The error handling is rudimentary, and an obvious next step would be to add an
error handler. For example, the `lat` and `lon` query string parameters are validated,
but without an error handler, the validation messages are not propagated back to the
caller. Similarly, internal errors (which would not be returned to the caller) are
not logged.

There is limited unit testing: a single unit test in `OpenWeatherDataSpec`.
This is useful for documenting this project's interpretation of the data returned
by the Open Weather One Call API, and the transformed version of this data that is
returned as the `current-weather` service result. While this is the most interesting
bit to unit test, it is not a complete suite of unit tests.

This project is not intended to show best practices for a RESTful API.
