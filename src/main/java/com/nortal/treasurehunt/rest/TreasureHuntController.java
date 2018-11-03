package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.model.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TreasureHuntController {
  private List<Team> teams = new ArrayList<>();

  @GetMapping("/teams")
  public ResponseEntity<List<Team>> getTeams() {
    return ResponseEntity.ok(teams);
  }

  @PostMapping("/team")
  public ResponseEntity<Team> addTeam(@RequestBody Team team) {
    Optional<Team> exsisting = teams.stream().filter(t -> t.getName().equals(team.getName())).findFirst();
    if (exsisting.isPresent()) {
      return ResponseEntity.ok(exsisting.get());
    }
    team.setId(Long.valueOf(teams.size()));
    teams.add(team);
    return ResponseEntity.ok(team);
  }
}
