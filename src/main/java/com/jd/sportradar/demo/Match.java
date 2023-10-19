package com.jd.sportradar.demo;

import java.math.BigInteger;
import java.util.Optional;
import lombok.Getter;

@Getter
public final class Match {
  private final Team homeTeam;
  private final Team awayTeam;
  private final Score score;
  private static BigInteger arrivalIndexSequence = BigInteger.ZERO;
  private final BigInteger arrivalIndex;

  private Match(
      Team homeTeam, Team awayTeam, int homeScore, int awayScore, BigInteger arrivalIndex) {
    this.homeTeam = homeTeam;
    this.awayTeam = awayTeam;
    this.score = new Score(homeScore, awayScore);
    this.arrivalIndex = arrivalIndex;
  }

  static Match createMatch(Team homeTeam, Team awayTeam) {
    arrivalIndexSequence = arrivalIndexSequence.add(BigInteger.ONE);
    return createMatch(homeTeam, awayTeam, 0, 0, arrivalIndexSequence);
  }

  static Match createMatch(
      Team homeTeam, Team awayTeam, int homeScore, int awayScore, BigInteger arrivalIndex) {
    if (homeTeam == null || awayTeam == null) {
      return null;
    }
    return new Match(homeTeam, awayTeam, homeScore, awayScore, arrivalIndex);
  }

  static Match createMatchFrom(Match match) {
    return new Match(
        match.homeTeam,
        match.awayTeam,
        match.getScore().getHomeScore(),
        match.getScore().getAwayScore(),
        match.getArrivalIndex());
  }

  // This method is updating the score by creating the new match with old values and new score
  // NOTE: the arrival index is being copied, meaning that update is not changing the ordering in
  // the scoreboard
  public Match updateScore(int homeScore, int awayScore) {
    return createMatch(this.homeTeam, this.awayTeam, homeScore, awayScore, this.arrivalIndex);
  }

  public int getScoreSum() {
    if (null == score) {
      return 0;
    }
    return score.getAwayScore() + score.getHomeScore();
  }
}
