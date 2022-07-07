import java.sql.*;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
            e.printStackTrace();
            return;
        }
        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/project_movie", "postgres", "cse3207");
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println(connection);
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
        
        //write your code on this
        Statement stmt = connection.createStatement();
        ResultSet rs = null;
        
        //1. Create the tables and insert the proper data based on the provided data.
        //You should make the movie, actor, director, and customer tables first and insert data into other related tables.
        stmt.executeUpdate("create table director(directorID serial not null PRIMARY KEY, directorName varchar(30) not null, dateOfbirth date not null, dataOfDeath date null);\n"+
        				"create table actor(actorID serial not null PRIMARY KEY, actorName varchar(30) not null, dateOfBirth date not null, dateOfDeath date null, gender varchar(6) not null);\n"+
        				"create table movie(movieID serial not null PRIMARY KEY, movieName varchar(50) not null, releaseYear integer not null, releaseMonth integer not null, releaseDate integer not null, publisherName varchar(50) not null, avgRate double precision null);\n"+ 
        				"create table award(awardID serial not null PRIMARY KEY, awardName varchar(50) not null);\n"+
        				"create table genre(genreName varchar(20) not null PRIMARY KEY);\n"+
        				"create table movieGenre(movieID integer REFERENCES movie on delete cascade, genreName varchar(20) REFERENCES genre, CONSTRAINT movieGenre_pkey PRIMARY KEY (movieID, genreName));\n"+
        				"create table movieObtain(movieID integer REFERENCES movie on delete cascade, awardID integer REFERENCES award, year integer not null, CONSTRAINT movieObtain_pkey PRIMARY KEY (movieID, awardID));\n"+ 
        				"create table actorObtain(actorID integer REFERENCES actor, awardID integer REFERENCES award, year integer not null, CONSTRAINT actorObtain_pkey PRIMARY KEY (actorID, awardID));\n"+
        				"create table directorObtain(directorID integer REFERENCES director, awardID integer REFERENCES award, year integer not null, CONSTRAINT directorObtain_pkey PRIMARY KEY (directorID, awardID));\n"+
        				"create table casting(movieID integer REFERENCES movie on delete cascade, actorID integer REFERENCES actor, role varchar(20) not null, CONSTRAINT casting_PKey PRIMARY KEY(movieID, actorID));\n"+
        				"create table make(movieID integer REFERENCES movie on delete cascade, directorID integer REFERENCES director, CONSTRAINT make_PKey PRIMARY KEY(movieID, directorID));\n"+
        				"create table customer(customerID serial not null, customerName varchar(30) not null, dateOfBirth date not null, gender varchar(6) not null, PRIMARY KEY (customerID));\n"+
        				"create table customerRate(customerID integer REFERENCES customer on delete cascade, movieID integer REFERENCES movie on delete cascade, rate int NOT NULL, CONSTRAINT customerRate_PKey PRIMARY KEY(customerID, movieID));"
        				);
        
        
        //2. Insert the proper data from the following statements.
        stmt.executeUpdate("insert into director(directorName, dateOfBirth) values ('Lee lsaac Chung', '1978.10.19'), ('Tim Burton', '1958.8.25'), ('David Fincher', '1962.8.28'), ('Christopher Nolan', '1970.7.30')");
        stmt.executeUpdate("insert into actor(actorName, dateOfbirth, dateOfDeath, gender) values ('Steven Yeun', '1983.12.21', null, 'Male'), ('Youn Yuhjung', '1947.6.19', null, 'Female'), ('Johnny Depp', '1963.6.9', null, 'Male'), ('Winona Ryder', '1971.10.29', null, 'Female'), ('Anne Hathaway', '1982.11.12', null, 'Female'), ('Christian Bale', '1974.1.30', null, 'Male'), ('Heath Ledger', '1979.4.4', '2008.1.22', 'Male'), ('Jesse Eisenberg', '1983.10.5', null, 'Male'), ('Andrew Garfield', '1983.8.20', null, 'Male');");
        stmt.executeUpdate("insert into customer(customerName, dateOfBirth, gender) values ('Bob', '1997.11.14', 'Male'), ('John', '1978.01.23', 'Male'), ('Jack', '1980.05.04', 'Male'), ('Jill', '1981.04.17', 'Female'), ('Bell', '1990.05.14', 'Female');");
        stmt.executeUpdate("insert into movie(movieName, releaseYear, releaseMonth, releaseDate, publisherName) values ('Minari', 2020, 12, 11, 'A24'), ('Edward Scissorhands', 1991, 6, 29, '20th Century Fox Presents'), ('Alice In Wonderland', 2010, 3, 4, 'Korea Sony Pictures'), ('The Social Network', 2010, 11, 18, 'Korea Sony Pictures'), ('The Dark Knight', 2008, 8, 6, 'Warner Brothers Korea');");
        
        stmt.executeUpdate("insert into make(movieID, directorID) values ((select movieID from movie where movieName = 'Minari'), (select directorID from director where directorName = 'Lee lsaac Chung')),\n"+ 
					"((select movieID from movie where movieName = 'Edward Scissorhands'), (select directorID from director where directorName = 'Tim Burton')),\n"+
                    "((select movieID from movie where movieName = 'Alice In Wonderland'), (select directorID from director where directorName = 'Tim Burton')),\n"+
                    "((select movieID from movie where movieName = 'The Social Network'), (select directorID from director where directorName = 'David Fincher')),\n"+
                    "((select movieID from movie where movieName = 'The Dark Knight'), (select directorID from director where directorName = 'Christopher Nolan'));");
  	    
        stmt.executeUpdate("insert into casting(movieID, actorID, role) values ((select movieID from movie where movieName = 'Minari'),(select actorID from actor where actorName = 'Steven Yeun'), 'Main actor'),\n"+
        			"((select movieID from movie where movieName = 'Minari'), (select actorID from actor where actorName = 'Youn Yuhjung'), 'Supporting actor'),\n"+
        			"((select movieID from movie where movieName = 'Edward Scissorhands'), (select actorID from actor where actorName = 'Johnny Depp'), 'Main actor'),\n"+
        		    "((select movieID from movie where movieName = 'Edward Scissorhands'), (select actorID from actor where actorName = 'Winona Ryder'), 'Main actor'),\n"+
        		    "((select movieID from movie where movieName = 'Alice In Wonderland'), (select actorID from actor where actorName = 'Johnny Depp'), 'Main actor'),\n"+
        		    "((select movieID from movie where movieName = 'Alice In Wonderland'), (select actorID from actor where actorName = 'Anne Hathaway'), 'Main actor'),\n"+
        		    "((select movieID from movie where movieName = 'The Social Network'), (select actorID from actor where actorName = 'Jesse Eisenberg'), 'Main actor'),\n"+
        		    "((select movieID from movie where movieName = 'The Social Network'), (select actorID from actor where actorName = 'Andrew Garfield'), 'Supporting Actor'),\n"+
        		    "((select movieID from movie where movieName = 'The Dark Knight'), (select actorID from actor where actorName = 'Christian Bale'), 'Main actor'),\n"+
        		    "((select movieID from movie where movieName = 'The Dark Knight'), (select actorID from actor where actorName = 'Heath Ledger'), 'Main actor');");
        
        stmt.executeUpdate("insert into genre(genreName) values ('Drama'), ('Fantasy'), ('Romance'), ('Adventure'), ('Family'), ('Action'), ('Mystery'), ('Thriller');");
        stmt.executeUpdate("insert into movieGenre (movieID, genreName) values ((select movieID from movie where movieName = 'Minari'), 'Drama'),\n"+
        			"((select movieID from movie where movieName = 'Edward Scissorhands'), 'Fantasy'),\n"+
        			"((select movieID from movie where movieName = 'Edward Scissorhands'), 'Romance'),\n"+
        			"((select movieID from movie where movieName = 'Alice In Wonderland'), 'Fantasy'),\n"+
        			"((select movieID from movie where movieName = 'Alice In Wonderland'), 'Adventure'),\n"+
        			"((select movieID from movie where movieName = 'Alice In Wonderland'), 'Family'),\n"+
        			"((select movieID from movie where movieName = 'The Social Network'), 'Drama'),\n"+
        			"((select movieID from movie where movieName = 'The Dark Knight'), 'Action'),\n"+
        			"((select movieID from movie where movieName = 'The Dark Knight'), 'Drama'),\n"+
        			"((select movieID from movie where movieName = 'The Dark Knight'), 'Mystery'),\n"+
        			"((select movieID from movie where movieName = 'The Dark Knight'), 'Thriller');");
        
        
        //2.1 Winona Ryder won the 'Best supporting actor' award in 1994
        System.out.println("\nStatement : Winona Ryder won the 'Best supporting actor' award in 1994");
        System.out.println("Translated SQL : select actorID from actor where actorName = 'Winona Ryder'");
        // awardName에 고유 제약 조건 추가
        System.out.println("Translated SQL : alter table award add constraint awardName_unique unique (awardName);");
        // 상 이름 추가
        System.out.println("Translated SQL : insert into award(awardName) values ('Best supporting actor') on conflict (awardName) do nothing;");
        // 상 id 받아오기
        System.out.println("Translated SQL : select awardID from award where awardName = 'Best supporting actor'");
        // actorObtain에 추가
        System.out.println("Translated SQL : insert into actorObtain(actorID, awardID, year) values ((select actorID from actor where actorName = 'Winona Ryder'),\n (select awardID from award where awardName = 'Best supporting actor'), 1994);");
        // 실제 쿼리문 수행
        stmt.executeUpdate("alter table award add constraint awardName_unique unique (awardName);");
        stmt.executeUpdate("insert into award(awardName) values ('Best supporting actor') on conflict (awardName) do nothing;");
        stmt.executeUpdate("insert into actorObtain(actorID, awardID, year) values ((select actorID from actor where actorName = 'Winona Ryder'), (select awardID from award where awardName = 'Best supporting actor'), 1994);");
        
        System.out.println("\nUpdated Tables");
        System.out.println("award table");
        System.out.println("+-------------------------------");
        
        rs = stmt.executeQuery("select * from award;");
        System.out.println("|awardID\t|awardName");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("awardID") + "\t\t|" + rs.getString("awardName"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("actorObtain table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from actorObtain;");
        System.out.println("|actorID\t|awardID\t|year");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("actorID")+"\t\t|" + rs.getString("awardID") + "\t\t|"+rs.getInt("year"));
        }
        
        //2.2 Andrew Garfield won the 'Best supporting actor' award in 2011
        System.out.println("\nStatement : Andrew Garfield won the 'Best supporting actor' award in 2011");
        System.out.println("Translated SQL : select actorID from actor where actorName = 'Andrew Garfield'");
        System.out.println("Translated SQL : insert into award(awardName) values ('Best supporting actor') on conflict (awardName) do nothing;");
        System.out.println("Translated SQL : select awardID from award where awardName = 'Best supporting actor'");
        System.out.println("Translated SQL : insert into actorObtain(actorID, awardID, year) values ((select actorID from actor where actorName = 'Andrew Garfield'),\n (select awardID from award where awardName = 'Best supporting actor'), 2011);");
        
        stmt.executeUpdate("insert into award(awardName) values ('Best supporting actor') on conflict (awardName) do nothing;");
        stmt.executeUpdate("insert into actorObtain(actorID, awardID, year) values ((select actorID from actor where actorName = 'Andrew Garfield'), (select awardID from award where awardName = 'Best supporting actor'), 2011);");
        
        System.out.println("\nUpdated Tables");
        System.out.println("award table");
        System.out.println("+-------------------------------");
        
        rs = stmt.executeQuery("select * from award;");
        System.out.println("|awardID\t|awardName");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("awardID") + "\t\t|" + rs.getString("awardName"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("actorObtain table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from actorObtain;");
        System.out.println("|actorID\t|awardID\t|year");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("actorID")+"\t\t|" + rs.getString("awardID") + "\t\t|"+rs.getInt("year"));
        }
        
        //2.3 Jesse Eisenberg won the 'Best main actor' award in 2011
        System.out.println("\nStatement : Jesse Eisenberg won the 'Best main actor' award in 2011");
        System.out.println("Translated SQL : select actorID from actor where actorName = 'Jesse Eisenberg'");
        System.out.println("Translated SQL : insert into award(awardName) values ('Best main actor') on conflict (awardName) do nothing;");
        System.out.println("Translated SQL : select awardID from award where awardName = 'Best main actor'");
        System.out.println("Translated SQL : insert into actorObtain(actorID, awardID, year) values ((select actorID from actor where actorName = 'Jesse Eisenberg'),\n (select awardID from award where awardName = 'Best main actor'), 2011);");
        
        stmt.executeUpdate("insert into award(awardName) values ('Best main actor') on conflict (awardName) do nothing;");
        stmt.executeUpdate("insert into actorObtain(actorID, awardID, year) values ((select actorID from actor where actorName = 'Jesse Eisenberg'), (select awardID from award where awardName = 'Best main actor'), 2011);");
        
        System.out.println("\nUpdated Tables");
        System.out.println("award table");
        System.out.println("+-------------------------------");
        
        rs = stmt.executeQuery("select * from award;");
        System.out.println("|awardID\t|awardName");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("awardID") + "\t\t|" + rs.getString("awardName"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("actorObtain table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from actorObtain;");
        System.out.println("|actorID\t|awardID\t|year");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("actorID")+"\t\t|" + rs.getString("awardID") + "\t\t|"+rs.getInt("year"));
        }
        
        //2.4 Johnny Depp won the 'Best villain actor' award in 2011
        System.out.println("\nStatement : Johnny Depp won the 'Best villain actor' award in 2011");
        System.out.println("Translated SQL : select actorID from actor where actorName = 'Johnny Depp'");
        System.out.println("Translated SQL : insert into award(awardName) values ('Best villain actor') on conflict (awardName) do nothing;");
        System.out.println("Translated SQL : select awardID from award where awardName = 'Best villain actor'");
        System.out.println("Translated SQL : insert into actorObtain(actorID, awardID, year) values ((select actorID from actor where actorName = 'Johnny Depp'),\n (select awardID from award where awardName = 'Best villain actor'), 2011);");
        
        stmt.executeUpdate("insert into award(awardName) values ('Best villain actor') on conflict (awardName) do nothing;");
        stmt.executeUpdate("insert into actorObtain(actorID, awardID, year) values ((select actorID from actor where actorName = 'Johnny Depp'), (select awardID from award where awardName = 'Best villain actor'), 2011);");
        
        rs = stmt.executeQuery("select * from award;");
        System.out.println("|awardID\t|awardName");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("awardID") + "\t\t|" + rs.getString("awardName"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("actorObtain table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from actorObtain;");
        System.out.println("|actorID\t|awardID\t|year");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("actorID")+"\t\t|" + rs.getString("awardID") + "\t\t|"+rs.getInt("year"));
        }
        
        //2.5 Edward Scissorhands won the 'Best fantasy movie' award in 1991
        System.out.println("\nStatement : Edward Scissorhands won the 'Best fantasy movie' award in 1991");
        System.out.println("Translated SQL : select movieID from actor where movieName = 'Edward Scissorhands'");
        System.out.println("Translated SQL : insert into award(awardName) values ('Best fantasy movie') on conflict (awardName) do nothing;");
        System.out.println("Translated SQL : select awardID from award where awardName = 'Best fantasy movie'");
        System.out.println("Translated SQL : insert into movieObtain(movieID, awardID, year) values ((select movieID from movie where movieName = 'Edward Scissorhands'),\n (select awardID from award where awardName = 'Best fantasy movie'), 1991);");
        
        stmt.executeUpdate("insert into award(awardName) values ('Best fantasy movie') on conflict (awardName) do nothing;");
        stmt.executeUpdate("insert into movieObtain(movieID, awardID, year) values ((select movieID from movie where movieName = 'Edward Scissorhands'), (select awardID from award where awardName = 'Best fantasy movie'), 1991);");
        
        rs = stmt.executeQuery("select * from award;");
        System.out.println("|awardID\t|awardName");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("awardID") + "\t\t|" + rs.getString("awardName"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("movieObtain table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from movieObtain;");
        System.out.println("|movieID\t|awardID\t|year");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("movieID")+"\t\t|" + rs.getString("awardID") + "\t\t|"+rs.getInt("year"));
        }
        
        //2.6 Alice In Wonderland won the 'Best fantasy movie' award in 2011
        System.out.println("\nStatement : Alice In Wonderland won the 'Best fantasy movie' award in 2011");
        System.out.println("Translated SQL : select movieID from actor where movieName = 'Alice In Wonderland'");
        System.out.println("Translated SQL : insert into award(awardName) values ('Best fantasy movie') on conflict (awardName) do nothing;");
        System.out.println("Translated SQL : select awardID from award where awardName = 'Best fantasy movie'");
        System.out.println("Translated SQL : insert into movieObtain(movieID, awardID, year) values ((select movieID from movie where movieName = 'Alice In Wonderland'),\n (select awardID from award where awardName = 'Best fantasy movie'), 2011);");
        
        stmt.executeUpdate("insert into award(awardName) values ('Best fantasy movie') on conflict (awardName) do nothing;");
        stmt.executeUpdate("insert into movieObtain(movieID, awardID, year) values ((select movieID from movie where movieName = 'Alice In Wonderland'), (select awardID from award where awardName = 'Best fantasy movie'), 2011);");
        
        rs = stmt.executeQuery("select * from award;");
        System.out.println("|awardID\t|awardName");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("awardID") + "\t\t|" + rs.getString("awardName"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("movieObtain table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from movieObtain;");
        System.out.println("|movieID\t|awardID\t|year");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("movieID")+"\t\t|" + rs.getString("awardID") + "\t\t|"+rs.getInt("year"));
        }
        
        //2.7 Youn Yuhjung won the 'Best Supporting actor' award in 2021
        System.out.println("\nStatement : Youn Yuhjung won the 'Best Supporting actor' award in 2021");
        System.out.println("Translated SQL : select actorID from actor where actorName = 'Youn Yuhjung'");
        System.out.println("Translated SQL : insert into award(awardName) values ('Best supporting actor') on conflict (awardName) do nothing;");
        System.out.println("Translated SQL : select awardID from award where awardName = 'Best supporting actor'");
        System.out.println("Translated SQL : insert into actorObtain(actorID, awardID, year) values ((select actorID from actor where actorName = 'Youn Yuhjung'),\n (select awardID from award where awardName = 'Best supporting actor'), 2021);");
        
        stmt.executeUpdate("insert into award(awardName) values ('Best supporting actor') on conflict (awardName) do nothing;");
        stmt.executeUpdate("insert into actorObtain(actorID, awardID, year) values ((select actorID from actor where actorName = 'Youn Yuhjung'), (select awardID from award where awardName = 'Best supporting actor'), 2021);");
        
        rs = stmt.executeQuery("select * from award;");
        System.out.println("|awardID\t|awardName");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("awardID") + "\t\t|" + rs.getString("awardName"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("actorObtain table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from actorObtain;");
        System.out.println("|actorID\t|awardID\t|year");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("actorID")+"\t\t|" + rs.getString("awardID") + "\t\t|"+rs.getInt("year"));
        }
        
        //2.8 Minari won the 'Best Foreign Language Film' award in 2021
        System.out.println("\nStatement : Minari won the 'Best Foreign Language Film' award in 2021");
        System.out.println("Translated SQL : select movieID from actor where movieName = 'Minari'");
        System.out.println("Translated SQL : insert into award(awardName) values ('Best Foreign Language Film') on conflict (awardName) do nothing;");
        System.out.println("Translated SQL : select awardID from award where awardName = 'Best Foreign Language Film'");
        System.out.println("Translated SQL : insert into movieObtain(movieID, awardID, year) values ((select movieID from movie where movieName = 'Minari'),\n (select awardID from award where awardName = 'Best Foreign Language Film'), 2021);");
        
        stmt.executeUpdate("insert into award(awardName) values ('Best Foreign Language Film') on conflict (awardName) do nothing;");
        stmt.executeUpdate("insert into movieObtain(movieID, awardID, year) values ((select movieID from movie where movieName = 'Minari'), (select awardID from award where awardName = 'Best Foreign Language Film'), 2021);");
        
        rs = stmt.executeQuery("select * from award;");
        System.out.println("|awardID\t|awardName");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("awardID") + "\t\t|" + rs.getString("awardName"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("movieObtain table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from movieObtain;");
        System.out.println("|movieID\t|awardID\t|year");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("movieID")+"\t\t|" + rs.getString("awardID") + "\t\t|"+rs.getInt("year"));
        }
         
        //3.1 Bob rates 3 to 'The Dark Knight'
        System.out.println("\nStatement : Bob rates 3 to 'The Dark Knight'");
        System.out.println("Translated SQL : insert into customerRate (customerID, movieID, rate) values ((SELECT customerID from customer where customerName = 'Bob'),\n (SELECT movieID from movie where movieName = 'The Dark Knight'), 3);");
        System.out.println("Translated SQL : update movie set avgRate = (select avg(rate) from customerRate where movieID = (SELECT movieID from movie where movieName = 'The Dark Knight'))\n where movieID = (SELECT movieID from movie where movieName = 'The Dark Knight');");
        
        stmt.executeUpdate("insert into customerRate (customerID, movieID, rate) values ((SELECT customerID from customer where customerName = 'Bob'), (SELECT movieID from movie where movieName = 'The Dark Knight'), 3);");
        stmt.executeUpdate("update movie set avgRate = (select avg(rate) from customerRate where movieID = (SELECT movieID from movie where movieName = 'The Dark Knight')) where movieID = (SELECT movieID from movie where movieName = 'The Dark Knight');");
        
        rs = stmt.executeQuery("select * from customerRate");
        System.out.println("|customerID\t|movieID\t|rate");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("customerID") + "\t\t|" + rs.getInt("movieID") + "\t\t|" + rs.getInt("rate"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("movie table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from movie;");
        System.out.println("|movieID\t|movieName\t\t\t|releaseYear\t|releaseMonth\t|releaseDate\t\t|publisherName\t\t\t|avgRate");
        while(rs.next()) {
        	int movieID = rs.getInt("movieID");
        	if(movieID == 1) {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	else {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	
        	double avgRate = rs.getDouble("avgRate");
        	if(avgRate == 0 && rs.wasNull())
            {
        		if(movieID == 1) { System.out.println("\t\t\t\t|" + "null"); }
        		else if (movieID == 2) { System.out.println("\t|" + "null"); }
        		else {System.out.println("\t\t|" + "null");}
            }
        	else{
            	if(movieID == 1) { System.out.println("\t\t\t\t|" + avgRate); }
        		else if (movieID == 2) { System.out.println("\t|" + avgRate); }
        		else { System.out.println("\t\t|" + avgRate); }
            }
        }

        
        //3.2 Bell rates 5 to the movies whose director is 'Tim Burton'
        System.out.println("\nStatement : Bell rates 5 to the movies whose director is 'Tim Burton'");
        System.out.println("Translated SQL : CREATE OR REPLACE FUNCTION rate_movie_from_director(character varying(20), character varying(50), integer) RETURNS void AS $$\r\n"
        		+ "DECLARE\r\n"
        		+ "	r RECORD;\r\n"
        		+ "BEGIN\r\n"
        		+ "	for r in (select movieID from make where directorID = (select directorID from director where directorName = $2))\r\n"
        		+ "		LOOP\r\n"
        		+ "			insert into customerRate(customerID, movieID, rate) values ((select customerID from customer where customerName = $1), r.movieID, $3);\r\n"
        		+ "			update movie set avgRate = (select avg(rate) from customerRate where movieID = r.movieID) where movieID = r.movieID;\r\n"
        		+ "		END LOOP;\r\n"
        		+ "RETURN;\r\n"
        		+ "END;\r\n"
        		+ "$$ language plpgsql;");
        System.out.println("Translated SQL : select rate_movie_from_director('Bell', 'Tim Burton', 5);");
        stmt.execute("CREATE OR REPLACE FUNCTION rate_movie_from_director(character varying(20), character varying(50), integer) RETURNS void AS $$\r\n"
        		+ "DECLARE\r\n"
        		+ "	r RECORD;\r\n"
        		+ "	BEGIN\r\n"
        		+ "	for r in (select movieID from make where directorID = (select directorID from director where directorName = $2))\r\n"
        		+ "		LOOP\r\n"
        		+ "			insert into customerRate(customerID, movieID, rate) values ((select customerID from customer where customerName = $1), r.movieID, $3);\r\n"
        		+ "			update movie set avgRate = (select avg(rate) from customerRate where movieID = r.movieID) where movieID = r.movieID;\r\n"
        		+ "		END LOOP;\r\n"
        		+ "RETURN;\r\n"
        		+ "END;\r\n"
        		+ "$$ language plpgsql;\r\n"
        		+ "");
        stmt.executeQuery("select rate_movie_from_director('Bell', 'Tim Burton', 5);");

        rs = stmt.executeQuery("select * from customerRate");
        System.out.println("|customerID\t|movieID\t|rate");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("customerID") + "\t\t|" + rs.getInt("movieID") + "\t\t|" + rs.getInt("rate"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("movie table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from movie;");
        System.out.println("|movieID\t|movieName\t\t\t|releaseYear\t|releaseMonth\t|releaseDate\t\t|publisherName\t\t\t|avgRate");
        while(rs.next()) {
        	int movieID = rs.getInt("movieID");
        	if(movieID == 1) {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	else {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	
        	double avgRate = rs.getDouble("avgRate");
        	if(avgRate == 0 && rs.wasNull())
            {
        		if(movieID == 1) { System.out.println("\t\t\t\t|" + "null"); }
        		else if (movieID == 2) { System.out.println("\t|" + "null"); }
        		else {System.out.println("\t\t|" + "null");}
            }
        	else{
            	if(movieID == 1) { System.out.println("\t\t\t\t|" + avgRate); }
        		else if (movieID == 2) { System.out.println("\t|" + avgRate); }
        		else { System.out.println("\t\t|" + avgRate); }
            }
        }
        
        //3.3 Jill rates 4 to the movies whose main actor is female
        System.out.println("\nStatement : Jill rates 4 to the movies whose main actor is female");
        System.out.println("Translated SQL : CREATE OR REPLACE FUNCTION rate_movie_from_gender_role(character varying(20), character(6), character varying(20), integer) RETURNS void AS $$\r\n"
        		+ "DECLARE\r\n"
        		+ "	r RECORD;\r\n"
        		+ "	rr RECORD;\r\n"
        		+ "BEGIN\r\n"
        		+ "	for r in (select actorID from actor where gender = $2)\r\n"
        		+ "		LOOP\r\n"
        		+ "		   for rr in (select movieID from casting where actorID = r.actorID and role = $3)\r\n"
        		+ "		   	LOOP\r\n"
        		+ "			     	insert into customerRate(customerID, movieID, rate) values ((select customerID from customer where customerName = $1), rr.movieID, $4);\r\n"
        		+ "			     	update movie set avgRate = (select avg(rate) from customerRate where movieID = rr.movieID) where movieID = rr.movieID;\r\n"
        		+ "			END LOOP;\r\n"
        		+ "		END LOOP;\r\n"
        		+ "RETURN;\r\n"
        		+ "END;\r\n"
        		+ "$$ language plpgsql;");
        System.out.println("Translated SQL : select rate_movie_from_gender_role('Jill', 'Female', 'Main actor', 4);");
       
        
        
        stmt.execute("CREATE OR REPLACE FUNCTION rate_movie_from_gender_role(character varying(20), character(6), character varying(20), integer) RETURNS void AS $$\r\n"
        		+ "DECLARE\r\n"
        		+ "	r RECORD;\r\n"
        		+ "	rr RECORD;\r\n"
        		+ "BEGIN\r\n"
        		+ "	for r in (select actorID from actor where gender = $2)\r\n"
        		+ "		LOOP\r\n"
        		+ "			for rr in (select movieID from casting where actorID = r.actorID and role = $3)\r\n"
        		+ "				LOOP\r\n"
        		+ "					insert into customerRate(customerID, movieID, rate) values ((select customerID from customer where customerName = $1), rr.movieID, $4);\r\n"
        		+ "					update movie set avgRate = (select avg(rate) from customerRate where movieID = rr.movieID) where movieID = rr.movieID;\r\n"
        		+ "				END LOOP;\r\n"
        		+ "		END LOOP;\r\n"
        		+ "RETURN;\r\n"
        		+ "END;\r\n"
        		+ "$$ language plpgsql;\r\n"
        		+ "");
        stmt.executeQuery("select rate_movie_from_gender_role('Jill', 'Female', 'Main actor', 4);");
        
        rs = stmt.executeQuery("select * from customerRate");
        System.out.println("|customerID\t|movieID\t|rate");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("customerID") + "\t\t|" + rs.getInt("movieID") + "\t\t|" + rs.getInt("rate"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("movie table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from movie;");
        System.out.println("|movieID\t|movieName\t\t\t|releaseYear\t|releaseMonth\t|releaseDate\t\t|publisherName\t\t\t|avgRate");
        while(rs.next()) {
        	int movieID = rs.getInt("movieID");
        	if(movieID == 1) {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	else {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	
        	double avgRate = rs.getDouble("avgRate");
        	if(avgRate == 0 && rs.wasNull())
            {
        		if(movieID == 1) { System.out.println("\t\t\t\t|" + "null"); }
        		else if (movieID == 2) { System.out.println("\t|" + "null"); }
        		else {System.out.println("\t\t|" + "null");}
            }
        	else{
            	if(movieID == 1) { System.out.println("\t\t\t\t|" + avgRate); }
        		else if (movieID == 2) { System.out.println("\t|" + avgRate); }
        		else { System.out.println("\t\t|" + avgRate); }
            }
        }
        
        //3.4 Jack rates 4 to the fantasy movies
        System.out.println("\nStatement : Jack rates 4 to the fantasy movies");
        System.out.println("Translated SQL : CREATE OR REPLACE FUNCTION rate_movie_from_genre(character varying(20), character varying(20), integer) RETURNS void AS $$\r\n"
        		+ "DECLARE\r\n"
        		+ "	r RECORD;\r\n"
        		+ "BEGIN\r\n"
        		+ "	for r in (select movieID from movieGenre where genreName = $2)\r\n"
        		+ "		LOOP\r\n"
        		+ "			INSERT INTO customerRate(customerId, movieID, rate) values ((select customerID from customer where customerName = $1), r.movieID, $3);\r\n"
        		+ "			update movie set avgRate = (select avg(rate) from customerRate where movieID = r.movieID) where movieID = r.movieID;\r\n"
        		+ "		END LOOP;\r\n"
        		+ "RETURN;\r\n"
        		+ "END;\r\n"
        		+ "$$ language plpgsql;");
        System.out.println("select rate_movie_from_genre('Jack', 'Fantasy', 4);");
        
        stmt.execute("CREATE OR REPLACE FUNCTION rate_movie_from_genre(character varying(20), character varying(20), integer) RETURNS void AS $$\r\n"
        		+ "DECLARE\r\n"
        		+ "	r RECORD;\r\n"
        		+ "BEGIN\r\n"
        		+ "	for r in (select movieID from movieGenre where genreName = $2)\r\n"
        		+ "		LOOP\r\n"
        		+ "			INSERT INTO customerRate(customerId, movieID, rate) values ((select customerID from customer where customerName = $1), r.movieID, $3);\r\n"
        		+ "			update movie set avgRate = (select avg(rate) from customerRate where movieID = r.movieID) where movieID = r.movieID;\r\n"
        		+ "		END LOOP;\r\n"
        		+ "RETURN;\r\n"
        		+ "END;\r\n"
        		+ "$$ language plpgsql;\r\n"
        		+ "");
        stmt.executeQuery("select rate_movie_from_genre('Jack', 'Fantasy', 4);");
        
        rs = stmt.executeQuery("select * from customerRate");
        System.out.println("|customerID\t|movieID\t|rate");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("customerID") + "\t\t|" + rs.getInt("movieID") + "\t\t|" + rs.getInt("rate"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("movie table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from movie;");
        System.out.println("|movieID\t|movieName\t\t\t|releaseYear\t|releaseMonth\t|releaseDate\t\t|publisherName\t\t\t|avgRate");
        while(rs.next()) {
        	int movieID = rs.getInt("movieID");
        	if(movieID == 1) {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	else {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	
        	double avgRate = rs.getDouble("avgRate");
        	if(avgRate == 0 && rs.wasNull())
            {
        		if(movieID == 1) { System.out.println("\t\t\t\t|" + "null"); }
        		else if (movieID == 2) { System.out.println("\t|" + "null"); }
        		else {System.out.println("\t\t|" + "null");}
            }
        	else{
            	if(movieID == 1) { System.out.println("\t\t\t\t|" + avgRate); }
        		else if (movieID == 2) { System.out.println("\t|" + avgRate); }
        		else { System.out.println("\t\t|" + avgRate); }
            }
        }
        
        //3.5 John rates 5 to the movies whose actor won the “Best supporting actor” award
        System.out.println("\nJohn rates 5 to the movies whose actor won the “Best supporting actor” award");
        System.out.println("Translated SQL : CREATE OR REPLACE FUNCTION rate_movie_from_award(character varying(20), character varying(50), integer) RETURNS void AS $$\r\n"
        		+ "DECLARE\r\n"
        		+ "	r RECORD;\r\n"
        		+ "	rr RECORD;\r\n"
        		+ "BEGIN\r\n"
        		+ "	for r in (select actorID from actorObtain where awardID = (select awardID from award where awardName = $2))\r\n"
        		+ "		LOOP\r\n"
        		+ "		   for rr in (select movieID from casting where actorID = r.actorID)\r\n"
        		+ "		     LOOP\r\n"
        		+ "			    insert into customerRate(customerID, movieID, rate) values ((select customerID from customer where customerName = $1), rr.movieID, $3);\r\n"
        		+ "			    update movie set avgRate = (select avg(rate) from customerRate where movieID = rr.movieID) where movieID = rr.movieID;\r\n"
        		+ "		     END LOOP;\r\n"
        		+ "		END LOOP;\r\n"
        		+ "RETURN;\r\n"
        		+ "END;\r\n"
        		+ "$$ language plpgsql;\r\n"
        		+ "");
        System.out.println("Translated SQL : select rate_movie_from_award('John', 'Best supporting actor', 5);");
        
        stmt.execute("CREATE OR REPLACE FUNCTION rate_movie_from_award(character varying(20), character varying(50), integer) RETURNS void AS $$\r\n"
        		+ "DECLARE\r\n"
        		+ "	r RECORD;\r\n"
        		+ "	rr RECORD;\r\n"
        		+ "BEGIN\r\n"
        		+ "	for r in (select actorID from actorObtain where awardID = (select awardID from award where awardName = $2))\r\n"
        		+ "		LOOP\r\n"
        		+ "			for rr in (select movieID from casting where actorID = r.actorID)\r\n"
        		+ "				LOOP\r\n"
        		+ "					insert into customerRate(customerID, movieID, rate) values ((select customerID from customer where customerName = $1), rr.movieID, $3);\r\n"
        		+ "					update movie set avgRate = (select avg(rate) from customerRate where movieID = rr.movieID) where movieID = rr.movieID;\r\n"
        		+ "				END LOOP;\r\n"
        		+ "		END LOOP;\r\n"
        		+ "RETURN;\r\n"
        		+ "END;\r\n"
        		+ "$$ language plpgsql;");
        stmt.executeQuery("select rate_movie_from_award('John', 'Best supporting actor', 5);");
        
        rs = stmt.executeQuery("select * from customerRate");
        System.out.println("|customerID\t|movieID\t|rate");
        while(rs.next()) {
        	System.out.println("|" + rs.getInt("customerID") + "\t\t|" + rs.getInt("movieID") + "\t\t|" + rs.getInt("rate"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("movie table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from movie;");
        System.out.println("|movieID\t|movieName\t\t\t|releaseYear\t|releaseMonth\t|releaseDate\t\t|publisherName\t\t\t|avgRate");
        while(rs.next()) {
        	int movieID = rs.getInt("movieID");
        	if(movieID == 1) {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	else {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	
        	double avgRate = rs.getDouble("avgRate");
        	if(avgRate == 0 && rs.wasNull())
            {
        		if(movieID == 1) { System.out.println("\t\t\t\t|" + "null"); }
        		else if (movieID == 2) { System.out.println("\t|" + "null"); }
        		else {System.out.println("\t\t|" + "null");}
            }
        	else{
            	if(movieID == 1) { System.out.println("\t\t\t\t|" + avgRate); }
        		else if (movieID == 2) { System.out.println("\t|" + avgRate); }
        		else { System.out.println("\t\t|" + avgRate); }
            }
        }
        
        //4. Select the names of the movies whose actor are dead
        System.out.println("\nSelect the names of the movies whose actor are dead");
        System.out.println("Translated SQL : select movieName from movie where movieID = (select movieID from casting where actorID = (select actorID from actor where dateOfDeath is not null));");
        
        rs = stmt.executeQuery("select movieName from movie where movieID = (select movieID from casting where actorID = (select actorID from actor where dateOfDeath is not null));");
        
        System.out.println("|movieName");
        while(rs.next()) {
        	System.out.println("|" + rs.getString("movieName"));
        }
        
        //5. Select the names of the directors who cast the same actor more than once
        System.out.println("\nSelect the names of the directors who cast tha same actor more than once");
        System.out.println("Translated SQL : select directorName\r\n"
        		+ "from director natural join (select C.directorID, count(directorID) as cnt_direc from\r\n"
        		+ "(select actorID, directorID from\r\n"
        		+ "(select T.actorID, count(actorID) as cnt_actor\r\n"
        		+ "from (select * from make join casting using(movieID)) T\r\n"
        		+ "group by actorID) M, make\r\n"
        		+ "where cnt_actor > 1) C\r\n"
        		+ "group by C.directorID) A\r\n"
        		+ "where cnt_direc > 1;");
        
        rs = stmt.executeQuery("select directorName\r\n"
        		+ "from director natural join (select C.directorID, count(directorID) as cnt_direc from\r\n"
        		+ "(select actorID, directorID from\r\n"
        		+ "(select T.actorID, count(actorID) as cnt_actor\r\n"
        		+ "from (select * from make join casting using(movieID)) T\r\n"
        		+ "group by actorID) M, make\r\n"
        		+ "where cnt_actor > 1) C\r\n"
        		+ "group by C.directorID) A\r\n"
        		+ "where cnt_direc > 1;");
        
        System.out.println("\n|directorName");
        while(rs.next()) {
        	System.out.println("|" + rs.getString("directorName"));
        }
        
        //6. Select the names of the movies and the genres, where movies have the common genre
        System.out.println("\nSelect the names of the movies and the genres, where movies have the common genre");
        System.out.println("Translated SQL : select movieName, T.genreName\r\n"
        		+ "from (select genreName, count(genreName) as cnt_movie_from_genre from movieGenre group by genreName) T,\r\n"
        		+ "(select distinct genreName, movieID from movieGenre) M join movie using(movieID)\r\n"
        		+ "where T.cnt_movie_from_genre > 1 and M.genreName = T.genreName;");
        
        rs = stmt.executeQuery("select movieName, T.genreName\r\n"
        		+ "from (select genreName, count(genreName) as cnt_movie_from_genre from movieGenre group by genreName) T,\r\n"
        		+ "(select distinct genreName, movieID from movieGenre) M join movie using(movieID)\r\n"
        		+ "where T.cnt_movie_from_genre > 1 and M.genreName = T.genreName;");
        System.out.println("\n|movieName" +"\t\t|genreName");
        while(rs.next()) {
        	String Mname = rs.getString("movieName");
        	if (Mname.equals("Minari")) { System.out.printf("|%-9s\t\t|%s\n", Mname, rs.getString("genreName")); }
        	else { System.out.printf("|%-9s\t|%s\n", Mname, rs.getString("genreName")); }	
        }
        
        //7. Delete the movies whose director or actor did not get any award and delete data from related tables
        System.out.println("\nDelete the movies whose director or actor did not get any award and delete data from related tables");
        System.out.println("Translated SQL : delete from movie where movieID not in\r\n"
        		+ "((select movieID from actorObtain join casting using(actorID)) union\r\n"
        		+ "(select movieID from directorObtain join make using(directorID)));");
        
        stmt.executeUpdate("delete from movie where movieID not in\r\n"
        		+ "((select movieID from actorObtain join casting using(actorID)) union\r\n"
        		+ "(select movieID from directorObtain join make using(directorID)));");
        
        System.out.println("\nUpdated Tables");
        System.out.println("movie table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from movie;");
        System.out.println("|movieID\t|movieName\t\t\t|releaseYear\t|releaseMonth\t|releaseDate\t\t|publisherName\t\t\t|avgRate");
        while(rs.next()) {
        	int movieID = rs.getInt("movieID");
        	if(movieID == 1) {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	else {
        		System.out.printf("|" + movieID + "\t\t|" + rs.getString("movieName") + "\t\t|" + rs.getInt("releaseYear") + "\t\t|" + rs.getInt("releaseMonth")+ "\t\t|" + rs.getInt("releaseDate")+ "\t\t\t|" + rs.getString("publisherName"));   
        	}
        	
        	double avgRate = rs.getDouble("avgRate");
        	if(avgRate == 0 && rs.wasNull())
            {
        		if(movieID == 1) { System.out.println("\t\t\t\t|" + "null"); }
        		else if (movieID == 2) { System.out.println("\t|" + "null"); }
        		else {System.out.println("\t\t|" + "null");}
            }
        	else{
            	if(movieID == 1) { System.out.println("\t\t\t\t|" + avgRate); }
        		else if (movieID == 2) { System.out.println("\t|" + avgRate); }
        		else { System.out.println("\t\t|" + avgRate); }
            }
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("movieGenre table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from movieGenre");
        System.out.println("|movieID" +"\t|genreName");
        while(rs.next()) {
        	System.out.printf("|%-9d\t|%s\n", rs.getInt("movieID"), rs.getString("genreName"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("casting table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from casting");
        System.out.println("|movieID" +"\t|actorID"+"\t|role");
        while(rs.next()) {
        	System.out.printf("|%-9d\t|%-9d\t|%s\n", rs.getInt("movieID"), rs.getInt("actorID"), rs.getString("role"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("make table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from make");
        System.out.println("|movieID" +"\t|directorID");
        while(rs.next()) {
        	System.out.printf("|%-9d\t|%-9d\n", rs.getInt("movieID"), rs.getInt("directorID"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("customerRate table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from customerRate");
        System.out.println("|customerID" +"\t|movieID" + "\t|rate");
        while(rs.next()) {
        	System.out.printf("|%-9d\t|%-9d\t|%-9d\n", rs.getInt("customerID"), rs.getInt("movieID"), rs.getInt("rate"));
        }
        
        //8. Delete all customers and delete data from related tables
        System.out.println("\nDelete all customers and delete data from related tables");
        System.out.println("Translated SQL : delete from customer;");
        
        stmt.executeUpdate("delete from customer;");
        
        
        System.out.println("\nUpdated Tables");
        System.out.println("customer table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from customer;");
        System.out.println("|customerID" +"\t|customerName" + "\t|dateOfBirth" + "\t|gender");
        
        while(rs.next()) {
        	System.out.printf("|%-9d\t|%s\t%s\t|%s\n", rs.getInt("customerID"), rs.getString("customerName"), rs.getDate("rate").toString(), rs.getString("gender"));
        }
        
        System.out.println("\nUpdated Tables");
        System.out.println("customerRate table");
        System.out.println("+-------------------------------------");
        rs = stmt.executeQuery("select * from customerRate");
        System.out.println("|customerID" +"\t|movieID" + "\t|rate");
        while(rs.next()) {
        	System.out.printf("|%-9d\t|%-9d\t|%-9d\n", rs.getInt("customerID"), rs.getInt("movieID"), rs.getInt("rate"));
        }
        
        //9. Delete all tables and date(make the database empty)
        stmt.execute("DROP TABLE IF EXISTS director CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS actor CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS movie CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS award CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS genre CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS movieGenre CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS movieObtain CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS actorObtain CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS directorObtain CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS casting CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS make CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS customer CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS customerRate CASCADE;");
        
        stmt.execute("DROP FUNCTION if exists rate_movie_from_director(character varying, character varying, integer);");
        stmt.execute("DROP FUNCTION if exists rate_movie_from_gender_role(character varying, character, character varying, integer);");
        stmt.execute("DROP FUNCTION if exists rate_movie_from_genre(character varying, character varying, integer);");
        stmt.execute("DROP FUNCTION if exists rate_movie_from_award(character varying, character varying, integer);");
        
        connection.close();
    }
}
