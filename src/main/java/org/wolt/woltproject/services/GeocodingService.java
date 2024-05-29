package org.wolt.woltproject.services;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.RestaurantEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.models.RouteInfo;
import org.wolt.woltproject.repositories.RestaurantRepository;
import org.wolt.woltproject.repositories.UserRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient = new OkHttpClient();

    public RouteInfo getRouteFromUserToRestaurant(Integer userId, Integer restaurantId) throws IOException {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));

        double lat1 = user.getLat();
        double lon1 = user.getLon();

        double lat2 = restaurant.getLat();
        double lon2 =  restaurant.getLon();

        return getRoute(lat1, lon1, lat2, lon2);
    }

    private RouteInfo getRoute(double lat1, double lon1, double lat2, double lon2) throws IOException {
        String url = "http://router.project-osrm.org/route/v1/driving/" + lon1 + "," + lat1 + ";" + lon2 + "," + lat2 + "?overview=false";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            double duration = jsonResponse.get("routes").get(0).get("duration").asDouble();
            double distance = jsonResponse.get("routes").get(0).get("distance").asDouble();
            return RouteInfo.builder().duration(duration).distance(distance).build();
        }
    }
}
