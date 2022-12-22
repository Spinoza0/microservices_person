package com.evolentacourses.person.controller;

import com.evolentacourses.person.model.Person;
import com.evolentacourses.person.model.Weather;
import com.evolentacourses.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping(value = "/person")
public class PersonController {

    @Autowired
    private PersonRepository repository;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${location.url}")
    private String locationUrl;

    @GetMapping
    public Iterable<Person> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Person> findById(int id) {
        return repository.findById(id);
    }

    @GetMapping("{id}/weather")
    public ResponseEntity<Weather> getWeather(@PathVariable int id) {
        return repository.findById(id)
                .map(person -> {
                    String url = String.format("%s=%s", locationUrl, person.getLocation());
                    Weather weather = restTemplate.getForObject(url, Weather.class);
                    return new ResponseEntity<>(weather, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Person> save(@RequestBody Person person) {
        return repository.findById(person.getId())
                .map(it -> new ResponseEntity<>(it, HttpStatus.BAD_REQUEST))
                .orElse(new ResponseEntity<>(repository.save(person), HttpStatus.CREATED));
    }
}
