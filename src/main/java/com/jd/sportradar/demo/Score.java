package com.jd.sportradar.demo;

import lombok.Getter;

public final class Score {

  @Getter private final int homeScore;
  @Getter private final int awayScore;

  public Score(int homeScore, int awayScore) {
    this.homeScore = homeScore;
    this.awayScore = awayScore;
  }
}
