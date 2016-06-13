(ns mytweeter.models.user
  (:require [clojure.java.jdbc :as sql])
  (:require [mytweeter.db :as db]))

(defn all []
  (into [] (sql/query db/spec "select * from users")))

(defn valid? [user]
  (not (empty? user )))

(defn insert-user [user]
  (sql/insert! db/spec :users [:first_name] [(get-in user ["user" "first_name"])]))

(defn create [user]
  (try
    (let [[status]  (vec (insert-user user))]
      (= status 1))
    (catch Exception e
      (println (str "got exception " e))
      false)))
