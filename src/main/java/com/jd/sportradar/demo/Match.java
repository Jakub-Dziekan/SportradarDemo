package com.jd.sportradar.demo;

import java.math.BigInteger;
import lombok.Getter;

@Getter
public final class Match extends Event {
  private final Team homeTeam;
  private final Team awayTeam;
  private final Score score;

  private Match(Team homeTeam, Team awayTeam, int homeScore, int awayScore) {
    super();
    this.homeTeam = homeTeam;
    this.awayTeam = awayTeam;
    this.score = new Score(homeScore, awayScore);
  }

  private Match(
      Team homeTeam, Team awayTeam, int homeScore, int awayScore, BigInteger arrivalIndex) {
    super(arrivalIndex);
    this.homeTeam = homeTeam;
    this.awayTeam = awayTeam;
    this.score = new Score(homeScore, awayScore);
  }

  static Match createMatch(Team homeTeam, Team awayTeam) {
    return createMatch(homeTeam, awayTeam, 0, 0);
  }

  // creator for new objects (new Event -> arrivalIndex to be set)
  static Match createMatch(Team homeTeam, Team awayTeam, int homeScore, int awayScore) {
    if (homeTeam == null || awayTeam == null) {
      return null;
    }
    return new Match(homeTeam, awayTeam, homeScore, awayScore);
  }

  // copying creator for existing objects (arrivalIndex already known)
  static Match copyMatch(
      Team homeTeam, Team awayTeam, int homeScore, int awayScore, BigInteger arrivalIndex) {
    if (homeTeam == null || awayTeam == null) {
      return null;
    }
    return new Match(homeTeam, awayTeam, homeScore, awayScore, arrivalIndex);
  }

  // This method is updating the score by creating the new match with old values and new score
  // NOTE: the arrival index is being copied, meaning that update is not changing the ordering in
  // the scoreboard
  public Match updateScore(int homeScore, int awayScore) {
    return copyMatch(this.homeTeam, this.awayTeam, homeScore, awayScore, this.getArrivalIndex());
  }

  public int getScoreSum() {
    if (null == score) {
      return 0;
    }
    return score.getAwayScore() + score.getHomeScore();
  }
}
