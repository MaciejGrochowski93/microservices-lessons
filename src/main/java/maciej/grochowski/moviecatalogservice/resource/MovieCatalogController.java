package maciej.grochowski.moviecatalogservice.resource;

import maciej.grochowski.moviecatalogservice.model.CatalogItem;
import maciej.grochowski.moviecatalogservice.model.Movie;
import maciej.grochowski.moviecatalogservice.model.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalogOfUser(@PathVariable String userId) {

        // movies watched by specific user
        // response that should come from the real API, hard-coding for now
        List<Rating> ratingList = Arrays.asList(
                new Rating("Harry Potter 1", 6),
                new Rating("Harry Potter 2", 7),
                new Rating("Harry Potter 3", 8)
        );


        // for each movie ID call info service and get details
        // replacing Rating with the CatalogItem
//        return ratingList
//                .stream()
//                .map(rating -> {
//                    // RestTemplate contains 2 parameters: the link it refers to, and the class of object which is supposed to be recreated
//                    Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
//                    return new CatalogItem(movie.getMovieName(), "description", rating.getRating());
//                })
//                .collect(Collectors.toList());


        // asynchronous
        return ratingList
                .stream()
                .map(rating -> {
                    Movie movie = webClientBuilder
                            // build it, use GET() [could be any other CRUD], give the link and retrieve information.
                            // change received String into instance of Movie, and then BLOCK until the whole list is sent
                            .build()
                            .get()
                            .uri("http://localhost:8082/movies/" + rating.getMovieId())
                            .retrieve()
                            .bodyToMono(Movie.class)
                            .block();
                    return new CatalogItem(movie.getMovieName(), "description", rating.getRating());
                })
                .collect(Collectors.toList());
    }
}
