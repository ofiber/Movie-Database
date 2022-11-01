package cs249.finalProject;

// Name: Owen Fiber
// Course: CS249
// Date: 04/20/21
// Desc: Defines MovieDBHandler class for use in TMDBTester Project
//       implements Movie class and HttpConnectionHandler

// Imports ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
// Imports ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

public class MovieDBHandler implements Serializable
{
    // Private Data Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private ArrayList<Movie> movieList;
    private static ArrayList<String> castList;
    private HttpConnectionHandler handler = new HttpConnectionHandler();
    // Private Data Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Default constructor
    public MovieDBHandler()
    {
        System.out.println("Created");

        // Create new ArrayList of type Movie
        movieList = new ArrayList<Movie>();
    }

    public void ListMovies()
    {
        // Method iterates through movieList ArrayList
        // to output all Movie objects in list

        Iterator<Movie> iterator = movieList.iterator();

        while(iterator.hasNext())
        {
            Movie mov = iterator.next();

            System.out.println(mov.getMovieTitle());
        }
    }

    public void AddMovie(JsonObject movie)
    {
        // AddMovie method
        System.out.println(movie.get("title"));

        Movie movieOBJ = new Movie();

        // Set movie id
        movieOBJ.setMovieID(Integer.parseInt(movie.get("id").toString().replace("\"", "")));

        // Set movie title
        movieOBJ.setMovieTitle(movie.get("title").toString().replace("\"", ""));

        // Set movie description
        movieOBJ.setMovieDesc(movie.get("overview").toString().replace("\"", ""));

        // Set movie's poster path
        movieOBJ.setPosterPath(movie.get("poster_path").toString().replace("\"", ""));

        // Set movie's release date
        movieOBJ.setRelDate(movie.get("release_date").toString().replace("\"", ""));

        // Set movie's average vote count
        movieOBJ.setVoteAvg(movie.get("vote_average").getAsDouble());

        // Set movie's total vote count
        movieOBJ.setVoteCount(movie.get("vote_count").getAsInt());

        // Get movie's id, store as string
        String id = movie.get("id").getAsString();

        String movieCast = null;


        try
        {
            // Get movie cast Json
            movieCast = handler.getInformation("https://api.themoviedb.org/3/movie/" + id + "/credits?api_key=2960a8435920b05de4baa37a54f3b79d&language=en-US");

        } catch (IOException e)
        {
            // Catch exceptions

            // Location of error
            System.out.println("IN MOVIEDBHANDLER");
            System.out.println("Cast List Retrieval Error: IO Exception");
        }

        // New Json parser
        JsonParser parser = new JsonParser();

        // Parse movieCast json to Json element
        JsonElement jsonElement = parser.parse(movieCast);

        // New json array for movie cast list
        JsonArray jsonArray1 = jsonElement.getAsJsonObject().getAsJsonArray("cast");

        // New ArrayList of type String
        castList = new ArrayList<String>();

        // For each element in Json array
        // Store cast member name in castList
        for (JsonElement x: jsonArray1)
        {
            castList.add(x.getAsJsonObject().get("name").getAsString());
        }

        // Set cast list of current movie object
        movieOBJ.setCastList(castList);

        // Add movie object to movie list
        movieList.add(movieOBJ);
    }

    public void RemoveMovie(Integer id)
    {
        // Method to remove movie from movieList
        int x = -1;

        // Loop through movieList, save position
        // of target movie
        for(int i = 0; i < movieList.size(); i++)
        {
            if(movieList.get(i).getMovieID() == id)
                x = i;
        }

        // If movieList only contains
        // one movie, clear list
        if(movieList.size() == 1)
        {
            movieList.clear();
        }
        else
        {
            // Else, remove target movie
            movieList.remove(x);
        }
    }

    // Unused method
    /*public Movie GetMovie(Integer id)
    {
        Movie mov = new Movie();
        System.out.println(id);
        return (mov);
    }*/

    // Returns ID of current movie
    public int GetId(int i)
    {
        return movieList.get(i).getMovieID();
    }

    // Returns true if target movie is in list
    // Returns false if it is not
    public boolean Exists(int id)
    {
        for(int i = 0; i < movieList.size(); i++)
        {
            if(movieList.get(i).getMovieID() == id)
            {
                return true;
            }
        }

        return false;
    }

    // Returns size of movie list
    public int Size()
    {
        return movieList.size();
    }

    // Returns poster path of current movie
    public String GetPath(int i)
    {
        return movieList.get(i).getPosterPath();
    }

    // Returns title of current movie
    public String GetTitle(int i)
    {
        return movieList.get(i).getMovieTitle();
    }

    // Returns description of current movie
    public String GetDesc(int i)
    {
        return movieList.get(i).getMovieDesc();
    }

    // Returns release of current movie
    public String GetRelDate(int i)
    {
        System.out.println(movieList.get(i).getRelDate());

        return movieList.get(i).getRelDate();

    }

    // Returns average vote of current movie
    public String GetVoteAvg(int i)
    {
        // Get average as a percentage
        Double avg = movieList.get(i).getVoteAvg() * 10;

        return avg.toString();
    }

    // Returns cast list of current movie
    public ArrayList GetCastList(int i)
    {
        return movieList.get(i).CastList();
    }

    // Returns total vote count of current movie
    public Integer GetVoteCnt(int i)
    {
        return movieList.get(i).getVoteCount();
    }
}
