package com.zuehlke.securesoftwaredevelopment.controller;

import com.zuehlke.securesoftwaredevelopment.config.SecurityUtil;
import com.zuehlke.securesoftwaredevelopment.domain.Person;
import com.zuehlke.securesoftwaredevelopment.domain.User;
import com.zuehlke.securesoftwaredevelopment.repository.PersonRepository;
import com.zuehlke.securesoftwaredevelopment.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Controller

public class PersonsController {

    private static final Logger LOG = LoggerFactory.getLogger(PersonsController.class);

    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    public PersonsController(PersonRepository personRepository, UserRepository userRepository) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/persons/{id}")
    @PreAuthorize("hasAuthority('VIEW_PERSON')")
    public String person(@PathVariable int id, Model model) {
        model.addAttribute("person", personRepository.get("" + id));
        model.addAttribute("username", userRepository.findUsername(id));
        return "person";
    }

    @GetMapping("/myprofile")
    @PreAuthorize("hasAuthority('VIEW_MY_PROFILE')")
    public String self(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("person", personRepository.get("" + user.getId()));
        model.addAttribute("username", userRepository.findUsername(user.getId()));
        return "person";
    }

    @DeleteMapping("/persons/{id}")
    public ResponseEntity<Void> person(@PathVariable int id) {
        User currentUser = SecurityUtil.getCurrentUser();
        
        if (currentUser == null) {
            LOG.warn("User isn't logged in");
            return ResponseEntity.status(403).build();
        }
        

        if (!SecurityUtil.hasPermission("UPDATE_PERSON")) {
            LOG.warn("User doesn't have UPDATE_PERSON permission");
            return ResponseEntity.status(403).build();
        }
        

        if (!SecurityUtil.hasPermission("VIEW_PERSON") && currentUser.getId() != id) {
            LOG.warn("User " + currentUser.getUsername() + " tried to delete person " + id + " without permission");
            return ResponseEntity.status(403).build();
        }
        
        personRepository.delete(id);
        userRepository.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update-person")
    public String updatePerson(Person person, @RequestParam("username") String username) throws AccessDeniedException {
        User currentUser = SecurityUtil.getCurrentUser();

        if(currentUser == null){
            LOG.warn("User isn't logged in");
            throw new AccessDeniedException("Access forbidden!");
        }

        if( currentUser.getId() != Integer.parseInt(person.getId()) && !SecurityUtil.hasPermission("UPDATE_PERSON") ){
            LOG.warn("User " + currentUser.getUsername() + " tried to update person " + person.getId() + " without permission" );
            throw new AccessDeniedException("Access forbidden!");
        }

        personRepository.update(person);

        userRepository.updateUsername(Integer.parseInt(person.getId()), username);

        if( currentUser.getId() != Integer.parseInt(person.getId()) ){
            return "redirect:/persons/" + person.getId();
        }else {
            return "redirect:/myprofile";
        }
    }

    @GetMapping("/persons")
    @PreAuthorize("hasAuthority('VIEW_PERSONS_LIST')")
    public String persons(Model model) {
        model.addAttribute("persons", personRepository.getAll());
        return "persons";
    }

    @GetMapping(value = "/persons/search", produces = "application/json")
    @ResponseBody
    @PreAuthorize("hasAuthority('VIEW_PERSONS_LIST')")
    public List<Person> searchPersons(@RequestParam String searchTerm) throws SQLException {
        return personRepository.search(searchTerm);
    }
}
