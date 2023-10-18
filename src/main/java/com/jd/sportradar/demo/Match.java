package com.jd.sportradar.demo;

import lombok.Getter;

import java.util.Optional;

@Getter
public final class Match {
  private final Team homeTeam;
  private final Team awayTeam;
  private final Score score;

  private Match(Team homeTeam, Team awayTeam, int homeScore, int awayScore) {
    this.homeTeam = homeTeam;
    this.awayTeam = awayTeam;
    this.score = new Score(homeScore, awayScore);
  }

  public Optional<Match> createMatch(Team homeTeam, Team awayTeam) {
    return Optional.of(new Match(homeTeam, awayTeam, 0, 0));
  }

  public Optional<Match> updateScore(int homeScore, int awayScore) {
    return Optional.of(new Match(this.homeTeam, this.awayTeam, homeScore, awayScore));
  }
}
