(ns dbtools.database
  (:require [environ.core :refer [env]])
  (:use [korma.db]
        [korma.core]))


(defdb dblocal (postgres {:subprotocol (env :dbsubprotocol)
                          :subname     (env :dbsubname)
                          :user        (env :dbuser)
                          :password    (env :dbpassword)}))

; Using the following tables:
; create table users (userid serial primary key, name text);
; create table memelement (memelementid serial primary key, easiness real);
; create table answer (answerid serial primary key, userid int references
; users(userid), memelementid int references memelement(memelementid),
; repn int, easiness real, quality int, timestamp timestamp with time zone not null default now());

(declare user memelement answer)

(defentity user
  (database dblocal)
  (table :users)
  (pk :userid)
  (entity-fields :name)
  (has-many answer {:fk :userid}))

(defentity memelement
  (database dblocal)
  (table :memelement)
  (pk :memelementid)
  (entity-fields :easiness)
  (has-many answer {:fk :memelementid}))

(defentity answer
  (database dblocal)
  (table :answer)
  (pk :answerid)
  (entity-fields :userid
                 :memelementid
                 :repn
                 :easiness
                 :quality
                 :timestamp)
  ) ; Foreign key relations pending

(defn new-user [name]
  "Creates a new user in db and returns its ID."
  (:userid
   (insert user
           (values [{:name name}]))))

(defn new-memelement []
  "Creates a new memelement in db and returns its ID."
  (:memelementid
   (insert memelement
           (values [{:easiness 2.5}]))))

(defn get-users []
  (select user
          (fields :userid
                  :name)))

(defn get-memelements []
  (select memelement
          (fields :memelementid
                  :easiness)))

(defn answer-attempt [userid memelementid]
  "Retrieves from db the information for the user attempt of answering memelement."
  (select answer
          (fields :repn :easiness)
          (order :timestamp)
          (limit :1)
          (where {:userid userid
                  :memelementid memelementid})))

(defn save-answer [userid memelementid repn easiness quality]
  "Saves in database the result of a user answering a memelement."
  (insert answer
          (values [{:userid userid
                    :memelementid memelementid
                    :repn repn
                    :easiness easiness
                    :quality quality}])))
