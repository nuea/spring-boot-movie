package com.wongnai.interview.movie.search;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieRepository;
import com.wongnai.interview.movie.MovieSearchService;

import javax.persistence.criteria.CriteriaBuilder;

@Component("invertedIndexMovieSearchService")
@DependsOn("movieDatabaseInitializer")
public class InvertedIndexMovieSearchService implements MovieSearchService {
	@Autowired
	private MovieRepository movieRepository;

	private HashMap<String, List<Long>> movieTable = new HashMap<String, List<Long>>();

	@Override
	public List<Movie> search(String queryText) {
		//TODO: Step 4 => Please implement in-memory inverted index to search movie by keyword.
		// You must find a way to build inverted index before you do an actual search.
		// Inverted index would looks like this:
		// -------------------------------
		// |  Term      | Movie Ids      |
		// -------------------------------
		// |  Star      |  5, 8, 1       |
		// |  War       |  5, 2          |
		// |  Trek      |  1, 8          |
		// -------------------------------
		// When you search with keyword "Star", you will know immediately, by looking at Term column, and see that
		// there are 3 movie ids contains this word -- 1,5,8. Then, you can use these ids to find full movie object from repository.
		// Another case is when you find with keyword "Star War", there are 2 terms, Star and War, then you lookup
		// from inverted index for Star and for War so that you get movie ids 1,5,8 for Star and 2,5 for War. The result that
		// you have to return can be union or intersection of those 2 sets of ids.
		// By the way, in this assignment, you must use intersection so that it left for just movie id 5.


		List<Long> movieIds = new LinkedList<Long>();
		List<Movie> target = new ArrayList<Movie>();
		if( movieTable.isEmpty()) {
			for (Movie movie : movieRepository.findAll()) {
				for (String word : movie.getName().split(" ")) {
					if (movieTable.containsKey(word.toLowerCase())) {
						movieTable.get(word.toLowerCase()).add(movie.getId());
					} else {
						List<Long> number = new LinkedList<>();
						number.add(movie.getId());
						movieTable.put(word.toLowerCase(), number);
					}
				}
			}
		}

		for (String word : queryText.toLowerCase().split(" ")) {
			if(movieTable.containsKey(word)) {
				if(movieIds.isEmpty()){
					movieIds.addAll(movieTable.get(word));
				} else {
					movieIds = movieIds.stream()
							.filter(id -> movieTable.get(word).contains(id))
							.collect(Collectors.toList());
				}
			} else {
				return new LinkedList<Movie>();
			}
		}

		movieRepository.findAllById(movieIds).forEach(target::add);

		return target;
	}
}
