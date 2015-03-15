# sejm-scrapper

This small program allows to download data from the [Polish Parliament website](http://sejm.gov.pl/Sejm7.nsf/agent.xsp?symbol=posglos&NrKadencji=7) and save them in the SQLite database.

## Getting started

Clone the repo `git clone git://github.com/elaq/sejm-scrapper.git`

Arguments that have to be provided to the program: `db_name parliament_term first_session last_session`

For example to get all data for the current term (7):  `all_data 7 1 88`

It may take up to one hour to save all data.

## Database details

SQLite database gets created in the main repo directory. It will consist of two tables:

*  VotingData - voting data (data from pages like [this](http://sejm.gov.pl/Sejm7.nsf/agent.xsp?symbol=glosowania&NrKadencji=7&NrPosiedzenia=88&NrGlosowania=1))
*  SessionData - session description (data from pages like [this](http://sejm.gov.pl/Sejm7.nsf/agent.xsp?symbol=listaglos&IdDnia=1437))

There is no support for detailed data regarding votes of particular MPs at the moment
(like on page like [this](http://sejm.gov.pl/Sejm7.nsf/agent.xsp?symbol=klubglos&IdGlosowania=42353&KodKlubu=PO) or [that](http://www.sejm.gov.pl/Sejm7.nsf/agent.xsp?symbol=GLOPOSLA&NrKadencji=7&Nrl=001&IdDnia=1437)).

I use free [SQLiteStudio](http://sqlitestudio.pl/) to access the database.

## Simple SQL commands to start with

Show rows

```sql
select * from VotingData
select * from SessionData
```

Count rows

```sql
select count(*) from VotingData
select count(*) from SessionData
```

Check how often parties vote non-uniformly

```sql
select count(*) from VotingData where (club="PO" AND voted_for <> 0 AND voted_against <> 0)
select count(*) from VotingData where ((club="RP" OR club="TR") AND voted_for <> 0 AND voted_against <> 0)
select count(*) from VotingData where ((club="SP" OR club="KPSP") AND voted_for <> 0 AND voted_against <> 0)
select club, count(*) as non-uniform_sessions from VotingData where (voted_for <> 0 AND voted_against <> 0) group by club
```

Check how many votes clubs attended

```sql
select club, count(*) as sessions from VotingData group by club
```

Check how many sessions were missed by at least 5 MPs from the club

```sql
select club, count(*) as absence_sessions from VotingData where (not_voted > 5) group by club
```

Join VotingData and SessionData tables

```sql
select * from VotingData LEFT JOIN SessionData ON (VotingData.term=VotingData.term AND VotingData.session_id=SessionData.session_id AND VotingData.voting_id=SessionData.voting_id)
```