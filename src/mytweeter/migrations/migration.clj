(ns mytweeter.migrations.migration
  (:require [mytweeter.db :as db])
  (:require [clojure.java.jdbc :as sql]))

(defn table-exists? [tablename]
  (empty?
   (sql/query db/spec [(str "select * from information_schema.tables where table_name ='" tablename  "'" )])))

(defn migrate []
  (if (reduce #(and %2 %1) (map table-exists? '("users" "tweets")))
    (do
      (println "Running migrations")
      (sql/db-do-commands db/spec
                          (sql/create-table-ddl
                           :tweets
                           [:id :serial "Primary Key"]
                           [:body :varchar "NOT NULL"]
                           [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
      (sql/db-do-commands db/spec
                          (sql/create-table-ddl
                           :users
                           [:id :serial "Primary Key"]
                           [:first_name :varchar "NOT NULL"])))
    (println "Already migrated")))
