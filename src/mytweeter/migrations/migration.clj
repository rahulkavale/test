(ns mytweeter.migrations.migration
  (:require [mytweeter.db :as db])
  (:require [clojure.java.jdbc :as sql]))

(defn table-exists? [tablename]
  (empty?
   (sql/query db/spec [(str "select * from information_schema.tables where table_name ='" tablename  "'" )])))

;TODO add foreign key constraints
(defn migrate []
  (if (reduce #(and %2 %1) (map table-exists? '("users" "tweets" "followers")))
    (do
      (println "Running migrations")
      (sql/db-do-commands db/spec
                          (sql/create-table-ddl
                           :tweets
                           [:id :serial "Primary Key"]
                           [:body :varchar "NOT NULL"]
                           [:user_id :int "NOT NULL"]
                           [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
      (sql/db-do-commands db/spec
                          (sql/create-table-ddl
                           :users
                           [:id :serial "Primary Key"]
                           [:first_name :varchar "NOT NULL"]))
      (sql/db-do-commands db/spec
                          (sql/create-table-ddl
                           :followers
                           [:id :serial "Primary Key"]
                           [:user_id :int "NOT NUll"]
                           [:follower_id :int "NOT NULL"])))
    (println "Already migrated")))
