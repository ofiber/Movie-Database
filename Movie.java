package cs249.finalProject;

// Name :Owen Fiber
// Course: CS249
// Date: 04/06/21
// Last Edited: 05/08/21
// Desc: Movie class defines a movie object for use in MovieDBHandler

// Imports ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
import java.io.Serializable;
import java.util.ArrayList;
// Imports ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

public class Movie implements Serializable
{
    // Private Data Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private String movieTitle;
    private String movieDesc;
    private String relDate;
    private int movieID;
    private Integer voteCount;
    private Double voteAvg;
    private String posterPath;
    private ArrayList<String> castList;
    // Private Data Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Default constructor
    public Movie(){}

    // Non-Default constructor
    public Movie(String movieTitle, String movieDesc, String relDate, String movieID, String posterPath)
    {
        this.movieTitle = movieTitle;
        this.movieDesc = movieDesc;
        this.relDate = relDate;
        this.movieID = Integer.parseInt(movieID);
        this.posterPath = posterPath;
    }

    // Non-Default constructor
    public Movie(String movieTitle, String movieDesc, String relDate, String movieID, ArrayList<String> castList)
    {
        this.movieTitle = movieTitle;
        this.movieDesc = movieDesc;
        this.relDate = relDate;
        this.movieID = Integer.parseInt(movieID);

        this.castList = castList;
    }

    // Returns size of current movie's cast list
    public int getCastListSize()
    {
        try
        {
            try
            {
                return castList.size();

            }catch (IndexOutOfBoundsException ex)
            {
                return -1;
            }
        }catch (NullPointerException exception)
        {
            return -1;
        }
    }

    // Return movie title
    public String getMovieTitle()
    {
        return movieTitle;
    }

    // Set movie title
    public void setMovieTitle(String movieTitle)
    {
        this.movieTitle = movieTitle;
    }

    // Return movie description
    public String getMovieDesc()
    {
        return movieDesc;
    }

    // Set movie description
    public void setMovieDesc(String movieDesc)
    {
        this.movieDesc = movieDesc;
    }

    // Return movie ID
    public int getMovieID()
    {
        return movieID;
    }

    // Set movie ID
    public void setMovieID(int movieID)
    {
        this.movieID = movieID;
    }

    // Return movie release date
    public String getRelDate()
    {
        return relDate;
    }

    // Set movie release date
    public void setRelDate(String relDate)
    {
        this.relDate = relDate;
    }

    // Return movie cast list
    public ArrayList CastList()
    {
        return castList;
    }

    // Set movie cast list
    public void setCastList(ArrayList<String> castList)
    {
        this.castList = castList;
    }

    // Return movie total vote count
    public Integer getVoteCount()
    {
        return voteCount;
    }

    // Set movie vote count
    public void setVoteCount(Integer voteCount)
    {
        this.voteCount = voteCount;
    }

    // Return movie average vote
    public Double getVoteAvg()
    {
        return voteAvg;
    }

    // Set movie average vote
    public void setVoteAvg(Double voteAvg)
    {
        this.voteAvg = voteAvg;
    }

    // Return movie poster path
    public String getPosterPath()
    {
        return posterPath;
    }

    // Set movie poster path
    public void setPosterPath(String posterPath)
    {
        if(posterPath.contains("\""))
        {
            posterPath = posterPath.replace("\"", "");
        }

        this.posterPath = posterPath;
    }
}
