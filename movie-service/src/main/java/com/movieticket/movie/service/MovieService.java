package com.movieticket.movie.service;
import com.movieticket.movie.model.Movie;
import com.movieticket.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
public class MovieService {
    private final MovieRepository movieRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found: " + id));
    }

    public Movie addMovie(Movie movie) {
        Movie saved = movieRepository.save(movie);
        log.info("[MOVIE ADDED] id={} title={}", saved.getId(), saved.getTitle());
        return saved;
    }

    public Movie updateMovie(Long id, Movie updated) {
        Movie movie = getMovieById(id);
        movie.setTitle(updated.getTitle());
        movie.setDescription(updated.getDescription());
        movie.setDurationMinutes(updated.getDurationMinutes());
        movie.setPrice(updated.getPrice());
        movie.setAvailableSeats(updated.getAvailableSeats());
        movie.setGenre(updated.getGenre());
        movie.setShowTime(updated.getShowTime());
        return movieRepository.save(movie);
    }

    public boolean checkAndReduceSeats(Long movieId, int seats) {
        Movie movie = getMovieById(movieId);
        if (movie.getAvailableSeats() < seats) return false;
        movie.setAvailableSeats(movie.getAvailableSeats() - seats);
        movieRepository.save(movie);
        return true;
    }
}
