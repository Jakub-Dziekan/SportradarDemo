package com.jd.sportradar.demo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Team {
  ITALY_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Italy"),
  SPAIN_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Spain"),
  POLAND_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Poland"),
  SLOVENIA_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Slovenia"),
  MEXICO_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Mexico"),
  CANADA_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Canada"),
  BRAZIL_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Brazil"),
  AUSTRALIA_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Australia"),
  ARGENTINA_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Argentina"),
  FRANCE_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "France"),
  GERMANY_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Germany"),
  URUGUAY_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Uruguay"),
  USA_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "USA"),
  CAMEROON_NATIONAL(TeamCategories.NATIONAL_FOOTBALL_MEN_ELITE, "Cameroon");
  private final TeamCategories category;
  private final String name;
}
