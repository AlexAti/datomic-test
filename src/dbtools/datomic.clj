(ns dbtools.datomic
  (:require [environ.core :refer [env]]
            [datomic.api :as d]))


(defn datomic-uri-from-project-file [profile]
  (->> "project.clj"
       slurp
       read-string
       rest ; defproject
       rest ; datomic-test
       rest ; version
       (apply hash-map)
       :profiles
       profile
       :datomic
       :db-uri))

(def dev-uri (datomic-uri-from-project-file :dev))

(defn get-user-schema [conn]
  (let [db (d/db conn)
        query '[:find ?id
                :where [?e :db/ident ?id]
                       [_ :db.install/attribute ?e]
                       [?e :db.install/partition :db.part/db]]
        result (d/q query db)]
      result))

;------ three versions -----------
;(defn log-append-event [conn user-id event]
;  (let [id (d/tempid :db.part/user)]
;    @(d/transact conn [[:db/add id :log/user-id user-id]
;                       [:db/add id :log/event event]])))

;(defn log-append-event [conn user-id event]
;  @(d/transact conn [{:db/id (d/tempid :db.part/user)
;                      :log/user-id user-id
;                      :log/event event}]))
;
(defn log-append-event [conn user-id event]
  @(d/transact conn [[:db/add (d/tempid :db.part/user -1) :log/user-id user-id]
                     [:db/add (d/tempid :db.part/user -1) :log/event event]]))
; -----------------end-------------)


(defn log-head [conn user]
  (let [db (d/db conn)
        query '[:find ?e ?event
                :in $ ?user-id
                :where [?e :log/user-id ?user-id]
                       [?e :log/event ?event]]

        result (d/q query db user)]
    (take 2 result)))

(defn at-least-do-something-without-schema [conn]
  @(d/transact conn [[:db/add :db.part/user :db/doc "Hey ho! I work"]]))

(defn test-datomic []
  (let [user "someone"
        dummy (d/create-database dev-uri)
        conn (d/connect dev-uri)]

    (println "dummy is " dummy)
    (println "conn is " conn)

    (println "Behold the defined attributes...")
    (println (get-user-schema conn))
    (doseq [a (get-user-schema conn)]
      (println "Attr: " a))

    (println "Writing to the database...")
    (doseq [ev ["ate bread" "flossed" "jumped a wall" "chanted La Marseillese"]]
      (println "..." user " " ev " en db " conn)
      (log-append-event conn
                        user
                        ev))
    (println "Trying to get back the log...")
    (println (log-head conn
                       user))))

;(test-datomic)

;(defn worldmap-add-country [db countryname regioncoll]
;  (let [newcountryid (d/tempid :db.part/user)]
;    (d/transact db [{:db/id newcountryid
;                     :worldmap/country countryname}
;                    {:db/id (d/tempid :db.part/user)
;                     :worldmap/regions
;                            #{:worldmap/worldarea #{{:worldmap/coordinates {:worldmap/longitude 1 :worldmap/latitude 0}}
;                                                    {:worldmap/coordinates {:worldmap/longitude 1 :worldmap/latitude 0}}}
;                              :worldmap/worldarea #{{:worldmap/coordinates {:worldmap/longitude 1 :worldmap/latitude 0}}
;                                                    {:worldmap/coordinates {:worldmap/longitude 1 :worldmap/latitude 0}}}}}])))
;
;
;(defn worldmap-get-all-regions [])
