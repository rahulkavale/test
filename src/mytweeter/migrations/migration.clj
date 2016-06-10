(ns mytweeter.migrations.migration
  (:require [mytweeter.db :as db])
  (:require [clojure.java.jdbc :as sql]))

(defn table-exists? []
  (not (empty? (sql/query db/spec [(str "select * from information_schema.tables where"
                                        " table_name = 'tweets'")]))))

(defn migrate []
  (if (not (table-exists?))
    (do
      (println "Running migrations")
      (sql/db-do-commands db/spec
                          (sql/create-table-ddl
                           :tweets
                           [:id :serial "Primary Key"]
                           [:body :varchar "NOT NULL"]
                           [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"])))
    (println "Already migrated")))
