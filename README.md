datomic-test
============

I have some problems with saving information that uses the schemas loaded with lein datomic initialize:

````
    (in-ns 'dbtools.datomic)
    dev-uri ; returns the uri that we will use
    (d/create-database dev-uri)
    (def conn (d/connect dev-uri)) ; I get a proper value for a connection here without a problem

    ; Now the errors begin:

    (get-user-schema conn)
    ; Returns nothing, although schemas were defined

    (log-append-event conn "James" "jumps")
    ; IllegalArgumentExceptionInfo :db.error/not-an-entity \
    ; Unable to resolve entity: :log/user-id  datomic.error/arg (error.clj:57)

    (at-least-do-something-without-schema conn) ; sometimes works! But sometimes timeout...
````


Steps to replicate environment
==============================


###Prerequisites and configuration for running
  1. Install ansible
  2. Install leiningen
  3. Install a datomic pro transactor somewhere and write its location in ~/.lein/profiles.clj, e.g.:

  ````
  {:user
    {:datomic {:install-location "~/Code/datomic/datomic-pro-0.9.5359"}}}
  ````
  4. The following environment variables need to be set for the program to execute correctly:
     - MYDATOMICCOMUSERNAME (can be retrieved from my.datomic.com)
     - MYDATOMICCOMPASSWORD (can be retrieved from my.datomic.com)
  5. Also add your license key in confmgmt/dbserver/datomic-sql-transactor.properties
  6. Execute:

  ````
      cd confmgmt/dbserver; vagrant up; cd -; lein datomic start & sleep 10; lein datomic initialize; lein repl
  ````
