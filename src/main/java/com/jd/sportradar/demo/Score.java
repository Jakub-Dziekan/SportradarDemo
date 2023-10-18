package com.jd.sportradar.demo;

import lombok.Getter;

@Getter
public final class Score {

  private final int homeScore;
  private final int awayScore;

  public Score(int homeScore, int awayScore) {
    this.homeScore = homeScore;
    this.awayScore = awayScore;
  }
}
