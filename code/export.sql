--------------------------------------------------------
--  File created - Thursday-December-31-2009   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table ARTISTS
--------------------------------------------------------

  CREATE TABLE "ARTISTS" 
   (	"ARTIST_ID" NUMBER, 
	"ARTIST_NAME" VARCHAR2(500)
   ) ;
--------------------------------------------------------
--  DDL for Table DISCS
--------------------------------------------------------

  CREATE TABLE "DISCS" 
   (	"DISC_ID" VARCHAR2(8), 
	"TITLE" VARCHAR2(1000), 
	"ARTIST" NUMBER, 
	"GENRE" NUMBER, 
	"YEAR" NUMBER(4,0), 
	"TRACKS_NUM" NUMBER(2,0), 
	"REVISION" NUMBER(3,0), 
	"EXT" VARCHAR2(4000)
   ) ;
--------------------------------------------------------
--  DDL for Table GENRES
--------------------------------------------------------

  CREATE TABLE "GENRES" 
   (	"GENRE_ID" NUMBER, 
	"GENRE_NAME" VARCHAR2(50)
   ) ;
--------------------------------------------------------
--  DDL for Table THEMES
--------------------------------------------------------

  CREATE TABLE "THEMES" 
   (	"DISC_ID" VARCHAR2(8), 
	"ARTIST" NUMBER, 
	"TRACK_NUM" NUMBER(2,0), 
	"THEME_NAME" VARCHAR2(1000), 
	"DISC_REVISION" NUMBER(3,0), 
	"EXT" VARCHAR2(4000)
   ) ;
--------------------------------------------------------
--  Constraints for Table DISCS
--------------------------------------------------------

  ALTER TABLE "DISCS" ADD CONSTRAINT "DISCS_PK" PRIMARY KEY ("DISC_ID", "REVISION") ENABLE;
 
  ALTER TABLE "DISCS" MODIFY ("DISC_ID" NOT NULL ENABLE);
 
  ALTER TABLE "DISCS" MODIFY ("TITLE" NOT NULL ENABLE);
 
  ALTER TABLE "DISCS" MODIFY ("REVISION" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ARTISTS
--------------------------------------------------------

  ALTER TABLE "ARTISTS" ADD CONSTRAINT "ARTISTS_PK" PRIMARY KEY ("ARTIST_ID") ENABLE;
 
  ALTER TABLE "ARTISTS" MODIFY ("ARTIST_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table THEMES
--------------------------------------------------------

  ALTER TABLE "THEMES" MODIFY ("DISC_ID" NOT NULL ENABLE);
 
  ALTER TABLE "THEMES" MODIFY ("TRACK_NUM" NOT NULL ENABLE);
 
  ALTER TABLE "THEMES" MODIFY ("DISC_REVISION" NOT NULL ENABLE);
 
  ALTER TABLE "THEMES" ADD CONSTRAINT "THEMES_PK" PRIMARY KEY ("DISC_ID", "DISC_REVISION", "TRACK_NUM") ENABLE;
--------------------------------------------------------
--  Constraints for Table GENRES
--------------------------------------------------------

  ALTER TABLE "GENRES" ADD CONSTRAINT "GENRES_PK" PRIMARY KEY ("GENRE_ID") ENABLE;
--------------------------------------------------------
--  DDL for Index DISCS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DISCS_PK" ON "DISCS" ("DISC_ID", "REVISION") 
  ;
--------------------------------------------------------
--  DDL for Index GENRES_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GENRES_PK" ON "GENRES" ("GENRE_ID") 
  ;
--------------------------------------------------------
--  DDL for Index THEMES_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "THEMES_PK" ON "THEMES" ("DISC_ID", "DISC_REVISION", "TRACK_NUM") 
  ;
--------------------------------------------------------
--  DDL for Index ARTISTS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ARTISTS_PK" ON "ARTISTS" ("ARTIST_ID") 
  ;

--------------------------------------------------------
--  Ref Constraints for Table DISCS
--------------------------------------------------------

  ALTER TABLE "DISCS" ADD CONSTRAINT "DISCS_FK" FOREIGN KEY ("ARTIST")
	  REFERENCES "ARTISTS" ("ARTIST_ID") ENABLE;
 
  ALTER TABLE "DISCS" ADD CONSTRAINT "DISCS_FK2" FOREIGN KEY ("GENRE")
	  REFERENCES "GENRES" ("GENRE_ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table THEMES
--------------------------------------------------------

  ALTER TABLE "THEMES" ADD CONSTRAINT "THEMES_FK" FOREIGN KEY ("DISC_ID", "DISC_REVISION")
	  REFERENCES "DISCS" ("DISC_ID", "REVISION") ENABLE;
 
  ALTER TABLE "THEMES" ADD CONSTRAINT "THEMES_FK2" FOREIGN KEY ("ARTIST")
	  REFERENCES "ARTISTS" ("ARTIST_ID") ENABLE;
