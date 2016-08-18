(defproject datomic-test "0.0.1"
  :repositories [["my.datomic.com" {:url "https://my.datomic.com/repo"
                                    :username :env/mydatomiccomusername
                                    :password :env/mydatomiccompassword}]]

  :dependencies
  [[org.clojure/clojure "1.7.0"]

   ; Database-related
   [org.xerial/sqlite-jdbc "3.7.2"]
   [postgresql "9.1-901.jdbc4"]
   [com.datomic/datomic-pro "0.9.5359"]
   [korma "0.4.2"]
   [environ "1.0.3"]]

; Lein plugins' dependencies raise several warnings... hence clojure core excluded
  :plugins [[lein-environ    "1.0.3"]
            [lein-datomic    "0.2.0"]]

  :repl-options {:timeout 1200000} ; 120s

  :pedantic? :warn

  :min-lein-version "2.0.0"

  :profiles {:dev {:dependencies [;[lein-light-nrepl "0.0.9"]
                                  [org.clojure/tools.trace "0.7.9"]]

                   :env { ; Local dbserver for development
                         :dbsubprotocol "postgresql"
                         :dbsubname "//127.0.0.1:5432/datomic-test"
                         :dbuser "defusu"
                         :dbpassword "password"}
                    :datomic {:config "confmgmt/dbserver/datomic-sql-transactor.properties"
                              :db-uri "datomic:sql://datomic-test?jdbc:postgresql://127.0.0.1:5432/datomic-test?user=defusu&password=password"}}}

  :datomic {:schemas ["confmgmt/dbserver" ["userschema.edn" "worldschema.edn"]]}

  :main ^:skip-aot dbtools.datomic)
