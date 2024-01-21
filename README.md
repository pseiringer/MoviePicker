[https://github.com/pseiringer/MoviePicker](https://github.com/pseiringer/MoviePicker)

# Credits

This app uses [The Movie DB](https://www.themoviedb.org/) as its primary data source.

![alt text](https://github.com/pseiringer/MoviePicker/blob/main/tmdb_logo_short.svg)


# Project Structure

The source code for the MoviePicker Android app is in the folder "MoviePicker".

In order to run, the app needs the ASP.NET Core Backend from the folder "MoviePickerBackend". For more information see [Setup](#setup).

The folder "Presentation" contains a Powerpoint/PDF presentation demonstrating the app.

# Setup

## Hosting the Backend

To create a docker image containing the backend, move into the folder "MoviePickerBackend\MoviePickerBackend" (the folder which contains the "dockerfile"). Rebuild the image using the following command:

`docker build -t movie-picker-backend .`

Then run a container based on the image using:

`docker container run -d -p 5001:80 --name movie-picker movie-picker-backend`

If you already have a movie-picker container running with a wrong version of the backend you may need to stop and remove it before running the new container.

## Connecting to the Backend/API

In the folder "MoviePicker", make sure you have a file called "local.apikey.properties". Create it if it does not exist.

In local.apikey.properties you must create the following properties:

```
TMDBBaseUrl="https://api.themoviedb.org/3/"
TMDBToken="XXX"
BackendBaseUrl="http://YYY:5001/api/"
```

Replace "XXX" with the API Read Access Token from TMDB (https://www.themoviedb.org/). If you do not have an Access Token, you can register for a free account.

Replace "YYY" with the hostname or IP address of your backend host.