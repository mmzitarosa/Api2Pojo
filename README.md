# Api2Pojo

With Api2Pojo you can generate Java classes from a URL that exposes JSON API.

For example, from this URL (print a beer):

	https://api.punkapi.com/v2/beers?page=1&per_page=1
	
That exposesc this JSON:
```json
{
   "head2head":{
      "numberOfMatches":4,
      "totalGoals":4,
      "homeTeam":{"id":98, "name":"AC Milan", "wins":2, "draws":2, "losses":0},
      "awayTeam":{"id":449, "name":"Brescia Calcio", "wins":0, "draws":2, "losses":2}
   },
   "match":{
      "id":279239,
      "competition":{
         "id":2019,
         "name":"Serie A",
         "area":{"name":"Italy", "code":"ITA", "ensignUrl":"https://upload.wikimedia.org/wikipedia/en/0/03/Flag_of_Italy.svg"}
      },
      "season":{"id":530, "startDate":"2019-08-24", "endDate":"2020-05-24", "currentMatchday":25, "winner":null},
      "utcDate":"2019-08-31T16:00:00Z",
      "status":"FINISHED",
      "venue":"Stadio Giuseppe Meazza",
      "matchday":2,
      "stage":"REGULAR_SEASON",
      "group":"Regular Season",
      "lastUpdated":"2019-09-26T15:34:12Z",
      "odds":{"msg":"Activate Odds-Package in User-Panel to retrieve odds."},
      "score":{
         "winner":"HOME_TEAM",
         "duration":"REGULAR",
         "fullTime":{"homeTeam":1, "awayTeam":0},
         "halfTime":{"homeTeam":1, "awayTeam":0},
         "extraTime":{"homeTeam":null, "awayTeam":null},
         "penalties":{"homeTeam":null, "awayTeam":null}
      },
      "homeTeam":{"id":98, "name":"AC Milan"},
      "awayTeam":{"id":449,"name":"Brescia Calcio"},
      "referees":[
         {"id":11006, "name":"Rosario Abisso", "nationality":null},
         {"id":11052, "name":"Matteo Bottegoni", "nationality":null},
         {"id":11121, "name":"Tarcisio Villa", "nationality":null},
         {"id":11104, "name":"Ivan Pezzuto", "nationality":null},
         {"id":11072, "name":"Gianluca Manganiello", "nationality":null}
      ]
   }
}
```
	
The Java classes generated are as follows:

Class | Contains
------| --------
Area| 
AwayTeam|
Competition| Area
ExtraTime|
FullTime|
HalfTime|
Head2head| AwayTeam, HomeTeam
HomeTeam|
Match| AwayTeam, Competition, Score, Odds, Season, HomeTeam, List<Referees>
Matches | Head2head, March
Odds|
Penalties|
Referees|
Score| Penalties, HalfTime, FullTime, ExtraTime
Season|

An example of generated source code:
	 
```java
package org.football_data.api2pojo;

public class Head2head {

    private AwayTeam awayTeam;
    private HomeTeam homeTeam;
    private Number totalGoals;
    private Number numberOfMatches;

    public Head2head() {
    }

    public Head2head(AwayTeam awayTeam, HomeTeam homeTeam, Number totalGoals, Number numberOfMatches) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.totalGoals = totalGoals;
        this.numberOfMatches = numberOfMatches;
    }

    public AwayTeam getAwayTeam() {
        return this.awayTeam;
    }

    public void setAwayTeam(AwayTeam awayTeam) {
        this.awayTeam = awayTeam;
    }

    public HomeTeam getHomeTeam() {
        return this.homeTeam;
    }

    public void setHomeTeam(HomeTeam homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Number getTotalGoals() {
        return this.totalGoals;
    }

    public void setTotalGoals(Number totalGoals) {
        this.totalGoals = totalGoals;
    }

    public Number getNumberOfMatches() {
        return this.numberOfMatches;
    }

    public void setNumberOfMatches(Number numberOfMatches) {
        this.numberOfMatches = numberOfMatches;
    }

}
```

The auto-generated classes are ready to use and contain getters and setters, an empty constructor and another with fields. 
