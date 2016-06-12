(ns mytweeter.models.tweet
  (:require [clojure.java.jdbc :as sql])
  (:require [mytweeter.db :as db]))

(defn all []
  (into [] (sql/query db/spec "select * from tweets")))

(defn valid? [tweet]
  (not (empty? tweet )))

(defn create [tweet]
  (try
    (let [[status]  (vec (sql/insert! db/spec :tweets [:body] [tweet]))]
      (= status 1))
    (catch Exception e
      (println (str "got exception " e))
      false)))

(defn delete-all []
  (sql/delete! db/spec :tweets ["true = ? " true]))
