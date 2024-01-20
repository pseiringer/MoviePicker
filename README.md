# Read Me

## Hosting the Backend

To create a docker image containing the backend, move into the folder "MoviePickerBackend\MoviePickerBackend" (the folder which contains the "dockerfile").
Rebuild the image using the following command:

`docker build -t movie-picker-backend .`

Then run a container based on the image using:

`docker container run -d -p 5001:80 --name movie-picker movie-picker-backend`

If you already have a movie-picker container running with a wrong version of the backend you may need to stop and remove it before running the new container.

## Connecting to the Backend

In the folder "MoviePicker", make sure you have a file called "local.apikey.properties". Create it if it does not exist.

In local.apikey.properties you must create the following properties:

```
TMDBBaseUrl="https://api.themoviedb.org/3/"
TMDBToken="XXX"
BackendBaseUrl="http://YYY:5001/api/"
```

Replace "XXX" with the API Read Access Token from TMDB (https://www.themoviedb.org/).
If you do not have an Access Token, you can register for a free account.

Replace "YYY" with the hostname or IP address of your backend host.