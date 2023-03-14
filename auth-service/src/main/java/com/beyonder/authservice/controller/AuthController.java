package com.beyonder.authservice.controller;

import com.beyonder.authservice.dto.JobList;
import com.beyonder.authservice.dto.ResponseDTO;
import com.beyonder.authservice.dto.UserDTO;
import com.beyonder.authservice.entity.User;
import com.beyonder.authservice.service.TokenService;

import com.beyonder.authservice.service.impl.JpaUserDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final JpaUserDetailService jpaUserDetailService;
    private final RestTemplate restTemplate;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> addUser(@RequestBody UserDTO userDTO) {
        tokenService.addUser(userDTO);
        return new ResponseEntity<ResponseDTO>(ResponseDTO.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("success add user")
                .data(userDTO).build(), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> token(@RequestBody User user) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        String token = tokenService.generatedToken(authentication);
        return new ResponseEntity(ResponseDTO.builder().httpStatus(HttpStatus.OK).message("token created").data(token).build(), HttpStatus.OK);

    }
    @GetMapping("/user-data")
    public ResponseEntity<ResponseDTO<Object>> userInfo(@RequestHeader(name = "Authorization") String tokenBearer) {

        UserDTO user = tokenService.decodeToken(tokenBearer);

        //add deeper structure
        return new ResponseEntity(ResponseDTO.builder().httpStatus(HttpStatus.OK).message("token found").data(user).build(), HttpStatus.OK);
        // return user;
    }
    //user
    @GetMapping("/user-data-2")
    public ResponseEntity<UserDTO> userInfo2(@RequestHeader(name = "Authorization") String tokenBearer) {
        UserDTO user = tokenService.decodeToken(tokenBearer);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping("/all-users")
    public ResponseEntity<List<UserDTO>> allUsers() {
        log.info("AuthController method allUsers");
        return new ResponseEntity<>(jpaUserDetailService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/get-job-list-api")
    public ResponseEntity<String> getDataJob() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String endpointUrl = "http://dev3.dansmultipro.co.id/api/recruitment/positions.json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                endpointUrl,
                HttpMethod.GET,
                entity,
                String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Use Jackson to parse the original JSON response and create a list of custom objects
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> originalObjectList = objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, String>>>() {});
            // Create a new list of custom objects with only the "a" attribute
            List<Map<String, String>> modifiedObjectList = new ArrayList<>();
            for (Map<String, String> originalObject : originalObjectList) {
                Map<String, String> modifiedObject = new HashMap<>();
                modifiedObject.put("title", originalObject.get("title"));
                modifiedObjectList.add(modifiedObject);
            }
            // Use Jackson to convert the modified list of custom objects to JSON
            String modifiedJsonResponse = objectMapper.writeValueAsString(modifiedObjectList);
            return ResponseEntity.ok(modifiedJsonResponse);

        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }

    @GetMapping("/get-job-detail-api")
    public ResponseEntity<String> getDataJobDetail() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String endpointUrl = "http://dev3.dansmultipro.co.id/api/recruitment/positions.json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                endpointUrl,
                HttpMethod.GET,
                entity,
                String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Use Jackson to parse the original JSON response and create a list of custom objects
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> originalObjectList = objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, String>>>() {});
            // Create a new list of custom objects with only the "a" attribute
            List<Map<String, String>> modifiedObjectList = new ArrayList<>();
            for (Map<String, String> originalObject : originalObjectList) {
                Map<String, String> modifiedObject = new HashMap<>();
                modifiedObject.put("description", originalObject.get("description"));
                modifiedObjectList.add(modifiedObject);
            }
            // Use Jackson to convert the modified list of custom objects to JSON
            String modifiedJsonResponse = objectMapper.writeValueAsString(modifiedObjectList);
            return ResponseEntity.ok(modifiedJsonResponse);

        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }

    @GetMapping("/download-job-list-api")
    public ResponseEntity<String> downloadDataJob() throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        RestTemplate restTemplate = new RestTemplate();
        String endpointUrl = "http://dev3.dansmultipro.co.id/api/recruitment/positions.json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                endpointUrl,
                HttpMethod.GET,
                entity,
                String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Use Jackson to parse the original JSON response and create a list of custom objects
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> dataList = objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, String>>>() {});
            // Create a list of maps containing only the "title" key and value pairs
            List<Map<String, String>> aList = new ArrayList<>();
            for (Map<String, String> data : dataList) {
                Map<String, String> aMap = new HashMap<>();
                aMap.put("title", data.get("title"));
                aList.add(aMap);
            }

            // Convert the list to a CSV file
            Writer writer = new FileWriter("output.csv");
            StatefulBeanToCsv<Map<String, String>> beanToCsv = new StatefulBeanToCsvBuilder<Map<String, String>>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(false)
                    .build();
            beanToCsv.write(aList);
            writer.close();

        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
        return response;
    }


}
