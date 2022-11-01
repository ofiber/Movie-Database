package cs249.finalProject;

// Name: Owen Fiber
// Course: CS249
// Date: 04/20/21
// Desc: CS249 Final project. Application to access themoviedb.org, search for movies,
//		 and store them in a .dat file. User has ability to add and remove movies to their
//		 database and view cast list of stored movies

// Imports ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
// Imports ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


public class TMDBTester extends Application
{
	// Private Data Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	static TMDBMainHandler tmdbHandler;
	static MovieDBHandler movieDBHandler;
	public boolean alertShown = false;
	// Private Data Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		 tmdbHandler = new TMDBMainHandler();

		 // Check for existing movies.dat file, if not found, create a new one
		 try
		 {
		 	FileInputStream inFile = new FileInputStream("movies.dat");
		 	ObjectInputStream objIn = new ObjectInputStream(inFile);

		 	Object container = objIn.readObject();

		 	if(container.getClass() == MovieDBHandler.class)
			{
				// File was found, continue to application
				movieDBHandler = (MovieDBHandler) container;
				System.out.println("File found! Continuing. . .");
			}
		 	else
			{
				// File was not found, creating a new one
				movieDBHandler = new MovieDBHandler();
			}

		 	// Closing input streams
		 	objIn.close();
		 	inFile.close();

		 }catch (IOException | ClassNotFoundException e)
		 {
		 	// Error in getting movies.dat file, creating a new one
		 	System.out.println("File Retrieval Error: IO Exception/ClassNotFound Exception");
		 	System.out.println("No .dat file found. Creating new MovieDBHandler object");
		 	movieDBHandler = new MovieDBHandler();
		 }

		// Launching program
		launch(args);

	}

	@Override
	public void start(Stage primaryStage)
	{
		// Create new border pane and grid pane
		BorderPane pane = new BorderPane();
		GridPane resGP = new GridPane();

		// Set visible gridlines and vgap
		resGP.setGridLinesVisible(true);
		resGP.setVgap(10);

		// Create new scroll pane
		ScrollPane sP = new ScrollPane(resGP);

		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		// Search box label
		Label searchLb = new Label("Search for a movie title:"); // Change font size
		searchLb.setPrefSize(130,25);

		// New search bar text field
		TextField searchBar = new TextField();
		Button searchBtn = new Button("Search");

		// New warning text
		Text warning = new Text("Add movie button may be blocked out after removing said movie from DB. Click search again to solve this issue.");
		warning.setFill(Color.RED);

		// New add movie button
		Button showList = new Button("View your Movie DB");
		ViewMovieListActionHandler handle = new ViewMovieListActionHandler();

		// Set button action
		showList.setOnAction(handle);

		// New Hbox
		HBox input = new HBox(10);

		// Set hbox children
		input.getChildren().addAll(searchLb, searchBar, searchBtn, showList, warning);

		// Set top and center of border pane
		pane.setTop(input);
		pane.setCenter(sP);

		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		// Search button action
		searchBtn.setOnAction(event ->
		{
			// Search button was clicked

			// Clears previous search results from grid pane
			resGP.getChildren().clear();

			String strRes = null;

			// If user did not enter a query in search field, display an error alert
			try
			{
				strRes = tmdbHandler.searchMovies(searchBar.getText());
			} catch (IOException | NullPointerException e)
			{
				System.out.println("Search Button Error: IO Exception");
			}

			// Show error alert
			if(strRes ==null)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a query in the search bar before clicking the search button!");

				alert.setTitle("SEARCH ERROR");
				alert.setHeaderText("Error in search field!");

				alert.show();

				System.out.println("Search Button Error: NullPointerException. Alert displayed");
			}
			else
			{
				// Else, continue with search action event
				System.out.println(strRes);

				JsonParser parser = new JsonParser();

				JsonElement jsonElement = parser.parse(strRes);

				JsonArray resArray = jsonElement.getAsJsonObject().getAsJsonArray("results");

				int row = 0;

				// For each element in the json array, display element in application window
				for (JsonElement o : resArray)
				{
					BorderPane movie = new BorderPane();
					GridPane info = new GridPane();

					info.setHgap(10);
					info.setVgap(10);

					ImageView poster;

					// If movie does not have a poster, display default poster image
					if (o.getAsJsonObject().get("poster_path").isJsonNull())
					{
						poster = new ImageView("img_not_found.png");
						poster.setFitWidth(342.0);
						poster.setFitHeight(550.0);

					} else
					{
						// Else, display movie's poster path
						poster = new ImageView(tmdbHandler.returnPosterImageUrl(o.getAsJsonObject().get("poster_path").getAsString()));

						poster.setFitWidth(342.0);
					}


					// Formatting movie display ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					info.add(new Text("Title:"), 1, 0);
					info.add(new Text(o.getAsJsonObject().get("title").getAsString()), 2, 1);

					info.add(new Text("Description:"), 1, 2);

					TextArea desc = new TextArea(o.getAsJsonObject().get("overview").getAsString());

					desc.setWrapText(true);
					desc.setMaxWidth(500);
					desc.setEditable(false);

					info.add(desc, 2, 3);

					Text alertInfo = new Text();
					info.add(alertInfo, 3, 5);

					Button addMovieBtn = new Button("Add movie to your DB");
					// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

					int movID = Integer.parseInt(o.getAsJsonObject().get("id").getAsString());

					// Checks db for movie, if current movie exists in db, disable add button
					for (int i = 0; i < movieDBHandler.Size(); i++)
					{
						if (movieDBHandler.Exists(movID))
						{
							// If current movie exists in user's movie list, disable Add Movie button
							addMovieBtn.setDisable(true);
							addMovieBtn.setTextFill(Color.RED);
							addMovieBtn.setText("Movie Exists in DB!");
						} else
							break; // Else, continue
					}


					addMovieBtn.setId(o.getAsJsonObject().get("id").getAsString());

					addMovieBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event1 ->
					{
						// Add movie button was clicked
						int id = Integer.parseInt(((Control) event1.getSource()).getId());
						System.out.println(((Control) event1.getSource()).getId());

						String movieInfo = tmdbHandler.getMovie(id);
						JsonObject movieInfoJson = (JsonObject) parser.parse(movieInfo);

						alertInfo.setText("Adding. . .");

						// Added movie to database, display success message
						movieDBHandler.AddMovie(movieInfoJson);
						alertInfo.setFill(Color.GREEN);
						alertInfo.setText("Success!");

						// Disable Add button if it's been clicked.
						addMovieBtn.setDisable(true);
						addMovieBtn.setTextFill(Color.RED);
						addMovieBtn.setText("Movie Exists in DB!");

						// Do this part...
						Thread t = new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									Thread.sleep(6000);
									Platform.runLater(() ->
									{
										alertInfo.setText("");
									});

									System.out.println("Added");
								} catch (InterruptedException ex)
								{
									System.out.println("Thread Sleep Error: Interrupted Exception");
								}
							}
						});

						t.start(); // Restart the thread?
					});


					// Add elements to border pane
					movie.setLeft(poster);
					movie.setRight(addMovieBtn);
					movie.setCenter(info);

					// Add movie to grid pane
					resGP.add(movie, 0, row);

					// Increment row by 1
					row++;
				}
			}
		});


		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


		// Create scene with border pane, of set width and height
		Scene scene = new Scene(pane, 1600, 900);

		// Set stage icons and title
		primaryStage.getIcons().add(new Image("icon.jpg"));
		primaryStage.setTitle("Movie Database Accessor");

		// Set stage scene and show stage
		primaryStage.setScene(scene);
		primaryStage.show();
	}


	@Override
	public void stop() throws Exception
	{
		// Stops the application
		super.stop();
		System.out.println("01010011 01100101 01100101 00100000 01111001 01100001 00100000");
		System.out.println("01101100 01100001 01110100 01100101 01110010 00100000 00111010");
		System.out.println("                           00101001                           ");

		FileOutputStream outFile = null;

		// Try to open a new output stream
		try
		{
			outFile = new FileOutputStream("movies.dat");

			ObjectOutputStream objOut = new ObjectOutputStream(outFile);

			// Write user movie list to output file
			objOut.writeObject(movieDBHandler);

			// Close object and output stream
			objOut.close();
			outFile.close();

		}catch (FileNotFoundException e)
		{
			// Catch file not found exception
			e.printStackTrace();
			System.out.println("Stop Error: File not found!");
		}catch (IOException e)
		{
			// Catch IO exception
			e.printStackTrace();
			System.out.println("Stop Error: IO exception!");
		}
	}

	private class ViewMovieListActionHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent event)
		{
			// Print movie list to the console
			movieDBHandler.ListMovies();

			// Create new stage and application window elements
			Stage stage = new Stage();
			BorderPane pane = new BorderPane();
			GridPane gP = new GridPane();
			HBox top = new HBox();

			// Set Hbox warning text
			Text warning = new Text("Movie Database may not update immediately when removing movies! Try closing and reopening if it doesn't update!");
			warning.setFill(Color.RED);

			// Add warning text to Hbox
			top.getChildren().add(warning);

			// Set alignment of Hbox
			top.setAlignment(Pos.TOP_CENTER);

			// Disable gridlines, set vgap
			gP.setGridLinesVisible(false);
			gP.setVgap(10);

			// Create new scroll pane
			ScrollPane sP = new ScrollPane(gP);

			// Set scroll pane and hbox positions
			pane.setCenter(sP);
			pane.setTop(top);

			int row = 0;

			// Warning alert is displayed the first time user clicks View Movie List button
			Alert infoWarning = new Alert(Alert.AlertType.INFORMATION, "Please only remove 1 movie at a time. " +
					"Removing more than one movie at a time results in only the first movie being removed." +
					"\nClick the 'OK' button to continue to movie list.");

			infoWarning.setTitle("READ BEFORE CONTINUING");
			infoWarning.setHeaderText("Important Information!");

			// If alertShown variable is false, show alert
			if(!alertShown)
			{
				infoWarning.showAndWait();
			}

			if(movieDBHandler.Size() == 0)
			{
				// If movie list is empty, display warning alert
				Alert listEmpty = new Alert(Alert.AlertType.WARNING, "Your movie DB is empty!!");

				listEmpty.setTitle("Movie DB Error!");
				listEmpty.setHeaderText("Important Information");

				listEmpty.show();

				System.out.println("View Movie DB Error: Movie list is empty. Alert displayed.");
			}
			else if(!infoWarning.isShowing() || alertShown) // Continue if alert is still shown, or if alert has already been shown
			{
				// Alert has been shown, set variable to true
				alertShown = true;

				for (int i = 0; i < movieDBHandler.Size(); i++)
				{
					// Create new border pane and grid pane
					BorderPane movie = new BorderPane();
					GridPane info = new GridPane();

					// Create new buttons to remove movies and view cast lists
					Button rmBtn = new Button("Remove movie from DB");
					Button castBtn = new Button("View cast list");

					info.setHgap(10);
					info.setVgap(10);

					ImageView poster;

					if (tmdbHandler.returnPosterImageUrl(movieDBHandler.GetPath(i)).equals("null"))
					{
						// If movie does not have a poster, display default poster image
						poster = new ImageView("img_not_found.png");
						poster.setFitWidth(342.0);
						poster.setFitHeight(550.0);

					} else
					{
						// Else, display movie's poster
						poster = new ImageView(tmdbHandler.returnPosterImageUrl(movieDBHandler.GetPath(i)));
						poster.setFitWidth(342.0);
					}


					// Create title field
					info.add(new Text("Title:"), 1, 1);
					info.add(new Text(movieDBHandler.GetTitle(i)), 2, 1);

					// Create description field
					info.add(new Text("Description:"), 1, 2);
					TextArea desc = new TextArea(movieDBHandler.GetDesc(i));

					desc.setWrapText(true);
					desc.setMaxWidth(500);
					desc.setEditable(false);

					info.add(desc, 2, 3);

					// Add cast button to GP
					info.add(castBtn, 1, 3);

					// Create movie score field
					info.add(new Text("Movie Score: "), 1, 5);
					info.add(new Text(movieDBHandler.GetVoteAvg(i) + "%"), 2, 5);

					// Create vote count field
					info.add(new Text("Vote Count: "), 1, 6);
					info.add(new Text(movieDBHandler.GetVoteCnt(i).toString()), 2, 6);

					// Create release date field
					info.add(new Text("Release Date:"), 1, 7);
					info.add(new Text(movieDBHandler.GetRelDate(i)), 2, 7);

					// Set remove button id to movie id
					String movieID = String.valueOf(movieDBHandler.GetId(i));
					rmBtn.setId(movieID);

					// For some reason can't use 'i' with below code
					int x = i;


					castBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event2 ->
					{
						// View cast list button has been clicked

						// Create new application window to
						// display cast list in a 3 x N grid
						Stage castStage = new Stage();
						BorderPane castPane = new BorderPane();
						GridPane castGP = new GridPane();

						castGP.setGridLinesVisible(false);
						castGP.setVgap(10);
						castGP.setHgap(10);

						ScrollPane castSP = new ScrollPane(castGP);
						castPane.setCenter(castSP);

						ArrayList<String> castList = movieDBHandler.GetCastList(x);

						// Display cast list in a 3 x N grid
						for(int j = 0; j < castList.size(); j = j+3)
						{
							try
							{
								castGP.add(new Text(castList.get(j)), 1, j);
								castGP.add(new Text(castList.get(j + 1)), 2, j);
								castGP.add(new Text(castList.get(j + 2)), 3, j);
							}catch (IndexOutOfBoundsException exception)
							{
								System.out.println("Cast List Exception: out of bounds");
							}
						}

						// Set castList window scene
						Scene castScene = new Scene(castPane, 440, 300);
						castStage.setScene(castScene);

						// Set stage and icons
						castStage.getIcons().add(new Image("icon.jpg"));
						castStage.setTitle(movieDBHandler.GetTitle(x) + " Cast List");



						// Show stage
						castStage.show();

					});

					rmBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event1 ->
					{
						// Remove movie button has been clicked

						try
						{
							// Try to remove movie from movie list
							movieDBHandler.RemoveMovie(movieDBHandler.GetId(x));

							// Disable remove button
							rmBtn.setDisable(true);
							rmBtn.setTextFill(Color.RED);
							rmBtn.setText("Movie removed from DB");

							System.out.println("Removed " + movieDBHandler.GetTitle(x));

						} catch (IndexOutOfBoundsException ex)
						{
							// Catch index out of bound exception
							System.out.println("Remove Movie Error: Out of bounds! Current pos: " + x + " Bound: " + movieDBHandler.Size());
						}
					});


					// Set positions of buttons, poster image, and info pane
					movie.setRight(rmBtn);
					movie.setLeft(poster);
					movie.setCenter(info);

					// Add border pane to grid pane
					gP.add(movie, 0, row);

					// Increment row by one
					row++;
				}

				// Create new scene
				Scene scene = new Scene(pane, 1065, 720);

				// Set title and icons
				stage.getIcons().add(new Image("icon.jpg"));
				stage.setTitle("Your Movie Database");

				// Set scene and show stage
				stage.setScene(scene);
				stage.show();
			}
		}
	}
}
